package org.Fcocco01.DocumentClassifier


object Classify {

  import Util.I_O.GetDocContent
  import Types._
  import Token.Tokenizer.{TokenizedText, StopWords}
  import Analysis.{ModelFunctions,IDF}
  import ModelFunctions._


  /**
    * Dictionary of non-trivial words in the whole corpus.
    *
    * @param docsPathsList Paths to the documents to be analysis.
    * @param tokenizer
    */
  class Dictionary(docsPathsList: Paths, tokenizer: Tokenizer) {
    def apply(tokenizer: Tokenizer) : Tokens = {
      docsPathsList.flatMap(x => tokenizer(GetDocContent(x)))
        .toVector.distinct
    }
  }
  object Dictionary {
    def apply(d: Paths, tokenizer: Tokenizer) : Tokens = {
      val newInstance = new Dictionary(d,tokenizer)
      newInstance(tokenizer)
    }
  }

  /**
    * Convert a document to a spatial vector.
    */
  abstract class DocumentVector {
    def docId : String
    def size : Int
    def tokens : Tokens
    def vector : Map[Token,Double]
  }

  /**
    * Vectorize a single document using a custom way of tokenization.
    *
    * @param tokenizer
    * @param docPath Path to document to be analysed.
    * @param extractor Optional
    */
  class SingleVector(private val tokenizer: Tokenizer, docPath: String, extractor: Option[String => String],
                       modeller: Option[Scheme]) extends DocumentVector {
    def vector = modeller match {
      case Some(y) => tokens.toArray.distinct.map(x => y(x, tokens)).toMap
      case None => tokens.toArray.distinct.map(x => (x,0.0)).toMap
    }
    def tokens = {
      extractor match {
        case Some(x) => tokenizer(x(docPath))
        case None => tokenizer("")
      }
    }
    def docId = docPath
    def size = vector.size
  }

  object SingleVector {
    def apply(tokenizer: Tokenizer, docPath: String, extractor: Option[String => String],
              modeller: Option[Scheme] = None) = {
      new SingleVector(tokenizer, docPath, extractor, modeller)
    }
  }

  /**
    * A document vector needs to be normalised to the other vectors in the analysis using
    * a uniformed dictionary.
    *
    * @constructor new vector using a dictionary and a not-normalised vector.
    * @param dictionary Defined all non-trivial terms in the current analysis.
    * @param unNormalisedVector Vector that needs to be normalised.
    * @param modeller Function which models the vector after a chosen model data
    */
  class NormalisedVector(dictionary: Tokens,
                         unNormalisedVector: SingleVector,
                         modeller: Scheme) extends DocumentVector {
    def tokens = unNormalisedVector.tokens
    val vector = {
      var m = Array[(Token,Double)]()
      for(d <- dictionary) m = m :+ modeller(d,tokens)
      m.toMap
    }
    def docId = unNormalisedVector.docId
    def size = dictionary.size
    override def toString() =
      (for ((k, v) <- vector) yield s" ${k} -> ${Util.Formatting.roundDecimals(v)}").mkString
  }
  object NormalisedVector{
    def apply(dictionary: Tokens,
              unNormalisedVector: SingleVector,
              modeller: Scheme): NormalisedVector =
      new NormalisedVector(dictionary, unNormalisedVector, modeller)
  }

//  class Matrix_Normalised(v: Vector[NormalisedVector]) {
//    val x = v.head.vector.map(_._1)
//    val y = v.map(_.docId)
//    val matrix = for (i <- v) {
//      val x = for {
//        y <- i.vector
//      } yield y._2
//    }
//  }
}