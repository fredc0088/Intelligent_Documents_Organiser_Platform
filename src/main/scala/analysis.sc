package org.Fcocco01.DocumentClassifier

object analysis {

  import Util.Types._
  import Util.I_O.{GetDocContent}
  import Util.Operators.|>

  def ExtractDocContent(docPath: String): String = {
    GetDocContent(docPath)
  }

  /**
    * This trait allow to incorporate a per-document analysis of each term
    * regarding their weight in the document.
    *
    * @tparam T type of element to represent the tokens
    *
    */
  trait TF[T] {
    def tf(term: T, document: Traversable[T])= {
      val frequency = () => {
        var f = 0
        for (w <- document if term == w) {
          f += 1
        }
        f
      }
      frequency() / document.size
    }
  }

  trait IDF {
    def idf(term: Token, documents: List[DocumentVector]) = {
      val docs = documents.map(_.getVector)
      for (doc <- docs) {
        doc
      }
    }
  }

  class TokenizedText(regex: String, stopWordsFilePath: String) extends Tokenizer {
    val stopwords = GetDocContent(stopWordsFilePath)
    def apply(text: String) = text.toLowerCase.split(regex).filter(!stopwords.contains(_)).toVector
  }
  object TokenizedText {
    def apply(regex: String, stopWordsFilePath: String) (text: String) = {
      val newInstance = new TokenizedText(regex, stopWordsFilePath)
      newInstance(text)
    }
  }

  class BagOfWordsDictionary(docsPathsList: Paths, tokenizer: Tokenizer) extends BagOfWordsModeller[Paths](tokenizer){
    override def apply(tokenizer: Tokenizer) : Vector[Token] = {
      docsPathsList.flatMap(x => tokenizer(ExtractDocContent(x))).toVector.distinct
    }
  }
  object BagOfWordsDictionary {
    def apply(d: Paths, tokenizer: Tokenizer) : Vector[Token]= {
      val newInstance = new BagOfWordsDictionary(d,tokenizer)
      newInstance(tokenizer)
    }
  }

  /**
    * Vectorize a document using a custom way of tokenization and a custom dictionary
    * To follow the Bag-Of-World model
    * @param dictionary
    * @param tokenizer
    * @param docPath
    */
  abstract class DocumentVector(dictionary: BagOfWords, private val tokenizer: Tokenizer, docPath: String) {
    def getVector() : Vector[Term]
  }

  /** A Document is transformed into a vector with information regarding the
    * frequency of dictionary's term in the document, for purpose of analysis.
    * This specifically let freely use any n-gram model for the tokens, but implements
    * a Term-Frequency technique to weight in each token.
    *
    *  @constructor new vector using a dictionary, a tokenizer and the path to the document
    *  @param dictionary Dictionary of all words necessary to the creation of the vector
    *  @param tokenizer a process to tokezize a text
    *  @param docPath absolute or relative path to the documents or any file containing text
    */
  class DocumentVectorTfWeighted(dictionary: BagOfWords, private val tokenizer: Tokenizer, docPath: String)
  extends DocumentVector (dictionary,tokenizer,docPath) with TF[Tokens]{
    val completeText = ExtractDocContent(docPath)
    private val tokensInDocuments = tokenizer(completeText)
    private val tokens =
      for {
        d <- dictionary
      } yield d -> tf(d, tokensInDocuments)
    val docId= docPath
    val size = tokensInDocuments.size

    override def toString() = {
      val s = for((k,v) <- tokens) yield s"${k}\n${v}"
      s.mkString
    }

    override def getVector() = {
      tokens
    }
  }
  object DocumentVectorTfWeighted {
    def apply(dictionary: BagOfWords, tokenizer: Tokenizer, docPath: String): DocumentVectorTfWeighted =
      new DocumentVectorTfWeighted(dictionary, tokenizer, docPath)
  }



  def main(args: Array[String]): Unit = {
    val testPath = "C:/Users/USER/odrive/Google Drive (2)/Various/Daily Exercises.docx"
    val r : Paths= Array(testPath)
    val t = TokenizedText(raw"""b[a-zA-Z]w+""", "./../resources/stop-word-list.txt")
    val dictionary : BagOfWords = BagOfWordsDictionary(r,t)
    val doc = DocumentVectorTfWeighted(dictionary,t,testPath)
    println(doc.toString())
  }


  def idf(docs: List[String]) {

    Math.log(1.2)
  }


}