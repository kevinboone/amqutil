/*==========================================================================
amqutil
Usage.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import java.io.*;

/** 
 * Methods for printing and formatting usage information.
 */
public class Usage
{
public static void showBriefUsage (PrintStream p)
  {
  p.println ("Usage: amqutil {command} [options]");
  p.println ("\"amqutil help\" for more information");
  p.println ("\"amqutil manual\" to display complete manual");
  }
}





