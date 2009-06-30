package org.bridgedb.rest;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class Server {
	public static void main(String[] args) {
		Component component = new Component();
		component.getServers().add(Protocol.HTTP, 8182);
		component.getDefaultHost().attach(new IDMapperService());
		
		try {
			component.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
