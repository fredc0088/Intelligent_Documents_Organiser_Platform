package org.Fcocco01.DocumentClassifier.Test


class AnalysisTest extends UnitTest ("Analysis"){

  import TestingResources._
  var timeTaken: Double = _

  override def beforeAll(): Unit = {
    timeTaken = System.nanoTime()
  }


}
