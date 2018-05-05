package org.Fcocco01.DocumentClassifier.Essentials

import org.apache.commons.io.IOUtils

import scala.io.Codec

package object Constants {
  val ZERO = 0
  val HALF = 0.5
  val ZERO_SEVEN = 0.7
  val ZERO_NINE = 0.9
  val ONE = 1
  val TWO = 2
  val TWO_HALF = 2.5
  val THREE = 3
  val THREE_HALF = 3.5
  val FOUR = 4
  val FOUR_HALF = 4.5
  val FIVE = 5
  val FIVE_HALF = 5.5
  val SEVEN = 7
  val NINE = 9
  val TEN = 10
  val TWENTY = 20
  val THIRTY = 30
  val FIFTY = 50
  val HUNDRED_FIFTY = 150
  val THREE_HUNDRED = 300

  val EMPTY_STRING = ""
  val SPACE = " "

  object Defaults {
    /* Loads a stopwords file content */
    val stopwords: String = org.apache.commons.io.IOUtils.toString(getClass.getClassLoader.
      getResourceAsStream("stop-word-list.txt"), Codec.ISO8859.name)
    val regexWord1Gram = "[^a-z0-9]"
    /* Errors logs folder in the HOME directory, if it  does not exist, a new one is created */
    val logFile: String = {
      val pathToHome = javax.swing.filechooser.FileSystemView.getFileSystemView.getHomeDirectory
      if(!pathToHome.listFiles.map(_.getPath.split("/").last).contains("DC_logs"))
        new java.io.File(s"$pathToHome/DC_logs").mkdirs
      pathToHome.listFiles.find(x => x.getPath.contains("DC_logs")).get.getCanonicalPath
    }
  }
}
