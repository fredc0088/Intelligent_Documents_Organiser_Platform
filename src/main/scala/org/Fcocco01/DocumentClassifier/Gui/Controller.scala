package org.Fcocco01.DocumentClassifier.Gui

import org.Fcocco01.DocumentClassifier

import scalafx.scene.control.TextField
import scalafx.scene.control.Button
import scalafx.scene.control.ListView
import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml
import scalafx.stage.DirectoryChooser

@sfxml
class TestController(input: TextField,
                     create: Button,
                     recentInputs: ListView[String]
                    // ,dep: AnAdditionalDependency
                    ) {

  // event handlers are simple public methods:
  def onCreate(event: ActionEvent) {
    // ...
  }

  def choose(event: ActionEvent): Unit = {

  }


}
