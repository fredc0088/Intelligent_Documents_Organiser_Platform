package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Utils
import Utils.Types.TypeClasses.{Document, TermWeighted, TokenSuite}
import Utils.Types.{Paths, Scheme, Term, Token, Tokens, TxtExtractor, Weight}
import Utils.Constants.{ONE, ZERO}

/**
  * Provide functions for analysis a [[Term]],
  * under prove Natural Language Processing methodologies
  * and newly created.
  */
package object Weight {

  /**
    * Analyses the Inverse Document Frequency of a [[Term]] against a collection of documents.
    * That is the logarithmic inversion of the mean frequency of this term in a set of documents.
    */
  object IDF {

    type IDFFun = (Term, Traversable[String]) => Weight

    def simpleIdf(term: Term, documents: Traversable[String]) : Weight = {
      val count = documents.count(_.contains(term))
      val denominator = if(count == ZERO) ZERO else documents.size.toDouble / count

      //        println(s"DEBUG: Term: ${term} IDF: ${Math.log10(ONE + denominator)}")

      Math.log10(ONE + denominator)
    }

    /**
      * A term and its Inverse Document Frequency (see [[IDF]]) value.
      *
      * @param term The term being weighted
      * @param documentsAsTokens Contents of the documents
      */
    class IDFValue(val term: Term, documentsAsTokens: Traversable[String], idfFunc: IDFFun) {
      private val idf : Double = idfFunc(term, documentsAsTokens)
      def apply : Double = idf
    }

    /**
      * Factory object for [[IDFValue]]
      */
    object IDFValue {
      /**
        * Create an instance of [[IDFValue]] accepting the paths to the documents and
        * proceeding with the text extraction process on them.
        *
        * @param term The term being weighted
        * @param documents paths to the documents in the file system
        * @param extractor method for extracting text from a file using its path
        * @return instance of [[IDFValue]]
        */
      def apply(term: Term,documents: Paths,
                extractor: TokenSuite, idfFunc: IDFFun): IDFValue = {
        val tokens = documents.par.map(x => extractor.tokenizer(extractor.extract(x)))
          .filterNot(_.isEmpty).map(_.mkString(" ")).seq
        new IDFValue(term, tokens, idfFunc)
      }

      /**
        * Create an instance of [[IDFValue]] using a set of tokenised documents.
        *
        * @param term The term being weighted
        * @param documentsAsTokens Optional set of already tokenised documents
        * @return instance of [[IDFValue]]
        */
      def apply(term: Term)(idfFunc: IDFFun)(implicit documentsAsTokens: Option[Traversable[Option[Document]]]) : IDFValue = {
        val docs = documentsAsTokens match {
          case Some(x) => x.par.map(_.getOrElse(Document("", Array.empty[Token])))
            .filterNot(_.tokens.isEmpty)
          case None => Vector(Document("",Array("")))
        }
        val checked = if(docs.isEmpty) Vector("") else docs.map(_.tokens.mkString(" ")).toVector
        new IDFValue(term,checked,idfFunc)
      }
    }
  }

  /**
    * Helper function, checks how often a term appear in a single document.
    *
    * @param document tokens from tokenised document
    * @param term checked term
    * @return number of times the term appear in the document, 0 if document is empty
    */
  def GetFrequency(document: Tokens, term: Term) : Int =
    if(document.isEmpty) ZERO else document.count(_ == term)

  /** Contains function for modelling a document vector, weighting the terms in relation to the document. */
  object ModelFunctions {

    /**
      *
      *
      * @param term
      * @param document tokens from tokenised document
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def bag(term: Term, document: Tokens) : TermWeighted =
      if(document.isEmpty) TermWeighted(term, 0.0)
      else TermWeighted(term, GetFrequency(document, term).toDouble)

    /**
      *
      *
      * @param term
      * @param document tokens from tokenised document
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def tfLog(term: Term, document: Tokens) : TermWeighted =
      if(document.isEmpty) TermWeighted(term, 0.0)
      else TermWeighted(term, ONE + Math.log10(GetFrequency(document, term)))

    /**
      *
      *
      * @param term
      * @param document tokens from tokenised document
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def tf(term: Term, document: Tokens) : TermWeighted =
      if(document.isEmpty) TermWeighted(term, 0.0)
      else TermWeighted(term, (GetFrequency(document, term).toDouble / document.size))

    /**
      *
      *
      * @param term
      * @param document tokens from tokenised document
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def wdf(term: Term, document: Tokens) : TermWeighted =
      if(document.isEmpty) TermWeighted(term, 0.0)
      else TermWeighted(term, (Math.log(GetFrequency(document, term).toDouble + ONE) /
        ONE + Math.log(document.size)))

    type IDFValue = IDF.IDFValue

    /**
      * Helper function to extract the IDF value of a given term.
      *
      * @param term
      * @param values IDF values for the current document collection
      * @return a IDF weight value of the term
      */
    private def idf(term: Term, values: Traversable[IDFValue]) : Weight =
      if(values.isEmpty) 0.0
      else
        values.find(_.term == term) match {
          case Some(x) => x.apply
          case None => 0.0
        }

//    /**
//      *
//      *
//      * @param idfValues
//      * @return the term accordingly weighted, as 0 if document is empty
//      */
//    def tfidf(idfValues: Traversable[IDFValue]) : (Term,Tokens) => TermWeighted =
//      (term: Term, document: Tokens) =>
//        if(document.isEmpty) TermWeighted(term, 0.0)
//        else TermWeighted(term, tf(term, document).weight * idf(term, idfValues))
//
//    /**
//      *
//      *
//      * @param idfValues
//      * @return the term accordingly weighted, as 0 if document is empty
//      */
//    def wdfidf(idfValues: Traversable[IDFValue]) : (Term,Tokens) => TermWeighted =
//      (term: Term, document: Tokens) =>
//        if(document.isEmpty) TermWeighted(term, 0.0)
//        else TermWeighted(term, wdf(term, document).weight * idf(term, idfValues))

    /**
      * This function is used as facade to obtain a [[TermWeighted]],
      * it is made with the use of a modelling function and an optional
      * set of value obtained with some kind of document frequency.
      *
      * @param weightingFun
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def compose_weighting_Fun(weightingFun : Scheme) =
      (idfValues: Option[Traversable[IDFValue]]) =>
        (term: Term, document: Tokens) => {
          val res = { if(document.isEmpty) 0.0
                    else weightingFun(term,document).weight }
          idfValues match {
            case Some(x) => TermWeighted(term, res * idf(term, x))
            case None => TermWeighted(term, res)
          }
        }

  }
}
