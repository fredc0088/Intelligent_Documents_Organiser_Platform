//package org.Fcocco01.DocumentClassifier.Visualisation
//
//import javafx.scene.control.Button
//import javafx.scene.control.Label
//import javafx.scene.control.ListCell
//import javafx.scene.layout.HBox
//import javafx.scene.layout.Pane
//import javafx.scene.layout.Priority
//import javafx.event.ActionEvent
//
//
//class XCell() extends ListCell[String] {
//  hbox.getChildren.addAll(label, pane, button)
//  HBox.setHgrow(pane, Priority.ALWAYS)
//  button.setOnAction((event: ActionEvent) => getListView.getItems.remove(getItem))
//  val hbox = new HBox
//  val label = new Label("")
//  val pane = new Pane
//  val button = new Button("Del")
//
//  override protected def updateItem(item: String, empty: Boolean): Unit = {
//    super.updateItem(item, empty)
//    setText(null)
//    setGraphic(null)
//    if (item != null && !empty) {
//      label.setText(item)
//      setGraphic(hbox)
//    }
//  }
//}
