package org.conveyorqueue.node

import scala.{PartialFunction => PF}
import org.conveyorqueue.io.NettyMessage._
import org.jboss.netty.channel._
import org.conveyorqueue.misc.{RingConnectionChain}

/** Signaller: 2 signaller per node (clockwise and anticlockwise)
 *    maintain the ring and handle inter-node communication
 *
 *  Phase 1 overview
 *  - keeps nodes connected in a ring
 *  - accepts copy of QueuedItem from clockwise node
 *  - stores copies on the conveyor
 *  - recycles copies from front of queue to back after first checking
 *      the state of the corresponding item on the clockwise node
 *      (NB. this implies empties should be stored in Anticlockwise
 *      signaller (ac-signaller))
 *
 *  Behaviours and (any) related storage structures:
 *  - copies pushed immediately (c-signaller)
 *  - (when conveyor pops, it peeks at next and if a copy, cycles)
 *  - (c-signaller) has *Store1* (c-signaller) for copies in recycling
 *  - (ac-signaller) has *Store2* for empties
 *
 *  @author Jude Payne
 *  @version 0.1
 * 
 */

abstract class Signaller () extends RingConnectionChain {





}


