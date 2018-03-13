package org.Fcocco01.DocumentClassifier

import org.Fcocco01.DocumentClassifier.Classify.DocumentVector
import Util.Operators.|>

import scala.annotation.tailrec
import scala.collection.parallel.mutable.ParArray

object Clustering {

  type DVector = DocumentVector

  object Similarity {
    type Vector = DocumentVector


    def cosine(vector1: DVector, vector2: DVector) = {
      val a: Double = getAbsoluteValue(vector1) |> Math.sqrt
      val b: Double = getAbsoluteValue(vector2) |> Math.sqrt
      getDocProduct(vector1, vector2).map(_._2).reduce(_ + _) / (a * b)
    }

    def getDocProduct(v1: DVector, v2: DVector) =
      v1.vector.map { case (k, v) => k -> (v * v2.vector.getOrElse(k, 0.0)) }

    def getAbsoluteValue(vector: DVector): Double = {
      vector.vector.toVector.map(x => Math.pow(x._2, 2)).reduce(_ + _)
    }
  }

  object HierarchicalClustering {

    import scala.collection.mutable.PriorityQueue

    type MatrixEl = (Double, DVector, DVector)
    type SimMatrix = Array[Array[MatrixEl]]

    sealed trait Cluster {
      def getVectors : List[DVector]
      def getTitle : String
      val n : String
      /*
      Merge two existing clusters
       */
      def merge(c: Cluster) : MultiCluster = MultiCluster(this, c)
      def hasVector(v: DVector) : Boolean = getVectors.exists(_ == v)
      val sim : Double // only for testing - To Be Removed
    }

    final case class SingleCluster(private val v: DVector) extends Cluster {
      override def getVectors : List[DVector] = List(v)
      override def getTitle = {
        v.vector.maxBy(_._2)._1
      }
      override val n = getVectors.head.docId
      val sim = getVectors(0).vector.map(_._2).sum // only for testing - To Be Removed
    }

    final case class MultiCluster(childL: Cluster = null, childR: Cluster = null) extends Cluster{

      override def getTitle: String = ???

      def getPair = if(childL != null && childR != null)
        (childL,childR) else null

      val sim = childR.sim + childL.sim // only for testing - To Be Removed
      // NOTE FOR ME: Use Either[A,B] to have different returns

      override def getVectors : List[DVector] =
        getVectors(this,List.empty[DVector])

      private def getVectors(c: Cluster, a: List[DVector]) : List[DVector] =
        if(c.isInstanceOf[SingleCluster]) c.getVectors
        else c.asInstanceOf[MultiCluster].childL.getVectors ::: c.asInstanceOf[MultiCluster].childR.getVectors

      override val n: String = scala.util.Random.alphanumeric.take(5).mkString
    }

    def getRightCluster(tree: List[Cluster], elToFind: DVector) : Cluster =
      tree.find(_.hasVector(elToFind)).get

    def cutMatrix(m: SimMatrix): Array[MatrixEl] =
      m.flatten.filterNot(x => x._2 == x._3).take(m.flatten.length / 2 + 1)


    trait Linkage_Strategy {
      def getDistance(m: Either[SimMatrix, Traversable[MatrixEl]]) : MatrixEl
      def getPQueue(m: SimMatrix) : PriorityQueue[MatrixEl]
    }

    object Single_Link extends Linkage_Strategy {

      override def getPQueue(m: SimMatrix) : PriorityQueue[MatrixEl] =
      PriorityQueue[MatrixEl](cutMatrix(m): _*)(Ordering.by(_._1))

      override def getDistance(m: Either[SimMatrix, Traversable[MatrixEl]]) = {
        val matrix = m match {
          case Left(x) => x.flatten.toList;
          case Right(y) => y.toList
        }

        @tailrec
        def find(a: List[MatrixEl]): MatrixEl = {
          val mx = a.maxBy(_._1)
          if (mx._2.docId == mx._3.docId) {
            find(a diff List(mx))
          } else mx
        }

        find(matrix)
      }
    }

    object Complete_Link extends Linkage_Strategy {

      override def getPQueue(m: SimMatrix) : PriorityQueue[MatrixEl] =
        PriorityQueue[MatrixEl](cutMatrix(m): _*)(Ordering.by(_._1)).reverse

      override def getDistance(m: Either[SimMatrix, Traversable[MatrixEl]]) = {
        val matrix = m match {
          case Left(x) => x.flatten.toList;
          case Right(y) => y.toList
        }

        @tailrec
        def find(a: List[MatrixEl]): MatrixEl = {
          val mx = a.minBy(_._1)
          if (mx._2.docId == mx._3.docId) {
            find(a diff List(mx))
          } else mx
        }

        find(matrix)
      }
    }


    def createSimMatrix(vectors: Traversable[DVector], f: (DVector, DVector) => Double): SimMatrix = {
      val v = vectors.toArray.par
      val size = v.size
      val matrix = Array.ofDim[MatrixEl](size, size)
      //      val active = Array.ofDim[Int](matrix.length,matrix.length)
      for (i <- 0 until v.size) {
//        val p = scala.collection.mutable.PriorityQueue.empty[MatrixEl](Ordering.by(_._1))
        for (j <- 0 until v.size) {
          //          active(i)(j) = 1
          matrix(i)(j) =
            (f(v(i), v(j)), v(i), v(j))
//          if(!(v(i).docId == v(j).docId))
//            p.enqueue((f(v(i), v(j)), v(i), v(j)))
        }

      }
      matrix
    }

    def HAC(m: SimMatrix, init: Seq[DVector] => List[Cluster], linkStrategy: Linkage_Strategy, v: DVector*) = {
      val cls = init(v.toSeq)
      val p : PriorityQueue[MatrixEl] = linkStrategy.getPQueue(m)
      @tailrec
      def createClusterTree(setL: Int, tree: List[Cluster]) : Cluster = {
        tree.size match {
          case 1 => tree.head
          case _ => {

            // NOTE FOR ME!!! MAKE THIS MORE FUNCTIONAL
            val c = linkStrategy.getDistance(Right(p))
            val cls1 = getRightCluster(tree, c._2)
            val cls2 = getRightCluster(tree, c._3)
            if (cls1 == cls2) {
              p.dequeue()
              createClusterTree(setL, tree)
            } else {
              val mergedCls = cls1.merge(cls2)
              val newTree = tree.filterNot(x => x == mergedCls.childL || x == mergedCls.childR)
              val newTree2 = newTree :+ mergedCls

              /*****Only for testing******/
              println(setL)
              println(c._1)
              println("1 - " + cls1.n + "  " + cls1.sim)
              println("2 - " + cls2.n + "  " + cls2.sim)
              println("!!!New is: " + mergedCls.n)
              /***************************/

              p.dequeue()
              createClusterTree(setL + 1, newTree2)
            }
          }
        }
      }
      createClusterTree(cls.size,cls)
    }


  }

  object FlatClustering {

  }

}
