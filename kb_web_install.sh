#!/bin/bash
mvn clean
(cd ..; tar cfvz /home/kevin/docs/kzone5/target/amqutil.tar.gz amqutil)
cp README_amqutil.html /home/kevin/docs/kzone5/source/
(cd /home/kevin/docs/kzone5; ./make.pl amqutil)

