package com.boomtownroi.benapi

import cats.effect.IO
import fs2.StreamApp
import io.circe._
import org.http4s._
import org.http4s.circe._
import org.http4s.dsl._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.client.blaze._

import scala.concurrent.ExecutionContext.Implicits.global

object BenServer extends StreamApp[IO] with Http4sDsl[IO] {

  val httpClient = Http1Client[IO]().unsafeRunSync

  case class Ben(query: String)

  def getBens(): IO[Json] = {
    val target = Uri.uri("https://en.wikipedia.org/w/api.php?action=opensearch&search=benjamin&limit=100")
    httpClient.expect[Json](target)
  }

  def chooseBen(bens: Json): Json = {
    // scalacheck
    val random = new scala.util.Random
    val randomNumber = random.nextInt(100)

    Json.obj(
      "Name" -> select(bens, 1, randomNumber),
      "Description" -> select(bens, 2, randomNumber),
      "Url" -> select(bens, 3, randomNumber)
    )
  }

  def select(element: Json, index: Int, random: Int): Json = {
    element.asArray.get(index).asArray.get(random)
  }

  val benService = HttpService[IO] {
    case GET -> Root / "ben" =>
      Ok(Json.obj("Result" -> getBens.map(chooseBen).unsafeRunSync()))
  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(benService, "/")
      .serve
}
