package org.bridgedb.ws;

/*
Some SMTP servers require a username and password authentication before you
can use their Server for Sending mail. This is most common with couple
of ISP's who provide SMTP Address to Send Mail.

This Program gives any example on how to do SMTP Authentication
(User and Password verification)

This is a free source code and is provided as it is without any warranties and
it can be used in any your code for free.

Author : Sudhir Ancha
*/

import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import java.io.*;
import org.bridgedb.IDMapperException;
import org.bridgedb.utils.ConfigReader;

public class SendMailUsingAuthentication
{

  private Properties properties;
  
  // Add List of Email address to who email needs to be sent to
  private static final String[] emailList = {"cyb@cs.man.ac.uk", "test1@brenn.co.uk"};

  public SendMailUsingAuthentication() throws IDMapperException{
      properties = ConfigReader.getProperties("mailerConfig.properties");
  }
  
  public static void main(String args[]) throws Exception
  {
    SendMailUsingAuthentication smtpMailSender = new SendMailUsingAuthentication();
    //smtpMailSender.autoPostMail(emailSubjectTxt, emailMsgTxt, fileName);
    smtpMailSender.postMail(emailList, "This is a test", "Hi Alex just delete these", "brenninc@cs.man.ac.uk");
    System.out.println("Sucessfully Sent mail to All Users");
  }

  public void postMail( String recipients[ ], String subject,
                            String message , String from) throws MessagingException
  {
    boolean debug = false;

    Authenticator auth = new SMTPAuthenticator();
    Session session = Session.getDefaultInstance(properties, auth);

    session.setDebug(debug);

    // create a message
    Message msg = new MimeMessage(session);

    // set the from and to address
    InternetAddress addressFrom = new InternetAddress(from);
    msg.setFrom(addressFrom);

    InternetAddress[] addressTo = new InternetAddress[recipients.length];
    for (int i = 0; i < recipients.length; i++)
    {
        addressTo[i] = new InternetAddress(recipients[i]);
        System.out.println("sending to " + addressTo[i] );
    }
    msg.setRecipients(Message.RecipientType.TO, addressTo);


    // Setting the Subject and Content Type
    msg.setSubject(subject);
    msg.setContent(message, "text/plain");
    Transport.send(msg);
 }

/**
* SimpleAuthenticator is used to do simple authentication
* when the SMTP server requires it.
*/
private class SMTPAuthenticator extends javax.mail.Authenticator
{

    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(properties.getProperty("smpt.auth.user"), properties.getProperty("smpt.auth.password"));
    }
}

}


