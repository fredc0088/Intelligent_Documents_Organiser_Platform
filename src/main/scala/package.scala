import Util.Control.using

import scala.util.{Failure, Success, Try}

package object Util {

  /**
    * Object's name: Control
    * Note: From the book, Beginning Scala, by David Pollak.
    * This makes use of the Loan Pattern ensuring the closing of
    * a resource after its use.
    */
  object Control {

    def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
      try {
        f(param)
      } finally {
        param.close()
      }
  }


  object Types {
    type Tokenizer = (String => Vector[String])
    type Word = String
    type Term = (String, Double)
    type Paths = Traversable[String]
    trait TextModel

  }

  object I_O {
    def readDocWithTry(file: String) = {
      Try {
        val lines = using(scala.io.Source.fromFile(file)) {
          source => (for (line <- source.getLines) yield line).toList
        }
        lines
      }
    }

    def GetDocContent(filePath: String) = {
      val content = readDocWithTry(filePath)
      content match {
        case Success(lines) => lines.mkString
        case Failure(t) => throw new Exception(t) { println(t.getStackTrace) }
      }

    }
  }

  object Operators {

    implicit class |>[A](val a: A) extends AnyVal {
      def |>[B](op: A => B) : B = op(a)
    }


  }


}
