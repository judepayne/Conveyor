package org.conveyorqueue.io

import actors.Actor
import org.jboss.netty.channel._
import org.conveyorqueue.io.NettyMessage._


abstract sealed class NettyMessage
object NettyMessage {
  case class MessageReceived(e: MessageEvent) extends NettyMessage
  case class ChannelConnected(e: ChannelStateEvent) extends NettyMessage
  case class ChannelDisconnected(e: ChannelStateEvent)  extends NettyMessage
}

class NettyActorBridge (val actorFactory: (Channel) => Actor) extends SimpleChannelHandler {

  private var actorHandler: Actor = null

  def getActor: Actor = actorHandler

  def send(message: NettyMessage) = {
    actorHandler ! message
  }

  override def messageReceived(ctx: ChannelHandlerContext, e:  MessageEvent) = send(MessageReceived(e))

  override def channelConnected(ctx: ChannelHandlerContext, e:  ChannelStateEvent) = {
    actorHandler = actorFactory(ctx.getChannel)
    send(ChannelConnected(e))
  }

  override def channelDisconnected(ctx: ChannelHandlerContext, e: ChannelStateEvent) = {
    actorHandler = actorFactory(ctx.getChannel)
    send(ChannelDisconnected(e))
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, e: ExceptionEvent) = {
    //TODO exception logic
  }

}