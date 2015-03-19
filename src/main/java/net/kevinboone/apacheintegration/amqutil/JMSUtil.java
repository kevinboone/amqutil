/*==========================================================================
amqutil
JMSUtil.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import javax.jms.TextMessage;
import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.Message;
import javax.jms.JMSException;
import org.slf4j.Logger;

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

}


