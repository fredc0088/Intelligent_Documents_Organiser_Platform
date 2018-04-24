package org.Fcocco01.DocumentClassifier.Test

import java.io.File

import org.Fcocco01.DocumentClassifier._
import Core.DocGathering.DocumentFinder
import Test.TestingResources.Paths.{testDirPath1, testDirPath2, testDirPath3, testDirPath4}
class DocumentFinderTest extends UnitTest("DocGathering.DocumentFinder") {

  val testPaths = Array(testDirPath1,testDirPath2,testDirPath3)

  it should "Return a List of paths" in {
    val paths = DocumentFinder.apply(testPaths)
    paths.isInstanceOf[List[String]] shouldBe true
    paths.size shouldBe 7
  }

  it should "Return only expected types of files" in {
    val paths = DocumentFinder(Array(testDirPath3),Array(new File(testDirPath4).getCanonicalPath))
    assert(paths.forall(x => x.contains(".pdf") || x.contains(".doc") || x.contains(".docx") || x.contains(".txt")
    || x.contains(".log")))
  }

  it should "Return paths" in {
    val paths = DocumentFinder(Array(testDirPath3),Array(new File(testDirPath4).getCanonicalPath))
    paths.length shouldBe 1
  }
}
