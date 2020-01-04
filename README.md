# amqutil

`amqutil` is a command-line Java utility for exercising message brokers.
It was originally written for use with Apache ActiveMQ, and then adapted
for Apache Artemis. However, by supporting the AMQP protocol, `amqutil`
might be used with a range of other brokers.

`amqutil` supports sending and receiving messages, browsing messages,
and listing message destinations (on brokers that support this, like 
ActiveMQ). It can create persistent and non-persistent messages of
arbitrary size, with specified or internally-generated content, and set
arbitrary properties (headers). It can act as a durable or non-durable
client, consume messages using JMS transactions if required, singly
or in fixed-size batches. Messaging operations can be timed for
benchmarking purposes.

For more details, see the HTML file README\_amqutil.html.
