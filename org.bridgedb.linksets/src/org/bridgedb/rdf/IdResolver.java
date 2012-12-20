/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.IOException;
import org.bridgedb.DataSource;

/**
 *
 * @author Christian
 */
public interface IdResolver {

    public String getDataSourceRdfLabel(DataSource dataSource);

}
