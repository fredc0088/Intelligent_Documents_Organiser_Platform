package org.Fcocco01.DocumentClassifier.Processes

import java.io.File

import org.Fcocco01.DocumentClassifier.{Core, Essentials}
import Core.DocGathering.DocumentFinder
import Core.DataSetMorph.{buildTokenSuite, tokenizeDocument}
import Core.Tokenization.{StopWords, TokenizedText}
import Essentials.Constants.{FOUR, ONE, TEN, ZERO}
import Essentials.Types.TypeClasses.Document
import Essentials.Util.I_O.GetDocContent
import Essentials.Util.Time.currentTimeMins

case class CorpusPreparation(directoriesChosen: Array[String] = Array(""),
                           exclusions: Array[String] = Array(""), stopwords: Option[String], regex: Option[String])
extends BaseProcess {

  def start() : Traversable[Option[Document]] = {

    val time = System.nanoTime

    println("Begin corpus creation")

    val paths = DocumentFinder(directoriesChosen, exclusions)

    println("Documents gathered in " + currentTimeMins(time))


    def urlses(cl: ClassLoader): Array[java.net.URL] = cl match {
      case null => Array()
      case u: java.net.URLClassLoader => u.getURLs ++ urlses(cl.getParent)
      case _ => urlses(cl.getParent)
    }

//    val  urls = urlses(getClass.getClassLoader)
//    println(urls.filterNot(_.toString.contains("ivy")).mkString("\n"))

    println(Essentials.Constants.Defaults.stopwordPath)
    println(new File(Essentials.Constants.Defaults.stopwordPath).canRead)
    println(new File(Essentials.Constants.Defaults.stopwordPath).canExecute)
      println(new File(Essentials.Constants.Defaults.stopwordPath).getParent)
      println(new File(Essentials.Constants.Defaults.stopwordPath).isFile)


    if (paths.size == ZERO) {
      setProgress(TEN)
      println("No document found")
      Vector.empty[Option[Document]]
    }
    else {

      val stopWords = stopwords match {
        case Some(s) => StopWords(s)
        case None => StopWords(Essentials.Constants.Defaults.stopwordPath)
      }


      val regexToUse = regex match {
        case Some(r) => r
        case None => Essentials.Constants.Defaults.regexWord1Gram
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
