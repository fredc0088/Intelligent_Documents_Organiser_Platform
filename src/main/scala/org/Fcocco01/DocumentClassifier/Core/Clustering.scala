package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Utils.{Util,Types,Constants}
import Constants.{ZERO,ONE,TWO,HALF}
import Util.Operators.|>

import scala.annotation.tailrec

/**
  * Provide grounds for clustering sets of document vectors.
  */
object Clustering {

  type DVector = Types.TypeClasses.Vectors.DocumentVector
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
    def cosine(v1: DVector, v2: DVector) = {
      val a: Double = getAbsoluteValue(v1) |> Math.sqrt
      val b: Double = getAbsoluteValue(v2) |> Math.sqrt
      getDocProduct(v1, v2).map(_._2).reduce(_ + _) / Math.sqrt(a * b)
    }

    def getDocProduct(v1: DVector, v2: DVector) =
      v1.apply.map { case (k, v) => k -> (v * v2.apply.getOrElse(k, ZERO.toDouble)) }

    def getAbsoluteValue(v: DVector): Double = {
      v.apply.toVector.map(x => Math.pow(x._2, TWO)).reduce(_ + _)
    }
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
    def euclidean(v1: DVector, v2: DVector) =
      v1.apply.map { x => {
        val y = x._2 - v2.apply.a(x._1); y * y
      }
      }.reduce(_ + _) |> Math.sqrt

    /**
      *
      * @param v1
      * @param v2
      * @return
      */
    def manhattan(v1: DVector, v2: DVector) =
      v1.apply.map { x => x._2 - v2.apply.a(x._1) }.reduce(_ + _)
  }

  /** Methods to perform an hierarchical clustering for a set of document vectors */
  object HierarchicalClustering {

    import scala.collection.immutable.ListMap
    import scala.collection.mutable.PriorityQueue

    type MatrixEl = (Double, DVector, DVector)
    type SimMatrix = Array[Array[MatrixEl]]

    sealed trait Cluster {

      val distance: Option[Double]
      val vectors: List[DVector]
      val name: String

      def merge(c: Cluster)(d: Option[Double]): MultiCluster = MultiCluster(this, c)(d) // Merge two existing clusters
      def hasVector(v: DVector): Boolean = vectors.exists(_ == v)

      def getHeight: Double =
        this.getChildren match {
          case None => HALF
          case Some((left, right)) => left.getHeight + right.getHeight
        }

      def getChildren: Option[(Cluster, Cluster)]
    }

    final case class SingleCluster(private val v: DVector) extends Cluster {
      override def getChildren = None

      lazy val vectors: List[DVector] = List(v)
      lazy val name = {
        v.apply.maxBy(_._2)._1
        vectors.head.id
      }
      override val distance = None
    }


    final case class MultiCluster(childL: Cluster, childR: Cluster)(val distance: Option[Double]) extends Cluster {

      override def getChildren = Some(childL, childR)

      // It does not work properly
      lazy val name: String = {
        val vectors = this.vectors.map(x => ListMap(x.apply.toSeq.sortWith(_._2 > _._2): _*))
        val highestTerms = vectors.map(x => x.headOption)
        val default = (scala.util.Random.alphanumeric.take(5).toString, ZERO.toDouble)

        @tailrec
        def constructTitle(string: String, n: Int, terms: List[Option[(String, Double)]], default: (String, Double)): String =
          if (terms.isEmpty) string
          else n match {
            case ZERO => string
            case _ => constructTitle(s"${string} ${terms.head.getOrElse(default)._1} ", n - ONE, terms.tail, default)
          }
        val height = getHeight.toInt
        if (height > 3 && highestTerms.length > 3)
          constructTitle("", height / 3, highestTerms, default)
        else constructTitle("", height, highestTerms, default)
      }

      lazy val vectors: List[DVector] =
        getVectors(this, List.empty[DVector])

      private def getVectors(c: Cluster, a: List[DVector]): List[DVector] =
        c.getChildren match {
          case None => c.vectors
          case Some((left,right)) => left.vectors ::: right.vectors
        }
    }

    def getRightCluster(tree: List[Cluster], elToFind: DVector): Cluster =
      tree.find(_.hasVector(elToFind)).get

    def cutMatrix(m: SimMatrix): Array[MatrixEl] =
      m.flatten.filterNot(x => x._2 == x._3).take(m.flatten.length / TWO + ONE)

    /** Define a strategy to pair different clusters, used mostly for a subsequent merge into one cluster.
      * It provides a [[collection.mutable.PriorityQueue]] ordered depending on the priority the strategy define
      * (like minimum value) and a function to obtain the right vectors depending on the value of comparison
      * defined by the strategy. */
    trait Linkage_Strategy {
      def getDistance(m: Either[SimMatrix, Traversable[MatrixEl]]): MatrixEl

      def getPQueue(m: SimMatrix): PriorityQueue[MatrixEl]
    }

    object Single_Link extends Linkage_Strategy {

      override def getPQueue(m: SimMatrix): PriorityQueue[MatrixEl] =
        PriorityQueue[MatrixEl](cutMatrix(m): _*)(Ordering.by(_._1))

      override def getDistance(m: Either[SimMatrix, Traversable[MatrixEl]]) = {
        val matrix = m match {
          case Left(x) => x.flatten.toList;
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

    object Complete_Link extends Linkage_Strategy {

      override def getPQueue(m: SimMatrix): PriorityQueue[MatrixEl] =
        PriorityQueue[MatrixEl](cutMatrix(m): _*)(Ordering.by(_._1)).reverse

      override def getDistance(m: Either[SimMatrix, Traversable[MatrixEl]]) = {
        val matrix = m match {
          case Left(x) => x.flatten.toList;
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

    def HAC(m: SimMatrix, init: Seq[DVector] => List[Cluster], linkStrategy: Linkage_Strategy, v: DVector*) = {
      val cls = init(v.toSeq)
      val p: PriorityQueue[MatrixEl] = linkStrategy.getPQueue(m)

      @tailrec
      def createClusterTree(setL: Int, tree: List[Cluster]): Cluster = {
        tree.size match {
          case 1 => tree.head
          case _ => {

            val c = linkStrategy.getDistance(Right(p))
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
      }

      createClusterTree(cls.size, cls)
    }

  }

  /** Methods to perform a flat clustering for a set of document vectors */
  object FlatClustering {

    case class Comparison(vector: DVector, vectorComparedTo: DVector, distance: Double)

    case class Cluster(center: DVector, elements: DVector*){
      val p = elements.map(_.id)
    }

    def K_Means(k: Int) (dist: DistanceORSimFun) (vectors: DVector*) : Vector[Cluster] = {
      var count = 0
      //      (f: Option[(Array[DVector], DistanceORSimFun) => List[DVector]]) => {
      //        val initialSeeds = f match {
      //          case Some(x) => x.curried.apply(vectors.toArray)(dist)
      //          case None => scala.util.Random.shuffle(vectors).take(k)
      //        }
      val initialSeeds = scala.util.Random.shuffle(vectors).take(k)

      def help(oldCentroids: Vector[DVector]): Vector[Cluster] = {
        count += 1
        val clustersDistances = oldCentroids.map {
          x => vectors.map(y => Comparison(y, x, dist(x, y))).zipWithIndex
        }.flatten.groupBy(_._2)
          .map(x => x._2.map(y => y._1).toList.minBy(z => z.distance)).toVector

        val newClusters = clustersDistances.groupBy(_.vectorComparedTo)
          .map(x => {
            val bestCandidateCenter = findBestCenter(x._2.map(_.distance).sum / x._2.size,x._2)
            Cluster(bestCandidateCenter.vector, x._2.map(_.vector): _*)
          }).toVector

        val newCentroids = newClusters.map(x => x.center)

        println(s"Iterations: $count")


        println("old:\n " + oldCentroids.map(_.id).mkString(",\n    "))
        println("new:\n " + newCentroids.map(_.id).mkString(",\n    "))

        println("Clusters:\n " + newClusters.map(_.elements).mkString(",\n     "))
        println(newCentroids.forall(oldCentroids.contains(_)))
        println("")



        newCentroids == oldCentroids match {
          case true => newClusters
          case false => help(newCentroids)
        }
      }
      help(initialSeeds.toVector)
    }

    def findBestCenter(value: Double, elements: Vector[Comparison]) = {
      var current = elements(0)
      for(el <- elements){
        if(Math.abs(value - el.distance) < (Math.abs(value - current.distance)))
          current = el
      }
      current
    }

    def printClusters(clusters: Cluster*) : Unit = {
      clusters.zipWithIndex.foreach {
        x => {
          println(s"Cluster ${x._2}: ")
          x._1.elements.foreach { y =>
            println(s"            ${y.id}")
          }
        }
      }
    }

    //    def `kmeans++`(v: Array[DVector]) (d: DistanceORSimFun) = {
    //      val randomSeed = scala.util.Random.shuffle(v).take(1)
    //      val sumOfDistances = v.par.map{ x => d(x,randomSeed) }.toArray.reduce(_ + _)
    ////      for{
    //
    //      }

    //    }
    //
    //    def `kmeans||`(v: Array[DVector])(oversamplingFactor : Int) (d: DistanceORSimFun) = {
    //      var c = List[DVector]
    //      val randomSeed = scala.util.Random.shuffle(v).head
    //      def getDistances(seed : DVector, v: List[DVector]) =  v.par.filter(_ != randomSeed)
    //        .map { x => Math.pow(d(x, randomSeed),2) }.toArray
    //      val iterations = Math.log(getDistances(randomSeed,v.toList).sum).round
    //
    //      for (0 <- iterations) {
    //        for(dp <- c){
    //          val distance = getDistances(dp,v.toList)
    //        }
    //      }
    //
    //
    //    }
  }

}
