package org.Fcocco01.DocumentClassifier

package object Types {

  type DocPath = String
  type TxtExtractor = String => String
  type Paths = Traversable[String]
  type Token = String
  type Term = String
  type Tokens = Traversable[Token]
  type Tokenizer = (String => Vector[Token])
  type TermWeighted = (Term,Double)
  type Scheme = (String, Traversable[String]) => TermWeighted

  type Text = String
  type Document = (DocPath,Tokens)
  type Weight = Double
  type TokenSuite = (TxtExtractor,Tokenizer)
//  class TermWeighted private(term: Term, weight: Double)
//  object TermWeighted {
//    def apply(term: Term, weight: Double): (Term,Double) =
//      (term,weight)
//  }
}
