/*==========================================================================
amqutil
CmdManual.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import java.io.*;
import java.util.*;
import java.net.*;

/**
 * Implementation of the "commands" command 
 */
public class CmdCommands extends Cmd
{
  @Override 
  public String getName () { return "commands"; }

  @Override 
  public String getShortUsage () { return "amqutil commands {options}"; }

  @Override 
  public String getShortDescription () 
    { return "Lists all amqutil commands"; }


  @Override 
  public int run() throws Exception
    {
    List<Cmd> list = ListOfCommands.listCmds();
    for (Cmd c : list)
      {
      System.out.println (c.getShortUsage());
      System.out.println (c.getShortDescription());
      System.out.println ("");
      }
    return 0;
    }

}



