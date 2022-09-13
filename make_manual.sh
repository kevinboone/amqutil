#!/bin/bash

#html2text -nobs -style pretty README.md |grep -v corner|fmt > src/main/resources/manual.txt
html2text README.md  > src/main/resources/manual.txt

 
