JAVA_HOME=/usr/jdk1.7.0_40/

all: manual dist 

manual:
	./make_manual.sh

dist: 
	./kb_web_install.sh
