# Intensive Course on Concurrent Programming

## Day 1

### Coarse-grained bank

In [`src/day1/CoarseGrainedBank.kt`](src/day1/CoarseGrainedBank.kt),
make the sequential bank implementation thread-safe.
Please follow the *coarse-grained locking* scheme to make synchronization efficient.
For that, you need to use a single lock to protect all bank operations.

To test your solution, please run:

* `./gradlew test --tests CoarseGrainedBankTest` on Linux or MacOS
* `gradlew test --tests CoarseGrainedBankTest` on Windows

### Fine-grained bank

In [`src/day1/FineGrainedBank.kt`](src/day1/FineGrainedBank.kt),
make the sequential bank implementation thread-safe.
Please follow the *fine-grained locking* scheme to make synchronization efficient.
For that, you need to use per-account locks, thus, ensuring natural parallelism
when accessing different accounts. The `totalAmount()` function should acquire
all the locks to get a consistent snapshot, while `transfer(..)` should acquire
the corresponding account locks.

To test your solution, please run:

* `./gradlew test --tests FineGrainedBankTest` on Linux or MacOS
* `gradlew test --tests FineGrainedBankTest` on Windows

### Treiber stack

In [`src/day1/TreiberStack.kt`](src/day1/TreiberStack.kt),
implement the classic Treiber stack algorithm.

To test your solution, please run:

* `./gradlew test --tests TreiberStackTest` on Linux or MacOS
* `gradlew test --tests TreiberStackTest` on Windows

### Treiber stack with elimination

In [`src/day1/TreiberStackWithElimination.kt`](src/day1/TreiberStackWithElimination.kt),
implement the classic Treiber stack algorithm with the elimination technique.

To test your solution, please run:

* `./gradlew test --tests TreiberStackWithEliminationTest` on Linux or MacOS
* `gradlew test --tests TreiberStackWithEliminationTest` on Windows

### Michael-Scott queue

In [`src/day1/MSQueue.kt`](src/day1/MSQueue.kt),
implement the Michael-Scott queue algorithm.
You might also be interested in the [original paper](http://www.cs.rochester.edu/~scott/papers/1996_PODC_queues.pdf).

To test your solution, please run:

* `./gradlew test --tests MSQueueTest` on Linux or MacOS
* `gradlew test --tests MSQueueTest` on Windows


## Day 2

### FAA-based queue: simplified

In [`src/day2/FAABasedQueueSimplified.kt`](src/day2/FAABasedQueueSimplified.kt),
implement a concurrent queue that leverages the `Fetch-and-Add` synchronization primitive.
The high-level design of this queue bases on a conceptually infinite array for storing elements and manipulates
`enqIdx` and `deqIdx` counters, which reference the next working cells in the infinite array for `enqueue(..)`
and `dequeue()` operations.

In this task, use a big plain array as the infinite array implementation.

To test your solution, please run:

* `./gradlew test --tests FAABasedQueueSimplifiedTest` on Linux or MacOS
* `gradlew test --tests FAABasedQueueSimplifiedTest` on Windows

### FAA-based queue

In [`src/day2/FAABasedQueue.kt`](src/day2/FAABasedQueue.kt),
implement a concurrent queue that leverages the `Fetch-and-Add` synchronization primitive.
The high-level design of this queue bases on a conceptually infinite array for storing elements and manipulates
`enqIdx` and `deqIdx` counters, which reference the next working cells in the infinite array for `enqueue(..)`
and `dequeue()` operations.

The infinite array implementation should be simulated via a linked list of
fixed-size segments. The overall algorithm should be obstruction-free or lock-free.

To test your solution, please run:

* `./gradlew test --tests FAABasedQueueTest` on Linux or MacOS
* `gradlew test --tests FAABasedQueueTest` on Windows

### Flat-combining queue

In [`src/day3/FlatCombiningQueue.kt`](src/day3/FlatCombiningQueue.kt), implement a concurrent queue via the
_flat-combining_ technique,
using a sequential queue under the hood. You might be interested in the corresponding
[academic paper]((https://dl.acm.org/doi/pdf/10.1145/1810479.1810540?casa_token=Yo13gxOeFhwAAAAA:qS33gvUFNhI4t_2ioHnZz0egK8PFq0Mg7MT0ma1-26aeQYKk7aZBzEHEY6iFMiu-GEmzsBMuSibDkg))

To test your solution, please run:

* `./gradlew test --tests FlatCombiningQueueTest` on Linux or MacOS
* `gradlew test --tests FlatCombiningQueueTest` on Windows
