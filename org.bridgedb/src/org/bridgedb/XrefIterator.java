// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
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