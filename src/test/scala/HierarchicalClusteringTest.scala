package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.{Core, Essentials}
import TestingResources.DummyObject.{vector1, vector2, vector3, vector4, vector5, vector6}
import Core.Clustering._
import Essentials.Types.TypeClasses.Vectors.{DocumentVector => V}
import Similarity._
import Distance._
import HierarchicalClustering._
import org.Fcocco01.DocumentClassifier.Essentials.Types.TypeClasses.Clusters.Hierarchical
import org.Fcocco01.DocumentClassifier.Essentials.Types.TypeClasses.Clusters.Hierarchical.{Cluster, MultiCluster, SingleCluster}
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks


class HierarchicalClusteringTest extends UnitTest("Core.Clustering.HierarchicalClustering") {

  var vectors: Vector[V] = _
  var matrix: SimMatrix = _
  var standard_init: Seq[V] => List[Cluster] = _
  var ahcResult: List[Cluster] = _

  override def beforeAll(): Unit = {
    vectors = Vector(vector1,vector2,vector3,vector4,vector5,vector6)
    matrix = createSimMatrix(vectors, cosine)
    standard_init = (x: Seq[DVector]) => x.map(SingleCluster).toList
    super.beforeAll()
  }


  "Similarity matrix's elements" should "tuple of three elements (Double,DocumentVector,DocumentVector)" in {
    assert(matrix.forall(_.forall(x => !x._1.isNaN || !x._1.isInfinite || x._1 >= 0)))
  }

  "Agglomerative Hierarchical clustering single-link" should "create the correct structure" in {
    val vectors = Array(vector1,vector2,vector3,vector4)
    val clusters = agglomerative_HC(matrix,standard_init,Single_Link,vectors: _*)
    val expected = MultiCluster(SingleCluster(vector3),SingleCluster(vector2))
  }



//  "Similarity matrix's elements" should "tuple of three elements (Double,DocumentVector,DocumentVector)" in {
//    assert(matrix.forall(_.isInstanceOf[(Double, V, V)]))
//  }

class Similarity extends PropSpec with TableDrivenPropertyChecks with Matchers {

}

/*

 */

}
