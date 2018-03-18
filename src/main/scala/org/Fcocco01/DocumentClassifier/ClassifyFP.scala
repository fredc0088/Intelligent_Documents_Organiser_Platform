package org.Fcocco01.DocumentClassifier

object ClassifyFP {

  import Types._

  class Dictionary private(tokens: Tokens) {
    def apply() : Tokens = tokens.toVector.distinct
  }
  object Dictionary {
    def apply(tokenizedText : Traversable[Option[Document]]): Tokens = {
      val tokens = tokenizedText
        .map(_.getOrElse(("",Vector.empty[Token])))
        .filterNot(_._2.size == 0).map(_._2)
        .reduce(_ ++ _)
      val instance = new Dictionary(tokens)
      instance()
    }
    def apply(paths: Paths, tokenizer: TokenSuite) : Tokens = {
      val tokens = paths.flatMap(x => tokenizer._2(tokenizer._1(x)))
      val instance = new Dictionary(tokens)
      instance()
    }
    def apply(vectors: DocumentVector*) : Tokens = {
      val tokens = vectors.par.filterNot(_.isEmpty).flatMap(x => x.apply.map(y => y._1)).toArray
      val instance = new Dictionary(tokens)
      instance()
    }
  }


  def buildTokenSuite(tokenizer: Tokenizer)(extractor: TxtExtractor) : TokenSuite = (extractor,tokenizer)

  def tokenizeDocument(t: TokenSuite) : DocPath => Option[Document] =
    (docPath: DocPath) => {
      val txt : Tokens = t._2(t._1(docPath))
      if(txt.size == 0) None
      else Some((docPath,txt))
    }

  def createVector(modeller: Scheme)(dictionary: Option[Tokens] = None) : Option[Document] => DocumentVector = {
    (tokenizedText: Option[Document]) => {
      tokenizedText match {
        case Some(t) => {
          var m = Array[(Token,Double)]()
          dictionary match {
            case Some(x) => {
              for(d <- x) m = m :+ modeller(d,t._2)
              DVector(t._1,m.toMap)
            }
            case None => {
              var m = Array[(Token,Double)]()
              for(y <- t._2) m = m :+ modeller(y,t._2)
            }
          }
          DVector(t._1,m.toMap)
        }
        case None => EmptyV
      }
    }
  }

  def createVector(tokenizer: Tokenizer)(extractor: TxtExtractor)(docPath: DocPath)
    : (Scheme,Option[Tokens]) => DocumentVector =
    (modeller: Scheme, dictionary: Option[Tokens]) =>
      (dictionary match {
        case Some(_) => createVector(modeller)(dictionary)
        case None => createVector(modeller)()
      })(tokenizeDocument(buildTokenSuite(tokenizer)(extractor))(docPath))


  abstract class DocumentVector(val id: String) {
    def size : Int
    def isEmpty : Boolean
    implicit def apply : Map[Token,Weight]
  }
  case class DVector(override val id: String, private val v: Map[Token,Weight])
    extends DocumentVector(id) {
    def isEmpty = false
    def get = v
    implicit def apply = v
    def size = v.size
  }
  case object EmptyV extends DocumentVector("") {
    override def isEmpty = true
    override def size = 0
    implicit def apply = Map.empty[Token,Weight]
  }

}