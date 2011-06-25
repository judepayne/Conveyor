package org.conveyorqueue.misc

import actors.Actor
import scala.{PartialFunction => PF}
import org.conveyorqueue.io.NettyMessage._
import org.jboss.netty.channel.ChannelStateEvent

/** ChainedActor: facilitates chaining together of (groups of)
 *  case statements arranged as traits to compose final behaviour
 *
 *  @author Jude Payne
 *  @version 0.1
 */
abstract trait ChainedActor extends Actor {

  //defines handler
  def handler: PF[Any, Unit]

  //defines default implementation of act
  def act = { react(handler) }
  //defines react method: passes handler to super.react
  override def react(h: PF[Any, Unit]): Nothing = super.react(h)

  //function that presents act as (Unit) => Unit so that it can be chained with PF's
  final def wrap(u: Unit): Unit = { act }
  //end each case statement with a call to act to make sure the whole is recursive
  final def complete(f1: PF[Any, Unit]): PF[Any, Unit] = f1 andThen wrap
  //for combining PartialFunctions ( case statements )
  final def combine(funcs: PF[Any, Unit]*) =
    { funcs.reduceLeft((f1, f2) => complete(f1) orElse complete(f2)) andThen wrap }

}


trait RingConnectionChain extends ChainedActor {

  private def connectionevent: PF[Any, Unit] = { case ChannelConnected(e)  => ringHandler(e)}
  private def disconnectionevent: PF[Any, Unit] = { case ChannelDisconnected(e) => ringHandler(e)}

  private def ringHandler(e: ChannelStateEvent): Unit = {
    /* Main logic goes here*/

  }

  def handler = combine(connectionevent, disconnectionevent)

}


trait NodeMessageChain extends ChainedActor {
  

}

