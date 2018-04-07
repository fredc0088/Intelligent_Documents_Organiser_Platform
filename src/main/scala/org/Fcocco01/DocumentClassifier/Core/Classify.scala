package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Utils.{Types,Constants}

object Classify {

  import Types.TypeClasses.Vectors._
  import Types.TypeClasses._
  import Types._
  import Constants.ZERO

  /**
    * Create a dictionary of unique terms.
    * This class is a set of tokens coming from every document
    * in the used collection.
    *
    * @param tokens
    */
  class Dictionary private(tokens: Tokens) {
    def apply() : Tokens = tokens.toVector.distinct
  }
  /** Factory for [[Dictionary]] instances. */
  object Dictionary {
    /**
      * Creates a Dictionary from a group of 1 or more already
      * processed documents.
      *
      * @param tokenizedText Set of tokenised documents
      * @return A collection of tokens that represent the dictionary
      */
    def apply(tokenizedText : Traversable[Option[Document]]): Tokens = {
      val tokens = tokenizedText
        .map(_.getOrElse(Document("",Vector.empty[Token])))
        .filterNot(_.tokens.size == ZERO).map(_.tokens)
        .reduce(_ ++ _)
      val instance = new Dictionary(tokens)
      instance()
    }

    /**
      * Creates a Dictionary using the paths to the actual document files
      * and a suite for tokenisation, taking care of that additional process
      * of processing the documents.
      *
      * @param paths Absolute paths to the documents.
      * @param tokenizer A tool constituted of an function that extract the text
      *                  using the document's path and a tokenisation method.
      * @return A collection of tokens that represent the dictionary
      */
    def apply(paths: Paths, tokenizer: TokenSuite) : Tokens = {
      val tokens = paths.flatMap(x => tokenizer.tokenizer(tokenizer.extract(x)))
      val instance = new Dictionary(tokens)
      instance()
    }

    /**
      * Creates a Dictionary from 1 or more Document Vectors.
      *
      * @param vectors A set of Vectors created from the respective documents
      * @return A collection of tokens that represent the dictionary
      */
    def apply(vectors: DocumentVector*) : Tokens = {
      val tokens = vectors.par.filterNot(_.isEmpty).flatMap(x => x.apply.map(y => y._1)).toArray
      val instance = new Dictionary(tokens)
      instance()
    }
  }

  /**
    * This method combine a tokenisation method with an extractor function to create a single tool
    * to be used elsewhere in order to tokenise a document.
    *
    * @param tokenizer Tokenisation method
    * @param extractor Text extracting function
    * @return A tuple composed as (1: Extracting function, 2: Tokenising method)
    */
  def buildTokenSuite(tokenizer: Tokenizer)(extractor: TxtExtractor) : TokenSuite = TokenSuite(extractor,tokenizer)

  /**
    * Using a Tokensuite tool, it can tokenise a document from its path in the file system.
    *
    * @param t Tokenisation suite as (1: Extracting function, 2: Tokenising method)
    * @return Tokenised document if the said doc would not result empty after the process
    */
  def tokenizeDocument(t: TokenSuite) : DocPath => Option[Document] =
    (docPath: DocPath) => {
      val txt : Tokens = t.tokenizer(t.extract(docPath))
      if(txt.size == ZERO) None
      else Some(Document(docPath,txt))
    }

  /**
    * This function can produce a Document Vector using a modelling function (to weight the terms in
    * the vector) and a Tokenised document as final input. If a None option is passed as document,
    * probably from a different chained function, it will produce a empty vector.
    * Optionally, a dictionary can be used as parameter as well, in order to normalise the vector produced.
    *
    * @param modeller Modelling Functions of [[Types.Scheme]]. Many are already contained in [[Analysis.ModelFunctions]]
    * @param dictionary A set of unique terms composing a dictionary (see [[Dictionary]])
    * @return [[DocumentVector]] instance, either [[DVector]] or [[EmptyV]]
    */
  def createVector(modeller: Scheme)(dictionary: Option[Tokens] = None) : Option[Document] => DocumentVector = {
    (tokenizedText: Option[Document]) => {
      tokenizedText match {
        case Some(t) => {
          var m = Array.empty[TermWeighted]
          dictionary match {
            case Some(x) => {
              for(d <- x) m = m :+ modeller(d,t.tokens)
              DVector(t.path,m.map(z => (z.term,z.weight)).toMap)
            }
            case None => {
              var m = Array.empty[TermWeighted]
              for(y <- t.tokens) m = m :+ modeller(y,t.tokens)
            }
          }
          DVector(t.path,m.map(z => (z.term,z.weight)).toMap)
        }
        case None => EmptyV
      }
    }
  }

  /**
    * This function can produce a Document Vector from a path to a document in the file system,
    * using a [[Types.TypeClasses.TokenSuite]] tool to extract and tokenised the text. Eventually, a modelling function
    * is needed for the weighting of the terms and, optionally, a dictionary in order to normalise
    * the vector.
    *
    * @param tokenizer
    * @param docPath
    * @return [[DocumentVector]] instance, either [[DVector]] or [[EmptyV]]
    */
  def createVector(tokenizer: TokenSuite)(docPath: DocPath)
    : (Scheme,Option[Tokens]) => DocumentVector =
    (modeller: Scheme, dictionary: Option[Tokens]) =>
      (dictionary match {
        case Some(_) => createVector(modeller)(dictionary)
        case None => createVector(modeller)()
      })(tokenizeDocument(tokenizer)(docPath))


}