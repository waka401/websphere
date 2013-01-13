#!/bin/bash

cd $(dirname "$0")

# Must point to a valid IBM 1.6 JRE installation
JAVA_HOME=""

# WAS Agent address and port
HOST="0.0.0.0"
PORT="9090"

# WAS Agent classpath
CLASSPATH=".:wasagent.jar"
for jar in $(find "lib" -name '*.jar'); do
  CLASSPATH=${CLASSPATH}:${jar};
done

# Starts the agent
${JAVA_HOME}/bin/java -Xmx16m -cp ${CLASSPATH} net.wait4it.graphite.wasagent.core.WASAgent ${HOST} ${PORT} > /dev/null 2>&1 &
