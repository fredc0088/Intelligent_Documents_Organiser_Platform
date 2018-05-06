package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Essentials
import Essentials.Types.TypeClasses.{Document, TermWeighted, TokenSuite}
import Essentials.Types.{Paths, Scheme, Term, Token, Tokens, Weight}
import Essentials.Constants.{ONE, ZERO, HALF}

/**
  * Provide functions for analysis a [[Term]],
  * under prove Natural Language Processing methodologies
  * and newly created.
  */
package object Features {

  /**
    * Analyses the Inverse Document Frequency of a [[Term]] against a collection of documents.
    * That is the logarithmic inversion of the mean frequency of this term in a set of documents.
    */
  object IDF {

    type IDFFun = (Term, Traversable[String]) => Weight

    /**
      * Simplest idf calculation formula. It count how many times a term appears in the corpus
      * in proportion to the number of documents. If the word does not appear in any document,
      * it will be 1. Then finds the logarithm based 10 of the result.
      *
      * @param term the term being weighted
      * @param documents corpus of documents
      * @return the weight of the term in relation to the corpus
      */
    def simpleIdf(term: Term, documents: Traversable[String]) : Weight = {
      val count = documents.count(_.contains(term))
      Math.log10(documents.size.toDouble / {if(count == 0) ONE else count})
    }

    /**
      * Like [[simpleIdf()]] but 1 is added to the result
      *
      * @param term the term being weighted
      * @param documents corpus of documents
      * @return the weight of the term in relation to the corpus
      */
    def smootherIdf(term: Term, documents: Traversable[String]) : Weight = {
      val count = documents.count(_.contains(term))
      ONE + Math.log10(documents.size.toDouble / {if(count == 0) ONE else count})
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
        val tokens = documents.par.map(x => extractor.getTokensFromFile(x))
          .filterNot(_.isEmpty).map(_.mkString(" ").toLowerCase).seq
        new IDFValue(term, tokens, idfFunc)
      }

      /**
        * Create an instance of [[IDFValue]] using a set of tokenized documents.
        *
        * @param term The term being weighted
        * @param documentsAsTokens Optional set of already tokenized documents
        * @return instance of [[IDFValue]]
        */
      def apply(term: Term)(idfFunc: IDFFun)(implicit documentsAsTokens: Option[Traversable[Option[Document]]]) : IDFValue = {
        val docs = documentsAsTokens match {
          case Some(x) => x.par.map(_.getOrElse(Document("", Array.empty[Token])))
            .filterNot(_.tokens.isEmpty)
          case None => Vector(Document("",Array("")))
        }
        val checked = if(docs.isEmpty) Vector("") else docs.map(_.tokens.mkString(" ").toLowerCase).toVector
        new IDFValue(term,checked,idfFunc)
      }
    }
  }

  /**
    * Helper function, checks how often a term appear in a single document.
    *
    * @param document tokens from tokenized document
    * @param term checked term
    * @return number of times the term appear in the document, 0 if document is empty
    */
  private def GetFrequency(document: Tokens, term: Term) : Int =
    if(document.isEmpty) ZERO else document.count(_ == term)

  /** Contains function for modelling a document vector into a Bag-Of-Words model,
    * weighting in each terms in relation to the document used to create the vector. */
  object Ranking_Modellers {

    /**
      *
      *
      * @param term
      * @param document tokens from tokenized document
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def rawBag(term: Term, document: Tokens) : TermWeighted =
      if(document.isEmpty) TermWeighted(term, ZERO)
      else TermWeighted(term, GetFrequency(document, term).toDouble)

    /**
      *
      *
      * @param term
      * @param document tokens from tokenized document
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def tfLog(term: Term, document: Tokens) : TermWeighted =
      if(document.isEmpty) TermWeighted(term, ZERO)
      else TermWeighted(term, Math.log(ONE + tf(term,document).weight))

    /**
      *
      *
      * @param term
      * @param document tokens from tokenized document
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def tf(term: Term, document: Tokens) : TermWeighted =
      if(document.isEmpty) TermWeighted(term, ZERO)
      else TermWeighted(term, GetFrequency(document, term).toDouble / document.size)

    /**
      * Augmented frequency, to prevent a bias towards longer documents,
      * e.g. raw frequency divided by the maximum raw frequency of any term in the document.
      * This computation tend to be heavy and affect performances.
      *
      * @param term
      * @param document tokens from tokenised document
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def augmented_tf(term: Term, document: Tokens) : TermWeighted =
      if(document.isEmpty) TermWeighted(term, HALF)
      else TermWeighted(term, HALF + ( HALF * GetFrequency(document, term).toDouble /
          document.par.map(x => GetFrequency(document,x)).toArray.max))

    type IDFValue = IDF.IDFValue

    /**
      * Helper function to extract the IDF value of a given term.
      *
      * @param term
      * @param values IDF values for the current document collection
      * @return a IDF weight value of the term
      */
    private def idf(term: Term, values: Traversable[IDFValue]) : Weight =
      if(values.isEmpty) ZERO
      else
        values.find(_.term == term) match {
          case Some(x) => x.apply
          case None => ZERO
        }

    /**
      * This function is used as facade to obtain a [[TermWeighted]],
      * it is made with the use of a modelling function and an optional
      * set of value obtained with some kind of document frequency.
      *
      * @param weightingFun
      * @return the term accordingly weighted, as 0 if document is empty
      */
    def compose_weighting_Fun(weightingFun : Scheme): Option[Traversable[IDFValue]] => (Term, Tokens) => TermWeighted =
      (idfValues: Option[Traversable[IDFValue]]) =>
        (term: Term, document: Tokens) => {
          val res = { if(document.isEmpty) ZERO
                    else weightingFun(term,document).weight }
          idfValues match {
            case Some(x) => TermWeighted(term, res * idf(term, x))
            case None => TermWeighted(term, res)
          }
        }

  }
}
