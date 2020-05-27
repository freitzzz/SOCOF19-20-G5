# How to measure the performance of a node in a real case ?

## Sources

* [How to Evaluate the performance of a parallel program](https://subscription.packtpub.com/book/application_development/9781785289583/1/ch01lvl1sec14/how-to-evaluate-the-performance-of-a-parallel-program)

* [Seven concurrency models in seven weeks when threads unravel]

* [Performance  Measurements  for  Multithreaded  Programs ](https://dl.acm.org/doi/epdf/10.1145/277851.277900)


## Introduction (needs grammar revision - early sketch)

Concurrent and parallel refer to related but different things.
A concurrent program has multiple logical threads of control. These threads may or may not run in parallel.
Concurrency is about dealing with lots of things at once.
Parallelism is about doing lots of things at once.

The focus of parallel computing is to solve large problems in a relatively short time. The factors that contribute to
the achievement of this objective are, for example, the type of hardware use, the degree of parallelism of the problem, and whice parallel parallel programing model is adopted.

Nowadays, many systems and applications are multithreaded, however, it is notrivial to achieve high performance in multitreading. A multithreaded program confronts more proboems than a sequential program: competition for resources among threads, slowdown due to synchroni
zation, context-switching  overheads, conflicting considerations in scheduling, trade-offs in task distribution, non-overlapped I/Os, and higher level interactions between these factores. In other words, multithreads programs are difficult to debug and tune because of t
he existence of multiple threads of control and their data and timing dependencies.
