# What languages and platforms to choose in a real-world implementation and why?

Our ambition may be to innovate and find a solution which grants us of high-levels of performance, with literally no time splits between the request start and the response arrival, by designing our desires as such, but this is theoretically speaking, where the concept of utopy takes place. In practice our desires are limitted to computational resources that are given by the computers which will perform the computation requests and the infrastructure that supports it. Technically speaking, if our problem is the lack of computational resources, we can always obtain more of these and scale our solution in space. Yet this brings us a bigger problem which is the cost of doing such. But we can be smarter and play with what we have and what is available to us, by conjugating our problem domain with the available technologies and computers. That being said it is possible to express this conjugation in two extrinsinc domains: platform (computer) and (programming) language.

As seen in Figure X, to conclude what platform to use, there is the need to analyze the domain drivers. It is known that the target achievement is the sum or product of integer lists. It is also known that multiple requests of these computations can occur and that large integer lists increase the computation time. To tackle these issues, in the scope of the computation domain, the problem is decentralized to a master and a group of slaves: the conductor and its orchestra. The slaves perform computational requests whereas the master commands the whole operation, by splitting the integer list in smaller parcels and sending these to the slaves. This leads to a bigger memory usage on Master and bigger CPU usage on Slaves side. This two concerns already constrain the language in which both sides will be programmed on. Also Slaves do not require any complex algorithm to perform the computation as they either sum or product numbers, while iterating an array, which in the perspective of the processor is just executing `ADD` and `JMP` instructions. From this it is possible to conclude that Master will require a programming language which provides a stable and reliable memory management, whereas Slave requires a language that provides fast operations execution, and that it is not necessary an *high-end* computer to perform the computation of the requests. Master also requires the store, join and watch of slaves results, which leads to concurrent operations on a shared datastructure. To keep the consistency of the datastructure records and operations atomicity, its either necessary the use of locks or lock-free algorithms. Lock-free operations are far more efficient than Lock operations as these do not require additional CPU work on controlling processes / threads access to critical regions, but require the ISA (Instruction Set Architecture) of the CPU to provide the `CMPXCHG` (Compare-and-Exchange/Swap (CAS)) instruction or alternatives to achieve an atomic CAS. Finally, to conclude on the platform requirements, it is also required that Master and Slaves communicate with each other. To allow such there is the need to establish a messaging protocol that both parties understand. There are several standardized protocols, each with their advantages and disadvantages, such as `OpenMPI` [1], `MQTT` [2], `AMQP` [3], `HTTP` [4] and `Websocket` [5]. Since the infrastructure and implementation are not complex, it is only required to choose one protocol that is reliable, fast and has support in the chosen programming language.

Given the requirements for the platforms, it is proposed that a cluster of Raspberry Pi nodes is assembled, as seen in Figure X, connected through Gigabit Ethernet cables, as each of these computers is relatively cheap and fit the requirements. There are various models of Raspberry Pi, each with different specifications, but share the same CPU ISA family (ARM). Desirably, each Slave node would run on a `Raspberry Pi 3 Model B+`, which has a `64-bit Broadcom BCM2837B0, Quad core Cortex-A53 (ARMv8)` processor spanned in four cores, with a maximum speed of `1.4GHz` (Theoretically, at least 1400 million instructions per second) [6,7]. These nodes would have the maximum performance index, but other nodes with less performance index could be introduced, by plugging in older Raspberry Pi models, such as the `Raspberry Pi 2 Model B` [8] and `Raspberry Pi 1 Model B+` [9], which have less computational power and do not support the Gigabit Ethernet cables, reducing the throughput of the communication and thus increasing the communication latency. The master would run on a `Raspberry Pi 4 Model B` that includes a `64-bit Broadcom BCM2711, Quad core Cortex-A72 (ARM v8)` processor with up to 2, 4 or 8GB of RAM [10]. This is great for Master as it has a lot more primary memory which can be used to store the records of the shared datastructure as well to store the temporary array parcels that are split on master request.

![cluster_4_raspberry_pi](../figures/cluster_4_raspberry_pi.jpeg)

<center>Figure X - Cluster of 4 Raspberry Pi serving a Kubernetes Cluster, taken from https://medium.com/nycdev/k8s-on-pi-9cc14843d43</center>

Alternatively to the usage of Raspberry Pi as the computers, other market computers/boards can be used [11] such as the `ASUS Tinker Board S` [12], but might be more expensive and the CPU ISA might not support the CAS operation. Also alternatively to building a cluster with physical hardware, one can also assemble the infrastructure in the cloud, but that also be much more expensive for the outcomes of the solution.

## Sources

1 - https://www.open-mpi.org/, accessed on 30/05/2020.

2 - http://mqtt.org/, accessed on 30/05/2020.

3 - https://www.amqp.org/, accessed on 30/05/2020.

4 - https://tools.ietf.org/html/rfc1945, accessed on 30/05/2020.

5 - https://developer.mozilla.org/docs/WebSockets, accessed on 30/05/2020.

6 - https://www.raspberrypi.org/products/raspberry-pi-3-model-b-plus/, accessed on 30/05/2020.

7 - https://en.wikipedia.org/wiki/Instructions_per_second#Millions_of_instructions_per_second_(MIPS), accessed on 30/05/2020.

8 - https://www.raspberrypi.org/products/raspberry-pi-2-model-b/, accessed on 30/05/2020.

9 - https://www.raspberrypi.org/products/raspberry-pi-1-model-b-plus/, accessed on 30/05/2020.

10 - https://www.raspberrypi.org/products/raspberry-pi-4-model-b/specifications/, accessed on 30/05/2020.

11 - https://itsfoss.com/raspberry-pi-alternatives/, accessed on 30/05/2020.

12 - https://www.asus.com/Single-Board-Computer/Tinker-Board-S/, accessed on 30/05/2020.