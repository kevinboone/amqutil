/*==========================================================================
amqutil
CmdManual.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import java.io.*;
import java.net.*;
import org.apache.commons.cli.*;
import org.apache.activemq.*;
import javax.jms.*;

/**
 * Implementation of the "produce" command 
 */
public class CmdProduce extends Cmd
{

  @Override 
  public String getName () { return "produce"; }

  @Override 
  public String getShortUsage () { return "amqutil produce {options} {count}"; }

  @Override 
  public String getShortDescription () 
    { return "Produces {count} messages to a destination (default 1)"; }

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
    boolean nonPersistent = false;
    boolean showpercent = false;

    String file = ""; // No default -- if not given, don't read/write file
    int n = 1; // n is the number of messages to process, or a specific
               //   message number, depending on content
    int length = 500; // Length of message generated internally
    String url = "";  // No default -- if not given, don't use it
    String type = "text";
    int sleep = 0;
    int batchSize = 0; 
    int priority = -1; // Use default priority

    String _destination = cl.getOptionValue ("destination");
    if (_destination != null) destination = _destination;
  
    String _host = cl.getOptionValue ("host");
    if (_host != null) host = _host;
  
    String _type = cl.getOptionValue ("msgtype");
    if (_type != null) type = _type;
  
    String _port = cl.getOptionValue ("port");
    if (_port != null) port = Integer.parseInt (_port);

    String _priority = cl.getOptionValue ("priority");
    if (_priority != null) priority = Integer.parseInt (_priority);

    String _properties = cl.getOptionValue ("properties");
    if (_properties != null) properties = _properties;

    String _sleep = cl.getOptionValue ("sleep");
    if (_sleep != null) sleep = Integer.parseInt (_sleep);

    String _batchSize = cl.getOptionValue ("batch");
    if (_batchSize != null) batchSize  = Integer.parseInt (_batchSize);

    String _n = cl.getOptionValue ("number");
    if (_n != null) n = Integer.parseInt (_n);

    String _L = cl.getOptionValue ("length");
    if (_L != null) length = Integer.parseInt (_L);

    String _file = cl.getOptionValue ("file");
    if (_file != null) file = _file; 

    String _user = cl.getOptionValue ("user");
    if (_user != null) user = _user; 

    String _pass = cl.getOptionValue ("password");
    if (_pass != null) pass = _pass; 

    String _url = cl.getOptionValue ("url");
    if (_url != null) url = _url; 

    if (cl.hasOption ("nonpersistent"))
      nonPersistent = true;

    if (cl.hasOption ("percent"))
      showpercent = true;
    
    // For convenience, set a flag if we are batch processing
    boolean batch = false;
    if (batchSize != 0) batch = true; 

    String[] nonSwitchArgs = cl.getArgs();
    if (nonSwitchArgs.length > 0)
      n = Integer.parseInt (nonSwitchArgs[0]);

    ActiveMQConnectionFactory factory = getFactory (host, port, url); 

    Connection connection = factory.createConnection(user, pass);
    Session session = connection.createSession
        (batch, Session.AUTO_ACKNOWLEDGE);

    Queue queue = session.createQueue(destination);

    MessageProducer producer = session.createProducer(queue);
    if (priority >= 0)
      producer.setPriority (priority);

    if (nonPersistent)
        producer.setDeliveryMode (DeliveryMode.NON_PERSISTENT);

    javax.jms.Message message = JMSUtil.makeMessage 
      (session, file, length, type);


    int oldpercent = 0;
    for (int i = 0; i < n; i++)
      {
      JMSUtil.setProperties (logger, message, properties);
      if (priority >= 0) 
        message.setJMSPriority (priority);
      producer.send(message);

      if (batch)
          if ((i + 1) % batchSize == 0) session.commit();

      if (sleep != 0)
          Thread.sleep (sleep);

      if (showpercent)
        {
        int percent = i * 100 / n;
        if (percent != oldpercent)
            System.out.println ("" + percent + "%");
        oldpercent = percent;
        }
      }

    if (batch) session.commit();

    connection.close();

    return 0;
    }

  @Override
  public void setupOptions ()
    {
    super.setupOptions();
    options.addOption ("b", "batch", true, "set batch size");
    options.addOption ("m", "msgtype", true, 
      "text|bytes");
    options.addOption ("d", "destination", true, 
      "destination (queue or topic) name");
    options.addOption ("i", "file", true, 
      "input message from text file");
    options.addOption (null, "length", true, 
      "length of internally-generated message, in characters (default 500)");
    options.addOption (null, "nonpersistent", false,  
      "enable non-persistent delivery");
    options.addOption (null, "host", true, "set server hostname");
    options.addOption (null, "percent", false, 
      "show progress percentage");
    options.addOption ("p", "password", true, "broker password for connection");
    options.addOption (null, "port", true, "set server port");
    options.addOption (null, "priority", true, "set message priority (0-9)");
    options.addOption (null, "properties", true, "add header properties");
    options.addOption (null, "sleep", true, 
      "sleep for the specified number of milliseconds between each message");
    options.addOption ("u", "user", true, "broker username for connection");
    options.addOption (null, "url", true, "broker connection url");
    }

}


