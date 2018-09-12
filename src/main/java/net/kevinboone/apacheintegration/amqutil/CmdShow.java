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
 * Implementation of the "show" command 
 */
public class CmdShow extends Cmd
{

  @Override 
  public String getName () { return "show"; }

  @Override 
  public String getShortUsage () { return "amqutil show {options} {index}"; }

  @Override 
  public String getShortDescription () 
    { return "Shows message {index} on a destination (default 0)"; }

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
    String format = "short";

    int n = 0; // n is the number of messages to process, or a specific
               //   message number, depending on content
    String url = "";  // No default -- if not given, don't use it

    String[] nonSwitchArgs = cl.getArgs();
    if (nonSwitchArgs.length > 0)
      n = Integer.parseInt (nonSwitchArgs[0]);

    String _destination = cl.getOptionValue ("destination");
    if (_destination != null) destination = _destination;
  
    String _host = cl.getOptionValue ("host");
    if (_host != null) host = _host;
  
    String _port = cl.getOptionValue ("port");
    if (_port != null) port = Integer.parseInt (_port);

    String _file = cl.getOptionValue ("file");
    if (_file != null) file = _file; 

    String _user = cl.getOptionValue ("user");
    if (_user != null) user = _user; 

    String _format = cl.getOptionValue ("format");
    if (_format != null) format = _format; 

    String _pass = cl.getOptionValue ("password");
    if (_pass != null) pass = _pass; 

    String _url = cl.getOptionValue ("url");
    if (_url != null) url = _url; 

    ConnectionFactory factory = getFactory (host, port, url); 

    Connection connection = factory.createConnection(user, pass);
    connection.start();

    Session session = connection.createSession
        (false, Session.AUTO_ACKNOWLEDGE);

    javax.jms.Queue queue = session.createQueue(destination);

    QueueBrowser qb = session.createBrowser (queue);

    Enumeration e = qb.getEnumeration();
    int num = 0;
    boolean gotOne = false;
    while (e.hasMoreElements() && !gotOne)
      {
      javax.jms.Message m = (javax.jms.Message)e.nextElement();
      if (true)
        {
        if (n == num)
          {
          gotOne = true;
          JMSUtil.outputMessage (format, m, file);
          }
        }
      num++;
      }

    if (!gotOne)
      logger.error ("No message with index " + n);

    qb.close();
    
    connection.close();

    return 0;
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
    options.addOption ("u", "user", true, "broker username for connection");
    options.addOption (null, "url", true, "broker connection url");
    options.addOption ("i", "file", true, 
      "output message to text file");
    }

}




