package org.Fcocco01.DocumentClassifier

package object Types {

  type Extractor = String => String
  type Paths = Traversable[String]
  type Token = String
  type Tokens = Traversable[Token]
  type Tokenizer = (String => Vector[Token])
  type Scheme = (String, Traversable[String]) => (Token,Double)
}
