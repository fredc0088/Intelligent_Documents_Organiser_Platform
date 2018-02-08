package org.Fcocco01.DocumentClassifier

package object Analysis {

  /**
    * This trait allow to incorporate a per-document analysis of each term
    * regarding their weight in the document.
    */
  trait Tf {
    def tf(document: Traversable[String], term: String) : Double = {
      if(document.size > 0) {
        val frequency = () => {
          var f = 0
          for (w <- document if term == w) {
            f += 1
          }
          f
        }
        frequency() / document.size
      } else 0.0
    }
  }
  object Tf extends Tf


  object Idf {

    type DocsLists = Traversable[String]
    type Unwrapper = DocsLists => Vector[Vector[String]]

    class IDFValue(val term: String, documents: Traversable[String],
                   extractor: String => String) {
      private val idf : Double = {
        val docs = documents.map(extractor(_))
        val term = this.term
        var count = 0
        for (doc <- docs if(doc.contains(term))) count = count + 1
        Math.log(documents.size / (1 + count))
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


    def _IDF(term: String, doc: Traversable[String],
             weightingFun: (Traversable[String],String) => Double,
             idf: Traversable[IDFValue]) = {
      weightingFun(doc, term) * idf.filter(_.term == term).head.get
    }
  }




}
