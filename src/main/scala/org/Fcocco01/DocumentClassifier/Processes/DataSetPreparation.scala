package org.Fcocco01.DocumentClassifier.Processes

import org.Fcocco01.DocumentClassifier.Core.DocumentDataSetMorph.{Dictionary, createVector}
import org.Fcocco01.DocumentClassifier.Core.Features.Bag_Of_Words_Models._
import org.Fcocco01.DocumentClassifier.Core.Features.IDF.{IDFValue, simpleIdf, smootherIdf}
import org.Fcocco01.DocumentClassifier.Utils.Constants.{FIVE_HALF, SEVEN}
import org.Fcocco01.DocumentClassifier.Utils.Types.TypeClasses.Document
import org.Fcocco01.DocumentClassifier.Utils.Types.TypeClasses.Vectors.DocumentVector
import org.Fcocco01.DocumentClassifier.Utils.Util.Time.currentTimeMins

case class DataSetPreparation(weightFun: String, idfChoice: String) extends BaseProcess {
  def start(corpus: Traversable[Option[Document]]) : Traversable[DocumentVector] = {

    println("Begin data set creation")
    val time = System.nanoTime
    val vectors : Traversable[DocumentVector] = corpus.isEmpty match {
      case true => Vector.empty[DocumentVector]
      case false => {
        val dictionary = if (idfChoice == "No Dictionary") None else Dictionary(corpus)

        if(idfChoice != "No Dictionary") println("Dictionary created in " + currentTimeMins(time))

        setProgress(FIVE_HALF)

        val idfWeightedTerms =
          if(idfChoice == "Normal" || dictionary == None)
            None
          else idfChoice match {
            case "Idf" => Some(dictionary.getOrElse(Vector("")).par
              .map(IDFValue(_)(simpleIdf)((Option(corpus)))).toVector)
            case "Smooth Idf" => Some(dictionary.getOrElse(Vector("")).par
              .map(IDFValue(_)(smootherIdf)((Option(corpus)))).toVector)
          }


        val vectorFun = weightFun match {
          case "Tf" => createVector(compose_weighting_Fun(tf(_,_))(idfWeightedTerms),dictionary)
          case "wdf" => createVector(compose_weighting_Fun(wdf(_,_))(idfWeightedTerms),dictionary)
          case "TFLog" => createVector(compose_weighting_Fun(tfLog(_,_))(idfWeightedTerms),dictionary)
          case "Bag-Of-Words" => createVector(compose_weighting_Fun(rawBag(_,_))(idfWeightedTerms),dictionary)
        }

        if(idfChoice != "Normal" || dictionary == None) println("Terms weighted to idf in " + currentTimeMins(time))

        corpus.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toVector
      }
    }

    println("Vectors obtained in " + currentTimeMins(time))

    println("Data set prepared")

    setProgress(SEVEN)

    vectors
  }
}