/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.iterator;

import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;

/**
 *
 * @author Christian
 */
public interface XrefByPossition {
    
    public Set<Xref> getXrefByPossition(int possition, int limit) throws IDMapperException;

    public Xref getXrefByPossition(int possition) throws IDMapperException;
    
    public Set<Xref> getXrefByPossition(DataSource ds, int possition, int limit) throws IDMapperException;

    public Xref getXrefByPossition(DataSource ds, int possition) throws IDMapperException;

}
