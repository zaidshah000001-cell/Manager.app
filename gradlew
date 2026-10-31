#!/bin/sh
APP_HOME="`pwd -P`"
DEFAULT_JVM_OPTS=""
CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar
exec java $DEFAULT_JVM_OPTS -jar "$CLASSPATH" "$@"
