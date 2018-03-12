
import org.apache.poi.UnsupportedFileFormatException

import scala.util.{Failure, Success, Try}
import scala.language.reflectiveCalls

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

  object I_O {

    import org.apache.poi.xwpf.usermodel.XWPFDocument
    import org.apache.poi.xwpf.extractor.XWPFWordExtractor
    import org.apache.poi.hwpf.HWPFDocument
    import org.apache.poi.hwpf.extractor.WordExtractor
    import org.apache.pdfbox.pdmodel.PDDocument
    import org.apache.pdfbox.text.PDFTextStripper
    import java.io.FileInputStream

    def readDocWithTry(file: String) = {
      file.split("\\.").last match {
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
    }

//    @throws( classOf[java.lang.NoSuchMethodException] )
    def GetDocContent(filePath: String): String = {
      readDocWithTry(filePath) match {
        case Success(lines) => lines.mkString(" ")
        case Failure(t) => t match {
            case e : org.apache.poi.EmptyFileException => ""
            case e : IllegalArgumentException =>
              if(e.getMessage.trim == "The document is really a UNKNOWN file") ""
              else throw new IllegalArgumentException(t) {println(t.getCause.getMessage)}
            case _ => throw new Exception(t) {println(t.getCause.getMessage)}
          }
      }
    }
  }

  object Operators {

    implicit class |>[A](val a: A) extends AnyVal {
      def |>[B](op: A => B): B = op(a)
    }

//      implicit class Semigroup[A,Double](val m1: Map[A,Double]) extends AnyVal {
//        def <*>(m2: Map[A,Double]) = {
//          m2.map{ case (k,v) => k -> (v * m1.getOrElse(k,v))}
//        }
//      }

    }

  object String_Manipulation {
    def onlyDigits(s: String) = {
      var isDigit = true
      for(c <- s if !Character.isDigit(c)) isDigit = false
      isDigit
    }
  }

  object Constants {
    val ZERO = 0
    val ZEROd = 0.0
    val ONE = 1
  }

  object Formatting {

    def roundDecimals(d: Double) = {
      new java.math.BigDecimal(d).toPlainString
    }
  }
}
