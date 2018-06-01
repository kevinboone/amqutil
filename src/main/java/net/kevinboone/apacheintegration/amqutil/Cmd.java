/*==========================================================================
amqutil
Cmd.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import org.slf4j.*;
import java.io.*;
import org.apache.commons.cli.*;
import org.apache.activemq.*;
import javax.jms.*;
import org.apache.commons.io.FileUtils;

/**
 * Base class extended by all amqutil command-line commands
 */
public abstract class Cmd
{
  protected Options options = null;
  protected CommandLine cl = null; 
  protected Logger logger = null;

  public static String DEFAULT_HOST = "localhost";
  public static int DEFAULT_PORT = 61616;
  public static String DEFAULT_USER= "admin";
  public static String DEFAULT_PASS= "admin";
  public static String DEFAULT_DESTINATION = "__test_destination";

  public Cmd ()
    {
    options = new Options();
    }

  /**
  Set the command-line options that all commands will support. Specidic
  commands will usually have to override this and add others. */
  public void setupOptions()
   {
   options.addOption (null, "time", false, 
      "show time to complete operation in msec");
   options.addOption (null, "help", false, 
      "show brief help");
   options.addOption (null, "loglevel", true, 
      "set log level -- error, info, etc");
   options.addOption ("q", "qpid", false, 
      "use AMQP protocol");
   }

  public abstract int run() throws Exception; 

  public void setLogger (Logger logger)
    {
    this.logger = logger;
    }

  /**
  doRun wraps the (abstract) run() method in timing and logging options
  */
  public int doRun() throws Exception
    {
    if (cl.hasOption ("help"))
      {
      briefHelp (System.out);
      return 0;
      }

    if (cl.hasOption ("qpid"))
      {
      System.setProperty ("amqutil.driver", "qpid");
      }

    boolean time = false;
    long start = 0;
    String _logLevel = cl.getOptionValue ("loglevel");
    if (_logLevel != null)
      System.setProperty ("log.level", _logLevel);

    logger = LoggerFactory.getLogger 
      ("net.kevinboone.apacheintegration");
  
    if (cl.hasOption ("time"))
      time = true;
    if (time)
      start = System.currentTimeMillis();
    int ret = run();
    if (time)
      {
      System.out.println ("Time elapsed: " + 
        (System.currentTimeMillis() - start) + " msec");
      }
    return ret;
    }

  public void parseArgs (String[] args)
      throws ArgParseException
    {
    CommandLineParser clp = new GnuParser();
    try
      {
      cl = clp.parse (options, args);
      }
    catch (Exception e)
      {
      throw new ArgParseException (e);
      }
    }

  public abstract String getName();
  public abstract String getShortDescription();
  public abstract String getShortUsage();

  public void showOptions ()
    {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp ("amqutil " + getName(), options);
    }

  public void briefHelp (PrintStream p)
    {
    p.println (getShortUsage());
    p.println (getShortDescription());
    showOptions();
    }

  /**
  Gets the Qpid Connection factory from either host/port or URL.
  URL takes precedence.
  */
  ConnectionFactory getQpidFactory (String host, int port, 
      String url)
    {
    ConnectionFactory factory = null; 
    if (url != null && url.length() != 0)
      {
      factory = new org.apache.qpid.jms.JmsConnectionFactory (url);
      if (!host.equals ("localhost") || port != 61616)
        {
        logger.warn ("Ignoring host/port arguments as a URL was specified");
        }
      }
    else
      {
      factory = new org.apache.qpid.jms.JmsConnectionFactory
          ("amqp://" + host + ":" + port);
      }
    return factory;
    }

  /**
  Gets the ActiveMQ Connection factory from either host/port or URL.
  URL takes precedence.
  */
  ActiveMQConnectionFactory getActiveMQFactory (String host, int port, 
      String url)
    {
    ActiveMQConnectionFactory factory = null; 
    if (url != null && url.length() != 0)
      {
      factory = new ActiveMQConnectionFactory (url);
      if (!host.equals ("localhost") || port != 61616)
        {
        logger.warn ("Ignoring host/port arguments as a URL was specified");
        }
      }
    else
      {
      factory = new ActiveMQConnectionFactory
          ("tcp://" + host + ":" + port);
      }
    return factory;
    }

  ConnectionFactory getFactory (String host, int port, 
      String url)
    {
    if ("qpid".equals (System.getProperty("amqutil.driver")))
      return getQpidFactory (host, port, url);
    else
      return getActiveMQFactory (host, port, url);
    }

  /** 
  Read a file into a string
  */
  static String readFile (String path) throws IOException
    {
    return FileUtils.readFileToString (new File (path));
    }


}

