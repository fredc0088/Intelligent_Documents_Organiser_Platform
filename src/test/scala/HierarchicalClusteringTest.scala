package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.{Core, Essentials}
import TestingResources.DummyObject.{vector1, vector2, vector3, vector4, vector5, vector6, vector7}
import Core.Clustering._
import Essentials.Types.TypeClasses._
import Vectors.{EmptyVector, DocumentVector => V}
import Similarity._
import HierarchicalClustering._
import Clusters.Hierarchical.{Cluster, MultiCluster, SingleCluster}

class HierarchicalClusteringTest extends UnitTest("Core.Clustering.HierarchicalClustering") {

  var vectors: Vector[V] = _
  var matrix: ScoreMatrix = _
  var standard_init: Seq[V] => List[Cluster] = _
  var clusters: Cluster = _

  override def beforeAll(): Unit = {
    vectors = Vector(vector1,vector2,vector3,vector4,vector7)
    matrix = createSimMatrix(vectors, cosine)
    standard_init = (x: Seq[DVector]) => x.map(SingleCluster).toList
    clusters = agglomerative_HC(matrix,standard_init,Single_Link)
    super.beforeAll
  }

  "Similarity matrix's elements" should "have a valid score" in {
    assert(matrix.matrix.forall(_.forall(x => !x.score.isNaN || !x.score.isInfinite || x.score >= 0)))
  }

  "Agglomerative Hierarchical clustering single-link" should "create the expected structure" in {
    val expected =
      MultiCluster(
        MultiCluster(
          MultiCluster(
          MultiCluster(
            SingleCluster(vector3),SingleCluster(vector2)
          )(Some(0.32964472509699594)),
          SingleCluster(vector7)
        )(Some(0.0)),
          SingleCluster(vector4)
        )(Some(0.0)), SingleCluster(vector1)
      )(Some(0.0))
    assert(expected == clusters)
  }

  "Agglomerative Hierarchical clustering single-link" should "return right distance for multicluster X" in {
    assertResult(0.0){ clusters.getChildren.get._1.distance.get }
  }

  "getRightCluster" should "return the cluster containing the given vector" in {
    val one = MultiCluster(SingleCluster(vector3),SingleCluster(vector2))(None)
    val two = MultiCluster(SingleCluster(vector1),SingleCluster(vector4))(None)
    val three = MultiCluster(SingleCluster(vector6),SingleCluster(vector5))(None)
    val clusters = List(one,two,three)
    assertResult(two){ getRightCluster(clusters, vector4).get }
  }

  "getRightCluster" should "return None if the required vector is not in any cluster in the list" in {
    val one = MultiCluster(SingleCluster(vector3),SingleCluster(vector2))(None)
    val two = MultiCluster(SingleCluster(vector1),SingleCluster(vector4))(None)
    val three = SingleCluster(vector7)
    val clusters = List(one,two,three)
    assertResult(None){ getRightCluster(clusters, vector5) }
  }

  "Name of the clusters" should "be equal to expectation" in {
    assertResult("embedded_ demonstrates") {clusters.name.split("__").head.trim}
    assertResult("embedded_ demonstrates") {clusters.getChildren.get._1.name.split("__").head.trim}
    assertResult("test.docx") {clusters.getChildren.get._2.name.split("__").head.trim}
  }

  "Merging two clusters" should "handle null" in {
    assertResult(EmptyVector){SingleCluster(vector1).merge(null)(None).getChildren.get._2.vectors.head}
  }

  "Singleton cluster" should "handle null" in {
    assertResult(EmptyVector){SingleCluster(null).vectors.head}
  }

  "Singleton cluster's name created with an empty vector or null" should "be an empty string" in {
    assertResult(""){SingleCluster(null).name}
    assertResult(""){SingleCluster(EmptyVector).name}
  }

}
