package org.conveyorqueue.node

import collection.mutable.{Queue, LinkedHashMap}
import org.conveyorqueue.node.ConveyorMessage._
import org.conveyorqueue.misc.{Queue => JQ}

//interface for FIFO Store
abstract trait FifoStore[ID <: Any] {
  protected def pushItem(id: ID, item: QueuedItem)
  protected def popItem: Option[QueuedItem]
  protected def peek(): Option[QueuedItem]
  protected def checkFor(ids: List[ID]): CheckResult[ID]
  protected def count(): Int
}

//uses a queue and a map
trait FifoMapNQueue[ID] extends FifoStore[ID] {
  private val q = new Queue[ID]() //queue to hold reference to messages/ copies and empties
  private val map = new LinkedHashMap[ID, QueuedItem]() //holds id to QueueEntry mapping.

  //put item on queue and in map
  def pushItem(id: ID, item: QueuedItem) = {
    map+=( id -> item )
    q.enqueue(id)
  }

  //pop item from front of queue, return associated value from map
  def popItem: Option[QueuedItem] = {
    if (q.length > 0) {
      try {
      val id = q.front
      val item = map.get(id)
      item match {
        case Some(x) =>  q.dequeue;  map -= id;  item
        case None => None
      }
      } catch {
        case ex: Exception => //strange problem. TODO log it
      }
    }
  None
  }

  def peek: Option[QueuedItem] = {
    val item = map.get(q.front)
    item match {
      case Some(x) => item
      case None => None
    }
  }

  //check if items in Array of T are in a Queue of T & return those present, not present
  def checkFor(_ids: List[ID]): CheckResult[ID] = {
    val idspresent = _ids.foldLeft(List[ID]())((b,a) =>
      if (q contains a) a :: b  else b )
    CheckResult(idspresent, _ids -- idspresent)
  }

  //get count of items
  def count: Int = q.length
}

//uses just a linkedhashmap. ~10% faster than FifoMapNQueue
trait FifoMap[ID] extends FifoStore[ID] {
   private var map = new LinkedHashMap[ID, QueuedItem]() //holds id to QueueEntry mapping.

   def pushItem(id: ID, item: QueuedItem) = map+=(id -> item)

   def pushItemAtHead(id: ID, item: QueuedItem) = {
     val tmpMap = new LinkedHashMap[ID, QueuedItem]()
     tmpMap += (id -> item)
     map = map ++ tmpMap
   }

   def popItem: Option[QueuedItem] = {
      map.headOption match {
       case Some(x) => {
          map-=(x _1)
          Some(x _2)
       }
       case None => None
     }
   }

   def peek(): Option[QueuedItem] = {
      map.headOption match {
       case Some(x) => {
          Some(x _2)
       }
       case None => None
     }
   }

  def checkFor(_ids: List[ID]): CheckResult[ID] = {
    val idspresent = _ids.foldLeft(List[ID]())((b,a) =>
      if (map contains a) a :: b  else b )
    CheckResult(idspresent, _ids -- idspresent)
  }

  def count: Int = map.size
}

import collection.immutable.{Queue => iQueue}

//based on two immutable queues ~3-4% faster than FifoMap
/*trait IQueue[ID] extends FifoStore[ID] {
  private var ids = new iQueue[ID](Nil, Nil)
  private var items = new iQueue[QueuedItem](Nil, Nil)

  def pushItem(id: ID, item: QueuedItem) = {
    ids = ids.enqueue(id)
    items = items.enqueue(item)
  }
  def pushItemAtHead(id: ID, item: QueuedItem) = {
    ids = ids.:+(id)
    items = items.:+(item)
  }
  def popItem: Option[QueuedItem] = {
    ids.isEmpty match {
      case true => None
      case false =>
        ids = ids.dequeue _2
        val t = items.dequeue
        items = t._2
        Some(t._1)
    }
  }
  def peek(): Option[QueuedItem] = {
    ids.isEmpty match {
      case true => None
      case false =>
        Some(items.front)
    }
  }

  def checkFor(_ids: List[ID]): CheckResult[ID] = {
    val idspresent = _ids.foldLeft(List[ID]())((b,a) =>
      if (ids contains a) a :: b  else b )
    CheckResult(idspresent, _ids -- idspresent)
  }

  def count(): Int = ids.length

}*/

//fast! based on own Odersky's queue implementation
trait JQueue[ID] extends FifoStore[ID] {
  private var ids = JQ[ID]()
  private var items = JQ[QueuedItem]()

  def pushItem(id: ID, item: QueuedItem) = {
    ids = ids.append(id)
    items = items.append(item)
  }

  def pushItemAtHead(id: ID, item: QueuedItem) = {
    val before = ids
    ids = ids.prepend(id)
    val after = ids
    items = items.prepend(item)
  }

  def popItem: Option[QueuedItem] = {
    ids.isEmpty match {
      case true => None
      case false =>
        ids = ids.dequeue._2
        val t = items.dequeue
        items = t._2
        Some(t._1)
    }
  }

  def peek(): Option[QueuedItem] = {
    ids.isEmpty match {
      case true => None
      case false =>
        Some(items.head)
    }
  }

  def checkFor(_ids: List[ID]): CheckResult[ID] = {
    val idspresent = _ids.foldLeft(List[ID]())((b,a) =>
      if (ids contains a) a :: b  else b )
    CheckResult(idspresent, _ids -- idspresent)
  }

  def count(): Int = ids.length


}

