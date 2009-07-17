package org.bridgedb.webservice.biomart;

public class Dataset {
	private String name;
    private String displayName;
    private Database database;
	
	public Dataset(String name, String displayName, Database database) {
		this.name = name;
        this.displayName = displayName;
        this.database = database;
	}
	
	public String getName() {
		return name;
	}

    public String getDisplyName() {
		return displayName;
	}

    public Database getDatabase() {
        return database;
    }

    public String toString() {
        return getDisplyName();
    }

}
