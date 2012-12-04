package org.bridgedb.gui;


//TODO: make part of BridgeDb
/**
 * Optional part of IDMapper interface to help GUI programs to configure IDMapper services.
 */
public interface BridgeDbParameterModel extends ParameterModel
{		
	enum Category
	{
		WEBSERVICE,
		DATABASE,
		CUSTOM,
	}
	
	/**
	 * @return a suitable connection string for configuring an IDMapper,
	 */
	public String getConnectionString();
	public String getName();
	public String getHelpHtml();
	public Category getCategory();
	
	/** load the required IDMapper class(es) 
	 * @throws ClassNotFoundException */
	public void loadClass() throws ClassNotFoundException;
	
	/** true if the required IDMapper class is enabled */
	public boolean isEnabled();
}