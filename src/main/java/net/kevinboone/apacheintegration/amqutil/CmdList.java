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
import org.apache.activemq.advisory.*;
import org.apache.activemq.command.*;
import org.apache.activemq.*;

/**
 * Implementation of the "list" command 
 */
public class CmdList extends Cmd
{

  @Override 
  public String getName () { return "list"; }

  @Override 
  public String getShortUsage () { return "amqutil list {options}"; }

  @Override 
  public String getShortDescription () 
    { return "Lists destinations on a broker"; }

  @Override 
  public int run() throws Exception
    {
    // Default values of command line arguments
    String host = DEFAULT_HOST; 
    int port = DEFAULT_PORT;
    String user = DEFAULT_USER;
    String pass = DEFAULT_PASS;
    String destination = DEFAULT_DESTINATION; 

    String url = "";  // No default -- if not given, don't use it

    String _host = cl.getOptionValue ("host");
    if (_host != null) host = _host;
  
    String _port = cl.getOptionValue ("port");
    if (_port != null) port = Integer.parseInt (_port);

    String _user = cl.getOptionValue ("user");
    if (_user != null) user = _user; 

    String _pass = cl.getOptionValue ("password");
    if (_pass != null) pass = _pass; 

    String _url = cl.getOptionValue ("url");
    if (_url != null) url = _url; 

    ActiveMQConnectionFactory factory = getFactory (host, port, url); 

    Connection connection = factory.createConnection(user, pass);
    connection.start();

    DestinationSource dsource = new DestinationSource (connection);
    dsource.start();
    Set<ActiveMQQueue> dsq = dsource.getQueues();
    for (ActiveMQQueue d : dsq)
        {
        System.out.println (d.getDestinationTypeAsString() 
          + " " + d.getPhysicalName());
        }
    Set<ActiveMQTempQueue> dstq = dsource.getTemporaryQueues();
    for (ActiveMQTempQueue d : dstq)
        {
        System.out.println (d.getDestinationTypeAsString() 
          + " " + d.getPhysicalName());
        }
    Set<ActiveMQTopic> dst = dsource.getTopics();
    for (ActiveMQTopic d : dst)
        {
        System.out.println (d.getDestinationTypeAsString() 
          + " " + d.getPhysicalName());
        }
    Set<ActiveMQTempTopic> dstt = dsource.getTemporaryTopics();
    for (ActiveMQTempTopic d : dstt)
        {
        System.out.println (d.getDestinationTypeAsString() 
          + " " + d.getPhysicalName());
        }
    
    connection.close();
    dsource.stop();

    return 0;
    }

  @Override
  public void setupOptions ()
    {
    super.setupOptions();
    options.addOption (null, "host", true, "set server hostname");
    options.addOption ("p", "password", true, "broker password for connection");
    options.addOption (null, "port", true, "set server port");
    options.addOption ("u", "user", true, "broker username for connection");
    options.addOption (null, "url", true, "broker connection url");
    }

}



