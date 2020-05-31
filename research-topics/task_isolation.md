## How to guarantee that tasks don’t interfere with the node natural functionality?

## Introduction

A critical challenge in the foundations of parallel programming models is that of isolation: the property that a task can access dynamic subsets of shared data structures without interference from other tasks.

Threads and locks are litle more than formalization of what the underlying hardware actually does. That's both their great strength and their great weakness.

Because they're so simple, almost all languages support them in one form or another, and they impose very few constraints on what can be achieved throught their use.


## Isolating a Task

When multiple threads access shared memory, they can end up stepping on each other's toes. We can avoid this through mutual exclusion via locks, which can be held by only a single thread at a time. Meaning we can isolate a task via locks.

After reading the first paragraph, we can come with the idea that the only way to be safe in a multithread world is to make every method synchronized (intrinsic lock). Well as we will see things arent that easy.

If every method were synchronized, most threads would probably spend most their time blocked. But this is the least of our worries - as soon as we have more than one lock, we can create the opportunity for threads to become deadlocked.

A simple rule that can guaratee that we will never deadlock is always acquire locks in a fixed, global order.




## Sources

1 - [Isolation for Nested Task Parallelism](https://dl.acm.org/doi/10.1145/2509136.2509534)

2 - [Seven Concurrency Models in Seven Weeks when Threads Unravel](http://shop.oreilly.com/product/9781937785659.do)

3 - [Performance Models For Master/Slave Parallel Programs](https://www.sciencedirect.com/science/article/pii/S1571066105001908)
