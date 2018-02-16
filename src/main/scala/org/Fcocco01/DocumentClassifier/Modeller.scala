package org.Fcocco01.DocumentClassifier

import Util.I_O.GetDocContent
import Types.{Modeller, Paths, Term, Token, Tokenizer, Tokens}
import Analysis.Tf.tf
import Analysis.TfIdf.{IDFValue,TFIDF}

import scala.collection.mutable

package object Modeller {

  object Models {

    private def getFrequency(vocabulary: Tokens, term: Token) = {
      var f = 0
      for (v <- vocabulary if term == v) f += 1
      f
    }

    class Dictionary(docsPathsList: Paths, tokenizer: Tokenizer){
      def apply(tokenizer: Tokenizer) : Vector[Token] = {
        docsPathsList.flatMap(x => tokenizer(GetDocContent(x)))
          .toVector.distinct
      }
    }
    object Dictionary {
      def apply(d: Paths, tokenizer: Tokenizer) : Vector[Token]= {
        val newInstance = new Dictionary(d,tokenizer)
        newInstance(tokenizer)
      }
    }


    abstract class Modeller(tokens: Tokens) {
      def apply(holder: AnyVal): Modeller = new Modeller(tokens)
    }

    case class BagOfWorld(dictionary: Tokens , docPath: String, tokens: Tokens) extends Modeller {
      def apply(holder: AnyVal) = {
        var m = new mutable.MutableList[(Token,Double)]() // mutable to try to improve perfomances
        for(d <- dictionary) m :+ (d, getFrequency(tokens,d).toDouble)
        m.toMap
      }
    }
    object BagOfWorld{
      def apply(dictionary: Tokens , docPath: String, tokens: Tokens) = {
        new BagOfWorld(dictionary, docPath, tokens)
      }
    }

    case class TfIDF(dictionary: Tokens , docPath: String, tokens: Tokens) extends Modeller {
      def apply(holder: Vector[IDFValue]) = {
        var m = new mutable.MutableList[(Token,Double)]()
        holder match {
          case x : Vector[IDFValue] => {
            //var m = new mutable.MutableList[(Token,Double)]() // mutable to try to improve perfomances
            for(d <- dictionary) m :+ TFIDF(d,tokens, getFrequency, x)
            m.toMap
          }
          case _ => {
            var m = new mutable.MutableList[(Token,Double)]() // mutable to try to improve perfomances
            for(d <- dictionary) m :+ (getFrequency(tokens, d) / tokens.size)
            m.toMap
          }
        }
        var m = new mutable.MutableList[(Token,Double)]() // mutable to try to improve perfomances
        for(d <- dictionary) m :+ (d, getFrequency(tokens,d).toDouble)
        m.toMap
      }
    }

  }
}
