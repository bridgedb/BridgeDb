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
public interface XrefByPosition {
    
    public Set<Xref> getXrefByPosition(int position, int limit) throws IDMapperException;

    public Xref getXrefByPosition(int position) throws IDMapperException;
    
    public Set<Xref> getXrefByPosition(DataSource ds, int position, int limit) throws IDMapperException;

    public Xref getXrefByPosition(DataSource ds, int position) throws IDMapperException;

}
