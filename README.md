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

We assume that the agent is running and listening on 'hydre1:9090', and we want to monitor a remote WAS instance
listening on its default SOAP connector (8880).

Let's try this simple query:

    curl -X POST -d 'hostname=hydre2&port=8880&jvm&thread-pool=Default,WebContainer' http://hydre1:9090/wasagent/WASAgent

We get the following output:

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

The last part of the metric (from the WAS logical name) can not be changed. However, you can provide a prefix and / or
a suffix option, for instance:

    curl -X POST -d 'prefix=emea.fr&hostname=hydre2&suffix=was&port=8880&jvm&thread-pool=Default,WebContainer' ...

Which will give you this result:

    emea.fr.hydre2.was.h2srv1.jvm.cpuUsage 1 1357237139
    emea.fr.hydre2.was.h2srv1.jvm.currentHeapSize 160 1357237139
    emea.fr.hydre2.was.h2srv1.jvm.currentHeapUsed 111 1357237139
    emea.fr.hydre2.was.h2srv1.jvm.maximumHeapSize 256 1357237139
    emea.fr.hydre2.was.h2srv1.threadPool.Default.activeCount 1 1357237139
    emea.fr.hydre2.was.h2srv1.threadPool.Default.currentPoolSize 4 1357237139
    emea.fr.hydre2.was.h2srv1.threadPool.Default.maximumPoolSize 20 1357237139
    emea.fr.hydre2.was.h2srv1.threadPool.WebContainer.activeCount 0 1357237139
    emea.fr.hydre2.was.h2srv1.threadPool.WebContainer.currentPoolSize 0 1357237139
    emea.fr.hydre2.was.h2srv1.threadPool.WebContainer.maximumPoolSize 50 1357237139

If metric names contain characters that can not be used in a metric scheme (like "." and "/"), they will be replaced by
an underscore.

Here is a summary of the available options:

### hostname (mandatory)

The hostname of the WAS instance you want to monitor.

### port (mandatory)

The SOAP connector of the WAS instance you want to monitor.

### prefix (optional)

The prefix string will be placed at the beginning of the metric scheme. Don't use a trailing period (i.e. 'emea.fr' is
correct, 'emea.fr.' is not).

### suffix (optional)

The suffix string will placed right after the hostname in the metric scheme. Don't use a leading period (i.e. 'was' is
correct, '.was' is not).

### jvm (optional)

This option doesn't take any value. It produces the following output:

    jvm.cpuUsage
    jvm.currentHeapSize
    jvm.currentHeapUsed
    jvm.maximumHeapSize

### thread-pool (optional)

This option takes a comma separated list of thread pool names, or a wildcard character (`thread-pool=*`) for the whole
thread pool list. The following output is produced when the wildcard character is passed:

    threadPool.Default.activeCount
    threadPool.Default.currentPoolSize
    threadPool.Default.maximumPoolSize
    threadPool.HAManager_thread_pool.activeCount
    threadPool.HAManager_thread_pool.currentPoolSize
    threadPool.HAManager_thread_pool.maximumPoolSize
    threadPool.MessageListenerThreadPool.activeCount
    threadPool.MessageListenerThreadPool.currentPoolSize
    threadPool.MessageListenerThreadPool.maximumPoolSize
    threadPool.ORB_thread_pool.activeCount
    threadPool.ORB_thread_pool.currentPoolSize
    threadPool.ORB_thread_pool.maximumPoolSize
    threadPool.ProcessDiscovery.activeCount
    threadPool.ProcessDiscovery.currentPoolSize
    threadPool.ProcessDiscovery.maximumPoolSize
    threadPool.SIBFAPInboundThreadPool.activeCount
    threadPool.SIBFAPInboundThreadPool.currentPoolSize
    threadPool.SIBFAPInboundThreadPool.maximumPoolSize
    threadPool.SIBFAPThreadPool.activeCount
    threadPool.SIBFAPThreadPool.currentPoolSize
    threadPool.SIBFAPThreadPool.maximumPoolSize
    threadPool.SoapConnectorThreadPool.activeCount
    threadPool.SoapConnectorThreadPool.currentPoolSize
    threadPool.SoapConnectorThreadPool.maximumPoolSize
    threadPool.TCPChannel_DCS.activeCount
    threadPool.TCPChannel_DCS.currentPoolSize
    threadPool.TCPChannel_DCS.maximumPoolSize
    threadPool.WMQJCAResourceAdapter.activeCount
    threadPool.WMQJCAResourceAdapter.currentPoolSize
    threadPool.WMQJCAResourceAdapter.maximumPoolSize
    threadPool.WebContainer.activeCount
    threadPool.WebContainer.currentPoolSize
    threadPool.WebContainer.maximumPoolSize

### jta (optional)

This option doesn't take any value. It produces the following output:

    jta.activeCount

[graphite]: http://graphite.wikidot.com
[wasagent]: http://yannlambret.github.com/websphere/
