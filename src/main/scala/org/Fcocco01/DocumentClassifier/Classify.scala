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
    * Vectorize a document using a custom way of tokenization.
    *
    * @param tokenizer
    * @param docPath Path to document to be analysed.
    * @param extractor Optional
    */
  class DocumentVector(private val tokenizer: Tokenizer, docPath: String, extractor: Option[String => String]) {
    def vector() = {
      extractor match {
        case Some(x) => tokenizer(x(docPath))
        case None => tokenizer("")
      }
    }
    def getId = docPath
    def size = vector.size
  }

  object DocumentVector {
    def apply(tokenizer: Tokenizer, docPath: String, extractor: Option[String => String]) = {
      new DocumentVector(tokenizer, docPath, extractor)
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
                         unNormalisedVector: DocumentVector,
                         modeller: (String, Traversable[String]) => (Token,Double)) {
    val tokens = unNormalisedVector.vector
    val vector = {
      var m = Array[(Token,Double)]()
      for(d <- dictionary) m = m :+ modeller(d,tokens)
      m.toMap
    }
    val docId = unNormalisedVector.getId
    val size = dictionary.size
    override def toString() =
      (for ((k, v) <- vector) yield s" ${k} -> ${Util.Formatting.roundDecimals(v)}").mkString
  }
  object NormalisedVector{
    def apply(dictionary: Tokens,
              unNormalisedVector: DocumentVector,
              modeller: (String, Traversable[String]) => (Token, Double)): NormalisedVector =
      new NormalisedVector(dictionary, unNormalisedVector, modeller)
  }

  class Matrix(v: Vector[NormalisedVector]) {
    val x = v.head.vector.map(_._1)
    val y = v.map(_.docId)
    val matrix = for (i <- v) {
      val x = for {
        y <- i.vector
      } yield y._2
    }

  }

  /************************OOP Style**************************************/
  /**
  /**
    * A document vector needs to be normalised to the other vectors in the analysis using
    * a uniformed dictionary.
    *
    * @constructor new vector using a dictionary and a not-normalised vector.
    * @param dictionary Defined all non-trivial terms in the current analysis.
    * @param unNormalisedVector Vector that needs to be normalised.
    */
  abstract class NormalisedVector(dictionary: Tokens,
                                  unNormalisedVector: DocumentVector) {
    val tokens = unNormalisedVector.vector
    val vector: Map[Token, Double]
    val docId = unNormalisedVector.getId
    val size = dictionary.size
    def getVector() = vector
    override def toString() = {
      val s = for ((k, v) <- vector) yield s" ${k} -> ${Util.Formatting.roundDecimals(v)}"
      s.mkString
    }
  }


  /**
    * A Document is transformed into a vector with information regarding the
    * frequency of dictionary's terms in the document, for purpose of analysis.
    * This specifically let freely use any n-gram model for the tokens, but implements
    * a Term-Frequency technique to weight in each token.
    *
    * @constructor new vector using a dictionary, a tokenizer and the path to the document
    * @param dictionary Defined all non-trivial terms in the current analysis.
    * @param unNormalisedVector Vector that needs to be normalised.
    * @param termsIdfed Collection of terms attached to their IDF weight.
    */
  case class TfIdfVector(dictionary: Tokens,
                         unNormalisedVector: DocumentVector,
                         termsIdfed: Traversable[IDF.IDFValue])
    extends NormalisedVector(dictionary,unNormalisedVector) with Tf with Idf {
    val vector = {
      var m = Array[(Token,Double)]()
      for(d <- dictionary) m = m :+ (d, tf(tokens,d) * getIdf(d,termsIdfed))
      m.toMap
    }
  }

  /**
    * A vector being normalised following a Bag-Of-Words model
    *
    * @constructor new vector using a dictionary, a tokenizer and the path to the document
    * @param dictionary Defined all non-trivial terms in the current analysis.
    * @param unNormalisedVector Vector that needs to be normalised.
    */
  case class BagOfWordsVector(dictionary: Tokens,
                         unNormalisedVector: DocumentVector)
    extends NormalisedVector(dictionary,unNormalisedVector) with DocumentFrequency.BagOfWords {
    val vector = {
      var m = Array[(Token,Double)]()
      for(d <- dictionary) m = m :+ makeBagTerm(d,tokens)
      m.toMap
    }
  }
  */



//  def main(args: Array[String]): Unit = {
//    val testPath1 =
//      ".\\src\\test\\resources\\1\\1.2\\Java - Generics by Oracle.docx"
//    val testPath2 =
//      ".\\src\\test\\resources\\1\\1.1\\demo.docx"
//    val testPath3 =
//      ".\\src\\test\\resources\\3\\3.2\\test.docx"
//    val testPath4 =
//      ".\\src\\test\\resources\\1\\1.2\\clustering.pdf"
//    val tests: Paths = Array(testPath1, testPath2, testPath3
//      //,testPath4
//    )
//    val stopWords = StopWords(".\\src\\main\\resources\\stop-word-list.txt")
//    val dictionary: Tokens =
//      Dictionary(tests, TokenizedText("\\s+", stopWords))
//    val idfWeightedTerms = for {
//      s <- dictionary
//    } yield IDF.IDFValue(s, tests, GetDocContent)
//    tests.size match {
//      case 0 => ???
//      case 1 => DocumentVector(TokenizedText("\\s+", stopWords), tests.head, Option(GetDocContent))
//      case _ => {
//        import scala.collection.parallel.mutable.ParArray
////        var vectors: Vector[TfIdfVector] = Vector[TfIdfVector]()
//        var vectors: ParArray[NormalisedVector] = new ParArray[NormalisedVector](tests.size)
//        for (i <- tests) {
////          val original = DocumentVector(TokenizedText("\\s+", stopWords), i, Option(GetDocContent))
////          vectors = vectors :+ TfIdfVector(dictionary,original,idfWeightedTerms)
//          val tfidfFun = tfidf(idfWeightedTerms) // Assign idf results to tfidf function to be used as modeller
//          val original = DocumentVector(TokenizedText("\\s+", stopWords), i, Option(GetDocContent))
//          vectors = vectors :+ NormalisedVector(dictionary,original,tfidfFun)
//        }
//        vectors.foreach{e => Thread.sleep(100); print(e)}
//        println(vectors(1).toString())
//      }
//    }
//  }
}