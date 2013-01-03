WAS Agent
=========

A network tool for WebSphere Application Server monitoring, that provides performance statistics in a suitable format for
[Graphite][graphite].

Current features are:

 * JVM heap monitoring
 * Server thread pools monitoring
 * Transactions monitoring
 * JDBC datasources monitorin
 * JMS connection factories monitoring
 * SIB queues depth monitoring
 * HTTP sessions monitoring
 * Clustering support

Concepts
--------

WAS Agent takes advantage of WebSphere Performance Monitoring infrastructure (PMI), as well as the regular JMX API.
The agent embedds a small Jetty container, and the monitoring itself is made through simple HTTP requests. This approach 
allows short response times for monitoring queries, and a very low resource consumption.

Installing WAS Agent
--------------------

Using WAS Agent
---------------

We assume that the agent is running and listening on 'localhost:9090', and we want to monitor a remote WAS instance
listening on its default SOAP connector (8880).

Let's try this:

    curl -X POST -d 'hostname=hydre2&port=8880&jvm&thread-pool=Default,WebContainer' http://localhost:9090/wasagent/WASAgent

We get this kind of output:

    hydre2.h2srv1.jvm.cpuUsage 1 1357230342
    hydre2.h2srv1.jvm.currentHeapSize 160 1357230342
    hydre2.h2srv1.jvm.currentHeapUsed 102 1357230342
    hydre2.h2srv1.jvm.maximumHeapSize 256 1357230342
    hydre2.h2srv1.threadPool.Default.activeCount 2 1357230342
    hydre2.h2srv1.threadPool.Default.currentPoolSize 4 1357230342
    hydre2.h2srv1.threadPool.Default.maximumPoolSize 20 1357230342
    hydre2.h2srv1.threadPool.WebContainer.activeCount 0 1357230342
    hydre2.h2srv1.threadPool.WebContainer.currentPoolSize 0 1357230342
    hydre2.h2srv1.threadPool.WebContainer.maximumPoolSize 50 1357230342

By default, the metric scheme is build as follows:

    <target hostname> + <WAS logical name> + <test type> + (item) + <metric name> <value> <timestamp>

[graphite]: http://graphite.wikidot.com
[wasagent]: http://yannlambret.github.com/websphere/
