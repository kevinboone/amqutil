/*==========================================================================
amqutil
JMSUtil.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import javax.jms.*;
import java.io.*;
import org.slf4j.Logger;
import org.apache.commons.io.FileUtils;

public class JMSUtil
{
/**
  getJMSType returns a textual type name for a message of a specific class 
*/
  public static String getJMSType (Message m)
    {
    if (m instanceof BytesMessage) return "BytesMessage";
    if (m instanceof MapMessage) return "MapMessage";
    if (m instanceof ObjectMessage) return "ObjectMessage";
    if (m instanceof StreamMessage) return "StreamMessage";
    if (m instanceof TextMessage) return "TextMessage";
    return "[unknown]";
    }

/**
  setProperties parses a string of properties and applies them to the
  message
*/
  public static void setProperties (Logger logger, Message m, String props)
      throws JMSException
    {
    if (props == null) return;
    if (props.length() == 0) return;
    String[] propArray = props.split (",");
    for (String prop : propArray)
      {
      String[] token = prop.split("=");
      if (token.length == 2)
        {
        String key = token[0];
        String value = token[1];
        m.setStringProperty (key, value);
        }
      else
        {
        logger.error ("Invalid property spec \"" + prop + "\"");
        }
      }
    }

public static void outputMessage (String format, Message message, String file)
    throws JMSException, IOException
  {
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
  }
}


