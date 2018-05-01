package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.{Essentials,Core}
import TestingResources.DummyObject.{vector1,vector2,vector3,vector4,vector5,vector6}
import Core.Clustering._
import Essentials.Types.TypeClasses.Vectors.{DocumentVector => V}
import Similarity._
import Distance._
import HierarchicalClustering._

import scala.collection.immutable.HashMap

class HierarchicalClusteringTest extends UnitTest("Core.Clustering.HierarchicalClustering") {

  var vectors: Vector[V] = _
  var matrix: SimMatrix = _

  override def beforeAll(): Unit = {
    vectors = Vector(vector1,vector2,vector3,vector4,vector5,vector6)


    super.beforeAll()
  }




/*

 */

}
