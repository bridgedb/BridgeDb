/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.url;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public interface URLListener {
    public void registerUriSpace(DataSource source, String uriSpace) throws IDMapperException;

}
