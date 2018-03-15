package com.boomtownroi.benapi

import org.specs2.matcher.MatchResult
import org.specs2.mutable._
import org.specs2.mock._

class BenServerSpec extends Specification with Mockito {

  "BenServer" should {
    "Select a Ben from a dataset correctly" in {
      selectTest()
    }
  }

  // test select method
  private def selectTest() : MatchResult[String] = {
    val selectedBenIndex0 = BenServer.select(DataSet.sampleData, 1, 0)
    val selectedBenIndex99 = BenServer.select(DataSet.sampleData, 1, 99)
    val selectedBenIndexOver = BenServer.select(DataSet.sampleData, 1, 256)

    selectedBenIndex0.asString.orNull must contain("Benjamin")
    selectedBenIndex99.asString.orNull must contain("Benjamin")
  }

}
