# How to measure the performance of a node in a real case ?

## Sources

* [How to Evaluate the performance of a parallel program](https://subscription.packtpub.com/book/application_development/9781785289583/1/ch01lvl1sec14/how-to-evaluate-the-performance-of-a-parallel-program)

* [Seven concurrency models in seven weeks when threads unravel]

* [Performance  Measurements  for  Multithreaded  Programs ](https://dl.acm.org/doi/epdf/10.1145/277851.277900)


## Introduction (needs grammar revision - early sketch)

Concurrent and parallel refer to related but different things.
A concurrent program has multiple logical threads of control. These threads may or may not run in parallel.

The focus of parallel computing is to solve large problems in a relatively short time. The factors that contribute to
the achievement of this objective are, for example, the type of hardware use, the degree of parallelism of the problem, and whice parallel  programing model is adopted.

Nowadays, many systems and applications are multithreaded however, it is notrivial to achieve high performance in multitreading. A multithreaded program confronts more problems than a sequential program: competition for resources among threads, slowdown due to synchronization, context-switching  overheads, conflicting considerations in scheduling, trade-offs in task distribution, non-overlapped I/Os, and higher level interactions between these factores. In other words, multithreads programs are difficult to debug and tune because of the existence of multiple threads of control and their data and timing dependencies.

Now that we got a grasp of the main concepts surrounding parallel computing, how can we measure the performance of a node in a master/slave program in a real world scenario?


## Measuring Performance
In a master/slave program, the master node generates and allocates work/tasks to N slave nodes. The slave node receives those tasks, computes them, and sends the result back to the master node. In short terms, the master node is responsible for receiving analyzing the computed results by slave nodes.
This kind of approach generally is suitable for shared-memory or message-passing platforms, since the interaction is naturally two-way. The main problem wih master/slave paradigm is that the master may become a bottleneck. This may happen if the tasks are too small of if there are too many slaves connected to one master.

The choice of the amount of work in each task is called granularity. One of the main performance issues in parallel programming is the choice of  many small tasks (fine grain) or few large tasks (coarse grain).
According to how the master distributes tasks and collects results from the slaves, we present two different kinds of master/slave implementations: programs with synchronous or asynchronous interaction.

Considering that the master/slave program was build using a threads-and-locks model lets try and determine how we can  measure the performance of a node in this case measure the performance of a slave node.
To tackle the issue, first we need to understand the strengths and weaknesses of the threads-and-locks programming and the direct influence it has on the slave nodes.

** write things about threads and locks and their ups and downs
** maybe add some text about amdahl law

In order to measure the performance we need to define some metrics to helps us validate the implementation.

* Waiting time between each pair of threads.
* Number of tasks.
* Number of slaves (number of threads).
