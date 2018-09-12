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
  if (format.equals ("text") || format.equals ("textonly"))
          {
          if (message instanceof TextMessage)
            System.out.println (((TextMessage)message).getText());
          else if (message instanceof BytesMessage)
            {
            BytesMessage bm = (BytesMessage)message;
            long l = bm.getBodyLength();
            byte[] bb = new byte [(int)l]; 
            bm.readBytes (bb, (int)l);
            for (int i = 0; i < l; i++)
              {
              byte b = bb[i];
              System.out.printf ("%02X ", b);
              if (((i + 1) % 16) == 0) System.out.println ("");
              }
            System.out.println ("");
            }
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
          else if (message instanceof BytesMessage)
            {
            BytesMessage bm = (BytesMessage)message;
            long l = bm.getBodyLength();
            byte[] bb = new byte [(int)l]; 
            bm.readBytes (bb, (int)l);
            FileOutputStream fos = new FileOutputStream (new File(file));
            fos.write (bb);
            fos.close();
            }
          }
  }

/** Make a JMS message of the specified type, from the specified file if
  * there is one.
 */
public static Message makeMessage (Session session, String file, int length,
      String type)
    throws JMSException, IOException, BadTypeException
  {
  if ("text".equals (type))
    {
    String text = "";
    if (file.equals(""))
      {
      // Make a text string
      StringBuffer sb = new StringBuffer (length);
      for (int j = 0; j < length; j++)
          sb.append ((char)('0' + (j % 10))); 
      text = new String (sb);
      }
    else
      {
      text = Cmd.readFile (file); 
      }
    return session.createTextMessage(text);
    }
  else if ("bytes".equals (type))
    {
    BytesMessage bm = session.createBytesMessage();
    if (file.equals(""))
      {
      // Make a bytes string
      byte[] bb = new byte[length];
      for (int j = 0; j < length; j++)
          bb [j] = (byte) (j % 256); 
      bm.writeBytes (bb, 0, length);
      }
    else
      {
      FileInputStream fis = new FileInputStream (new File (file));
      int l = fis.available();
      byte[] bb = new byte[l];
      fis.read(bb, 0, l);
      bm.writeBytes (bb);
      fis.close();
      }
    return bm; 
    }
  else
    throw new BadTypeException (type);
  }
}


