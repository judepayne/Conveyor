package org.conveyorqueue.node


object ItemType extends Enumeration {
  type ItemType = Value
  val message, copy, empty = Value
}
import ItemType._

//class to represent queued message
class QueuedItem private(val message: Any, val itemtype: ItemType) {

  def recreateItem = { new QueuedItem(message, ItemType.message)}
  def getCopy = { new QueuedItem(message, ItemType.copy) }
  def getEmpty = { new QueuedItem(message, ItemType.empty) }
}

object QueuedItem {
  def createItem(message: Any) = { new QueuedItem(message, ItemType.message) }
}

