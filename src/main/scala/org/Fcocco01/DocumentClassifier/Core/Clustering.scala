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
      if(v1.isEmpty || v2.isEmpty) ZERO
      else {
        val a: Double = getAbsoluteValue(v1) |> Math.sqrt
        val b: Double = getAbsoluteValue(v2) |> Math.sqrt
        val euclideanLength = a * b
        getDocProduct(v1, v2).values.sum / {if(euclideanLength > ZERO) euclideanLength else ONE}
      }
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
      if(v1.isEmpty || v2.isEmpty) ONE
      else {
        v1.features.map { x => {
          val y = x._2 - v2.features.a(x._1)
          y * y
        }
        }.sum |> Math.sqrt
      }
    /**
      *
      * @param v1
      * @param v2
      * @return
      */
    def manhattan(v1: DVector, v2: DVector): Double =
      if(v1.isEmpty || v2.isEmpty) ONE
      else {
        v1.features.map { x => Math.abs(x._2 - v2.features.a(x._1)) }.sum
      }
  }

  /** Methods to perform an hierarchical clustering for a set of document vectors */
  object HierarchicalClustering {

    import Types.TypeClasses.Clusters.Hierarchical.Cluster
    import scala.collection.mutable.PriorityQueue

    case class MatrixEl(score: Double, vectorL: DVector, vectorR: DVector)
    case class ScoreMatrix(matrix: Array[Array[MatrixEl]], elements: Traversable[DVector])


    /**
      * Find the cluster in the structure that contains a defined vector.
      *
      * @param tree a hierarchy of clusters
      * @param elToFind the vector sought
      * @return the cluster that contain the desired vector
      */
    def getRightCluster(tree: List[Cluster], elToFind: DVector): Option[Cluster] =
      tree.find(_.hasVector(elToFind))

    /**
      * Cut a matrix in half, excluding comparisons between the same vector and itself.
      * This following the concept that half of the matrix are comparisons between vectors already made.
      *
      *        1   2    3     4
      *  -----------------------
      *   1 |  1   .7   .5    .2
      *   2 | .7    1   .8    .1
      *   3 | .5   .8    1    .7
      *   4 | .2   .1   .7     1
      *
      * @param m a similarity matrix
      * @return an array which is one meaningful half of the matrix
      */
    def cutMatrix(m: ScoreMatrix): Array[MatrixEl] =
      m.matrix.flatten.filterNot(x => x.vectorL == x.vectorR).take(m.matrix.flatten.length / TWO + ONE)

    /** Define a strategy to pair different clusters, used mostly for a subsequent merge into one cluster.
      * It provides a [[collection.mutable.PriorityQueue]] ordered depending on the priority the strategy define
      * (like minimum value ) and a function to obtain the right vectors depending on the value of comparison
      * defined by the strategy. */
    trait Merging_Strategy {
      def getScore(m: Either[ScoreMatrix, Traversable[MatrixEl]]): MatrixEl

      def getPQueue(m: ScoreMatrix): mutable.PriorityQueue[MatrixEl]
    }

    /**
      * Single linkage is a inter-cluster metric that join two cluster based on their two closest
      * members.
      * IMPORTANT: It is currently set to natively work with SIMILARITY measures.
      * As such, if measures DISTANCE scoring was used, [[Complete_Link]]  where the intention
      * was to use [[Single_Link]] should be used instead for a correct result.
      */
    object Single_Link extends Merging_Strategy {

      override def getPQueue(m: ScoreMatrix): mutable.PriorityQueue[MatrixEl] =
        mutable.PriorityQueue[MatrixEl](cutMatrix(m): _*)(Ordering.by(_.score))

      override def getScore(m: Either[ScoreMatrix, Traversable[MatrixEl]]): MatrixEl = {
        val matrix = m match {
          case Left(x) => x.matrix.flatten.toList
          case Right(y) => y.toList
        }

        @tailrec
        def find(a: List[MatrixEl]): MatrixEl = {
          val mx = a.maxBy(_.score)
          if (mx.vectorL.id == mx.vectorR.id) {
            find(a diff List(mx))
          } else mx
        }

        find(matrix)
      }
    }

    /**
      * Complete linkage is a inter-cluster metric that join two cluster based on their two
      * furthest members.
      * IMPORTANT: It is currently set to natively work with SIMILARITY measures.
      * As such, if measures DISTANCE scoring was used, [[Single_Link]]  where the intention
      * was to use [[Complete_Link]] should be used instead for a correct result.
      */
    object Complete_Link extends Merging_Strategy {

      override def getPQueue(m: ScoreMatrix): mutable.PriorityQueue[MatrixEl] =
        mutable.PriorityQueue[MatrixEl](cutMatrix(m): _*)(Ordering.by(_.score)).reverse

      override def getScore(m: Either[ScoreMatrix, Traversable[MatrixEl]]) : MatrixEl = {
        val matrix = m match {
          case Left(x) => x.matrix.flatten.toList
          case Right(y) => y.toList
        }

        @tailrec
        def find(a: List[MatrixEl]): MatrixEl = {
          val mx = a.minBy(_.score)
          if (mx.vectorL.id == mx.vectorR.id) {
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
    def createSimMatrix(vectors: Traversable[DVector], f: DistanceORSimFun): ScoreMatrix = {
      val v = vectors.toArray.par
      val size = v.size
      val matrix = Array.ofDim[MatrixEl](size, size)
      for (i <- ZERO until v.size) {
        for (j <- ZERO until v.size) {
          matrix(i)(j) =
            MatrixEl(f(v(i), v(j)), v(i), v(j))
        }
      }
      ScoreMatrix(matrix, vectors)
    }

    /**
      *
      * @param m
      * @param init
      * @param linkStrategy
      * @return
      */
    def agglomerative_HC(m: ScoreMatrix, init: Seq[DVector] => List[Cluster], linkStrategy: Merging_Strategy): Cluster = {
      val cls = init(m.elements.toSeq)
      val p: mutable.PriorityQueue[MatrixEl] = linkStrategy.getPQueue(m)

      @tailrec
      def createClusterTree(setL: Int, tree: List[Cluster]): Cluster = {
        tree.size match {
          case ONE => tree.head
          case _ =>
            val c: MatrixEl = linkStrategy.getScore(Right(p))
            val cls1 = getRightCluster(tree, c.vectorL)
            val cls2 = getRightCluster(tree, c.vectorR)
            if (cls1.isEmpty || cls2.isEmpty || cls1 == cls2 ) {
              p.dequeue()
              createClusterTree(setL, tree)
            } else {
              val mergedCls = cls1.get.merge(cls2.get)(Some(p.dequeue.score))
              val newTree = tree.filterNot(x => {
                val c = mergedCls.getChildren.get
                x == c._1 || x == c._2})
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
    def k_Means(k: Int) (dist: DistanceORSimFun) (vectors: DVector*) : Vector[Cluster] = {
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
    private def valueMean(values: Vector[Double]) : Double = values.sum / values.size

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
