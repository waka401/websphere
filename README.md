WAS Agent
=========

A network tool for WebSphere Application Server monitoring, that provides performance statistics in a suitable format for
[Graphite][graphite].

Concepts
--------

WAS Agent takes advantage of WebSphere Performance Monitoring infrastructure (PMI), as well as the regular JMX API.
The agent embedds a small Jetty container, and the monitoring itself is made through simple HTTP requests. This approach 
allows short response times for monitoring queries, and a very low resource consumption.

Installation
------------




[graphite]: http://graphite.wikidot.com
[wasagent]: http://yannlambret.github.com/websphere/
