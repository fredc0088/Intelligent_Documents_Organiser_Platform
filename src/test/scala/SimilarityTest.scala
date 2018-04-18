package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.Core.Clustering.Similarity.{cosine, getDocProduct}
import org.Fcocco01.DocumentClassifier.Test.TestingResources.DummyObject._

import scala.collection.immutable.HashMap

class SimilarityTest extends UnitTest("Clustering.Similarity") {
  "Cosine similarity" should "return the expected value" in {
    val x = cosine(vector1, vector2)
    //    assertResult() {  }
  }

  "Getting the absolute value of a vector" should "return the expected result" in {
    //    getAbsoluteValue
  }

  "Calculation of the DOT Product of two vectors" should "return the expected result" in {
    //    getDocProduct()
    assertResult(0.005058770926783665) {
      vector2.apply("ability") * vector3.apply("ability")
    }

    assertResult(HashMap("embedded" -> 0.24, "plugin" -> 0.0, "test" -> 0.0,
      "fonts" -> 0.0,
      "knows" -> 0.0356546565, "essential" -> 0.023304703216552734375,
      "demonstrates" -> 0.0,
      "line" -> 0.0003122705599836995625,
      "document" -> 0.0,
      "compiler" -> 0.0093295826840972900390625,
      "data" -> 0.0110230489020908275390625,
      "inline" -> 0.0,
      "programmer" -> 0.0060836277948876953125,
      "demonstrate" -> 0.0, "text" -> 0.125259150456,
      "particular" -> 0.006423223754201637)) {
      getDocProduct(vector5,vector6)
    }

  }
}
