/*==========================================================================
amqutil
CmdEcho.java
(c)2019 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import java.io.*;
import java.net.*;
import org.apache.commons.cli.*;
import org.apache.activemq.*;
import javax.jms.*;
import org.apache.commons.io.FileUtils;
import java.util.Random;


/**
 * Implementation of the "echo" command 
 */
public class CmdEcho extends Cmd
{

  @Override 
  public String getName () { return "echo"; }

  @Override 
  public String getShortUsage () { return "amqutil echo {options}"; }

  @Override 
  public String getShortDescription () 
    { return "Receives messages and forwards them to the JMS reply-to address"; }

  @Override 
  public int run() throws Exception
    {
    // Default values of command line arguments
    String host = DEFAULT_HOST; 
    int port = DEFAULT_PORT;
    String user = DEFAULT_USER;
    String pass = DEFAULT_PASS;
    String destination = DEFAULT_DESTINATION; 
    String properties = "";
    String format = "short";
    int sleep = 0;

    String url = "";  // No default -- if not given, don't use it

    String[] nonSwitchArgs = cl.getArgs();

    String _destination = cl.getOptionValue ("destination");
    if (_destination != null) destination = _destination;
  
    String _host = cl.getOptionValue ("host");
    if (_host != null) host = _host;
  
    String _port = cl.getOptionValue ("port");
    if (_port != null) port = Integer.parseInt (_port);

    String _sleep = cl.getOptionValue ("sleep");
    if (_sleep != null) sleep = Integer.parseInt (_sleep);

    String _format = cl.getOptionValue ("format");
    if (_format != null) format = _format; 

    String _user = cl.getOptionValue ("user");
    if (_user != null) user = _user; 

    String _pass = cl.getOptionValue ("password");
    if (_pass != null) pass = _pass; 

    String _url = cl.getOptionValue ("url");
    if (_url != null) url = _url; 

    String _properties = cl.getOptionValue ("properties");
    if (_properties != null) properties = _properties;

  
    ConnectionFactory factory = getFactory (host, port, url); 

    Connection connection = factory.createConnection(user, pass);
    connection.start();

    Session session = connection.createSession
        (false, Session.AUTO_ACKNOWLEDGE);

    Queue queue = session.createQueue(destination);

    MessageConsumer consumer = session.createConsumer (queue);

    while (true) 
      {
      javax.jms.Message message = consumer.receive();
      JMSUtil.outputMessage (format, message, "");

      if (sleep != 0)
        Thread.sleep (sleep);

      Destination replyTo = message.getJMSReplyTo();
      if (replyTo != null)
        {
        System.out.println ("Replying to " + replyTo);
        MessageProducer producer = session.createProducer (replyTo);
	producer.send (message);
	producer.close();
        }
      else
        {
        logger.warn ("Message has no reply-to address");
	}
      }
    }

  @Override
  public void setupOptions ()
    {
    super.setupOptions();
    options.addOption (null, "format", true, 
      "display format: none|short|long|text|textonly");
    options.addOption ("d", "destination", true, 
      "destination (queue or topic) name");
    options.addOption (null, "host", true, "set server hostname");
    options.addOption ("p", "password", true, "broker password for connection");
    options.addOption (null, "port", true, "set server port");
    options.addOption (null, "sleep", true, 
      "sleep for the specified number of milliseconds between each message");
    options.addOption ("u", "user", true, "broker username for connection");
    options.addOption (null, "url", true, "broker connection url");
    options.addOption (null, "properties", true, "add header properties");
    }

}




