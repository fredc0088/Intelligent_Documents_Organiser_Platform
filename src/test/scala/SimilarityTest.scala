package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.Core.Clustering.Similarity.{cosine, getDocProduct}
import org.Fcocco01.DocumentClassifier.Essentials.Types.TypeClasses.Vectors.{EmptyVector, RealVector}
import org.Fcocco01.DocumentClassifier.Test.TestingResources.DummyObject._

import scala.collection.immutable.HashMap

class SimilarityTest extends UnitTest("Core.Clustering.Similarity") {
  it should "return the expected value" in {
    assertResult(0.32964472509699594) { cosine(vector3, vector2) }
  }

  it should "handle division to 0" in {
    assertResult(0) {
      cosine(RealVector("a", HashMap("" -> 0.0)), RealVector("b", HashMap("" -> 0.0))) }
  }

  it should "return Nan if one or both vector are empty" in {
    assert(cosine(RealVector("a", HashMap("" -> 0.0)), EmptyVector) === 0 )
  }


  "Calculation of the DOT Product of two vectors" should "return the expected result" in {
    assertResult(0.005058770926783665) {
      vector2.features("ability") * vector3.features("ability")
    }

    assertResult(HashMap("embedded" -> 0.12, "plugin" -> 0.0, "test" -> 0.0,
      "fonts" -> 0.0,
      "knows" -> 0.0009230198299408961, "essential" -> 0.023304703216552734,
      "demonstrates" -> 0.0,
      "line" -> 0.00031227055998369956,
      "document" -> 0.0,
      "compiler" -> 0.00932958268409729,
      "data" -> 0.011023048902090826,
      "inline" -> 0.0,
      "programmer" -> 0.006083627794887695,
      "demonstrate" -> 0.0, "text" -> 0.125259150456,
      "particular" -> 0.006423223754201637)) {
      getDocProduct(vector5,vector6)
    }

  }
}
