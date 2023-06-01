@file:Suppress("DuplicatedCode")

package day4

import day4.AtomicCounterArray.Status.*
import kotlinx.atomicfu.*
import kotlin.math.max
import kotlin.math.min

// This implementation never stores `null` values.
class AtomicCounterArray(size: Int) {
    private val array = atomicArrayOfNulls<Any>(size)

    init {
        // Fill array with zeros.
        for (i in 0 until size) {
            array[i].value = 0
        }
    }

    fun get(index: Int): Int {
        val curValue = array[index].value

        return if (curValue is Int) {
            curValue
        } else {
            curValue as IncrementDescriptor

            if (curValue.status.value == SUCCESS) {
                if (index == curValue.index1) curValue.newValue1
                else curValue.newValue2
            } else {
                if (index == curValue.index1) curValue.valueBeforeIncrement1
                else curValue.valueBeforeIncrement2
            }
        }
    }

    fun inc2(index1_: Int, index2_: Int) {
        require(index1_ != index2_) { "The indices should be different" }

        val index1 = min(index1_, index2_)
        val index2 = max(index1_, index2_)
        // TODO: This implementation is not linearizable!
        // TODO: Use `IncrementDescriptor` to perform the operation atomically.
        while (true) {
            val curValue1 = array[index1].value
            val curValue2 = array[index2].value

            if (curValue1 !is Int) {
                curValue1 as IncrementDescriptor

                curValue1.applyOperation()
                continue
            }

            if (curValue2 !is Int) {
                curValue2 as IncrementDescriptor

                curValue2.applyOperation()
                continue
            }

            val descriptor = IncrementDescriptor(index1, curValue1, index2, curValue2)

            if (array[index1].compareAndSet(curValue1, descriptor)) {
                if (array[index2].compareAndSet(curValue2, descriptor)) {
                    descriptor.status.compareAndSet(UNDECIDED, SUCCESS)
                } else {
                    descriptor.status.compareAndSet(UNDECIDED, FAILED)
                }

                descriptor.applyOperation()

                if (descriptor.status.value == SUCCESS) {
                    return
                }
            }
        }
    }

    // TODO: Implement the `inc2` operation using this descriptor.
    // TODO: 1) Read the current cell states
    // TODO: 2) Create a new descriptor
    // TODO: 3) Call `applyOperation()` -- it should try to increment the counters atomically.
    // TODO: 4) Check whether the `status` is `SUCCESS` or `FAILED`, restarting in the latter case.
    private inner class IncrementDescriptor(
        val index1: Int, val valueBeforeIncrement1: Int,
        val index2: Int, val valueBeforeIncrement2: Int
    ) {
        val status = atomic(UNDECIDED)

        val newValue1 = valueBeforeIncrement1 + 1
        val newValue2 = valueBeforeIncrement2 + 1

        // TODO: Other threads can call this function
        // TODO: to help completing the operation.
        fun applyOperation() {
            // TODO: Use the CAS2 algorithm, installing this descriptor
            // TODO: in `array[index1]` and `array[index2]` cells.
            val curStatus = status.value

            if (curStatus == SUCCESS) {
                array[index1].compareAndSet(this, newValue1)
                array[index2].compareAndSet(this, newValue2)
            } else if (curStatus == FAILED) {
                array[index1].compareAndSet(this, valueBeforeIncrement1)
                array[index2].compareAndSet(this, valueBeforeIncrement2)
            } else if (curStatus == UNDECIDED) {
                val curValue2 = array[index2].value

                if (curValue2 == valueBeforeIncrement2) {
                    if (array[index2].compareAndSet(curValue2, this)) {
                        status.compareAndSet(curStatus, SUCCESS)
                    }
                } else if (curValue2 == this) {
                    status.compareAndSet(curStatus, SUCCESS)
                } else {
                    status.compareAndSet(curStatus, FAILED)
                }

                applyOperation()
            }
        }
    }

    private enum class Status {
        UNDECIDED, FAILED, SUCCESS
    }
}