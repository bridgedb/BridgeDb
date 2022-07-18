package org.bridgedb;

public interface XrefIterator {
	/**
	 * Create an iterator that iterates over all Xrefs of a certain DataSource
	 * defined by this IDMapper.
	 * @param ds - the DataSource
	 * @throws IDMapperException - exception class
	 * @return the iterable Xref
	 */
	Iterable<Xref> getIterator(DataSource ds) throws IDMapperException;

	/**
	 * Create an iterator that iterates over all Xrefs defined by this IDMapper.
	 * @return the iterable Xref
	 * @throws IDMapperException - exception class
	 */
	Iterable<Xref> getIterator() throws IDMapperException;

}