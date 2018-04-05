package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.Core._
import TokenPackage.Tokenizer.StopWords

object TestingResources {

  object Regexes {
    val words1gram = "[^a-z0-9]"
  }
  val demoDocxMock=
    ""
  val testPath1 =
    "./src/test/resources/1/1.2/Java - Generics by Oracle.docx"
  val testPath2 =
    "./src/test/resources/1/1.1/demo.docx"
  val testPath3 =
    "./src/test/resources/3/3.2/test.docx"
  val testPath4 =
    "./src/test/resources/1/1.2/clustering.pdf"
  val testPath5 =
    "./src/test/resources/3/3.1/SampleDOCFile_100kb.doc"
  val testPath6 =
    "./src/test/resources/3/3.1/3.1.1/TestWordDoc.doc"
  val testPath7 =
    "./src/test/resources/1/Dfr.doc"
  val testPath8 =
    "./src/test/resources/2/a.docx"

  val stopWords = StopWords("./src/main/resources/stop-word-list.txt")
}
