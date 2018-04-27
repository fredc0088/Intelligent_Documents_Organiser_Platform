package org.Fcocco01.DocumentClassifier.Processes

import org.Fcocco01.DocumentClassifier.Core.DocGathering.DocumentFinder
import org.Fcocco01.DocumentClassifier.Core.DocumentDataSetMorph.{buildTokenSuite, tokenizeDocument}
import org.Fcocco01.DocumentClassifier.Core.Tokenization.{StopWords, TokenizedText}
import org.Fcocco01.DocumentClassifier.Utils
import org.Fcocco01.DocumentClassifier.Utils.Constants.{FOUR, ONE, TEN, ZERO}
import org.Fcocco01.DocumentClassifier.Utils.Types.TypeClasses.Document
import org.Fcocco01.DocumentClassifier.Utils.Util.I_O.GetDocContent
import org.Fcocco01.DocumentClassifier.Utils.Util.Time.currentTimeMins

case class CorpusPreparation(directoriesChosen: Array[String] = Array(""),
                           exclusions: Array[String] = Array(""), stopwords: Option[String], regex: Option[String])
extends BaseProcess {

  def start() : Traversable[Option[Document]] = {

    val time = System.nanoTime

    println("Begin corpus creation")

    val paths = DocumentFinder(directoriesChosen, exclusions)

    println("Documents gathered in " + currentTimeMins(time))

    if (paths.size == ZERO) {
      setProgress(TEN)
      println("No document found")
      Vector.empty[Option[Document]]
    }
    else {

      val stopWords = stopwords match {
        case Some(s) => StopWords(s)
        case None => StopWords(Utils.Constants.Defaults.stopwordPath)
      }

      val regexToUse = regex match {
        case Some(r) => r
        case None => Utils.Constants.Defaults.regexWord1Gram
      }
      val tknTool = buildTokenSuite(TokenizedText(regexToUse, stopWords))(GetDocContent)

      val tknFun = tokenizeDocument(tknTool)

      setProgress(ONE)

      val docs = paths.par.map(x => tknFun(x)).toArray

      println("Corpus created in " + currentTimeMins(time))

      setProgress(FOUR)

      docs
    }
  }
}
