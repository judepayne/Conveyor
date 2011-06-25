
package org.conveyorqueue.node

object Mode extends Enumeration {

  /* Represents the six modes a node can operate in, specified at start-up;
      1. Unconnected: a node makes no connection to any other node
      2. Unconnected_Persisted: " & messages are persisted to disk against failure
      3. Ring: nodes connect to form a ring but messages are not moved around the ring
      4. Ring_Persisted: " & messages are persisted to disk against failure
      5. Conveyor: nodes form a ring, messages are cycled round the ring to prevent scenarios
              where consumers could be starved of messages
      6. Conveyor_Persisted: " & messages are persisted to disk against failure
   */
  type Mode = Value
  val Unconnected, Unconnected_Persisted, Ring, Ring_Persisted, Conveyor, Conveyor_Persisted = Value

}