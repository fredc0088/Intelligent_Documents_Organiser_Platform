package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.{Core, Essentials}
import TestingResources.DummyObject.{vector1, vector2, vector3, vector4, vector5, vector6}
import Core.Clustering._
import Essentials.Types.TypeClasses.Vectors.{DocumentVector => V}
import Similarity._
import Distance._
import HierarchicalClustering._
import org.scalatest.{Matchers, PropSpec}
import org.scalatest.prop.TableDrivenPropertyChecks


class HierarchicalClusteringTest extends UnitTest("Core.Clustering.HierarchicalClustering") {

  var vectors: Vector[V] = _
  var matrix: SimMatrix = _

  override def beforeAll(): Unit = {
    vectors = Vector(vector1,vector2,vector3,vector4,vector5,vector6)
    matrix = createSimMatrix(vectors, cosine)

    super.beforeAll()
  }


  "Similarity matrix's elements" should "tuple of three elements (Double,DocumentVector,DocumentVector)" in {
    assert(matrix.forall(_.forall(x => !x._1.isNaN || !x._1.isInfinite || x._1 >= 0)))
  }

//  "Similarity matrix's elements" should "tuple of three elements (Double,DocumentVector,DocumentVector)" in {
//    assert(matrix.forall(_.isInstanceOf[(Double, V, V)]))
//  }

class Similarity extends PropSpec with TableDrivenPropertyChecks with Matchers {

}

/*

 */

}
