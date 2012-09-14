/**
 * CronosWS.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.bridgedb.webservice.cronos;

public interface CronosWS extends java.rmi.Remote {
    public java.lang.String cronosWS(java.lang.String input_id, java.lang.String organism_3_letter, int query_int_id, int target_int_id) throws java.rmi.RemoteException;
    public boolean isinRedList(java.lang.String name, java.lang.String organism_3_letter) throws java.rmi.RemoteException;
}
