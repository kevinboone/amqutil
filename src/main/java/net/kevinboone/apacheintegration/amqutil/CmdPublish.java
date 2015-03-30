/*==========================================================================
amqutil
CmdManual.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.commons.cli.*;
import org.apache.activemq.*;
import javax.jms.*;
import org.apache.commons.io.FileUtils;

/**
 * Implementation of the "publish" command 
 */
public class CmdPublish extends Cmd
{

  @Override 
  public String getName () { return "publish"; }

  @Override 
  public String getShortUsage () { return "amqutil publish {options} {count}"; }

  @Override 
  public String getShortDescription () 
    { return "Publish {count} messages to a destination (default 1)"; }

  @Override 
  public int run() throws Exception
    {
    // Default values of command line arguments
    String host = DEFAULT_HOST; 
    int port = DEFAULT_PORT;
    String user = DEFAULT_USER;
    String pass = DEFAULT_PASS;
    String destination = DEFAULT_DESTINATION; 
    String file = ""; // No default -- if not given, don't read/write file
    int sleep = 0;
    boolean showpercent = false;
    int batchSize = 0; 
    int length = 500; // Length of message generated internally
    boolean nonPersistent = false;
    String properties = "";
    String type = "text";

    int n = 1; // n is the number of messages to process, or a specific
               //   message number, depending on content
    String url = "";  // No default -- if not given, don't use it

    String[] nonSwitchArgs = cl.getArgs();
    if (nonSwitchArgs.length > 0)
      n = Integer.parseInt (nonSwitchArgs[0]);

    String _destination = cl.getOptionValue ("destination");
    if (_destination != null) destination = _destination;
  
    String _host = cl.getOptionValue ("host");
    if (_host != null) host = _host;
  
    String _type = cl.getOptionValue ("msgtype");
    if (_type != null) type = _type;
  
    String _port = cl.getOptionValue ("port");
    if (_port != null) port = Integer.parseInt (_port);

    String _file = cl.getOptionValue ("file");
    if (_file != null) file = _file; 

    String _user = cl.getOptionValue ("user");
    if (_user != null) user = _user; 

    String _pass = cl.getOptionValue ("password");
    if (_pass != null) pass = _pass; 

    String _url = cl.getOptionValue ("url");
    if (_url != null) url = _url; 

    String _sleep = cl.getOptionValue ("sleep");
    if (_sleep != null) sleep = Integer.parseInt (_sleep);

    if (cl.hasOption ("percent"))
      showpercent = true;

    String _batchSize = cl.getOptionValue ("batch");
    if (_batchSize != null) batchSize  = Integer.parseInt (_batchSize);

    String _L = cl.getOptionValue ("length");
    if (_L != null) length = Integer.parseInt (_L);

    if (cl.hasOption ("nonpersistent"))
      nonPersistent = true;

    String _properties = cl.getOptionValue ("properties");
    if (_properties != null) properties = _properties;

    boolean batch = false;
    if (batchSize != 0) batch = true; 


    ActiveMQConnectionFactory factory = getFactory (host, port, url); 

    Connection connection = factory.createConnection(user, pass);
    Session session = connection.createSession
        (batch, Session.AUTO_ACKNOWLEDGE);

    Topic topic = session.createTopic(destination);

    MessageProducer producer = session.createProducer(topic);

    if (nonPersistent)
        producer.setDeliveryMode (DeliveryMode.NON_PERSISTENT);

    javax.jms.Message message = JMSUtil.makeMessage (session, file, 
      length, type);

    int oldpercent = 0;
    for (int i = 0; i < n; i++)
        {
	JMSUtil.setProperties (logger, message, properties);
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
    options.addOption ("i", "file", true, 
      "input message from text file");
    options.addOption ("d", "destination", true, 
      "destination (queue or topic) name");
    options.addOption ("m", "msgtype", true, 
      "text|bytes");
    options.addOption (null, "host", true, "set server hostname");
    options.addOption ("p", "password", true, "broker password for connection");
    options.addOption (null, "port", true, "set server port");
    options.addOption ("u", "user", true, "broker username for connection");
    options.addOption (null, "url", true, "broker connection url");
    options.addOption (null, "sleep", true, 
      "sleep for the specified number of milliseconds between each message");
    options.addOption (null, "percent", false, 
      "show progress percentage");
    options.addOption ("b", "batch", true, "set batch size");
    options.addOption (null, "length", true, 
      "length of internally-generated message, in characters (default 500)");
    options.addOption (null, "nonpersistent", false,  
      "enable non-persistent delivery");
    options.addOption (null, "properties", true, "add header properties");
    }

}




