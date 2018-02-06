package org.Fcocco01.DocumentClassifier

object Classify {

  import Util.I_O.GetDocContent
  import Types._
  import Token.Tokenizer.TokenizedText
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
                                 idf: Traversable[Analysis.Idf.IDFValue])
  extends DocumentVector (dictionary,tokenizer,docPath) with Analysis.Tf  {
    private val tokensInDocuments = tokenizer(docPath)
    override val tokens = (
      for {
        d <- dictionary
      } yield (d -> Analysis.Idf._IDF(d, tokensInDocuments, tf, idf))).toMap

    override val docId= docPath
    override val size = tokensInDocuments.size

    override def toString() = {
      val s = for((k,v) <- tokens) yield s"${k} -> ${v}\n"
      s.mkString
    }

    override def getVector() = {
      this.tokens
    }
  }
  object DocumentVectorTfWeighted {
    def apply(dictionary: BagOfWords, tokenizer: Tokenizer, docPath: String,
              idf: Traversable[Analysis.Idf.IDFValue]) =
      new DocumentVectorTfWeighted(dictionary, tokenizer, docPath, idf)
  }

    def main(args: Array[String]): Unit = {
      val testPath = "C:\\Users\\USER\\Documents\\Projects\\Git_Repos\\Intelligent_Documents_Classificator_Platform\\src\\test\\resources\\1\\1.2\\JavaLab7.doc"
      val r : Paths= Array(testPath)
      val dictionary : BagOfWords = BagOfWordsDictionary(r,TokenizedText(raw"""b[a-zA-Z]w+""", "C:\\Users\\USER\\Documents\\Projects\\Git_Repos\\Intelligent_Documents_Classificator_Platform\\src\\main\\resources\\stop-word-list.txt"))
      val idfWeightedTerms = for {
        s <- dictionary
      } yield Analysis.Idf.IDFValue(s, r, GetDocContent)
      var y : Vector[DocumentVectorTfWeighted] = Vector()
      for (i <- r)
        y = y :+ DocumentVectorTfWeighted(dictionary,TokenizedText(raw"""b[a-zA-Z]w+""", "C:\\Users\\USER\\Documents\\Projects\\Git_Repos\\Intelligent_Documents_Classificator_Platform\\src\\main\\resources\\stop-word-list.txt"),i,idfWeightedTerms)
      println(y(0).getVector.mkString)


    }




}