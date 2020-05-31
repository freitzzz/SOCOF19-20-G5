# How to measure the performance of a node in a real case?

## Introduction

Concurrent and parallel refer to related but different things.
A concurrent program has multiple logical threads of control. These threads may or may not run in parallel.

The focus of parallel computing is to solve large problems in a relatively short time. The factors that contribute to
the achievement of this objective are, for example, the type of hardware use, the degree of parallelism of the problem, and whice parallel programing model is adopted.

Nowadays, many systems and applications are multithreaded however, it is notrivial to achieve high performance in multitreading. A multithreaded program confronts more problems than a sequential program: competition for resources among threads, slowdown due to synchronization, context-switching  overheads, conflicting considerations in scheduling, trade-offs in task distribution, non-overlapped I/Os, and higher level interactions between these factores. In other words, multithreads programs are difficult to debug and tune because of the existence of multiple threads of control and their data and timing dependencies.


## Measuring Performance

In a master/slave program, the master node generates and allocates work/tasks to N slave nodes. The slave node receives those tasks, computes them, and sends the result back to the master node. In short terms, the master node is responsible for receiving analyzing the computed results by slave nodes.

This kind of approach generally is suitable for shared-memory or message-passing platforms, since the interaction is naturally two-way. The main problem wih master/slave paradigm is that the master may become a bottleneck. This may happen if the tasks are too small of if there are too many slaves connected to one master.

The choice of the amount of work in each task is called granularity. One of the main performance issues in parallel programming is the choice of many small tasks (fine grain) or few large tasks (coarse grain).
According to how the master distributes tasks and collects results from the slaves, we present two different kinds of master/slave implementations: programs with synchronous or asynchronous interaction.

Considering that the master/slave program was build using a threads-and-locks model how we can measure the performance of a node?

In simple terms, we can measure a node's performance by the amount of work in each task (granularity) and by the number of slaves in case of the master node. For the smaller amount of work on a task of a node, less time will take for it to complete, the less number of slaves the master node has to deal with, the less time will he take in responding. But that's not always the case, we have in mind that since we are using a threads-and-locks model meaning that the way we synchronize things and use locks in the proper order will have a direct effect on the performance. One thing we also need to watch for is excessive contention - too many threads are trying to access a single shared resource simultaneously.

The optimum number of threads will vary according to the hardware you're running on, wheter your threads are IO or CPU bound, what else the machine is doing at the same time, and a host of other factors.

Having said that, a good rule of thumb is that for computation-intensive tasks, you probably want to have approximately the same number of threads as available cores. Larger numbers are appropriate for IO-intensive tasks.


## Sources

1 - [How to Evaluate the Performance of a Parallel Program](https://subscription.packtpub.com/book/application_development/9781785289583/1/ch01lvl1sec14/how-to-evaluate-the-performance-of-a-parallel-program)

2 - [Seven Concurrency Models in Seven Weeks when Threads Unravel](http://shop.oreilly.com/product/9781937785659.do)

3 - [Performance Measurements for Multithreaded Programs](https://dl.acm.org/doi/epdf/10.1145/277851.277900)

4 - [Performance Models For Master/Slave Parallel Programs](https://www.sciencedirect.com/science/article/pii/S1571066105001908)
