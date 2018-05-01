import org.Fcocco01.DocumentClassifier.Core.Clustering.Distance._
import org.Fcocco01.DocumentClassifier.Test.TestingResources.DummyObject.{vector2, vector3, vector5, vector6}
import org.Fcocco01.DocumentClassifier.Test.UnitTest

class DistanceTest extends UnitTest("Core.Clustering.Distance"){
  "Euclidean distance" should "be the expected value" in {
    assertResult(0.2710370178090432) { euclidean(vector3, vector2) }
  }

  "Manhattan distance" should "be the expected value" in {
    assertResult(0.9260830906034545) { manhattan(vector3, vector2) }
  }
}
