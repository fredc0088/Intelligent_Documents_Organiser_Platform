package org.Fcocco01.DocumentClassifier

import Util.I_O.GetDocContent

package object Token {

  object Tokenizer {

    class TokenizedText(regex: String, stopWordsFilePath: String)
      extends Types.Tokenizer {
      val stopwords = GetDocContent(stopWordsFilePath)
      def apply(text: String) = text.toLowerCase.split(regex).filter(!stopwords.contains(_)).toVector
    }
    object TokenizedText {
      def apply(regex: String, stopWordsFilePath: String) (text: String) = {
        val newInstance = new TokenizedText(regex, stopWordsFilePath)
        newInstance(text)
      }
    }

}

}
