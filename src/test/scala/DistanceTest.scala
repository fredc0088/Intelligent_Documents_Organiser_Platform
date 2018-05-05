import org.Fcocco01.DocumentClassifier.Core.Clustering.Distance._
import org.Fcocco01.DocumentClassifier.Essentials.Types.TypeClasses.Vectors.{EmptyVector, RealVector}
import org.Fcocco01.DocumentClassifier.Test.TestingResources.DummyObject.{vector2, vector3}
import org.Fcocco01.DocumentClassifier.Test.UnitTest

import scala.collection.immutable.HashMap

class DistanceTest extends UnitTest("Core.Clustering.Distance"){
  "Euclidean distance" should "be the expected value" in {
    assertResult(0.2710370178090432) { euclidean(vector3, vector2) }
  }

  "Euclidean distance" should "return 1 if one or both vector are empty" in {
    assert(euclidean(RealVector("a", HashMap("" -> 0.0)), EmptyVector) === 1 )
  }

  "Manhattan distance" should "be the expected value" in {
    assertResult(0.9260830906034545) { manhattan(vector3, vector2) }
  }

  "Manhattan distance" should "return 1 if one or both vector are empty" in {
    assert(manhattan(RealVector("a", HashMap("" -> 0.0)), EmptyVector) === 1 )
  }
}
