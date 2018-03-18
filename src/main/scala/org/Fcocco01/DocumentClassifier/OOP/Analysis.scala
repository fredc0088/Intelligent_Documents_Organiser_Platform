package org.Fcocco01.DocumentClassifier.OOP

import org.Fcocco01.DocumentClassifier.Types._

package object Analysis {

  object IDF {

    class IDFValue(val term: Term, documents: Paths,
                   extractor: TxtExtractor, d: Option[Tokens] = None) {
      private val idf : Double = {
        val docs = d match {
          case Some(x) => x.filterNot(_ == "")
          case None => documents.map(extractor(_).replace("\n", " ").toLowerCase).filterNot(_ == "")
        }
        val term = this.term
        var count = 0
        for (doc <- docs if(doc.contains(term))) {count = count + 1}
        Math.log10(1 + (documents.size.toDouble / (count).toDouble))
      }
      def get() : Double = {
        idf
      }
    }
    object IDFValue {
      def apply(term: Term)(documents: Paths,
                extractor: TxtExtractor)(d: Option[Tokens] = None): IDFValue =
        new IDFValue(term, documents, extractor, d)
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
      values.filter(_.term == term).head.get
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
