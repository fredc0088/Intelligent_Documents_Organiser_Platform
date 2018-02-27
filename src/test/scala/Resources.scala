package org.Fcocco01.DocumentClassifier

import org.Fcocco01.DocumentClassifier.Token.Tokenizer.StopWords

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

  val stopWords = StopWords("./src/main/resources/stop-word-list.txt")
}
