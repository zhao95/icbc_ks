#!/bin/bash
MAIN_CLASS=org.apache.tools.ant.launch.Launcher

LIB_HOME=lib
# JAVA CLASSPATH
  JAVA_CLASSPATH=$JAVA_HOME/lib/tools.jar;%JAVA_HOME%\jre\lib\rt.jar
  if [ -d "$LIB_HOME" ]; then
    for i in "$LIB_HOME"/*.jar; do
      JAVA_CLASSPATH="$JAVA_CLASSPATH":"$i"
    done
  fi

java -cp $JAVA_CLASSPATH $MAIN_CLASS -f build.xml $1
echo "done"
