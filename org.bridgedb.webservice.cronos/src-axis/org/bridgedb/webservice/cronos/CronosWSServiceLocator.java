/**
 * CronosWSServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package org.bridgedb.webservice.cronos;

public class CronosWSServiceLocator extends org.apache.axis.client.Service implements org.bridgedb.webservice.cronos.CronosWSService {

    public CronosWSServiceLocator() {
    }


    public CronosWSServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CronosWSServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CronosWSPort
    private java.lang.String CronosWSPort_address = "http://146.107.217.143:8080/CronosWSService/CronosWS";

    public java.lang.String getCronosWSPortAddress() {
        return CronosWSPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CronosWSPortWSDDServiceName = "CronosWSPort";

    public java.lang.String getCronosWSPortWSDDServiceName() {
        return CronosWSPortWSDDServiceName;
    }

    public void setCronosWSPortWSDDServiceName(java.lang.String name) {
        CronosWSPortWSDDServiceName = name;
    }

    public org.bridgedb.webservice.cronos.CronosWS getCronosWSPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CronosWSPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCronosWSPort(endpoint);
    }

    public org.bridgedb.webservice.cronos.CronosWS getCronosWSPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            org.bridgedb.webservice.cronos.CronosWSPortBindingStub _stub = new org.bridgedb.webservice.cronos.CronosWSPortBindingStub(portAddress, this);
            _stub.setPortName(getCronosWSPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCronosWSPortEndpointAddress(java.lang.String address) {
        CronosWSPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (org.bridgedb.webservice.cronos.CronosWS.class.isAssignableFrom(serviceEndpointInterface)) {
                org.bridgedb.webservice.cronos.CronosWSPortBindingStub _stub = new org.bridgedb.webservice.cronos.CronosWSPortBindingStub(new java.net.URL(CronosWSPort_address), this);
                _stub.setPortName(getCronosWSPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CronosWSPort".equals(inputPortName)) {
            return getCronosWSPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.cronos/", "CronosWSService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.cronos/", "CronosWSPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CronosWSPort".equals(portName)) {
            setCronosWSPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
