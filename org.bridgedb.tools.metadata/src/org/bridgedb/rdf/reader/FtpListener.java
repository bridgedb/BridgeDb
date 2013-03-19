/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf.reader;

import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.SocketClient;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 *
 * @author Christian
 */
public class FtpListener implements ProtocolCommandListener{

    private Logger logger;
    
    public FtpListener(Logger logger){
        this.logger = logger;
        logger.setLevel(Level.DEBUG);
    }
    
    @Override
    public void protocolCommandSent(ProtocolCommandEvent event) {
        if (logger.isDebugEnabled()){
            String cmd = event.getCommand();
            if ("PASS".equalsIgnoreCase(cmd) || "USER".equalsIgnoreCase(cmd)) {
                logger.debug(cmd + " *******"); 
            } else {
                final String IMAP_LOGIN = "LOGIN";
                if (IMAP_LOGIN.equalsIgnoreCase(cmd)) { // IMAP
                    String msg = event.getMessage();
                    msg=msg.substring(0, msg.indexOf(IMAP_LOGIN)+IMAP_LOGIN.length());
                    logger.debug(msg + " *******"); // Don't bother with EOL marker for this!
                } else {
                    logPrintableString(event.getMessage());
                }
         	}
        }
    }
    
    private void logPrintableString(String msg) {
        msg = msg.trim();
        int pos = msg.indexOf(SocketClient.NETASCII_EOL);
     	if (pos > 0) {
          	logger.debug(msg.substring(0,pos));
            String part2 = msg.substring(pos).trim();
            if (!part2.isEmpty()){
                logger.debug(part2);
            }   
        } else {
            if (!msg.isEmpty()){
                logger.debug(msg);
            }
     	}
    }
    
    @Override
    public void protocolReplyReceived(ProtocolCommandEvent event) {
        if (logger.isDebugEnabled()){
            logPrintableString(event.getMessage());
        }
    }
    
}
