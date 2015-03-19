/*==========================================================================
amqutil
App.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import org.apache.activemq.ActiveMQConnectionFactory;
import javax.jms.Connection;
import javax.jms.Session;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.jms.MessageProducer;
import javax.jms.DeliveryMode;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;
import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.io.FileUtils;
import java.io.IOException; 
import java.io.PrintStream; 
import java.io.File; 
import java.io.InputStream; 
import java.io.InputStreamReader; 
import java.io.BufferedReader; 
import java.net.URL; 
import java.util.Enumeration; 
import java.util.Set; 
import java.util.Map; 
import java.util.Iterator; 
import org.apache.activemq.advisory.DestinationSource;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTempQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.apache.activemq.command.ActiveMQTempTopic;
import org.apache.activemq.ActiveMQMessageConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/*=========================================================================
 main class for amqutil
=========================================================================*/
public class App 
  {
/*=========================================================================
  showUsage 
=========================================================================*/
  static void showUsage (Options options, PrintStream o)
    {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp ("amqutil", options);
    }

/*=========================================================================
  showVersion
=========================================================================*/
  static void showVersion (PrintStream o)
    {
    o.println ("amqutil version 0.0.1 Copyright (c)2015 Kevin Boone");
    }

/*=========================================================================
  showLongHelp
=========================================================================*/
  static void showLongHelp () throws Exception
    {
    URL url = ClassLoader.getSystemClassLoader().getResource ("longhelp.txt");
    InputStream is = url.openStream();
    BufferedReader br = new BufferedReader (new InputStreamReader (is)); 
    String line;
    while ((line = br.readLine()) != null)
      {
      System.out.println (line);
      }
    br.close();
    is.close();
    }

/*=========================================================================
  showManual
=========================================================================*/
  static void showManual () throws Exception
    {
    URL url = ClassLoader.getSystemClassLoader().getResource ("manual.txt");
    InputStream is = url.openStream();
    BufferedReader br = new BufferedReader (new InputStreamReader (is)); 
    String line;
    while ((line = br.readLine()) != null)
      {
      System.out.println (line);
      }
    br.close();
    is.close();
    }

/*=========================================================================
  readFile
=========================================================================*/
  static String readFile (String path) throws IOException
    {
    return FileUtils.readFileToString (new File (path));
    }

/*=========================================================================
  getFactory 
=========================================================================*/
  static ActiveMQConnectionFactory getFactory (String host, int port, 
      String url)
    {
    ActiveMQConnectionFactory factory = null; 
    if (url != null && url.length() != 0)
      {
      factory = new ActiveMQConnectionFactory (url);
      }
    else
      {
      factory = new ActiveMQConnectionFactory
          ("tcp://" + host + ":" + port);
      }
    return factory;
    }

/*=========================================================================
  main 
=========================================================================*/
  public static void main( String[] args )
      throws Exception
    {
    // Default values of command line arguments
    String host = "localhost";
    int port = 61616;
    String logLevel = "error";
    String user = "admin";
    String pass = "admin";
    String destination = "__test_destination";
    String durable = null; 
    String format = "short";
    String properties = "";

    String file = ""; // No default -- if not given, don't read/write file
    int n = 1; // n is the number of messages to process, or a specific
               //   message number, depending on content
    int length = 500; // Length of message generated internally
    String url = "";  // No default -- if not given, don't use it
    int sleep = 0;
    int batchSize = 0; 

    // Flags set by parsing command line
    boolean produce = false;
    boolean publish = false;
    boolean consume = false;
    boolean subscribe = false;
    boolean browse = false;
    boolean show = false;
    boolean time = false;
    boolean list = false;
    boolean longhelp = false;
    boolean manual = false;
    boolean showpercent = false;
    boolean nonPersistent = false;

    // donesomthing is used to track whether the supplied command-line options
    //  actually resulted in some operation being performed
    boolean donesomething = false;

    // Start of command-line parsing
 
    Options options = new Options();
    options.addOption ("b", "batch", true, "set batch size");
    options.addOption (null, "browse", false, "browse messages on a queue");
    options.addOption (null, "consume", false, "consume messages from a queue");
    options.addOption ("d", "destination", true, 
      "destination (queue or topic) name");
    options.addOption (null, "durable", true, 
      "enable durable subscription with specified client ID");
    options.addOption (null, "format", true, 
      "display format with --consume: none|short|long|text");
    options.addOption (null, "file", true, 
      "file to read or write text messages");
    options.addOption (null, "list", false, 
      "list destinations");
    options.addOption ("h", "help", false, "show help summary");
    options.addOption (null, "length", true, 
      "length of internally-generated message, in characters (default 500)");
    options.addOption (null, "loglevel", true, 
      "set log level -- error, info, etc");
    options.addOption (null, "longhelp", false, "show example usage");
    options.addOption (null, "manual", false, "print the whole manual");
    options.addOption ("n", "number", true, "message number or count");
    options.addOption (null, "nonpersistent", false,  
      "enable non-persistent delivery");
    options.addOption (null, "host", true, "set server hostname");
    options.addOption (null, "percent", false, 
      "show progress percentage");
    options.addOption ("p", "password", true, "broker password for connection");
    options.addOption (null, "port", true, "set server port");
    options.addOption (null, "properties", true, "add header properties");
    options.addOption (null, "publish", false, "publish to a topic");
    options.addOption (null, "produce", false, "produce messages");
    options.addOption (null, "show", false, 
      "show the message at position given by --number");
    options.addOption (null, "sleep", true, 
      "sleep for the specified number of milliseconds between each message");
    options.addOption (null, "subscribe", false, 
      "consume messages by subscribing to a topic");
    options.addOption (null, "time", false, 
      "show time to complete operation in msec");
    options.addOption ("u", "user", true, "broker username for connection");
    options.addOption (null, "url", true, "broker connection url");
    options.addOption ("v", "version", false, "show version");

    CommandLineParser clp = new GnuParser();
    try
      {
      CommandLine cl = clp.parse (options, args);

      if (cl.hasOption ("v"))
        {
        showVersion (System.out);
        System.exit (0);
        }

      if (cl.hasOption ("h"))
        {
        showUsage (options, System.out);
        System.exit (0);
        }
     
      if (cl.hasOption ("longhelp"))
        {
        showLongHelp ();
        System.exit (0);
        }
     
      if (cl.hasOption ("manual"))
        {
        showManual ();
        System.exit (0);
        }
     
      String _destination = cl.getOptionValue ("destination");
      if (_destination != null) destination = _destination;
  
      String _logLevel = cl.getOptionValue ("loglevel");
      if (_logLevel != null) logLevel = _logLevel;
  
      String _durable = cl.getOptionValue ("durable");
      if (_durable != null) durable = _durable;
  
      String _host = cl.getOptionValue ("host");
      if (_host != null) host = _host;
  
      String _port = cl.getOptionValue ("port");
      if (_port != null) port = Integer.parseInt (_port);

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

      String _format = cl.getOptionValue ("format");
      if (_format != null) format = _format; 

      String _file = cl.getOptionValue ("file");
      if (_file != null) file = _file; 

      String _user = cl.getOptionValue ("user");
      if (_user != null) user = _user; 

      String _pass = cl.getOptionValue ("password");
      if (_pass != null) pass = _pass; 

      String _url = cl.getOptionValue ("url");
      if (_url != null) url = _url; 

      if (cl.hasOption ("produce"))
        produce = true;

      if (cl.hasOption ("publish"))
        publish = true;

      if (cl.hasOption ("nonpersistent"))
        nonPersistent = true;

      if (cl.hasOption ("consume"))
        consume = true;

      if (cl.hasOption ("browse"))
        browse = true;

      if (cl.hasOption ("show"))
        show = true;

      if (cl.hasOption ("subscribe"))
        subscribe = true;

      if (cl.hasOption ("time"))
        time = true;

      if (cl.hasOption ("list"))
        list = true;

      if (cl.hasOption ("percent"))
        showpercent = true;
      }
    catch (Exception e)
      {
      System.err.println (e.getMessage());
      showUsage (options, System.err);
      System.exit (-1);
      }
    
    // End of command-line parsing

    // Set the log level early, before anything gets logged. In particular,
    //  we can't even call getLogger until we've done this
    System.setProperty ("log.level", logLevel);
    
    Logger logger = LoggerFactory.getLogger 
      ("amqutil");


    // For convenience, set a flag if we are batch processing
    boolean batch = false;
    if (batchSize != 0) batch = true; 


    long start = System.currentTimeMillis();
 
    if (produce)
      {
      // Start of message production 

      logger.debug ("Started message production");     

      donesomething = true; 

      ActiveMQConnectionFactory factory = getFactory (host, port, url); 

      Connection connection = factory.createConnection(user, pass);
      Session session = connection.createSession
        (batch, Session.AUTO_ACKNOWLEDGE);

      Queue queue = session.createQueue(destination);

      MessageProducer producer = session.createProducer(queue);

      if (nonPersistent)
        producer.setDeliveryMode (DeliveryMode.NON_PERSISTENT);

      String text = "";
      if (file.equals(""))
        {
        // Make a text string
        for (int j = 0; j < length; j++)
          text += (char)('0' + (j % 10)); 
        }
      else
        {
        text = readFile (file); 
        }

      int oldpercent = 0;
      for (int i = 0; i < n; i++)
        {
        // Create a simple text message and send it
        TextMessage message = session.createTextMessage (text);

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
      // End of message production 

      logger.debug ("Finished message production");     
      }

    if (publish)
      {
      // Start of message publishing 
      logger.debug ("Started message publishing");     

      donesomething = true; 

      ActiveMQConnectionFactory factory = getFactory (host, port, url); 

      Connection connection = factory.createConnection(user, pass);
      Session session = connection.createSession
        (batch, Session.AUTO_ACKNOWLEDGE);

      Topic topic = session.createTopic(destination);

      MessageProducer producer = session.createProducer(topic);

      if (nonPersistent)
        producer.setDeliveryMode (DeliveryMode.NON_PERSISTENT);

      String text = "";
      if (file.equals(""))
        {
        // Make a text string
        for (int j = 0; j < length; j++)
          text += (char)('0' + (j % 10)); 
        }
      else
        {
        text = readFile (file); 
        }

      int oldpercent = 0;
      for (int i = 0; i < n; i++)
        {
        // Create a simple text message and send it
        TextMessage message = session.createTextMessage (text);

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

      logger.debug ("Finished message publishing");     
      // End of message publishing
      }

    if (consume)
      {
      // Start of message consumption 
      logger.debug ("Started message consumption");     

      donesomething = true; 
 
      ActiveMQConnectionFactory factory = getFactory (host, port, url); 

      Connection connection = factory.createConnection(user, pass);
      connection.start();

      Session session = connection.createSession
        (batch, Session.AUTO_ACKNOWLEDGE);

      Queue queue = session.createQueue(destination);

      MessageConsumer consumer = session.createConsumer(queue);

      int oldpercent = 0;
      for (int i = 0; i < n; i++)
        {
        Message message = consumer.receive();

        if (batch)
          if ((i + 1) % batchSize == 0) session.commit();

        if (sleep != 0)
          Thread.sleep (sleep);

        if (format.equals ("short") || format.equals("long") 
            || format.equals ("text"))
          {
          System.out.printf ("%s %s\n", message.getJMSMessageID(), 
            JMSUtil.getJMSType(message));
          }
        if (format.equals("long") || format.equals ("text"))
          {
          System.out.println (message);
          }
        if (format.equals ("text"))
          {
          if (message instanceof TextMessage)
            System.out.println (((TextMessage)message).getText());
          else
            System.out.println ("[Not a text message]");
          }
        if (!file.equals(""))
          {
          if (message instanceof TextMessage)
            {
            FileUtils.writeStringToFile (new File(file), 
             ((TextMessage)message).getText(), true);
            }
          }
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

      logger.debug ("Finished message consumption");     

      // End of message consumption 
      }

    if (subscribe)
      {
      // Start of message subscription 
      logger.debug ("Started message subscription");     

      donesomething = true; 
 
      ActiveMQConnectionFactory factory = getFactory (host, port, url); 

      Connection connection = factory.createConnection(user, pass);

      if (durable != null)
        connection.setClientID (durable);

      connection.start();
      
      Session session = connection.createSession
        (batch, Session.AUTO_ACKNOWLEDGE);

      Topic topic = session.createTopic(destination);

      MessageConsumer consumer = null; 
      if (durable != null)
        consumer = session.createDurableSubscriber (topic, "amqutil");
      else
        consumer = session.createConsumer(topic);

      int oldpercent = 0;
      for (int i = 0; i < n; i++)
        {
        Message message = consumer.receive();

        if (batch)
          if ((i + 1) % batchSize == 0) session.commit();

        if (sleep != 0)
          Thread.sleep (sleep);

        if (format.equals ("short") || format.equals("long") 
            || format.equals ("text"))
          {
          System.out.printf ("%s %s\n", message.getJMSMessageID(), 
            JMSUtil.getJMSType(message));
          }
        if (format.equals("long") || format.equals ("text"))
          {
          System.out.println (message);
          }
        if (format.equals ("text"))
          {
          if (message instanceof TextMessage)
            System.out.println (((TextMessage)message).getText());
          else
            System.out.println ("[Not a text message]");
          }
        if (!file.equals(""))
          {
          if (message instanceof TextMessage)
            {
            FileUtils.writeStringToFile (new File(file), 
             ((TextMessage)message).getText(), true);
            }
          }
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
      // End of message subscription 

      logger.debug ("Finished message subscription");     
      }

    if (browse || show)
      {
      // Start of message browsing 
      logger.debug ("Started message browsing");     

      donesomething = true; 

      ActiveMQConnectionFactory factory = getFactory (host, port, url); 

      Connection connection = factory.createConnection(user, pass);
      connection.start();

      Session session = connection.createSession
        (false, Session.AUTO_ACKNOWLEDGE);

      Queue queue = session.createQueue(destination);

      QueueBrowser qb = session.createBrowser (queue);

      Enumeration e = qb.getEnumeration();
      int num = 0;
      while (e.hasMoreElements())
        {
        Message m = (Message)e.nextElement();
        if (browse)
          System.out.printf ("% 6d %s %s\n", num, m.getJMSMessageID(), 
            JMSUtil.getJMSType(m));
        if (show)
          {
          if (n == num)
            {
            System.out.printf ("% 6d %s %s\n", num, m.getJMSMessageID(), 
              JMSUtil.getJMSType(m));
            System.out.println (m);
            if (m instanceof TextMessage)
              System.out.println (((TextMessage)m).getText());
            }
          }
        num++;
        }

      qb.close();
    
      connection.close();

      logger.debug ("Finished message browsing");     

      // End of message browsing 
      }

    if (list)
      {
      // Start of destination listing 
      logger.debug ("Started destination listing");     

      donesomething = true; 

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

      logger.debug ("Finished destination listing");     
      // End of destination listing 
      }

    if (time && donesomething)
      System.out.println ("" + (System.currentTimeMillis() - start) + " msec");

    if (!donesomething)
      {
      System.err.println 
("Use one of the options --produce, --consume, --publish, --subscribe,");
      System.err.println 
("  --show, or --browse. Use --help or --longhelp for more information.");
      }
    }
  }
