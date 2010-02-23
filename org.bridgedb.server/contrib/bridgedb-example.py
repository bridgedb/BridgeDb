#!/usr/bin/python

import urllib

## If you want to use a local bridgedb idmapper in python, setup a local service
## and change the url below. See http://bridgedb.org/wiki/LocalService
## for information on how to run a local service.
webserviceUrl = "http://webservice.bridgedb.org";

def mapID (id, code):
	species = "Human";
	url = webserviceUrl + '/' + species + '/xrefs/' + code + '/' + id;
	data = urllib.urlopen (url).read();
	
	print data;


mapID ("1234", "L");
