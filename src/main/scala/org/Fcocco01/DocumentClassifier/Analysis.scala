package org.Fcocco01.DocumentClassifier

import scala.math.BigDecimal.RoundingMode

package object Analysis {

  /**
    * This trait allow to incorporate a per-document analysis of each term
    * regarding their weight in the document.
    */
  object Tf {
    def tf(f: (Traversable[String],String) => Double, document: Traversable[String], term : String) : Double = {
      if(document.size > 0) {
        f(document, term) / document.size
      } else 0.0
    }
  }

  object TfIdf {

    class IDFValue(val term: String, documents: Traversable[String],
                   extractor: String => String) {
      private val idf : Double = {
        val docs = documents.map(extractor(_).replace("\n"," ").toLowerCase)
        val term = this.term
        var count = 0
        for (doc <- docs if(doc.contains(term))) {count = count + 1}
        Math.log10(documents.size.toDouble / (count).toDouble)
      }
      def get() : Double= {
        idf
      }
    }
    object IDFValue {
      def apply(term: String, documents: Traversable[String],
                extractor: String => String): IDFValue =
        new IDFValue(term, documents, extractor)
    }


    def TFIDF(term: String, doc: Traversable[String], f: (Traversable[String],String) => Double,
             idf: Traversable[IDFValue]) = {
//        BigDecimal((weightingFun(doc, term) * idf.filter(_.term == term).head.get).toString)
//          .setScale(5,RoundingMode.HALF_UP)
//          .toDouble
      (f(doc, term) / doc.size) * idf.filter(_.term == term).head.get
    }
  }




}
