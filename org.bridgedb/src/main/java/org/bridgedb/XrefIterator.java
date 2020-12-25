package org.bridgedb;

public interface XrefIterator {
	/**
	 * Create an iterator that iterates over all Xrefs of a certain DataSource
	 * defined by this IDMapper.
	 */
	Iterable<Xref> getIterator(DataSource ds) throws IDMapperException;

	/**
	 * Create an iterator that iterates over all Xrefs defined by this IDMapper.
	 */
	Iterable<Xref> getIterator() throws IDMapperException;

}