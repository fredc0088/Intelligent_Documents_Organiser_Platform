package org.Fcocco01.DocumentClassifier

import Utils._
import Util.I_O.GetDocContent
import Util.Time.currentTimeMins
import Types.Token
import Types.TypeClasses._
import Core._
import Clustering.Similarity.cosine
import Clustering.HierarchicalClustering._
import DocGathering.DocumentFinder
import TokenPackage.Tokenizer.{StopWords, TokenizedText}
import Classify.{Dictionary, buildTokenSuite, createVector, tokenizeDocument}
import Analysis.{IDF, ModelFunctions}
import ModelFunctions.tfidf
import Clustering.DVector
import Visualisation.{HierarchicalGraphic, HierarchicalGraphic_Test}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage


object OnlyForTesting_ToBeRemoved extends JFXApp{


    object Test {

      def get() : Cluster  = {
        object TestingResources {

          object Regexes {
            val words1gram = "[^a-z0-9]"
          }

          val demoDocxMock =
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
          val testPath9 =
            "./Notes/Vectorisation and tfidf.docx"
          val testPath10 =
            "./Notes/Vector space representation and similarity.docx"
          val testPath11 =
            "./Notes/Tf.docx"
          val testPath12 =
            "./Notes/Term frequency and weighting.docx"
          val testPath13 =
            "./Notes/Note On Developing.docx"


        }

        import TestingResources._
        import Regexes.words1gram


          val time = System.nanoTime

          println("Start initialisation after " + currentTimeMins(time))

          val tests = DocumentFinder(Array(
//            "./src"
//            ,
            "./src/test/resources/3/3.2"
            ,"./src/test/resources/1/1.3"
            , "C:/Users/USER/Documents/Projects/Git_Repos/Document_Clusterizer_Notes"
//            ,"C:/Users/USER/odrive"
//            ,"C:/Users/USER/Documents/Important docs"
//            ,"C:/Users/USER/Desktop"
//            //      ,"C:/Users/USER"
//                      ,"C:/Users/USER/Downloads"
          ), Array(
            "./src/main/resources"
          )
          )

          val stopWords = StopWords("./src/main/resources/stop-word-list.txt")

          val tknTool = buildTokenSuite(TokenizedText(words1gram, stopWords))(GetDocContent)

          val tknFun = tokenizeDocument(tknTool)

          implicit val docs = tests.par.map(x => tknFun(x)).toArray

          println("Documents tokenised in " + currentTimeMins(time))

          val dictionary = Dictionary(docs)

          println("Dictionary created in " + currentTimeMins(time))

          val idfWeightedTerms = dictionary.par.map(IDF.IDFValue(_)(Option(docs.map(_.getOrElse(Document( "", Vector.empty[Token])))))).toVector

          println("Terms weighted to idf in " + currentTimeMins(time))

          val tfidfFun = tfidf(idfWeightedTerms)

          val vectorFun = createVector(tfidfFun)(Some(dictionary))

          val vectors = docs.par.map(x => vectorFun(x)).filterNot(_.isEmpty).toArray

          println("Vectors obtained in " + currentTimeMins(time))

          println("Clustering after  " + currentTimeMins(time))
          val m = createSimMatrix(vectors, cosine)
          val te = (x: Seq[DVector]) => x.map(SingleCluster(_)).toList
          val o = HAC(m, te, Single_Link, vectors: _*)
          println("Finished after " + currentTimeMins(time))
          o.vectors.map(_.id).foreach(println)
          o
      }
    }
  stage = new PrimaryStage
  val o : Cluster= Test.get()
  val newStage = HierarchicalGraphic_Test.Tree(o)(stage)
  newStage.show()


}
