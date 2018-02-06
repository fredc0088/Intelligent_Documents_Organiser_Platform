package org.Fcocco01.DocumentClassifier

package object Types {
  type BagOfWordsModeller[T] = (Tokenizer) => Vector[Token]
  type BagOfWords = Vector[Token]
  type Paths = Traversable[String]
  type Token = String
  type Tokenizer = (String => Vector[Token])
  type TFIDFValue = Double
  type Term = Map[Token,TFIDFValue]
}
