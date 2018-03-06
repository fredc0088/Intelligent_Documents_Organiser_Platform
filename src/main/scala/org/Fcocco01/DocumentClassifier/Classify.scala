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

  class Matrix_Normalised(v: Vector[NormalisedVector]) {
    val x = v.head.vector.map(_._1)
    val y = v.map(_.docId)
    val matrix = for (i <- v) {
      val x = for {
        y <- i.vector
      } yield y._2
    }

  }


//
//  def main(args: Array[String]): Unit = {
//    val testPath1 =
//      "./src/test/resources/1/1.2/Java - Generics by Oracle.docx"
//    val testPath2 =
//      "./src/test/resources/1/1.1/demo.docx"
//    val testPath3 =
//      "./src/test/resources/3/3.2/test.docx"
//    val testPath4 =
//      "./src/test/resources/1/1.2/clustering.pdf"
//    val tests: Paths = Array(testPath1, testPath2, testPath3
//      //,testPath4
//    )
//    val stopWords = StopWords("./src/main/resources/stop-word-list.txt")
//    val dictionary: Tokens =
//      Dictionary(tests, TokenizedText("[^a-z0-9]", stopWords))
//    val idfWeightedTerms = for {
//      s <- dictionary
//    } yield IDF.IDFValue(s, tests, GetDocContent)
//    tests.size match {
//      case 0 => ???
//      case 1 => SingleVector(TokenizedText("[^a-z0-9]", stopWords), tests.head, Option(GetDocContent), Option(tf))
//      case _ => {
//        import scala.collection.parallel.mutable.ParArray
//        //        var vectors: Vector[TfIdfVector] = Vector[TfIdfVector]()
//        var vectors: Vector[NormalisedVector] = Vector[NormalisedVector]()
//        for (i <- tests) {
////          val original = DocumentVector(TokenizedText("\\s+", stopWords), i, Option(GetDocContent))
////          vectors = vectors :+ TfIdfVector(dictionary,original,idfWeightedTerms)
//val tfidfFun = tfidf(idfWeightedTerms) // Assign idf results to tfidf function to be used as modeller
//          val original = SingleVector(TokenizedText("[^a-z0-9]", stopWords), i, Option(GetDocContent))
//          vectors = vectors :+ NormalisedVector(dictionary, original, tfidfFun)
//        }
//        vectors.foreach { e => Thread.sleep(100); print(e) }
//        println(vectors(1).toString())
//      }
//    }
//  }

  def g = {
  object TestingResources {

    object Regexes {
      val words1gram = "[^a-z0-9]"
    }

    val demoDocxMock =
      ""
    val testPath1 =
      "./src/test/resources/1/1.2/Java - Generics by Oracle.docx"
    val testPath2 =
      "./src/test/resources/1/1.1/demo.docx"
    val testPath3 =
      "./src/test/resources/3/3.2/test.docx"
    val testPath4 =
      "./src/test/resources/1/1.2/clustering.pdf"
    val testPath5 =
      "./src/test/resources/3/3.1/SampleDOCFile_100kb.doc"
    val testPath6 =
      "./src/test/resources/3/3.1/3.1.1/TestWordDoc.doc"

    val stopWords = StopWords("./src/main/resources/stop-word-list.txt")
  }





    import TestingResources._
    import Regexes.words1gram

  val tests = Array(testPath1, testPath2, testPath3
    //,testPath4
    ,testPath5,testPath6
  )

    val timeInstantiation = System.nanoTime()
    val dictionary =
      Dictionary(tests, TokenizedText(words1gram, stopWords))
    val idfWeightedTerms = for {
      s <- dictionary
    } yield IDF.IDFValue(s, tests, GetDocContent)
    println("Ready at " + (System.currentTimeMillis() / 6000) )
    val testsPar = tests.par
    val vectors = testsPar.map{ a =>
      val tfidfFun = tfidf(idfWeightedTerms) // Assign idf results to tfidf function to be used as modeller
      NormalisedVector(dictionary, SingleVector(TokenizedText(words1gram, stopWords), a, Option(GetDocContent)), tfidfFun)
    }(collection.breakOut)

    testsPar.foreach{e => Thread.sleep(100000); println(e)}
  vectors
  }
}