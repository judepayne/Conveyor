package org.conveyorqueue.test.perf


trait PerformanceMeasure {
  //Performance testing functions
  var numTests = 0
  var testMode = false
  var testsDone = 0
  var sT: Long = 0
  var testManager: TestManager = _

  def StartTestMode(manager: TestManager, num: Int) = {
    testMode = true
    numTests = num
    testManager = manager
    sT = System.nanoTime
    println("starting test...")
  }

  def StopTestMode = {
    testMode = false
    testsDone = 0
    sT = 0
  }

  def printStats = {
    val dur: Double = System.nanoTime - sT
    val rate: Double = numTests/(dur/1000000000)
    testManager.stat.set(rate)
    println("Duration: " + dur/1000000000 + " s     Rate: " + rate + " s-1")
  }

  def testDone() = {
     testsDone += 1
     if (CheckTestsFinished) {
       printStats
       testMode = false
     }
  }

  private def CheckTestsFinished(): Boolean = {
    if (testsDone == numTests) true else false
  }

}