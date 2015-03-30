/*==========================================================================
amqutil
ArgParseException.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;

/**
 * Thrown to indicate an error in command-line parsing 
 */
public class ArgParseException extends Exception
{
  public ArgParseException (Exception e)
    {
    super (e.getMessage());
    }

  public ArgParseException (String s)
    {
    super (s);
    }
}


