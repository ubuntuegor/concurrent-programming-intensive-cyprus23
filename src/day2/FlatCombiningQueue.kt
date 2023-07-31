package day2

import day1.*
import kotlinx.atomicfu.*
import java.util.concurrent.*

class FlatCombiningQueue<E> : Queue<E> {
    private val queue = ArrayDeque<E>() // sequential queue
    private val combinerLock = atomic(false) // unlocked initially
    private val tasksForCombiner = atomicArrayOfNulls<Any?>(TASKS_FOR_COMBINER_SIZE * 2)

    private fun tryLock() = combinerLock.compareAndSet(false, true)
    private fun unlock() {
        combinerLock.value = false
    }

    private fun runCombiner() {
        for (i in 0 until TASKS_FOR_COMBINER_SIZE) {
            val task = tasksForCombiner[i * 2].value

            if (task == null || task == PROCESSED) {
                continue
            }

            when (task) {
                DEQUE_TASK -> {
                    val result = queue.removeFirstOrNull()
                    tasksForCombiner[i * 2 + 1].value = result
                }

                else -> {
                    queue.addLast(task as E)
                }
            }

            tasksForCombiner[i * 2].value = PROCESSED
        }
    }

    override fun enqueue(element: E) {
        // TODO: Make this code thread-safe using the flat-combining technique.
        // TODO: 1.  Try to become a combiner by
        // TODO:     changing `combinerLock` from `false` (unlocked) to `true` (locked).
        // TODO: 2a. On success, apply this operation and help others by traversing
        // TODO:     `tasksForCombiner`, performing the announced operations, and
        // TODO:      updating the corresponding cells to `PROCESSED`.
        // TODO: 2b. If the lock is already acquired, announce this operation in
        // TODO:     `tasksForCombiner` by replacing a random cell state from
        // TODO:      `null` to the element. Wait until either the cell state
        // TODO:      updates to `PROCESSED` (do not forget to clean it in this case),
        // TODO:      or `combinerLock` becomes available to acquire.

        if (tryLock()) {
            queue.addLast(element)
            runCombiner()
            unlock()
        } else {
            var i = randomTaskIndex()

            while (!tasksForCombiner[i * 2].compareAndSet(null, element)) {
                i = randomTaskIndex()
            }

            while (true) {
                if (tasksForCombiner[i * 2].compareAndSet(PROCESSED, null)) {
                    return
                } else if (tryLock()) {
                    runCombiner()
                    unlock()
                } else {
                    Thread.onSpinWait()
                }
            }
        }
    }

    override fun dequeue(): E? {
        // TODO: Make this code thread-safe using the flat-combining technique.
        // TODO: 1.  Try to become a combiner by
        // TODO:     changing `combinerLock` from `false` (unlocked) to `true` (locked).
        // TODO: 2a. On success, apply this operation and help others by traversing
        // TODO:     `tasksForCombiner`, performing the announced operations, and
        // TODO:      updating the corresponding cells to `PROCESSED`.
        // TODO: 2b. If the lock is already acquired, announce this operation in
        // TODO:     `tasksForCombiner` by replacing a random cell state from
        // TODO:      `null` to `DEQUE_TASK`. Wait until either the cell state
        // TODO:      updates to `PROCESSED` (do not forget to clean it in this case),
        // TODO:      or `combinerLock` becomes available to acquire.
        if (tryLock()) {
            val result = queue.removeFirstOrNull()
            runCombiner()
            unlock()
            return result
        } else {
            var i = randomTaskIndex()

            while (!tasksForCombiner[i * 2].compareAndSet(null, DEQUE_TASK)) {
                i = randomTaskIndex()
            }

            while (true) {
                if (tasksForCombiner[i * 2].value == PROCESSED) {
                    val result = tasksForCombiner[i * 2 + 1].value
                    tasksForCombiner[i * 2 + 1].value = null
                    tasksForCombiner[i * 2].value = null
                    return result as E?
                } else if (tryLock()) {
                    runCombiner()
                    unlock()
                } else {
                    Thread.onSpinWait()
                }
            }
        }
    }

    private fun randomTaskIndex(): Int =
        ThreadLocalRandom.current().nextInt(TASKS_FOR_COMBINER_SIZE)
}

private const val TASKS_FOR_COMBINER_SIZE = 3 // Do not change this constant!

// TODO: Put this token in `tasksForCombiner` for dequeue().
// TODO: enqueue()-s should put the inserting element.
private val DEQUE_TASK = Any()

// TODO: Put this token in `tasksForCombiner` when the task is processed.
private val PROCESSED = Any()