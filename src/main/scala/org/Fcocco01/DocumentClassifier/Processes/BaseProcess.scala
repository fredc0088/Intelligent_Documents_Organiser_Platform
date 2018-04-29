package org.Fcocco01.DocumentClassifier.Processes

import javafx.beans.property.{ReadOnlyDoubleProperty, ReadOnlyDoubleWrapper}
import javafx.beans.value.ChangeListener

class BaseProcess() {
  private val progress: ReadOnlyDoubleWrapper = new ReadOnlyDoubleWrapper()

  def getProgress: Double = progressProperty.get

  def progressProperty: ReadOnlyDoubleProperty = progress

  def setProgress(value: Double): Unit = progress.set(value)

  def setExternalHandler(listener: ChangeListener[_ >: Number]): Unit = this.progressProperty.addListener(listener)
}

