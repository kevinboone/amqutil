<h1>amqutil</h1>

Version 0.1.5<p/>

A simple test/benchmark utility for Apache message brokers.

<h2>What is this?</h2>

<code>amqutil</code> is a simple Java command-line utility for testing
and exercising Apache (and other) JMS message brokers. It works with the 
Apache ActiveMQ and Artemis brokers, 
and also with Red Hat productized equivalents, A-MQ 6 and AMQ 7. 
Since
adding support for Apache Qpid (in version 0.1.2), amqutil can also work
with any message broker or router that supports AMQP 1.x, 
like <code>qdrouterd</code>. For Artemis-based
brokers, <code>amqutil</code> can use the OpenWire or native Artemis
wire protocols. The native Artemis protocol can be used to exercise
the message broker built into recent versions of Wildfly (and JBoss
EAP).
<p/>
<code>amqutil</code>  can put messages onto queues or
topics,
consume from queues, set message header properties, 
subscribe to topics, list queue contents, 
and dump individual messages in various formats.
Messages can be read from and written to text files, or just generated
internally by the utility. <code>amqutil</code> supports transacted
batches, durable subscriptions, inter-message delays, and persistent
and non-persistent production. It supports various acknowledgement modes.
<p/>
<code>amqutil</code> was originally intended to extend the 
test client functionality
provided by the <code>mq-client.jar</code> utility that was 
supplied with ActiveMQ, and so its command-line is similar (but not identical)
to that utility's command line. However, <code>amqutil</code> now
supports many brokers, and 
provides a number of features that
<code>mq-client.jar</code> never did, particularly around queue and
broker browsing, and message formatting.
<p/>
<code>amqutil</code> is supplied as a Java JAR file that embeds the ActiveMQ,
Qpid, and Artemis client runtimes, so installation consists of 
building the application using Maven and copying the output JAR file
to any convenient directory.

<h2>Sample command lines</h2>

<code>amqutil</code> is supplied as a Java JAR file (see the Downloads
section), and should be invoked like this:

<pre class="codeblock">
java -jar /path/to/amqutil.jar [command] {options}
</pre>

However, throughout this document the command <code>amqutil</code> is used
as an abbreviation for this longer command line. Of course, 
it should be possible
to create a script or batch file that allows <code>amqutil</code> to be
invoked this way (and a sample is included in the source code bundle).

<p/>

Please note that the default wire protocol is OpenWire. You can change
this using the <code>--qpid</code> or <code>--artemis</code> options.

<pre class="codeblock">
amqutil browse --host mybroker --port 6161 --destination testqueue 
  List messages on destination testqueue, on broker mybroker:6161
  Note that --url can be used instead of --host and --port

amqutil browse --user fred --password secret --destination testqueue 
  List messages on destination testqueue, on default broker localhost:6161,
  using specific connection credentials

amqutil list
  List the destinations on the default broker

amqutil show 23 --destination testqueue 
  Print to the console details of message 23 on destination testqueue,
  without consuming the message

amqutil produce 1000 --destination testqueue --batch 10
  Produce 1000 arbitrary 500-character text messages to testqueue
  in transacted batches of 10 messages

amqutil produce 1000 --destination testqueue 
  Produce 1000 arbitrary 500-character text messages to testqueue

amqutil produce --destination testqueue --length 42
 Produce a single arbitrary text message of length 42 characters to testqueue

amqutil --produce --destination testqueue --file data.txt
  Produce text file data.txt to testqueue

amqutil consume --destination testqueue --format long
  Consume from the head of testqueue and display all message headers

amqutil consume --destination testqueue --sleep 1000
  Consume from the head of testqueue and display the message ID only,
  pausing for 1000 msec after each message

amqutil consume --destination testqueue --number 1000 --format none --times
  Time how long it takes to consume 1000 messages from testqueue

amqutil subscribe --destination testtopic --format long
  Subscribe to testtopic and display one message in long format
  when it is published

amqutil publish --destination testtopic --file topic.txt
  Publish the text in topic.txt to testtopic

amqutil subscribe --destination testtopic --durable my-client
  Create a durable subscription with client ID my-client

amqutil count --destination testtopic 
  Count the messages on the specified destinatioN 

amqutil help subscribe
  Get detailed usage for the subscribe command
</pre>


<h2>Installing and running amqutil</h2>

You will need the compiled binary JAR file (see the Downloads section
below) and a recent Java JVM (Java 7 or later.) To run the utility, 
install the JAR in any convenient directory, and then:

<pre class="codeblock">
java -jar /path/to/amqutil.jar {options}
</pre>


<h2>Building</h2>

To build <code>amqutil</code>, obtain and
unpack the source code bundle, and then 

<pre class="codeblock">
mvn compile assembly:single
</pre>

or

<pre>
mvn package 
</pre>

This will create a JAR file in the <code>target</code> directory, which
contains the <code>amqutil</code> code and all the necessary ActiveMQ client
support. To run the utility, no other dependencies should be necessary;
just use <code>java -jar</code>:

<pre>
java -jar target/amqutil-0.0.1-jar-with-dependencies.jar {options}
</pre>

For Linux users, a script <code>install_linux.sh</code> is provided 
that will copy
the compiled JAR to <code>/usr/share/amqutil</code> and 
create an executable script
<code>/usr/bin/amqutil</code> to run it. Thereafter, you should be able to run
simply:

<pre>
$ amqutil {options}
</pre>

Note that Maven will automatically download all the dependencies needed to
build <code>amqutil</code> -- about 20Mb of them. This might take some
time on the first build.
<p/>
<code>amqutil</code> is configured to use specific versions of
the various client libraries it supports. These can be changed
by 
modifying the versions in
<code>pom.xml</code>: the values to be changd should be self-explanatory.

<p/>
The latest version of the source can be obtained from
<a href="https://github.com/kevinboone/amqutil">github</a>.

<h2>Basic usage</h2>

All <code>amqutil</code> invocations takes the following form:

<pre class="codeblock">
amqutil [command] {options}
</pre>

The <code>command</code> is mandatory, and specifies the mode of 
operation. Further options depend on the command. In all cases, a
brief description of the available options can be obtained thus:

<pre class="codeblock">
amqutil [command] --help 
</pre>

To find out how to get more general information, just run

<pre class="codeblock">
amqutil help 
</pre>

The commands (modes of operation) available to <code>amqutil</code> are
a follows.

<h4>browse</h4>
<pre class="codeblock">
amqutil browse
</pre>

Display a list of messages on the destination whose name is 
given by the <code>--destination</code> option. To see individual
message contents, use <code>show</code>, passing the index of the
message from the output of <code>browse</code>.

<h4>commands</h4>
<pre class="codeblock">
amqutil commands 
</pre>

Show a list of available commands.

<h4>consume</h4>
<pre class="codeblock">
amqutil consume {number} {--destination queue} {...}
</pre>

Consume <code>number</code> messages from the queue destination whose 
name is given by the
<code>--destination</code> option. If the number is omitted, one message
will be consumed. 
The format of the displayed
messages is controlled using the <code>--format</code> option; to write
consumed text messages to a text file, using <code>--file</code>. 
If asked to consume more
messages than are waiting on the destination, <code>amqutil</code> will 
block until new messages are available.

<h4>count</h4>
<pre class="codeblock">
amqutil count {--destination queue} {...} 
</pre>

Displays the number of messages currently waiting on the specified destination.


<h4>help</h4>
<pre class="codeblock">
amqutil help 
</pre>
Display a summary of command-line options.


<h4>list</h4>
<pre class="codeblock">
amqutil list 
</pre>
List the destinations on the message broker.


<h4>manual</h4>
<pre class="codeblock">
amqutil manual 
</pre>
Dump the whole manual (this file) to standard output in plain text format,
formatted for 80 character width.

<h4>produce</h4>
<pre class="codeblock">
amqutil produce {number} {--destination queue} {...}
</pre>
Produce <code>number</code> messages to the queue destination whose name 
is given by the
<code>--destination</code> option. By default, one message is produced.
To pproduce messages from a text file, use <code>--file</code>; otherwise
a message of <code>--length</code> characters is generated internally.


<h4>publish</h4>
<pre class="codeblock">
amqutil publish {number} {--destination topic} {...}
</pre>
Publish <code>number</code> messages to the topic destination whose 
name is given by the
<code>--destination</code> option. By default, one message is published.
To publish messages from a text file, use <code>--file</code>; otherwise
a message of <code>--length</code> characters is generated internally.

<h4>show</h4>
<pre class="codeblock">
amqutil show {message_index} {--destination queue} {...}
</pre>

Displays in detail the message whose index is given by 
<code>message_index</code>,
on the queue whose name is given by <code>--destination</code>.
The indices are arbitrary, and correspond to the numbers displayed when
using the <code>browse</code> command. However, message zero is always
the head of the queue, that is, the message that will be consumed next.
If the message is a text message, its contents will be displayed. Note
that it isn't an error to use this option with a topic, but no messages
will ever be shown.

<h4>subscribe</h4>
<pre class="codeblock">
amqutil subscribe {number} {--destination queue} {--durable ID} {...}
</pre>
Subscribe to the topic destination whose name is given by the
<code>--destination</code> option. This operation blocks until 
<code>number</code> messages are published to the topic.
If no <code>number</code> is given, wait for one message.
The message read may be displayed, according to 
the <code>--format</code> option. 
<p></p>
The <code>--durable</code> switch creates a durable subscription with
the specific client ID on the connection. The subscription name is 
"amqutil", which cannot be changed. The <code>--shared</code> switch
creates a shared subscription, as defined in the JMS 2.0 specification.
This can be combined with <code>--durable</code> if required. Note that
shared subscription support is only available for wire protocols that
are compatible with JMS 2.0; in other cases an exception will be
thrown.

<h4>version</h4>
<pre class="codeblock">
amqutil version 
</pre>
Show program version.


<h2>Command-line options</h2>

Not all command-line options are relevant to a specific command, although
<code>--host</code>, 
<code>--port</code>, 
<code>--url</code>, 
<code>--qpid</code>, 
<code>--artemis</code>, 
<code>--user</code>, and
<code>--password</code> are generally applicable. To see which switches are
applicable to a specific command, use <code>amqutil [command] --help</code>.
<p/>
<b>a,--artemis</b><br>
Use the native Artemis wire protocol
<p/>
<b>--batch {size}</b><br>
Produce or consume messages in transacted batches of the specified
size. Use <code>--batch 1</code> to use a transacted session without
batching.
<p/>
<b>-d, --destination {destination_name}</b><br>
Specifies the queue or topic name for most other operations. If no name
is given, the default is "__test_queue".
<p/>
<b>--durable {client-ID}</b><br>
Used in conjunction with <code>amqutil subscribe</code>, makes subscriptions
durable with the specified client ID.
<p/>
<b>--file {filename}</b><br>
When publishing or producing messages, read the (text) message body from
the specified file, rather than generating it internally. The whole file
is read or written, and any <code>--length</code> option is ignored.
This option reads and writes using the platform's default character
encoding (usually UTF-8 on Linux systems), even though this may not be
the encoding format used within the JMS broker itself.
<p/>
<b>--format {none|short|long|text}</b><br>
Format for messages that are consumed with <code>consume</code>, 
<code>subscribe</code> or
<code>show</code>. 
<code>none</code> is useful for
benchmarking, because console output is often slower than messaging operations.
<code>short</code> displays only the message ID and type; <code>long</code>
displays all headers; <code>text</code> displays all headers and also the
message body, if it is text.
<p/>
<b>--host {hostname}</b><br>
The hostname or IP of the broker; defaults to <code>localhost</code>.
<p/>
<b>--jmstype {string}</b><br>
Set the value of the JMSType header.
<p/>
<b>--length {number}</b><br>
When generating messages internally, use the specified number of characters.
Note that the number of bytes  actually stored will depend on the character
encoding used by the JVM and/or the JMS broker, and will typically be
larger (rarely smaller) than the given number of characters.
<p/>
<b>--loglevel {error|warn|info|debug}</b><br>
Sets the logging level, both of this utility and the ActiveMQ client.
In practice, although all these levels are defined, the only ones that
the ActiveMQ client uses are 'error' and 'debug.' The default is
'error,' which means silent operation in normal circumstances. 
<p/>
<b>--linger {msec}</b><br>
In batch mode (see <code>--batch</code>), delay for the specified time between
receiving the batch, and acknowledging it. The batch size can be usefully be 1.
This switch simulates an 'abnormal' condition, where the consumer takes a substantial
time to acknowledge a message.
<p/>
<b>--nonpersistent</b><br>
Enable non-persistent delivery, when used with <code>produce</code>
or <code>publish</code>.
<p/>
<b>-p. --password {password}</b><br>
Specifies the password for authentication against the AMQ broker. 
Default 'admin'.
<p/>
<b>--percent</b><br>
Prints percentage completion as an operation is performed; this can be useful
when dealing with huge numbers of messages.
<p/>
<b>--port {number}</b><br>
Broker's TCP/IP port number; default 61616
<p/>
<b>--properties {name=value[,name=value]...}</b><br>
Sets arbitrary string properties to the message header before sending.
Properties are specified as <code>name1=value1,name2=value2...</code>.
<p/>
<b>-q,--qpid</b><br>
Use the Apache Qpid JMS client to support the AMQP 1.0 protocol.
<p/>
<b>--sleep {msec}</b><br>
Sleep (wait) for the specified number of milliseconds after dispatching or
consuming each message. See also <code>--linger</code>.
<p/>
<b>--selector {expression}</b><br>
When consuming messages from a queue, apply the specified selector expression
<p/>
<b>--time</b><br>
Show the time in milliseconds taken to complete whatever operation 
was specified.
<p/>
<b>--ttl {msec}</b><br>
When producing a message to a queue, set the expiration time to the specified 
time.
<p/>
<b>-u, --user {username}</b><br>
Username for authentication on the broker. Default 'admin'.
<p/>
<b>--url {broker_url}</b><br>
Use a URL for connection to the broker, rather than a simple host/port
combination. This will be necessary if you want to work with multiple 
brokers in a failover group. An example might be
<code>--url failover:\(tcp://broker1:61616,tcp://broker2:61616\)</code>.
Note that the parentheses have to be escaped here to protect them from
the shell. If <code>--url</code> is specified, then <code>--host</code> and
<code>--port</code> are ignored.
<p/>

<h2>Notes</h2>

When listing queue contents with <code>browse</code>, the first entry 
shown is the head of
the queue, that is, the message that would next be consumed. 
When displaying messages without consuming them using <code>show</code>, 
the numeric argument specifies the position in the queue, with zero
being the head.
<p/>
<code>browse</code>, <code>show</code>, and <code>count</code> all
rely on the JMS queue browsing API. ActiveMQ can be configured such
such case, <code>amqutil</code> will not have access to all the messages
on a destination.
<p/>
When producing a message to a destination, if no message file 
(<code>--file</code>) is
provided, then a string of 500 arbitrary characters will be produced,
or of length set by the <code>--length</code> switch.
</p>
If a file is specified when consuming messages, then messages will be appended
to that file, provided they are text messages. Any non-text messages are
silently ignored
<p/>
When listing queue contents with <code>--browse</code>, no guarantee
can be given that the queue contents will not change before the next
operation. 
<p/>
When producing a message from a text file, the file is read in the 
platform's default character encoding.
<p/>
It is not actually an error to use the <code>--browse</code> option on
a topic, but no messages will ever be shown -- that is a consequence of
the way that publish/subscribe messaging works.
<p/>
When the <code>--consume</code> option is used, and it blocks because there
are insufficient messages to consume, then the only way to stop the 
program is to kill it (e.g., ctrl+C). If the <code>--batch</code> mode
is used, and the session is therefore transacted, any messages
consumed will be returned to the destination. This is not a bug although 
it can be confusing -- transacted sessions are designed to work that way.
<p/>
Message delivery is persistent by default. To test non-persistent
delivery, use the <code>--nonpersistent</code> switch.
<p/>
ActiveMQ supports multi-destination production and consumption. To use
this feature with <code>amqutil</code>, separate the multiple destinations
with commas. For example: <code>--destination d1,d2,d3</code>.
<p/>
<code>amqutil</code> can monitor broker operation via
<a href="http://activemq.apache.org/advisory-message.html">advisory topics</a>. 
For example, to monitor connections being opened and closed in real time:

<pre class="codeblock">
amqutil --subscribe --destination ActiveMQ.Advisory.Connection
</pre>

However, most advisory topics are not enabled by default, and some administrators
are wary of enabling them, because of the additional network and memory load
they might create.
<p/>
The <code>--properties</code> switch can be used to exercise some specific
ActiveMQ behavior. For example, setting the <code>JMSXGroupID</code> header
should ensure that a single consumer gets all the messages, regardless 
of the number of consumers attached. 

<pre class="codeblock">
amqutil --properties JMSXGroupID=something --produce ...
</pre>

<h2>Limitations</h2>

<code>amqutil</code> is really only useful for
sending and receiving text messages; JMS does support other formats, of
course, but
their contents are typically only meaningful to specific the applications
the use them. However, the utility can also work with bytes messages --
and these are usually quicker to generate when they need to be large.
<p/>
The utility was developed and tested using Sun/Oracle JDK 1.7. Although it
uses no specific Java 7 language features or APIs, there are
many dependent libraries and drivers, and these may not work with earlier
Java versions. 
<p/>
Apache Artemis supports the OpenWire protocol that was developed for ActiveMQ,
so the same client runtime (from ActiveMQ) can be used for both brokers.
Howeve, ActiveMQ-specific features of OpenWire, such as the ability to
list destinations on the broker, may not be supported by Artemis. 
The same limitation applies to the AMQP protocol: not every command
provided by <code>amqutil</code> will work with an AMQP-based broker.
<p/>
At present, <code>amqutil</code> is Enlish-language only.

<h2>Revision history</h2>

<table cellpadding="5" cellspacing="5">
<tr>
<td valign="top">
0.1.5,&nbsp;September&nbsp;2022
</td>
<td valign="top">
Added JMS 2.0 shared subscription support 
</td>
</tr>
<tr>
<td valign="top">
0.1.4,&nbsp;February&nbsp;2020
</td>
<td valign="top">
Added Artemis support. Fixed broken manual command.
</td>
</tr>
<tr>
<td valign="top">
0.1.3,&nbsp;September&nbsp;2018
</td>
<td valign="top">
Added "textonly" format
</td>
</tr>
<tr>
<td valign="top">
0.1.2,&nbsp;January&nbsp;2018
</td>
<td valign="top">
Preliminary AMQP support using Apache Qpid
</td>
</tr>
<tr>
<td valign="top">
0.1.0,&nbsp;March&nbsp;2015
</td>
<td valign="top">
Re-written so that command-line arguments are specified differently for
each sub-command, reducing the chance of the user providing 
arguments that have no effect.
</td>
</tr>
<tr>
<td valign="top">
0.0.1
</td>
<td valign="top">
First functional release
</td>
</tr>
</table>
 
<h2>Author</h2>

<code>amqutil</code> is mainted by Kevin Boone (kevin at railwayterrace dot com). It is distributed under the terms of the GNU Public License, version :.0,
in the hope that it will be useful. However, there is no warranty of any kind.
Please report bugs through GitHub.

