package org.Fcocco01.DocumentClassifier.Visualisation.Gui

import javafx.event.ActionEvent
import javafx.scene.{control => jfxsc, layout => jfxsl}

class XCell() extends jfxsc.ListCell[String] {

  //noinspection VarCouldBeVal
  var hbox: jfxsl.HBox = new jfxsl.HBox
  //noinspection VarCouldBeVal
  var label: jfxsc.Label = new jfxsc.Label
  //noinspection VarCouldBeVal
  var pane: jfxsl.Pane = new jfxsl.Pane
  //noinspection VarCouldBeVal
  var button: jfxsc.Button = new jfxsc.Button("Del")

  hbox.getChildren.addAll(button, pane, label)
//  HBox.setHgrow(pane, Priority.ALWAYS)
  button.setOnAction((event: ActionEvent) => getListView.getItems.remove(getItem))

  override protected def updateItem(item: String, empty: Boolean): Unit = {
    super.updateItem(item, empty)
    setText(null)
    setGraphic(null)
    if (item != null && !empty) {
      label.setText(item)
      setGraphic(hbox)
    }
  }
}