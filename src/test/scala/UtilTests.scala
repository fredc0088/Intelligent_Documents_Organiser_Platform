package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.Essentials.Util._
import org.scalatest.Suites
import TestingResources.Paths._
import org.Fcocco01.DocumentClassifier.Essentials.Types.TypeClasses.DocPath

class UtilTests extends Suites(new I_OTest, new OperatorsTest, new String_ManipulationTest)

class I_OTest extends UnitTest("Essentials.Util.I_O") {
  import I_O._
  "GetDocContent" should "return the content of a file" in {
    GetDocContent(DocPath(testPath8)).trim should be("HiMissing")
    GetDocContent(DocPath(testPath3)).trim should be("test")
    GetDocContent(DocPath(testPath11)).trim should be("test")
    GetDocContent(DocPath(testPath12)).trim should be("test")
  }
  "GetDocContent" should "return an empty string if fails" in {
    GetDocContent(DocPath("")).trim should be("")
  }
}

class OperatorsTest extends UnitTest("Essentials.Util.Operators") {

  import Operators._

  "Pipe operator |>" should "successfully pipe function a to function b" in {
    val sumPlus20 = (v: Int, i: Int) => v + i + 20
    val power = (v: Int) => v * v
    assertResult(2500) {
      sumPlus20(10, 20) |> power
    }
  }
}

class String_ManipulationTest
  extends UnitTest("Essentials.Util.String_Manipulation") {
  import String_Manipulation._
  "OnlyDigits" should "return false if a string is mixed" in {
    onlyDigits("24a2343") should be(false)
  }
  "OnlyDigits" should "return false if a string is null" in {
    onlyDigits(null) should be(false)
  }
  "OnlyDigits" should "return true if a string is empty" in {
    onlyDigits("") should be(true)
  }
}