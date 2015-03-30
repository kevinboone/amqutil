/*==========================================================================
amqutil
App.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
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
import org.slf4j.*;



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

/** 
 * Start here
 */
  public static void main( String[] args )
      throws Exception
    {
    if (args.length < 1)
      {
      Usage.showBriefUsage (System.err);
      System.exit(-1);
      }

    int ret = 0; // OS return code 

    Cmd cmd = ListOfCommands.findCmd (args[0]); 

    if (cmd != null)
      {
      try
        {
        String[] newArgs = new String[args.length - 1];
        System.arraycopy (args, 1, newArgs, 0, args.length - 1);
        cmd.setupOptions();
        cmd.parseArgs (newArgs);
        ret = cmd.doRun ();
        }
      catch (ArgParseException e)
        {
        Logger logger = LoggerFactory.getLogger 
          ("amqutil");
        logger.error ("Error parsing arguments for \"" + args[0] 
          + "\" command: " + e.getMessage());
        }
      }
    else
      {
      Logger logger = LoggerFactory.getLogger 
        ("amqutil");
      logger.error ("Unknown command \"" + args[0] + "\"");
      }

    System.exit (ret);
    }
  }



