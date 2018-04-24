package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Utils.{Types,Constants}

object DocumentDataSetMorph {

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
    def apply() : Option[Tokens] =
      if (tokens == null || tokens.isEmpty ||
        (tokens.size == 1 && tokens.head == "") || tokens.forall(_ == ""))
        None
      else Some(tokens.toVector.distinct)
  }
  /** Factory for [[Dictionary]] instances. */
  object Dictionary {
    /**
      * Creates a Dictionary from a group of 1 or more already
      * processed documents.
      *
      * @param tokenizedText Set of tokenized documents
      * @return A collection of tokens that represent the dictionary
      */
    def apply(tokenizedText : Traversable[Option[Document]]): Option[Tokens] = {
      val tokens = tokenizedText.par
        .map(_.getOrElse(Document("",Vector[Token](""))))
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
    def apply(paths: Paths, tokenizer: TokenSuite) : Option[Tokens] = {
      val tokens = paths.flatMap(x => tokenizer.getTokensFromFile(x))
      val instance = new Dictionary(tokens)
      instance()
    }

    /**
      * Creates a Dictionary from 1 or more Document Vectors.
      *
      * @param vectors A set of Vectors created from the respective documents
      * @return A collection of tokens that represent the dictionary
      */
    def apply(vectors: DocumentVector*) : Option[Tokens] = {
      val tokens = vectors.par.filterNot(_.isEmpty).flatMap(x => x.apply.map(y => y._1)).toArray
      val instance = new Dictionary(tokens)
      instance()
    }
  }

  /**
    * This method combines a tokenization method with an extractor function to create a single tool
    * to be used elsewhere in order to tokenize a document.
    *
    * @param tokenizer Tokenization method
    * @param extractor Text extracting function
    * @return A tuple composed as (1: Extracting function, 2: Tokenizing method)
    */
  def buildTokenSuite(tokenizer: Tokenizer)(extractor: TxtExtractor) : TokenSuite = TokenSuite(extractor,tokenizer)

  /**
    * Using a Tokensuite tool, it can tokenise a document from its path in the file system.
    *
    * @param t Tokenization suite as (1: Extracting function, 2: Tokenizing method)
    * @return Tokenized document if the said doc would not result empty after the process
    */
  def tokenizeDocument(t: TokenSuite) : DocPath => Option[Document] =
    (docPath: DocPath) => {
      val txt : Tokens = t.getTokensFromFile(docPath)
      if(txt.size == ZERO) None
      else Some(Document(docPath,txt))
    }

  /**
    * This function can produce a Document Vector using a modelling function (to weight the terms in
    * the vector) and a tokenized document as final input. If a None option is passed as document,
    * probably from a different chained function, it will produce a empty vector.
    * Optionally, a dictionary can be used as parameter as well, in order to normalise the vector produced.
    *
    * @param modeller Modelling Functions of [[Types.Scheme]]. Many are already contained in [[Features.ModelFunctions]]
    * @param dictionary A set of unique terms composing a dictionary (see [[Dictionary]])
    * @return [[DocumentVector]] instance, either [[DVector]] or [[EmptyV]]
    */
  def createVector(modeller: Scheme, dictionary: Option[Tokens] = None) : Option[Document] => DocumentVector = {
    (tokenizedText: Option[Document]) => {
      tokenizedText match {
        case Some(t) => {
          var m = Array.empty[TermWeighted]
          dictionary match {
            case Some(x) => for(d <- x) m = m :+ modeller(d,t.tokens)
            case None => for(y <- t.tokens.toVector.distinct) m = m :+ modeller(y,t.tokens)
          }
          DVector(t.path,m.map(_.toTuple).toMap)
          /*
            Depending on whether the createVector function is called on a PARALLEL COLLECTION
            should be good idea for the developer/maintainer to choose the above implementation,
            while the below implementation offer slightly worse performance but more consistent
            whether the collection is parallel or not.
          */
//          dictionary match {
//            case Some(x) => DVector(t.path,x.par.map(d => modeller(d, t.tokens).toTuple).toVector.toMap)
//            case None => DVector(t.path,t.tokens.par.map(d => modeller(d, t.tokens).toTuple).toVector.toMap)
//          }
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
        case Some(_) => createVector(modeller, dictionary)
        case None => createVector(modeller)
      })(tokenizeDocument(tokenizer)(docPath))

}