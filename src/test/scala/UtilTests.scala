package org.Fcocco01.DocumentClassifier.Test

import org.Fcocco01.DocumentClassifier.Essentials.Util._
import org.scalatest.Suites

class UtilTests extends Suites(new I_OTest, new OperatorsTest) {


}


class I_OTest extends UnitTest("I_O") {
  "" should "" in {

  }
}

class OperatorsTest extends UnitTest("Operators") {
  import Operators.|>
  "Pipe operator |>" should "successfully pipe function a to function b" in {
    val sumPlus20 = (v: Int, i: Int) => v + i + 20
    val power = (v: Int) => v * v
    assertResult(2500) { sumPlus20(10, 20) |> power }
  }
}