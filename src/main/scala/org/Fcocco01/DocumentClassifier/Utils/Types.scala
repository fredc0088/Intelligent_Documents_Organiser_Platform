package org.Fcocco01.DocumentClassifier.Utils

import org.Fcocco01.DocumentClassifier.Core.Clustering.DVector
import org.Fcocco01.DocumentClassifier.Utils.Constants.{FIVE, THREE}

/**
  * Aliases to be used around the code for a better readibility
  * and comprehension.
  */
package object Types {
  type DocPath = String
  type TxtExtractor = String => String
  type Paths = Traversable[String]
  type Token = String
  type Term = String
  type Tokens = Traversable[Token]
  type Tokenizer = (String => Vector[Token])
  type Scheme = (String, Traversable[String]) => TypeClasses.TermWeighted
  type Text = String
  type Weight = Double
  object TypeClasses {
    case class TokenSuite(extract : TxtExtractor, tokenizer : Tokenizer) {
      def getTokensFromFile(arg: String) = tokenizer(extract(arg))
    }
    case class Document(path: DocPath, tokens: Tokens)
    case class TermWeighted(term : Term, weight: Double) {
      def toTuple = (term,weight)
    }

    /** Object wrapping Document Vectors Types */
    object Vectors {

      /** Common property and behaviour for any type of document vector */
      sealed trait DocumentVector {
        val id: String
        def size : Int
        def isEmpty : Boolean
        implicit def apply : Map[Token,Weight]
        override def toString() =
          (for ((k, v) <- this.apply) yield s" ${k} -> ${Util.Formatting.roundDecimals(v)} ").mkString
      }

      /**
        * Document Vector with every term being properly weighted in relation to the document
        * used to create this vector.
        *
        * @constructor create a new vector with an id and a collection of terms mapped to values
        * @param id Vector unique ID
        * @param v  The content of the vector, to every term a value [[Weight]] is assigned
        */
      final case class RealVector(override val id: String, private val v: Map[Token, Weight])
        extends DocumentVector {
        def isEmpty = if(v.isEmpty)true else false
        implicit def apply = v
        def size = v.size
      }

      /**
        * An empty document vector, does not contain any term and it could be
        * generated from an empty document (tokenised) or an error.
        */
      final case object EmptyVector extends DocumentVector {
        override val id: String = ""
        override def isEmpty = true
        override def size = Constants.ZERO
        implicit def apply = Map.empty[Token,Weight]
      }

    }

    object Clusters {
      import Vectors.{DocumentVector => DVector}
      import scala.collection.immutable.ListMap
      import scala.annotation.tailrec
      import Constants.{ZERO, HALF, ONE}
      object Hierarchical {
        /**
          *
          */
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

        /**
          *
          * @param v
          */
        final case class SingleCluster(private val v: DVector) extends Cluster {
          override def getChildren = None

          lazy val vectors: List[DVector] = List(v)
          lazy val name = {
            v.apply.maxBy(_._2)._1
            vectors.head.id
          }
          override val distance = None
        }

        /**
          *
          * @param childL
          * @param childR
          * @param distance
          */
        final case class MultiCluster(childL: Cluster, childR: Cluster)(val distance: Option[Double]) extends Cluster {

          override def getChildren = Some(childL, childR)

          /* Name the cluster based on the main topic. It is not working properly at this stage. Under research */
          lazy val name: String = {
            val vectors = this.vectors.map(x => ListMap(x.apply.toSeq.sortWith(_._2 > _._2): _*))
            val highestTerms = vectors.map(x => x.headOption)
            val default = (scala.util.Random.alphanumeric.take(FIVE).toString, ZERO.toDouble)

            @tailrec
            def constructTitle(string: String, n: Int, terms: List[Option[(String, Double)]], default: (String, Double)): String =
              if (terms.isEmpty) string
              else n match {
                case ZERO => s"${string}_"
                case _ => constructTitle(s"${string} ${terms.head.getOrElse(default)._1}_", n - ONE, terms.tail, default)
              }
            val height = getHeight.toInt
            val name = if (height > FIVE && highestTerms.length > FIVE)
              constructTitle("", height / FIVE, highestTerms, default)
            else constructTitle("", height, highestTerms, default)
            s"${name}${Math.abs(this.hashCode).toString}"
          }

          lazy val vectors: List[DVector] =
            getVectors(this, List.empty[DVector])

          private def getVectors(c: Cluster, a: List[DVector]): List[DVector] =
            c.getChildren match {
              case None => c.vectors
              case Some((left,right)) => left.vectors ::: right.vectors
            }
        }
      }

      object Flat {
        /**
          * Represent a comparison between two vectors, storing their distance.
          *
          * @param vector
          * @param vectorComparedTo
          * @param distance
          */
        case class Comparison(vector: DVector, vectorComparedTo: DVector, distance: Double)

        /**
          * Cluster class for flat clustering, containing all closest vectors.
          *
          * @param center
          * @param elements
          */
        case class Cluster(center: DVector, elements: DVector*){
          lazy val vectorsID = elements.map(_.id)
          /* Picks the most important terms for all vectors to construct a name for
          * the cluster. Hashcode is used to ensure uniqueness. */
          lazy val name = {
            val mains = elements.map(x => x.apply.toArray.sortBy(_._2).reverse.head).sortBy(_._2).reverse
            val names  = if(mains.size > THREE) mains.map(_._1).toArray.distinct.take(THREE)
            else mains.map(_._1).toArray.distinct.take(ONE)
            names.mkString("_") ++ s"_${this.hashCode}"
          }
        }
      }
    }

  }
}
