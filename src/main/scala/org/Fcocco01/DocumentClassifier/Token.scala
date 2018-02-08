package org.Fcocco01.DocumentClassifier

import Util.I_O.GetDocContent
import Util.String_Manipulation.onlyDigits

package object Token {

  object Tokenizer {

    object StopWords {
      def apply(filePath: String) = {
        val stopWords = GetDocContent(filePath)
        stopWords
      }
    }

    class TokenizedText(regex: String, stopWords: String)
      extends Types.Tokenizer {
      def apply(text: String) = text.toLowerCase.split(regex)
        .filter(!onlyDigits(_))
        .filter(_.matches("^[a-zA-Z0-9äöüÄÖÜ]*$"))
        .filter(!stopWords.contains(_))
        .toVector
    }
    object TokenizedText {
      def apply(regex: String, stopWords: String) (text: String) = {
        val newInstance = new TokenizedText(regex, stopWords)
        newInstance(text)
      }
    }

}

}
