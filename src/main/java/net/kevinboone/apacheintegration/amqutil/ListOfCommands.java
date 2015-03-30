/*==========================================================================
amqutil
List.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import java.util.*;

/** Manages the list of commands available to amqutil. 
 */
public class ListOfCommands
{
/**
 * Gets the Cmd object that represents the command argument
 */
static Cmd findCmd (String arg)
  {
  Cmd cmd = null; 
  List<Cmd> list = listCmds();
  for (Cmd c : list)
    {
    if (c.getName().equalsIgnoreCase (arg))
      return c;
    }
  return null;
  }

static List<Cmd> listCmds()
  {
  // Try to keep the list in alphanetoc order of name, as it makes the
  //  'help' output nicer
  ArrayList list = new ArrayList<Cmd>();
  list.add (new CmdBrowse());
  list.add (new CmdCommands());
  list.add (new CmdConsume());
  list.add (new CmdCount());
  list.add (new CmdHelp());
  list.add (new CmdList());
  list.add (new CmdManual());
  list.add (new CmdProduce());
  list.add (new CmdPublish());
  list.add (new CmdShow());
  list.add (new CmdSubscribe());
  list.add (new CmdVersion());
  return list;
  }

}

