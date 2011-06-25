package org.conveyorqueue.misc

/*based on FunctionalQueue, Odersky et al
  Programming in Scala, chap. 19
  Added methods:
  dequeue, prepend, contains, isEmpty, length
 */

trait Queue[T] {
  def head: T
  def tail: Queue[T]
  def dequeue: (T, Queue[T])
  def append(x: T): Queue[T]
  def prepend(x: T): Queue[T]
  def isEmpty: Boolean
  def length: Int
  def contains(x: T): Boolean  
}

object Queue {

  def apply[T](xs: T*): Queue[T] =
    new QueueImpl[T](xs.toList, Nil)

  private class QueueImpl[T] (
    private var leading: List[T],
    private var trailing: List[T]
          ) extends Queue[T] {

    private def mirror() =
      if (leading.isEmpty) {
        while (!trailing.isEmpty) {
          leading = trailing.head :: leading
          trailing = trailing.tail
        }
      }

    def head: T = {
      mirror
      leading.head
    }

    def tail: QueueImpl[T] = {
      mirror()
      new QueueImpl(leading.tail, trailing)
    }

    def dequeue: (T, QueueImpl[T]) = {
      (head, new QueueImpl(leading.tail, trailing))
    }

    def append (x: T) = {
      new QueueImpl[T](leading, x :: trailing)
    }

    def prepend(x: T) = {
      new QueueImpl[T](x :: leading, trailing)
    }

    def contains(x: T): Boolean = {
      if (leading.contains(x) || trailing.contains(x)) true else false
    }

    def isEmpty: Boolean = {
      if (leading.isEmpty && trailing.isEmpty) true
        else false
    }

    def length: Int =
      leading.length + trailing.length
  }
}