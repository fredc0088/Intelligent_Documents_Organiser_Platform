package org.Fcocco01.DocumentClassifier

import Types._

package object AnalysisFP {

  object IDF {

    class IDFValue(val term: Term, documentsAsTokens: Traversable[String]) {
      private val idf : Double = {
        val term = this.term
        var count = 0
        for (doc <- documentsAsTokens if(doc.contains(term))) {count = count + 1}
        Math.log10(1 + (documentsAsTokens.size.toDouble / (count).toDouble))
      }
      def apply : Double = idf
    }
    object IDFValue {
      def apply(term: Term,documents: Paths,
                extractor: TxtExtractor): IDFValue = {
        val tokens = documents.map(extractor(_).replace("\n", " ").toLowerCase).filterNot(_ == "")
        new IDFValue(term, tokens)
        }
      def apply(term: Term)(implicit documentsAsTokens: Option[Traversable[Document]]) = {
        val docs : Traversable[Document] = documentsAsTokens match {
          case Some(x) => x.filterNot(_._2.isEmpty)
          case None => Array.empty[Document]
        }
        new IDFValue(term,docs.map(_._2.mkString(" ")))
      }
    }
  }

  def GetFrequency(document: Tokens, term: Term) = {
    var count = 0
    for (w <- document if term == w) count += 1
    count
  }

  object ModelFunctions {

    def tfLogNorm(term: Term, document: Tokens) = {
      (term, 1 + Math.log10(GetFrequency(document, term)))
    }

    def tf(term: Term, document: Tokens) = {
      (term, (GetFrequency(document, term).toDouble / document.size))
    }

    def wdf(term: Term, document: Tokens) = {
      (term, (Math.log10(GetFrequency(document, term).toDouble) /
        Math.log(document.size)))
    }

    type IDFValue = IDF.IDFValue

    def idf(term: Term, values: Traversable[IDFValue]) = {
      values.filter(_.term == term).head.apply
    }

    def tfidf(idfValues: Traversable[IDFValue]) =
      (term: Term, document: Tokens) => {
        (term, tf(term, document)._2 * idf(term, idfValues))
    }

    def wdfidf(idfValues: Traversable[IDFValue]) =
      (term: Term, document: Tokens) => {
        (term, wdf(term, document)._2 * idf(term, idfValues))
      }

    def bag(term: Term, document: Tokens) = {
      (term, GetFrequency(document, term).toDouble)
    }
  }

}
