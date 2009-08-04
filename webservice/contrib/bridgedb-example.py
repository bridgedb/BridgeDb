#!/usr/bin/python

import urllib

webserviceUrl = "http://localhost:8183";

def mapID (id, code):
	species = "Human";
	url = webserviceUrl + '/model/' + species + '/' + code + '/' + id + '/xrefs';
	data = urllib.urlopen (url).read();
	
	print data;


mapID ("1234", "L");