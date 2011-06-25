package org.conveyorqueue.test.perf

import org.scalatest.FunSuite
import java.util.UUID
import org.conveyorqueue.node.{ConveyorQ}
import actors.Actor
import scala.util.Random._
import scala.actors.Future
import scala.actors.Futures._
import org.conveyorqueue.misc.Promise
import org.conveyorqueue.node.ConveyorMessage.{Put, Pop}


class PerfSuite extends FunSuite {

  test("throughput should be in excess of 80,000 ops s-1") {
    val tm = new TestManager()
    val result = tm.runTest
    assert( result > 80000)
  }
}

class ClientTester(conveyor: ConveyorQ[UUID], ops: Int) extends Actor {
  var i = 0
  val bytes = new Array[Byte](1000)
  nextBytes(bytes)
  def act() = {
    while (i < ops) {
      recurse
      i += 1    }
  }
  def recurse = {
    if (nextBoolean) {
      conveyor ! Put(bytes)
    } else {
      conveyor ! Pop
    }
  }
}

class TestManager() {
  val numTesters = 100
  val numOperations = 8000    //TODO investigate why when num ops is very large (e.g. 100 x 80,000) cpu utilisation drops off. actor's mailbox blown?
  val conveyor = ConveyorQ.getUnconnectedConveyorQ("testQ")
  var stat = new Promise[Double]

  def runTest: Double = {
    //start test
    conveyor.start
    conveyor.StartTestMode(this, numOperations * numTesters)
    for ( i <- 0 until numTesters) {
      val t = new ClientTester(conveyor, numOperations)
      t.start }

    return stat.get
  }
}