package org.Fcocco01.DocumentClassifier.Visualisation

import javafx.event.ActionEvent
import javafx.scene.layout.{HBox, Priority}
import javafx.scene.{control => jfxsc, layout => jfxsl}

class XCell() extends jfxsc.ListCell[String] {

  var hbox: jfxsl.HBox = new jfxsl.HBox
  var label: jfxsc.Label = new jfxsc.Label
  var pane: jfxsl.Pane = new jfxsl.Pane
  var button: jfxsc.Button = new jfxsc.Button("Del")

  hbox.getChildren.addAll(label, pane, button)
  HBox.setHgrow(pane, Priority.ALWAYS)
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