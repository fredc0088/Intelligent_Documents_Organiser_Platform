package org.Fcocco01.DocumentClassifier.Core

import org.Fcocco01.DocumentClassifier.Essentials
import Essentials.Util.I_O.GetDocContent
import Essentials.Util.String_Manipulation.onlyDigits
import Essentials.Types.{ Tokenizer, TypeClasses}
import TypeClasses.DocPath

/**
  * Provides objects and classes for dealing with tokenization of text.
  * It also allows to create a dictionary of stopwords to be escaped during tokenization process.
  */
package object Tokenization {

    /**
      * Creates a [[String]] of words from a file to be used as stopwords.
      */
    object StopWords {
      def apply(filePath: DocPath) : String =
        GetDocContent(filePath)
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
      def apply(text: String): Vector[String] =
        text.toLowerCase.replaceAll("\\p{P}\\u00a0", " ").split(regex).par
          .filterNot(onlyDigits).filter(!stopWords.contains(_)).toVector
    }

    /**
      * Factory object for [[TokenizedText]]
      */
    object TokenizedText {
      def apply(regex: String, stopWords: String) (text: String): Vector[String] =
        (new TokenizedText(regex, stopWords))(text)
    }

}
