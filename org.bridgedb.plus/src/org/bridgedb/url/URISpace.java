package org.bridgedb.url;

import org.bridgedb.IDMapperException;

public interface URISpace {

	/**
	 * Create an iterator that iterates over all URLs with a certain URISpace
	 * defined by this IDMapper.
	 */
	Iterable<String> getURLIterator(String URISpace) throws IDMapperException;

	/**
	 * Create an iterator that iterates over all URLs defined by this IDMapper.
	 */
	Iterable<String> getURLIterator() throws IDMapperException;

}