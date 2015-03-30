/*==========================================================================
amqutil
BadTypeException.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;

/**
 * Thrown to indicate that the user specified an unsupported message type 
 */
public class BadTypeException extends Exception
{
  public BadTypeException (Exception e)
    {
    super (e.getMessage());
    }

  public BadTypeException (String s)
    {
    super (s);
    }
}



