package org.conveyorqueue.node

import scala.actors._
import java.util.UUID
import org.conveyorqueue.test.perf.PerformanceMeasure
import org.conveyorqueue.node.ConveyorMessage._
import org.conveyorqueue.node.Mode._

sealed trait ConveyorMessage
object ConveyorMessage {
  case object Pop extends ConveyorMessage
  case class  Put(job: Any) extends ConveyorMessage
  case class  PutHead(job: Any) extends ConveyorMessage
  case object Peek extends ConveyorMessage
  case class  CheckIn[T](ids: List[T])
  case class  CheckResult[T](idspresent: List[T], idesnotpresent: List[T]) extends ConveyorMessage
  case object GetQueueLength extends ConveyorMessage
  case class  Count(n: Int) extends ConveyorMessage
  case object Success extends ConveyorMessage
  case object Fail extends ConveyorMessage
  case object Nada extends ConveyorMessage
  case class  Item(item: Any)
}


class ConveyorQ [ID <: AnyRef] private (val name: String, val mode: Mode, idGenerator: () => ID) extends Actor
        with PerformanceMeasure
        with JQueue[ID] {

  /* startup logic goes here */

  def act() = {
    def recurse: Unit = {
      react {
         //client request for queued item. pop the top off the queue
        case Pop => val entry = popItem
          entry match {
            case Some(x) => reply(Item(x.message)) //TODO consider changing this
            case None => reply(Nada)
          }
            if(testMode) testDone //test mode function
          recurse

        //client request for an item to be stored
        case Put(message: Any) =>
          pushItem(idGenerator(), QueuedItem.createItem(message))
          reply(Success)
            if(testMode) testDone //test mode function
          recurse

        //client request for item to be stored at head of the Q
        case PutHead(message: Any) =>
          pushItemAtHead(idGenerator(), QueuedItem.createItem(message))
          reply(Success)
            if(testMode) testDone //test mode function
          recurse

        //client request to peek at the top item
        case Peek => val entry = peek
          entry match {
            case Some(x) => {
              reply(Item(x.message))
            }
            case None => reply(Nada)
          }
          recurse

        //client request for queue length
        case GetQueueLength =>
          reply(Count(count))
          recurse

        //client request to see which of set of ids are present
        case i: CheckIn[ID] =>
          reply(checkFor(i.ids))
          recurse
        }
      }
    recurse
  }
}

object ConveyorQ {
  def getUnconnectedConveyorQ(queueName: String) = { new ConveyorQ[UUID](queueName, Unconnected, () => UUID.randomUUID) }

}