package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Essentials.{Constants, Types, Util}
import Constants.{ONE, TWO, ZERO}
import Util.Operators.|>
import org.Fcocco01.DocumentClassifier.Essentials.Types.Token
import org.Fcocco01.DocumentClassifier.Essentials.Types.TypeClasses.Vectors

import scala.annotation.tailrec
import scala.collection.mutable

/**
  * Provide grounds for clustering sets of document vectors.
  */
object Clustering {

  type DVector =  Types.TypeClasses.Vectors.DocumentVector
  type DistanceORSimFun = (DVector, DVector) => Double

  /**
    * Provides functions to find a similarity between document vectors.
    */
  object Similarity {

    /**
      *
      * @param v1
      * @param v2
      * @return
      */
    def cosine(v1: DVector, v2: DVector): Double = {
      val a: Double = getAbsoluteValue(v1) |> Math.sqrt
      val b: Double = getAbsoluteValue(v2) |> Math.sqrt
      getDocProduct(v1, v2).values.sum / (a * b)
    }

    def getDocProduct(v1: DVector, v2: DVector): Map[Token, Double] =
      v1.features.map { case (k, v) => k -> (v * v2.features.getOrElse(k, ZERO.toDouble)) }

    def getAbsoluteValue(v: DVector): Double = v.features.toVector.map(x => Math.pow(x._2, TWO)).sum
  }

  /**
    * Provides functions to calculate the conceptual distance between document vectors on a vector model plane.
    */
  object Distance {

    /**
      *
      * @param v1
      * @param v2
      * @return
      */
    def euclidean(v1: DVector, v2: DVector): Double =
      v1.features.map { x => {
        val y = x._2 - v2.features.a(x._1)
        y * y
      }
      }.sum |> Math.sqrt

    /**
      *
      * @param v1
      * @param v2
      * @return
      */
    def manhattan(v1: DVector, v2: DVector): Double =
      v1.features.map { x => Math.abs(x._2 - v2.features.a(x._1)) }.sum
  }

  /** Methods to perform an hierarchical clustering for a set of document vectors */
  object HierarchicalClustering {

    import Types.TypeClasses.Clusters.Hierarchical.Cluster
    import scala.collection.mutable.PriorityQueue

    type MatrixEl = (Double, DVector, DVector)
    type SimMatrix = Array[Array[MatrixEl]]


    /**
      * Find the cluster in the structure that contains a defined vector.
      *
      * @param tree a hierarchy of clusters
      * @param elToFind the vector sought
      * @return the cluster that contain the desired vector
      */
    def getRightCluster(tree: List[Cluster], elToFind: DVector): Cluster =
      tree.find(_.hasVector(elToFind)).get

    /**
      * Cut a matrix in half, excluding comparisons between the same vector and itself.
      * This following the concept that half of the matrix are comparisons between vectors already made.
      *
      *        1   2    3     4
      *  -----------------------
      *   1 |  1   .7   .5    .2
      *   2 | .7   1    .8    .1
      *   3 | .5   .8    1    .7
      *   4 | .2   .1   .7     1
      *
      * @param m a similarity matrix
      * @return an array which is one meaningful half of the matrix
      */
    def cutMatrix(m: SimMatrix): Array[MatrixEl] =
      m.flatten.filterNot(x => x._2 == x._3).take(m.flatten.length / TWO + ONE)

    /** Define a strategy to pair different clusters, used mostly for a subsequent merge into one cluster.
      * It provides a [[collection.mutable.PriorityQueue]] ordered depending on the priority the strategy define
      * (like minimum value) and a function to obtain the right vectors depending on the value of comparison
      * defined by the strategy. */
    trait Merging_Strategy {
      def getDistance(m: Either[SimMatrix, Traversable[MatrixEl]]): MatrixEl

      def getPQueue(m: SimMatrix): mutable.PriorityQueue[MatrixEl]
    }

    /**
      *
      */
    object Single_Link extends Merging_Strategy {

      override def getPQueue(m: SimMatrix): mutable.PriorityQueue[MatrixEl] =
        mutable.PriorityQueue[MatrixEl](cutMatrix(m): _*)(Ordering.by(_._1))

      override def getDistance(m: Either[SimMatrix, Traversable[MatrixEl]]): (Double, DVector, DVector) = {
        val matrix = m match {
          case Left(x) => x.flatten.toList
          case Right(y) => y.toList
        }

        @tailrec
        def find(a: List[MatrixEl]): MatrixEl = {
          val mx = a.maxBy(_._1)
          if (mx._2.id == mx._3.id) {
            find(a diff List(mx))
          } else mx
        }

        find(matrix)
      }
    }

    /**
      *
      */
    object Complete_Link extends Merging_Strategy {

      override def getPQueue(m: SimMatrix): mutable.PriorityQueue[MatrixEl] =
        mutable.PriorityQueue[MatrixEl](cutMatrix(m): _*)(Ordering.by(_._1)).reverse

      override def getDistance(m: Either[SimMatrix, Traversable[MatrixEl]]) : MatrixEl = {
        val matrix = m match {
          case Left(x) => x.flatten.toList
          case Right(y) => y.toList
        }

        @tailrec
        def find(a: List[MatrixEl]): MatrixEl = {
          val mx = a.minBy(_._1)
          if (mx._2.id == mx._3.id) {
            find(a diff List(mx))
          } else mx
        }

        find(matrix)
      }
    }

    /**
      * Create a matrix of the similarity/distance between all dataset of vectors.
      *
      * @param vectors dataset
      * @param f distance/similarity function to apply on two vectors
      * @return a matrix of [[MatrixEl]]
      */
    def createSimMatrix(vectors: Traversable[DVector], f: DistanceORSimFun): SimMatrix = {
      val v = vectors.toArray.par
      val size = v.size
      val matrix = Array.ofDim[MatrixEl](size, size)
      for (i <- ZERO until v.size) {
        for (j <- ZERO until v.size) {
          matrix(i)(j) =
            (f(v(i), v(j)), v(i), v(j))
        }
      }
      matrix
    }

    /**
      *
      * @param m
      * @param init
      * @param linkStrategy
      * @param v
      * @return
      */
    def HAC(m: SimMatrix, init: Seq[DVector] => List[Cluster], linkStrategy: Merging_Strategy, v: DVector*): Cluster = {
      val cls = init(v.toSeq)
      val p: mutable.PriorityQueue[MatrixEl] = linkStrategy.getPQueue(m)

      @tailrec
      def createClusterTree(setL: Int, tree: List[Cluster]): Cluster = {
        tree.size match {
          case ONE => tree.head
          case _ =>
            val c: MatrixEl = linkStrategy.getDistance(Right(p))
            val cls1 = getRightCluster(tree, c._2)
            val cls2 = getRightCluster(tree, c._3)
            if (cls1 == cls2) {
              p.dequeue()
              createClusterTree(setL, tree)
            } else {
              val mergedCls = cls1.merge(cls2)(Some(p.dequeue._1))
              val newTree = tree.filterNot(x => x == mergedCls.childL || x == mergedCls.childR)
              val newTree2 = newTree :+ mergedCls
              createClusterTree(setL + ONE, newTree2)
            }
        }
      }

      createClusterTree(cls.size, cls)
    }

  }

  /** Methods to perform a flat clustering for a set of document vectors */
  object FlatClustering {

    import Types.TypeClasses.Clusters.Flat.{Cluster, Comparison}

    /**
      * Simple K-means algorithm to assign the document vectors to k number of clusters
      * based on their distance from the center of that cluster.
      * A centroid is recomputed each iteration creating as a fake vector calculated from the mean
      * of the vectors in a cluster.
      *
      * @param k number of desired clusters
      * @param dist a distance function
      * @param vectors the dataset formed of document vectors
      * @return a set of clusters
      */
    def K_Means(k: Int) (dist: DistanceORSimFun) (vectors: DVector*) : Vector[Cluster] = {
      val n = if(k <= vectors.size && k > ZERO) k else Math.sqrt(k/TWO).toInt

      val initialSeeds = scala.util.Random.shuffle(vectors).take(n)

      def help(oldCentroids: Vector[DVector]): Vector[Cluster] = {
        val clustersDistances = oldCentroids.flatMap {
          x => vectors.map(y => Comparison(y, x, dist(x, y))).zipWithIndex
        }.groupBy(_._2)
          .map ( x => x._2.map(y => y._1).toList.minBy(z => z.distance) ) .toVector

        val newClusters = clustersDistances.groupBy(_.vectorComparedTo)
          .map(x => Cluster(computeNewCentroid(x._2.map(_.vector): _*), x._2.map(_.vector): _*)
          ).toVector

        val newCentroids = newClusters.map(x => x.center)

        if (newCentroids == oldCentroids) newClusters
        else help(newCentroids)
      }
      help(initialSeeds.toVector)
    }

    /* Possible method for future implementation of K-Medoids */
    //    def findBestCenter(value: Double, elements: Vector[Comparison]) = {
    //      var current = elements(0)
    //      for(el <- elements){
    //        if(Math.abs(value - el.distance) < (Math.abs(value - current.distance)))
    //          current = el
    //      }
    //      current
    //    }

    /**
      * Creates a centroid vector from the mean of a set of vectors.
      *
      * @param vectors the vectors used for the calculation
      * @return a new vector representing the mean vector of the input set
      */
    def computeNewCentroid(vectors: DVector*): Vectors.RealVector = {
      val vector = vectors.head.features.map(x => (x._1, valueMean(vectors.map(y => y.features(x._1)).toVector)))
      Types.TypeClasses.Vectors.RealVector(vector.hashCode.toString,vector)
    }

    /**
      * Get the mean value of two values.
      *
      * @param values
      * @return the mean value
      */
    private def valueMean(values: Vector[Double]) = {
      values.sum / values.size
    }

    /**
      * Print one or more clusters on the console with information on their content
      * @param clusters
      */
    def printClusters(clusters: Cluster*) : Unit = {
      clusters.zipWithIndex.foreach {
        x => {
          println(s"Cluster ${x._1.name}: ")
          x._1.elements.foreach { y =>
            println(s"            ${y.id}")
          }
        }
      }
    }
  }

}
