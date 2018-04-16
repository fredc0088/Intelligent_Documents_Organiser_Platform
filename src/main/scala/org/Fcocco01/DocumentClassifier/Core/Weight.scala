package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Utils
import Utils.Types.TypeClasses.{Document, TermWeighted}
import Utils.Types.{Term, Token, Tokens, TxtExtractor, Paths, Weight}
import Utils.Constants.{ZERO, ONE}

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

    /**
      * A term and its Inverse Document Frequency (see [[IDF]]) value.
      *
      * @param term The term being weighted
      * @param documentsAsTokens Contents of the documents
      */
    class IDFValue(val term: Term, documentsAsTokens: Traversable[String]) {
      private val idf : Double = {
        val count = documentsAsTokens.count(_.contains(this.term))
        val denominator = if(count == ZERO) ZERO else documentsAsTokens.size.toDouble / count

//        println(s"DEBUG: Term: ${term} IDF: ${Math.log10(ONE + denominator)}")

        Math.log10(ONE + denominator)
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
        val tokens = documents.par.map(extractor(_).replace("\n", " ").toLowerCase).filterNot(_ == "")
        new IDFValue(term, tokens.toVector)
      }

      /**
        * Create an instance of [[IDFValue]] using a set of tokenised documents.
        *
        * @param term The term being weighted
        * @param documentsAsTokens Optional set of already tokenised documents
        * @return instance of [[IDFValue]]
        */
      def apply(term: Term)(implicit documentsAsTokens: Option[Traversable[Option[Document]]]) : IDFValue = {
        val docs : Vector[Document] = documentsAsTokens match {
          case Some(x) => x.map(_.getOrElse(Document("", Array.empty[Token]))).filterNot(_.tokens.isEmpty).toVector
          case None => Vector(Document("",Array("")))
        }
        val checked = if(docs.isEmpty) Vector(Document("",Array(""))) else docs
        new IDFValue(term,checked.map(_.tokens.mkString(" ")))
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
    def tfLogNorm(term: Term, document: Tokens) : TermWeighted =
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
      else values.find(_.term == term).getOrElse(IDF.IDFValue(term)(None)).apply

    /**
      *
      *
      * @param idfValues
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def tfidf(idfValues: Traversable[IDFValue]) : (Term,Tokens) => TermWeighted =
      (term: Term, document: Tokens) =>
        if(document.isEmpty) TermWeighted(term, 0.0)
        else TermWeighted(term, tf(term, document).weight * idf(term, idfValues))

    /**
      *
      *
      * @param idfValues
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def wdfidf(idfValues: Traversable[IDFValue]) : (Term,Tokens) => TermWeighted =
      (term: Term, document: Tokens) =>
        if(document.isEmpty) TermWeighted(term, 0.0)
        else TermWeighted(term, wdf(term, document).weight * idf(term, idfValues))
  }
}
