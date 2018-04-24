package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Utils
import Utils.Util.I_O.GetDocContent
import Utils.Util.String_Manipulation.onlyDigits
import Utils.Types.Tokenizer

/**
  * Provides objects and classes for dealing with tokenization of text.
  * It also allows to create a dictionary of stopwords to be escaped during tokenization process.
  */
package object Tokenization {

    /**
      * Creates a [[String]] of words from a file to be used as stopwords.
      */
    object StopWords {
      def apply(filePath: String) = {
        val stopWords = GetDocContent(filePath)
        stopWords
      }
    }

    /**
      * Tool to transform a given text to a collection of tokens, processing the text also
      * in regards of a given regex and given stopwords.
      *
      * @constructor A regex and a series of stopwords to be excluded.
      * @param regex
      * @param stopWords
      */
    class TokenizedText(regex: String, stopWords: String)
      extends Tokenizer {
      def apply(text: String) =
        text.toLowerCase.replaceAll("\\p{P}\\u00a0", " ").split(regex).par
          .filterNot(onlyDigits(_)).filter(!stopWords.contains(_)).toVector
    }

    /**
      * Factory object for [[TokenizedText]]
      */
    object TokenizedText {
      def apply(regex: String, stopWords: String) (text: String) = {
        val newInstance = new TokenizedText(regex, stopWords)
        newInstance(text)
      }
    }


}
