package com.example.quickstart

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

  def getBens() : IO[Json] = {
    val target = Uri.uri("https://en.wikipedia.org/w/api.php?action=opensearch&search=benjamin&limit=100")
    httpClient.expect[Json](target)
  }

  def chooseBen(bens: IO[Json]) : Json = {
    val random = new scala.util.Random
    val randomNumber = random.nextInt(100)
    val name = bens.unsafeRunSync().asArray.get(1).asArray.get(randomNumber)
    Json.obj(
      "Name" -> bens.unsafeRunSync().asArray.get(1).asArray.get(randomNumber),
      "Description" -> bens.unsafeRunSync().asArray.get(2).asArray.get(randomNumber),
      "Url" -> bens.unsafeRunSync().asArray.get(3).asArray.get(randomNumber)
    )
  }

  val benService: HttpService[IO] = HttpService[IO] {
    case GET -> Root / "ben" =>
      Ok(Json.obj("Result" -> chooseBen(getBens())))
  }

  def stream(args: List[String], requestShutdown: IO[Unit]) =
    BlazeBuilder[IO]
      .bindHttp(8080, "localhost")
      .mountService(benService, "/")
      .serve
}
