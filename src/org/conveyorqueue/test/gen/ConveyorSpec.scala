package org.conveyorqueue.test.gen

import org.scalatest.{GivenWhenThen, FeatureSpec}
import scala.util.Random._
import org.conveyorqueue.node.ConveyorMessage._
import org.conveyorqueue.node._
import collection.mutable.Queue
import collection.immutable.{Queue => IQueue}

class ConveyorSpec extends FeatureSpec with GivenWhenThen {

  //set up values used in the tests
  val timeout: Long = 200
  val bytes1 = new Array[Byte](100)
  val bytes2 = new Array[Byte](500)
  val bytes3 = new Array[Byte](2000)
  val bytes = new Array[Array[Byte]](25,1000)
  val morebytes = new Array[Array[Byte]](25,100)

  nextBytes(bytes1); nextBytes(bytes2); nextBytes(bytes3)
  bytes.foreach( n => nextBytes(n) )
  morebytes.foreach(n => nextBytes(n))

  val bytesUsed = bytes3

  feature("The user can peek at and pop a message off conveyor queue") {

    scenario("peek and pop on an empty queue") {

      given("an empty queue")
      val emptyConveyor = ConveyorQ.getUnconnectedConveyorQ("test").start

      when ("when peek and pop are invoked")
      val peek = emptyConveyor !? (timeout, Peek)
      val pop = emptyConveyor !? (timeout, Pop)

      then("Conveyor replies with Nothing for a peek")
      assert(peek === Some(Nada))

      and("Conveyor replies with Nothing for a pop")
      assert(pop === Some(Nada))
    }

    scenario("peek and pop on a queue with one message") {

      given("a queue with one message on it")
      val conveyor = ConveyorQ.getUnconnectedConveyorQ("test").start
      conveyor !? (timeout, Put(bytesUsed))

      when("when peek and pop are invoked")
      val peek = conveyor !? (timeout, Peek)
      val pop = conveyor !? (timeout, Pop)

      then("peek should return the message put on the queue within the timeout period")
      assert(peek === Some(Item(bytesUsed)))

      and("pop should return the same as peek within the timeout period")
      assert(pop === Some(Item(bytesUsed)))

      and("the queue should now be empty")
      val i = conveyor !? (GetQueueLength)
      assert(i === Count(0))
    }

    scenario("put 25 messages on conveyor queue, pop them off checking FIFO order") {
      given("an empty queue which then has 25 items put on it")
      val conveyor = ConveyorQ.getUnconnectedConveyorQ("test").start
      val storequeue = new Queue[Array[Byte]]()
      bytes.foreach( n => { conveyor !? Put(n); storequeue.enqueue(n) } )

      when("25 pops are performed on the queue")
      then("items read off should match the items put on (in the right order)")
      for (i <- 1 to 25) {
        val pop = conveyor !? Pop
        assert( pop === Item(storequeue.dequeue))
      }

      and("queue should now be empty")
      val i = conveyor !? (GetQueueLength)
      assert(i === Count(0))
    }
  }
    scenario("push at both the tail and the head of the queue") {
      given("an empty queue")
      val conveyor = ConveyorQ.getUnconnectedConveyorQ("test").start
      var storequeue : List[Array[Byte]] = Nil

      when("a number of messages are pushed randomly to either the head or tail of the queue")
      bytes.foreach( n => {
        nextBoolean match {
          case true =>
            conveyor !? Put(n)
            storequeue = storequeue.reverse
            storequeue = storequeue.::(n)
            storequeue = storequeue.reverse
          case false =>
            conveyor !? PutHead(n)
            storequeue = storequeue.::(n)
        }
      })

      then("retrieved. their order is as expected")
      for (i <- 1 to 25) {
        val pop = conveyor !? Pop
        val item = Item(storequeue.head)
        storequeue = storequeue.tail
        assert( pop === item)
      }
      
      and("queue should now be empty")
      val i = conveyor !? (GetQueueLength)
      assert(i === Count(0))
    }
}
