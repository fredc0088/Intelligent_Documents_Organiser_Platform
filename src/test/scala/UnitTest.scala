package org.Fcocco01.DocumentClassifier

import org.scalatest._

abstract class UnitTest (component: String) extends FlatSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll  with BeforeAndAfterEach {
  behavior of component
}
