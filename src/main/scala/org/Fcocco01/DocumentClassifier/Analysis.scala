package org.Fcocco01.DocumentClassifier

package object Analysis {

  object IDF {

    class IDFValue(val term: String, documents: Traversable[String],
                   extractor: String => String) {
      private val idf : Double = {
        val docs = documents.map(extractor(_).replace("\n"," ").toLowerCase)
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
      def apply(term: String, documents: Traversable[String],
                extractor: String => String): IDFValue =
        new IDFValue(term, documents, extractor)
    }
  }

  def GetFrequency(document: Traversable[String], term: String) = {
    var count = 0
    for (w <- document if term == w) count += 1
    count
  }

  object ModelFunctions {

    def tfLogNorm(document: Traversable[String], term: String) = {
      (term, 1 + Math.log10(GetFrequency(document, term)))
    }

    def tf(document: Traversable[String], term: String) = {
      (term, (GetFrequency(document, term).toDouble / document.size))
    }

    def wdf(document: Traversable[String], term: String) = {
      (term, (Math.log10(GetFrequency(document, term).toDouble) /
        Math.log(document.size)))
    }

    type IDFValue = IDF.IDFValue

    def idf(term: String, values: Traversable[IDFValue]) = {
      values.filter(_.term == term).head.get
    }

    def tfidf(idfValues: Traversable[IDFValue]) =
      (term: String, document: Traversable[String]) => {
        (term, tf(document,term)._2 * idf(term, idfValues))
    }

    def wdfidf(idfValues: Traversable[IDFValue]) =
      (term: String, document: Traversable[String]) => {
        (term, wdf(document, term)._2 * idf(term, idfValues))
      }

    def bag(term: String, document: Traversable[String]) = {
      (term, GetFrequency(document,term).toDouble)
    }
  }

  /************************OOP Style**************************************/
  /**
    trait Tf {

      def tf(document: Traversable[String], term: String) = {
        if (document.size > 0) {
          val frequency = () => {
            var f = 0
            for (w <- document if term == w) {
              f += 1
            }
            f
          }
          frequency().toDouble / document.size
        } else 0.0
      }
    }

      trait Idf {

        type IDFValue = IDF.IDFValue

        def getIdf(term: String, values: Traversable[IDFValue]) = {
          values.filter(_.term == term).head.get
        }

      }

      trait BagOfWords {

        def makeBagTerm(term: String, document: Traversable[String]) =
          (term, GetFrequency(document,term).toDouble)
      }
  */

}
