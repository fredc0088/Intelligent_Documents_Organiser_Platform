package org.Fcocco01.DocumentClassifier.Utils

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
    case class TokenSuite(extract : TxtExtractor, tokenizer : Tokenizer)
    case class Document(path: DocPath, tokens: Tokens)
    case class TermWeighted(term : Term, weight: Double)
    /** Object wrapping Document Vectors Types */
    object Vectors {
      /** Commom property and behaviour for any type of document vector */
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
      final case class DVector(override val id: String, private val v: Map[Token, Weight])
        extends DocumentVector {
        def isEmpty = false
        def get = v
        implicit def apply = v
        def size = v.size
      }

      /**
        * An empty document vector, does not contain any term and it could be
        * generated from an empty document (tokenised) or an error.
        */
      final case object EmptyV extends DocumentVector {
        override val id: String = ""
        override def isEmpty = true
        override def size = Util.Constants.ZERO
        implicit def apply = Map.empty[Token,Weight]
      }
    }

  }
}
