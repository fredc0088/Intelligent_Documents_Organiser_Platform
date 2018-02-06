package org.Fcocco01.DocumentClassifier

import Util.I_O.GetDocContent
import Types.{BagOfWordsModeller,Token,Tokenizer,Paths}

package object Modeller {

  object Models {

    class BagOfWordsDictionary(docsPathsList: Paths, tokenizer: Tokenizer)
      extends BagOfWordsModeller[Paths]{
      override def apply(tokenizer: Tokenizer) : Vector[Token] = {
        docsPathsList.flatMap(x => tokenizer(GetDocContent(x)))
          .toVector.distinct
      }
    }
    object BagOfWordsDictionary {
      def apply(d: Paths, tokenizer: Tokenizer) : Vector[Token]= {
        val newInstance = new BagOfWordsDictionary(d,tokenizer)
        newInstance(tokenizer)
      }
    }

    class BagOfWordsSingleDoc(docPath : String, tokenizer: Tokenizer) extends BagOfWordsModeller[String] {
      override def apply(tokenizer: Tokenizer) : Vector[Token] = {
        tokenizer(GetDocContent(docPath)).distinct
      }
    }
    object BagOfWordsSingleDoc {
      def apply(docPath: String, tokenizer: Tokenizer): Vector[Token] = {
        val newInstance = new BagOfWordsSingleDoc(docPath, tokenizer)
        newInstance(tokenizer)
      }
    }
  }
}
