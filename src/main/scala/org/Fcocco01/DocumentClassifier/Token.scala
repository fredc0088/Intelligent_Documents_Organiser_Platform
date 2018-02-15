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
      def apply(text: String) = {
        val i = text.toLowerCase
          val f = i.replaceAll("\\p{P}\\u00a0", " ")
          val o = f.split(regex)
          val t = o.filterNot(onlyDigits(_))
          //.filter(_.matches("^[a-zA-Z0-9äöüÄÖÜ]*$"))
        //t(498).foreach(x => println(Integer.toHexString(x | 0x10000).substring(1))) //00a0
        text.toLowerCase.replaceAll("\\p{P}\\u00a0", " ").split(regex).filterNot(onlyDigits(_)).filter(!stopWords.contains(_)).toVector
      }
    }
    object TokenizedText {
      def apply(regex: String, stopWords: String) (text: String) = {
        val newInstance = new TokenizedText(regex, stopWords)
        newInstance(text)
      }
    }

}

}
