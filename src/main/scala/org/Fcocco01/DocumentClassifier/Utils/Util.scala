package org.Fcocco01.DocumentClassifier.Utils

import scala.language.reflectiveCalls
import scala.util.{Failure, Success, Try}

package object Util {

  import scala.io.Codec


  /**
    * Object's name: Control
    * Note: From the book, Beginning Scala, by David Pollak.
    * This makes use of the Loan Pattern ensuring the closing of
    * a resource after its use.
    */
  object Control {

    def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
      try f(param) finally param.close()
  }

  /**
    * Object containing functions for Input/Output
    */
  object I_O {

    import java.io.FileInputStream

    import org.apache.pdfbox.pdmodel.PDDocument
    import org.apache.pdfbox.text.PDFTextStripper
    import org.apache.poi.hwpf.HWPFDocument
    import org.apache.poi.hwpf.extractor.WordExtractor
    import org.apache.poi.xwpf.extractor.XWPFWordExtractor
    import org.apache.poi.xwpf.usermodel.XWPFDocument

    /**
      * Read document files using a Try monad for handling errors.
      * Handle relatively without problems:
      *   - txt
      *   - doc
      *   -docx
      *
      *   pdf and md are subject to testing and improvement
      *
      * @param file Path to the file
      * @return A collection of [[String]] representing the text of the file
      */
    def readDocWithTry(file: String) = file.split("\\.").last match {
      case "pdf" => Try {
        /**
          * To decide whether to keep it.
          * Mapping to unicode is not good and the process is way too slow
          */
        val document = PDDocument.load(new java.io.File(file))
        val stripper = new PDFTextStripper().getText(document)
        stripper.split(" ").toList
      }
      case "docx" => Try {
        val document = new XWPFDocument(new FileInputStream(file))
        val extractor = new XWPFWordExtractor(document)
        extractor.getText.split(" ").toList
      }
      case "doc" => Try {
        val document = new HWPFDocument(new FileInputStream(file))
        val extractor = new WordExtractor(document)
        extractor.getText.split(" ").toList
      }
      case _ => Try {
        val lines = Control.using(io.Source.fromFile(file, Codec.ISO8859.name)) {
          source =>
            (for (
              line <- source.getLines
            ) yield line.split(" ")).toList.flatten
        }
        lines
      }
    }

    /**
      * Handle result of a document extracting method that uses Try monad.
      *
      * @param filePath Path to file
      * @return A [[String]] representing the text extracted
      */
    def GetDocContent(filePath: String): String = readDocWithTry(filePath) match {
      case Success(lines) => lines.mkString(" ")
      case Failure(t) => ""
//        t match {
//          case e : org.apache.poi.EmptyFileException => ""
//          case e : IllegalArgumentException if(e.getMessage.trim == "The document is really a UNKNOWN file") =>  ""
//            if(e.getMessage.trim == "The document is really a UNKNOWN file") ""
//            else throw new IllegalArgumentException(t) {println(t.getCause.getMessage)}
//          case _ => throw new Exception(t) {println(t.getCause.getMessage)}
//        }
    }
  }

  /**
    * Contains useful operators.
    */
  object Operators {

    /**
      * Simple operator to pipe a function result into another function.
      * The variable assigned with the result needs to have the type declared.
      *
      * @param a
      * @tparam A
      */
    implicit class |>[A](val a: A) extends AnyVal {
      def |>[B](op: A => B) = op(a)
    }
  }

  /**
    * Methods to manipulate/checks/etc.. Strings
    */
  object String_Manipulation {
    /**
      * Check if the [[String]] contains only digits
      *
      * @param s
      * @return true if it is formed of only digits
      */
    def onlyDigits(s: String) = {
      var isDigit = true
      for(c <- s if !Character.isDigit(c)) isDigit = false
      isDigit
    }
  }

  object Constants {
    val ZERO = 0
    val ZEROF = 0.0
    val HALF = 0.5
    val ONE = 1
    val TWO = 2
  }

  /**
    * Contains methods to format values
    */
  object Formatting {

    /**
      * Make numbers with very big negative exponent readable.
      *
      * @param d A decimal number
      * @return [[String]] representing that value
      */
    def roundDecimals(d: Double) = new java.math.BigDecimal(d).toPlainString
  }

  /**
    * Contains functions concerning time value manipulation, date, etc...
    */
  object Time {
    /**
      * Show how many minutes have passed from a given time in nanoseconds.
      * High decimal precision.
      *
      * @param t Initial time to compare, in nanoseconds
      * @return Time difference in minutes from that given time to current time
      */
    def currentTimeMins(t: Double) = (((System.nanoTime - t) / 1E9) / 60) + " mins"
  }

}
