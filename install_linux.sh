#!/bin/bash
EXEC=/usr/bin/amqutil
SHARE=/usr/share/amqutil
mkdir -p $SHARE 
cp target/amqutil-0.1.5-jar-with-dependencies.jar $SHARE/amqutil.jar
rm -f $EXEC
echo "#!/bin/bash" > $EXEC
echo java -jar $SHARE/amqutil.jar \"\$\@\" >> $EXEC
chmod 755 $EXEC
