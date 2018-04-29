package org.Fcocco01.DocumentClassifier.Essentials

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

  object Defaults {
    val stopwordPath = "./src/main/resources/stop-word-list.txt"
    val regexWord1Gram = "[^a-z0-9]"
  }
}
