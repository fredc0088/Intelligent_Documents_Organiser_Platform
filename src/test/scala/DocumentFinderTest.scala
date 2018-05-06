package org.Fcocco01.DocumentClassifier.Test

import java.io.File

import org.Fcocco01.DocumentClassifier.{Core, Test}
import Core.DocGathering.DocumentFinder
import Test.TestingResources.Paths._
import org.Fcocco01.DocumentClassifier.Essentials.Types.TypeClasses.DocPath

class DocumentFinderTest extends UnitTest("Core.DocGathering.DocumentFinder") {

  val testPaths = Array(testDirPath1,testDirPath2,testDirPath3)

  it should "return a List of paths" in {
    val paths = DocumentFinder.apply(testPaths)
    paths.isInstanceOf[List[DocPath]] shouldBe true
    paths.size shouldBe 7
  }

  it should "return only expected types of files" in {
    val paths = DocumentFinder(Array(testDirPath3),Array(new File(testDirPath4).getCanonicalPath))
    assert(paths.forall(x => x.path.contains(".pdf") || x.path.contains(".doc") || x.path.contains(".docx") ||
      x.path.contains(".txt") || x.path.contains(".log")))
  }

  it should "return the expected number of paths" in {
    val paths = DocumentFinder(Array(testDirPath3),Array(new File(testDirPath4).getCanonicalPath))
    paths.length shouldBe 1
  }
}
