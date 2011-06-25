package org.conveyorqueue.test.gen

import org.conveyorqueue.test.perf.PerfSuite


object TestExecute {

    def main(args: Array[String]): Unit = {

      //functional tests for conveyor
      val cs = new ConveyorSpec
      cs.execute

      //performance test for conveyor
      val pf = new PerfSuite
      pf.execute

      sys.exit()



    }

}