package org.Fcocco01.DocumentClassifier.Test

import org.scalatest._
import org.scalamock.scalatest.MockFactory

abstract class UnitTest (component: String) extends FlatSpec with Matchers with BeforeAndAfter with BeforeAndAfterAll
  with BeforeAndAfterEach with Assertions with MockFactory {
  behavior of component
}
