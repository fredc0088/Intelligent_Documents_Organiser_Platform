package org.Fcocco01.DocumentClassifier.Processes

import org.Fcocco01.DocumentClassifier.{Core, Essentials}
import Core.DataSetMorph.{Dictionary, createVector}
import Core.Features.Ranking_Modellers._
import Core.Features.IDF.{IDFValue, simpleIdf, smootherIdf}
import Essentials.Constants.{FIVE_HALF, SEVEN}
import Essentials.Types.TypeClasses.Document
import Essentials.Types.TypeClasses.Vectors.DocumentVector
import Essentials.Util.Time.currentTimeMins

case class DataSetPreparation(weightFun: String, idfChoice: String) extends BaseProcess {
  def start(corpus: Traversable[Option[Document]]) : Traversable[DocumentVector] = {

    println("Begin data set creation")
    val time = System.nanoTime
    val vectors : Traversable[DocumentVector] = if (corpus.isEmpty) {
      Vector.empty[DocumentVector]
    } else {

      val dictionary = if (idfChoice == "No Dictionary") None else Dictionary(corpus)

      if (idfChoice != "No Dictionary") println("Dictionary created in " + currentTimeMins(time))

      setProgress(FIVE_HALF)

      val idfWeightedTerms =
        if (idfChoice == "Normal" || dictionary.isEmpty)
          None
        else idfChoice match {
          case "Idf" => Some(dictionary.getOrElse(Vector("")).par
            .map(IDFValue(_)(simpleIdf)(Option(corpus))).toVector)
          case "Smooth Idf" => Some(dictionary.getOrElse(Vector("")).par
            .map(IDFValue(_)(smootherIdf)(Option(corpus))).toVector)
        }


      val vectorFun = weightFun match {
        case "Tf" => createVector(compose_weighting_Fun(tf)(idfWeightedTerms), dictionary)
        case "Aug Tf" => createVector(compose_weighting_Fun(augmented_tf)(idfWeightedTerms), dictionary)
        case "TFLog" => createVector(compose_weighting_Fun(tfLog)(idfWeightedTerms), dictionary)
        case "Bag-Of-Words" => createVector(compose_weighting_Fun(rawBag)(idfWeightedTerms), dictionary)
      }

      if (idfChoice != "Normal" || dictionary.isEmpty) println("Terms weighted to idf in " + currentTimeMins(time))

      corpus.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toVector
    }

    println("Vectors obtained in " + currentTimeMins(time))

    println("Data set prepared")

    setProgress(SEVEN)


    val mar = vectors.toParArray.map(x => (x.id.split("Desktop").last,x.features.maxBy(_._2)._1,x.features.maxBy(_._2)._2))

    for (m <- mar) {
      println(s"For document ${m._1}\n   The most important term is ${m._2} with value ${m._3}")
      println("")
    }
    vectors
  }
}
