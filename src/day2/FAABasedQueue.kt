package day2

import day1.*
import kotlinx.atomicfu.*

// TODO: Copy the code from `FAABasedQueueSimplified`
// TODO: and implement the infinite array on a linked list
// TODO: of fixed-size segments.
class FAABasedQueue<E> : Queue<E> {
    private val head: AtomicRef<Node>
    private val tail: AtomicRef<Node>

    private val enqIdx = atomic(0)
    private val deqIdx = atomic(0)

    init {
        val dummy = Node(-1, 0)
        head = atomic(dummy)
        tail = atomic(dummy)
    }

    override fun enqueue(element: E) {
        while (true) {
            val curTail = tail.value
            val i = enqIdx.getAndIncrement()
            val node = findSegment(curTail, i / CHUNK_SIZE)
            if (node.id > curTail.id) tail.compareAndSet(curTail, node)
            if (node.array[i % CHUNK_SIZE].compareAndSet(null, element)) return
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        while (true) {
            // Is this queue empty?
            if (deqIdx.value >= enqIdx.value) return null
            val curHead = head.value
            val i = deqIdx.getAndIncrement()
            val node = findSegment(curHead, i / CHUNK_SIZE)
            if (node.id > curHead.id) head.compareAndSet(curHead, node)
            if (node.array[i % CHUNK_SIZE].compareAndSet(null, POISONED)) continue
            return node.array[i % CHUNK_SIZE].value as E
        }
    }

    private fun findSegment(start: Node, id: Int): Node {
        var node = start

        require(node.id <= id) { "ur late" }

        while (node.id != id) {
            val curNodeNext = node.next.value

            if (curNodeNext == null) {
                val newNode = Node(node.id + 1, CHUNK_SIZE)

                 if (node.next.compareAndSet(null, newNode)) {
                     node = newNode
                 }
            } else {
                node = curNodeNext
            }
        }

        return node
    }

    private class Node(val id: Int, size: Int) {
        val array = atomicArrayOfNulls<Any?>(size)
        val next = atomic<Node?>(null)
    }
}

private const val CHUNK_SIZE = 2
private val POISONED = Any()
