package org.Fcocco01.DocumentClassifier.OOP

import org.Fcocco01.DocumentClassifier.{Token, Types}

object Classify {

  import Types._
  import Token.Tokenizer.TokenizedText
  import Util.I_O.GetDocContent


  /**
    * Dictionary of non-trivial words in the whole corpus.
    *
    * @param docsPathsList Paths to the documents to be analysis.
    * @param tokenizer
    */
  class Dictionary(docsPathsList: Paths, tokenizer: => Tokenizer, d: Option[Traversable[DocumentVector]]) {
    def apply(tokenizer: Tokenizer) : Tokens = {
      d match {
        case Some(x) => x.toVector.flatMap(_.tokens).distinct
        case None => docsPathsList.flatMap(x => tokenizer(GetDocContent(x))).toVector.distinct
      }

    }
  }
  object Dictionary {
    def apply(paths: Paths, tokenizer: Tokenizer)(implicit d: Option[Traversable[DocumentVector]]): Tokens = {
      val newInstance = new Dictionary(paths, tokenizer, d)
      newInstance(tokenizer)
    }
  }

  /**
    * Unified type for accepting EITHER a String of text representing a content
    * or a function that takes a String, like a path to a document ad example,
    * and produces text
    */
  type ExtractorOrText = Either[Tokens,TxtExtractor]

  /**
    * Usable to produce either a vector no-normalised or an empty vector,
    * depending on whether the used document is empty/unreadble.
    */
  object VectorFactory {
    def apply(tokenizer: Tokenizer, docPath: DocPath,
                extractor: TxtExtractor, modeller: Option[Scheme] = None) = {
      val tks = tokenizer(extractor(docPath))
      if(tks.size == 0) EmptyVector
      else SingleVector(tokenizer, docPath, tks, modeller)
    }
  }

  /**
    * Convert a document to a spatial vector.
    */
  abstract class DocumentVector {
    def isEmpty: Boolean = false
    def docId : String
    def size : Int

    val tokens: Tokens
    def vector : Map[Token,Double]
  }

  /**
    * An empty vector to be handled.
    */
  object EmptyVector extends DocumentVector {
    override def isEmpty: Boolean = true

    def docId: String = ""

    def size: Int = 0

    val tokens: Tokens = List.empty[Token]
    val vector: Map[Token, Double] = Map.empty[Token, Double]
  }

  /**
    * Transform in vector a single document.
    *
    * @param tokenizer
    * @param docPath Path to document to be analysed.
    * @param extractorOrText
    */
  class SingleVector private(private val tokenizer: Tokenizer, docPath: DocPath,
                             extractorOrText: ExtractorOrText, modeller: Option[Scheme] = None)
    extends DocumentVector {

    def vector = modeller match {
      case Some(x) => tokens.toArray.distinct.map(y => x(y, tokens)).toMap
      case None => tokens.toArray.distinct.map(x => (x,0.0)).toMap
    }

    lazy val tokens = extractorOrText match {
      case Left(x) => x
      case Right(y) => tokenizer(y(docPath))
    }

    def docId = docPath
    def size = vector.size
  }
  object SingleVector {
    def apply(tokenizer: Tokenizer, docPath: DocPath,
              extractor: TxtExtractor, modeller: Option[Scheme] = None) =
      new SingleVector(tokenizer, docPath, Right(extractor), modeller)
    def apply(tokenizer: Tokenizer, docPath: String,
              tokensFromText: Tokens, modeller: Option[Scheme]) =
      new SingleVector(tokenizer, docPath, Left(tokensFromText), modeller)
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
  class NormalisedVector private(dictionary: Tokens,
                                 unNormalisedVector: SingleVector,
                                 modeller: Scheme) extends DocumentVector {
    val tokens = unNormalisedVector.tokens
    val vector = {
      var m = Array[(Token,Double)]()
      for(d <- dictionary) m = m :+ modeller(d,tokens)
      m.toMap
    }
    def docId = unNormalisedVector.docId
    def size = dictionary.size
    override def toString() =
      (for ((k, v) <- vector) yield s" ${k} -> ${Util.Formatting.roundDecimals(v)} ").mkString
  }
  object NormalisedVector{
    def apply(dictionary: Tokens,
              unNormalisedVector: SingleVector,
              modeller: Scheme): NormalisedVector =
      new NormalisedVector(dictionary, unNormalisedVector, modeller)
  }

  def getDefaultVectors(a: Paths, defaultRegex: String, defaultStopWords: String) =
    a.par.map(x => SingleVector(TokenizedText(defaultRegex, defaultStopWords) _, x, GetDocContent _)).toVector

}