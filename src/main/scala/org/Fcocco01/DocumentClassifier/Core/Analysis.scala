package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Utils
import Utils.Types.TypeClasses.{Document,TermWeighted}
import Utils.Types._
import Utils.Constants._

/**
  * Provide functions for analysis a [[Term]],
  * under prove Natural Language Processing methodologies
  * and newly created.
  */
package object Analysis {

  /**
    * Analyses the Inverse Document Frequency of a [[Term]] against a collection of documents.
    * That is the logarithmic inversion of the mean frequency of this term in a set of documents.
    */
  object IDF {

    /**
      * A term and its Inverse Document Frequency (see [[IDF]]) value.
      *
      * @param term The term being weighted
      * @param documentsAsTokens A set of documents'texts
      */
    class IDFValue(val term: Term, documentsAsTokens: Traversable[String]) {
      private val idf : Double = {
        val term = this.term
        var count = ZERO
        for (doc <- documentsAsTokens if(doc.contains(term))) {count = count + 1}
        Math.log10(1 + (documentsAsTokens.size.toDouble / (count).toDouble))
      }
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
                extractor: TxtExtractor): IDFValue = {
        val tokens = documents.map(extractor(_).replace("\n", " ").toLowerCase).filterNot(_ == "")
        new IDFValue(term, tokens)
      }

      /**
        * Create an instance of [[IDFValue]] using a set of tokenised documents.
        *
        * @param term The term being weighted
        * @param documentsAsTokens Optional set of already tokenised documents
        * @return instance of [[IDFValue]]
        */
      def apply(term: Term)(implicit documentsAsTokens: Option[Traversable[Document]]) : IDFValue = {
        val docs : Traversable[Document] = documentsAsTokens match {
          case Some(x) => x.filterNot(_.tokens.isEmpty)
          case None => Array.empty[Document]
        }
        new IDFValue(term,docs.map(_.tokens.mkString(" ")))
      }
    }
  }

  /**
    * Helper function, checks how often a term appear in a single document.
    *
    * @param document tokens from tokenised document
    * @param term checked term
    * @return number of times the term appear in the document
    */
  def GetFrequency(document: Tokens, term: Term) : Int = {
    var count = 0
    for (w <- document if term == w) count += ONE
    count
  }

  /** Contains function for modelling a document vector, weighting the terms in relation to the document. */
  object ModelFunctions {

    /**
      *
      *
      * @param term
      * @param document tokens from tokenised document
      * @return the term accordingly weighted
      */
    def bag(term: Term, document: Tokens) : TermWeighted = {
      TermWeighted(term, GetFrequency(document, term).toDouble)
    }

    /**
      *
      *
      * @param term
      * @param document tokens from tokenised document
      * @return the term accordingly weighted
      */
    def tfLogNorm(term: Term, document: Tokens) : TermWeighted = {
      TermWeighted(term, 1 + Math.log10(GetFrequency(document, term)))
    }

    /**
      *
      *
      * @param term
      * @param document tokens from tokenised document
      * @return the term accordingly weighted
      */
    def tf(term: Term, document: Tokens) : TermWeighted = {
      TermWeighted(term, (GetFrequency(document, term).toDouble / document.size))
    }

    def wdf(term: Term, document: Tokens) : TermWeighted = {
      TermWeighted(term, (Math.log10(GetFrequency(document, term).toDouble) /
        Math.log(document.size)))
    }

    type IDFValue = IDF.IDFValue

    /**
      * Helper function to extract the IDF value of a given term.
      *
      * @param term
      * @param values IDF values for the current document collection
      * @return a IDF weight value of the term
      */
    private def idf(term: Term, values: Traversable[IDFValue]) : Weight = {
      values.filter(_.term == term).head.apply
    }

    /**
      *
      *
      * @param idfValues
      * @return the term accordingly weighted
      */
    def tfidf(idfValues: Traversable[IDFValue]) : (Term,Tokens) => TermWeighted= {
      (term: Term, document: Tokens) => {
        TermWeighted(term, tf(term, document).weight * idf(term, idfValues))
    }}

    /**
      *
      *
      * @param idfValues
      * @return the term accordingly weighted
      */
    def wdfidf(idfValues: Traversable[IDFValue]) : (Term,Tokens) => TermWeighted = {
      (term: Term, document: Tokens) => {
        TermWeighted(term, wdf(term, document).weight * idf(term, idfValues))
      }}
  }
}
