package org.Fcocco01.DocumentClassifier

object Classify {

  import Util.I_O.GetDocContent
  import Types._
  import Token.Tokenizer.{TokenizedText, StopWords}
  import Modeller.Models.BagOfWordsDictionary

  def ExtractDocContent(docPath: String): String = {
    GetDocContent(docPath)
  }

  /**
    * Vectorize a document using a custom way of tokenization and a custom dictionary
    * To follow the Bag-Of-World model
    * @param dictionary
    * @param tokenizer
    * @param docPath
    */
  abstract class DocumentVector(dictionary: BagOfWords, private val tokenizer: Tokenizer, docPath: String) {
    def getVector() : Map[Token,TFIDFValue]
    val tokens: Map[Token,TFIDFValue]
    val docId : String
    val size : BigInt
  }

  /**
    * A Document is transformed into a vector with information regarding the
    * frequency of dictionary's terms in the document, for purpose of analysis.
    * This specifically let freely use any n-gram model for the tokens, but implements
    * a Term-Frequency technique to weight in each token.
    *
    *  @constructor new vector using a dictionary, a tokenizer and the path to the document
    *  @param dictionary Dictionary of all words necessary to the creation of the vector
    *  @param tokenizer a process to tokenize a text
    *  @param docPath absolute or relative path to the documents or any file containing text
    */
  class DocumentVectorTfWeighted(dictionary: BagOfWords, private val tokenizer: Tokenizer, docPath: String,
                                 idf: Traversable[Analysis.Idf.IDFValue],extractor: String => String)
  extends DocumentVector (dictionary,tokenizer,docPath) with Analysis.Tf  {
    private val tokensInDocuments = tokenizer(extractor(docPath))
    override val tokens = (
      for {
        d <- dictionary
      } yield (d -> Analysis.Idf._IDF(d, tokensInDocuments, tf, idf))).toMap

    override val docId= docPath
    override val size = tokensInDocuments.size

    override def toString() = {
      val s = for((k,v) <- tokens) yield s"${k} -> ${Util.Formatting.roundDecimals(v)}  \n"
      s.mkString
    }

    override def getVector() = {
      this.tokens
    }
  }
  object DocumentVectorTfWeighted {
    def apply(dictionary: BagOfWords, tokenizer: Tokenizer, docPath: String,
              idf: Traversable[Analysis.Idf.IDFValue], extractor: String => String) =
      new DocumentVectorTfWeighted(dictionary, tokenizer, docPath, idf,extractor)
  }

    def main(args: Array[String]): Unit = {
      val testPath1 =
        ".\\src\\test\\resources\\1\\1.2\\Java - Generics by Oracle.docx"
      val testPath2 =
        ".\\src\\test\\resources\\1\\1.1\\demo.docx"
      val testPath3 =
        ".\\src\\test\\resources\\3\\3.2\\test.docx"
      val testPath4 =
        ".\\src\\test\\resources\\1\\1.2\\clustering.pdf"
      val tests : Paths= Array(testPath1,testPath2,testPath3
        //,testPath4
      )
      val stopWords = StopWords(".\\src\\main\\resources\\stop-word-list.txt")
      val dictionary : BagOfWords =
        BagOfWordsDictionary(tests,TokenizedText("\\s+", stopWords))
      val idfWeightedTerms = for {
        s <- dictionary
      } yield Analysis.Idf.IDFValue(s, tests, GetDocContent)
      var vectors : Vector[DocumentVectorTfWeighted] = Vector()
      for (i <- tests)
        vectors = vectors :+ DocumentVectorTfWeighted(dictionary,TokenizedText("\\s+", stopWords),i,idfWeightedTerms, GetDocContent)
      println(vectors(0).getVector.mkString)

    }
}