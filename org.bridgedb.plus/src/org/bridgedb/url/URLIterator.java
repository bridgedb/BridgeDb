package org.bridgedb.url;

import org.bridgedb.IDMapperException;

public interface URLIterator {

	/**
	 * Create an iterator that iterates over all URLs with a certain NameSpace
	 * defined by this IDMapper.
	 */
	Iterable<String> getURLIterator(String nameSpace) throws IDMapperException;

	/**
	 * Create an iterator that iterates over all URLs defined by this IDMapper.
	 */
	Iterable<String> getURLIterator() throws IDMapperException;

}