#!/bin/bash

html2text -style pretty -nobs README_amqutil.html |grep -v corner|fmt > src/main/resources/manual.txt

 
