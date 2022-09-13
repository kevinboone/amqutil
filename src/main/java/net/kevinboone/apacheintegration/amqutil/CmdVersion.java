/*==========================================================================
amqutil
CmdVersion.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import java.io.*;
import java.net.*;

/**
 * Implementation of the "version" command 
 */
public class CmdVersion extends Cmd
{
  @Override 
  public String getName () { return "version"; }

  @Override 
  public String getShortUsage () { return "amqutil version {options}"; }

  @Override 
  public String getShortDescription () 
    { return "Displays the software version"; }


  @Override 
  public int run() throws Exception
    {
    System.out.println ("Version 0.1.5, copyright (c)2015-2022 Kevin Boone.");
    System.out.println 
       ("Distributed under the terms of the GNU Public Licence, v3.0");
    return 0;
    }

}



