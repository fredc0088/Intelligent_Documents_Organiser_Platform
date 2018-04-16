package org.Fcocco01.DocumentClassifier.Utils

import java.io.File
import java.nio.file.{Files, Paths, StandardOpenOption}
import java.util.Calendar

import scala.language.reflectiveCalls
import scala.util.{Failure, Success, Try}

package object Util {

  import scala.io.Codec


  /**
    * Object's name: Control
    *
    * Reference: From the book, Beginning Scala, by David Pollak.
    * This makes use of the Loan Pattern ensuring the closing of
    * a resource after its use.
    */
  object Control {

    def using[A <: {def close() : Unit}, B](param: A)(f: A => B): B =
      try f(param) finally param.close()
  }

  /** Object containing functions for Input/Output */
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
      *   - docx
      *   - log
      *
      *   pdf is implemented but can slow down and it is not always able to parse correctly.
      *   md is subject of consideration.
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
        document.close
        stripper.split(" ").toList
      }
      case "docx" => Try {
        val document = new XWPFDocument(new FileInputStream(file))
        val extractor = new XWPFWordExtractor(document)
        document.close
        extractor.getText.split(" ").toList
      }
      case "doc" => Try {
        val document = new HWPFDocument(new FileInputStream(file))
        val extractor = new WordExtractor(document)
        document.close
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
      case Failure(t) => {
        errorHandling.logAwayErrorsAndExceptions(t)
        ""
      }
    }

    def log(file: File, message: String, append: Boolean) = {
      append match {
        case true => Files.write(Paths.get(file.getAbsolutePath), message.getBytes, StandardOpenOption.APPEND)
        case false => Files.write(Paths.get(file.getAbsolutePath), message.getBytes)
      }
    }
  }

  /** Contains useful operators */
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

  /** Methods to manipulate/checks/etc.. Strings */
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

  /** Contains methods to format values */
  object Formatting {

    /**
      * Make numbers with very big negative exponent readable.
      *
      * @param d A decimal number
      * @return [[String]] representing that value
      */
    def roundDecimals(d: Double) = new java.math.BigDecimal(d).toPlainString
  }

  /** Contains functions concerning time value manipulation, date, etc... */
  object Time {
    /**
      * Show how many minutes have passed from a given time in nanoseconds.
      * High decimal precision.
      *
      * @param t Initial time to compare, in nanoseconds
      * @return Time difference in minutes from that given time to current time
      */
    def currentTimeMins(t: Double) : String = (((System.nanoTime - t) / 1E9) / 60) + " mins"
    def getCurrentDateString : String = {
      val todayDate = Calendar.getInstance
      s"${todayDate.get(Calendar.DAY_OF_MONTH)}-${todayDate.get(Calendar.MONTH)}-${todayDate.get(Calendar.YEAR)}"
    }
    def getCurrentTimeString : String = {
      val todayDate = Calendar.getInstance
      s"${todayDate.get(Calendar.HOUR_OF_DAY)}:${todayDate.get(Calendar.MINUTE)}:${todayDate.get(Calendar.SECOND)}"
    }
  }

  object errorHandling {
    def logAwayErrorsAndExceptions(e: Throwable) = {
      val day = Time.getCurrentDateString
      val file : File = (new File("./Error_Logs")).listFiles.find(_.getName.contains(day))
        .getOrElse(Files.createFile(Paths.get(s"./Error_Logs/$day - ErrorsLog.log")).toFile)
      val stackTrace = e.getStackTrace.map(s => s"      ${s.toString}\n").mkString
      val error = s"${Time.getCurrentTimeString} - [ ${e.getMessage} ]\n    ${e.getCause}\n     ${e.getLocalizedMessage}\n$stackTrace"
      I_O.log(file, error,true)
    }
  }

}
