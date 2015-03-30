/*==========================================================================
amqutil
CmdManual.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import java.io.*;
import java.net.*;

/**
 * Implementation of the "manual" command 
 */
public class CmdManual extends Cmd
{
  @Override 
  public String getName () { return "manual"; }

  @Override 
  public String getShortUsage () { return "amqutil manual {options}"; }

  @Override 
  public String getShortDescription () 
    { return "Displays the manual"; }


  @Override 
  public int run() throws Exception
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
    return 0;
    }

}


