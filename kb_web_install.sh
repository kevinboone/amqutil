#!/bin/bash
JAVA_HOME=/usr/jdk1.7.0_46
VERSION=0.1.0

mvn clean
(cd ..; tar cfvz /home/kevin/docs/kzone5/target/amqutil.tar.gz amqutil)
cp README_amqutil.html /home/kevin/docs/kzone5/source/
(cd /home/kevin/docs/kzone5; ./make.pl amqutil)
mvn package
cp target/amqutil-0.1.0-jar-with-dependencies.jar /home/kevin/docs/kzone5/target/amqutil-0.1.0.jar
