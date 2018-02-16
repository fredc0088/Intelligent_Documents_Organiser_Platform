package org.Fcocco01.DocumentClassifier

package object Types {
  type Tokenizer = (String => Vector[Token])
  type Paths = Traversable[String]

  type Tokens = Traversable[Token]

  type Token = String

  type TFIDFValue = Double
  type Term = (Token,TFIDFValue)

  type Modeller = AnyVal => Map[Token,Double]
}
