# The JGroups Chaos project #

## Motivation ##

An Infinispan cluster is built out of a number of nodes where data is stored. In order not to lose data in the presence of node failures, Infinispan copies the same data — cache entry in Infinispan parlance — over multiple nodes. This level of data redundancy ensures that as long as fewer than number of owners nodes crash simultaneously, Infinispan has a copy of the data available.

However, there might be catastrophic situations in which more than number of owners nodes disappear from the cluster:

### The problem: Split brain ###
Caused e.g. by a router crash, this splits the cluster in two or more partitions, or sub-clusters that operate independently. In these circumstances, multiple clients reading/writing from different partitions see different versions of the same cache entry, which for many application is problematic. Note there are ways to alleviate the possibility for the split brain to happen, such as redundant networks or IP bonding. These only reduce the window of time for the problem to occur, though.

### The framework: Why not a Chaos Framework ? ###
The idea of JGroups Chaos is broke internal components. The mainstream Chaos Frameworks deal with [Services](https://martinfowler.com/eaaCatalog/serviceLayer.html), like: when a Service goes down an assumption is made. In the other hand, JGroups Chaos will broke internally components, instrumenting classes during runtime.

`node1`
```
[org.jgroups.protocols.FD_ALL] edg-perf01-10536: suspecting [edg-perf05-51639]
```
`node5`
```
[org.jgroups.protocols.FD_ALL] edg-perf05-51639: haven't received a heartbeat from edg-perf01-10536 for 10002 ms, adding it to suspect list
```

Simulate those events with mainstream Chaos Frameworks is hard because I would like to introduce failures only for `FD_ALL` protocol and keep other protocols and system up and running.

### Main functionalities ###

* Introduce protocol failures
* Support `DISCARD` protocol
* Act as a master/slave and spawn process to another JVM
* Spawn process allow us to simulate GC stop-the-world events while the slave still able to communicate with master
* Be compatible with any project that has directly dependency of JGroups

# TODO #
* When JGroups have a commit, execute a build and run the tests.


