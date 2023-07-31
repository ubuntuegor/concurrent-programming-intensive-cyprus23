package day1

import kotlinx.atomicfu.*

class TreiberStack<E> : Stack<E> {
    // Initially, the stack is empty.
    private val top = atomic<Node<E>?>(null)

    override fun push(element: E) {
        // TODO: Make me linearizable!
        // TODO: Update `top` via Compare-and-Set,
        // TODO: restarting the operation on CAS failure.
        while (true) {
            val curTop = top.value
            val newTop = Node(element, curTop)
            if (top.compareAndSet(curTop, newTop)) {
                break
            }
        }
    }

    override fun pop(): E? {
        // TODO: Make me linearizable!
        // TODO: Update `top` via Compare-and-Set,
        // TODO: restarting the operation on CAS failure.
        while (true) {
            val curTop = top.value ?: return null
            if (!top.compareAndSet(curTop, curTop.next.value)) {
                continue
            }
            return curTop.element
        }
    }

    private class Node<E>(
        val element: E,
        next: Node<E>?
    ) {
        val next = atomic(next)
    }
}