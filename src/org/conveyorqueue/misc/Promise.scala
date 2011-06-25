package org.conveyorqueue.misc


//A Promise is a future value which will be provided by another thread

class Promise[T] {

  private var isDefined: Boolean = false
  private var value: T = _;

  def apply() = get

  def set(x: T) = synchronized {
    value = x
    isDefined = true
    notifyAll()
  }

  def unset = synchronized {
    isDefined = true
    notifyAll()
  }

  def get: T = synchronized {
    if (!isDefined) wait()
    value
  }

}