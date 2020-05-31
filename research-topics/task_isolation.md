## How to guarantee that tasks don’t interfere with the node natural functionality?

## Introduction

A critical challenge in the foundations of parallel programming models is that of isolation: the property that a task can access dynamic subsets of shared data structures without interference from other tasks [1].

Threads and locks are litle more than formalization of what the underlying hardware actually does. That's both their great strength and their great weakness [2].

Because they're so simple, almost all languages support them in one form or another, and they impose very few constraints on what can be achieved throught their use.

For context purposes, the programming language that we will addressing is java using threads-and-locks model in a master slave pattern. And also the terms task and thread are not interchangeable.

To keep things clear a Task is an independent work unit that needs to be executed at some point meanwhile Thread is an OS execution context that allows executing a stream of instructions, in sequence, independent of any other instructions that can be executed on the CPU [4].

## Isolating a Task

When multiple threads access shared memory, they can end up stepping on each other's toes. We can avoid this through mutual exclusion via locks, which can be held by only a single thread at a time. Meaning we can isolate a task via locks [3].

After reading the first paragraph, we can come with the idea that the only way to be safe in a multithread world is to make every method synchronized (intrinsic lock). Well as we will see things arent that easy.

If every method were synchronized, most threads would probably spend most their time blocked. But this is the least of our worries - as soon as we have more than one lock, we can create the opportunity for threads to become deadlocked [2].

A simple rule that can guaratee that we will never deadlock is always acquire locks in a fixed, global order.

Yes but kind of locks should i implement in order to isolate a task? Well to answer this question lets take a look on intrinsic locks and ReentrantLock.

Intrinsic locks are convenient but limited.

* There is no way to interrupt a thread that's blocked as a result of trying to acquire an intrinsic lock.
* There is no way to time out while trying to acquire an intrinsic lock.
* There's exactly one way to acquire an intrinsic lock: a synchronized lock.
* ```synchronized(object){<<method body>>}```
* This means that lock acquisition and release have to take place in the same method and have to be stricly nested. Note that declaring a method as synchronized is just syntactic sugar for surrounding the method's body with the following:
* ```synchronized(this){<<method body>>}```

ReentrantLock allow us to transcend these restrictions by providing explicit lock and unlock methods instead of using synchronized.


## Sources

1 - [Isolation for Nested Task Parallelism](https://dl.acm.org/doi/10.1145/2509136.2509534)

2 - [Seven Concurrency Models in Seven Weeks when Threads Unravel](http://shop.oreilly.com/product/9781937785659.do)

3 - [Performance Models For Master/Slave Parallel Programs](https://www.sciencedirect.com/science/article/pii/S1571066105001908)

4 - [Task, Threads, and Execution](https://lucteo.ro/2019/03/16/tasks-threads-and-execution/#one-thread-per-task)
