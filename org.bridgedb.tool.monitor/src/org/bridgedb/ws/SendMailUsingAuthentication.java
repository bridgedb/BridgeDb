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

  private static final String SMTP_HOST_NAME1 = "mail.smtp.host"; //Outgoing.manchester.ac.uk"; //"post.demon.co.uk";
  private static final String SMTP_AUTH_USER1 = "smpt.auth.user"; //"mbaxecb2"; //"meijer1";
  private static final String SMTP_AUTH_PWD1  = "smpt.auth.password"; //One+1+4=Six"; //skilift";

  private Properties properties;
  
  //private static final String emailMsgTxt      = "Hi, \n I am looking for a sewing machine for my neice. \n Thanks Christian";
  //private static final String emailSubjectTxt  = "[wilmslow_freecycle] WANTED Sewing Machine (Wilmslow)";

  // Add List of Email address to who email needs to be sent to
  private static final String[] emailList = {"cyb@cs.man.ac.uk", "test1@brenn.co.uk"};

  private static final String fileName = "c:\\DropBox\\Mailer\\addresses.csv";

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

  public void autoPostMail(String subject, String message , String fileName) throws MessagingException, FileNotFoundException, IOException
  {
     boolean debug = false;

    Authenticator auth = new SMTPAuthenticator();
    Session session = Session.getDefaultInstance(properties, auth);

    session.setDebug(debug);

    File file = new File(fileName);
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    while((line=reader.readLine())!=null) {

        String[] addresses = line.split(",");
        // create a message
        Message msg = new MimeMessage(session);

        System.out.print("Writing to "+addresses[0]);
        // set the from and to address
        InternetAddress addressFrom = new InternetAddress(addresses[1]);
        msg.setFrom(addressFrom);

        InternetAddress addressTo = new InternetAddress(addresses[0]);
        msg.setRecipient(Message.RecipientType.TO, addressTo);

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setContent(message, "text/plain");
        Transport.send(msg);
        System.out.println(" done");
    }
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


