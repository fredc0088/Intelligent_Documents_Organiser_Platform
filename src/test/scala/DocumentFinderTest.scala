package org.Fcocco01.DocumentClassifier

class DocumentFinderTest(dirToTest: Traversable[String]) extends UnitTest("DocumentFinder") {

  it should "Return a List of paths(strings)" in {
    DocumentsFinder.DocumentFinder.apply(Array("C:\\Users\\USER\\odrive\\Google Drive (2)"),Array()).isInstanceOf[List[String]] shouldBe true
  }



  it should "Return the defined paths" in {

  }
}
