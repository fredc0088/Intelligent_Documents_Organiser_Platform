package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.{Core, Essentials}
import TestingResources.DummyObject.{vector1, vector2, vector3, vector4, vector7}
import Core.Clustering.FlatClustering._
import Essentials.Types.TypeClasses.Vectors.{DocumentVector => V}
import Essentials.Types.TypeClasses.Clusters.Flat.Cluster
import Core.Clustering.Distance._

class FlatClusteringTest extends UnitTest("Core.Clustering.FlatClustering") {

  var vectors: Vector[V] = _
  var standard_init: Seq[V] => List[Cluster] = _

  override def beforeAll(): Unit = {
    vectors = Vector(vector1, vector2, vector3, vector4, vector7)
    super.beforeAll
  }

  "computeNewCentroid" should "return a new vector as the mean of a set of given vectors" in {
    assertResult(Array(("embedded", 0.01553266676302264), ("plugin", 0.01553266676302264),
      ("test", 0.23299000144533963), ("fonts", 0.01553266676302264), ("knows", 0.0), ("essential", 0.0),
      ("demonstrates", 0.06361616729595498),("line", 0.0), ("document", 0.06361616729595498),
      ("compiler", 0.0),("data", 0.0), ("inline", 0.01553266676302264), ("programmer", 0.0),
      ("demonstrate", 0.01060269454932583), ("text", 0.01553266676302264),("particular", 0.0),
      ("guarantee", 0.0),("safe", 0.0),("iterator", 0.0), ("object", 0.0),("ensure", 0.0),("returned", 0.0),
      ("various", 0.01553266676302264),("cast", 0.0), ("slightly", 0.0),("annoying", 0.0),
      ("variable", 0.0), ("ability", 0.06361616729595498),("placed", 0.0),
      ("calibre", 0.01553266676302264), ("integer", 0.0),("docx", 0.01553266676302264),
      ("kind", 0.0),("type", 0.0),("typically", 0.0),("required", 0.0),("assignment", 0.0),
      ("formatting", 0.01553266676302264), ("types", 0.01553266676302264),
      ("input", 0.01553266676302264),("list", 0.0)).toMap) {computeNewCentroid(vectors.take(3): _*).features}
  }

  "Kmeans" should "return the same number of cluster as established when the function is called" in {
    k_Means(4)(euclidean)(vectors: _*).size should be(4)
    k_Means(2)(euclidean)(vectors: _*).size should be(2)
  }

  "If K is more than the vectors used or k is 0 or , k_Means" should "return a number of cluster equal to k/2 squared" in {
    k_Means(7)(euclidean)(vectors: _*).size should be(1)
  }

}
