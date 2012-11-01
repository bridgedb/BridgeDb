/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.File;
import java.io.InputStream;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.utils.StoreType;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public class LinksetStatementReaderAndImporter extends StatementReaderAndImporter implements LinksetStatements{
    
    private Set<Statement> linkStatements;
    
    public LinksetStatementReaderAndImporter(File file, StoreType storeType) throws IDMapperException{
        LinksetStatementReader reader = new LinksetStatementReader(file);
        voidStatements = reader.getVoidStatements();
        linkStatements = reader.getLinkStatements();
        loadInfo(storeType);
    }

    public LinksetStatementReaderAndImporter(String fileName, StoreType storeType) throws IDMapperException{
        LinksetStatementReader reader = new LinksetStatementReader(fileName);
        voidStatements = reader.getVoidStatements();
        linkStatements = reader.getLinkStatements();
        loadInfo(storeType);
    }
    
    public LinksetStatementReaderAndImporter(String info, RDFFormat format, StoreType storeType) throws IDMapperException{
        LinksetStatementReader reader = new LinksetStatementReader(info, format);
        voidStatements = reader.getVoidStatements();
        linkStatements = reader.getLinkStatements();
        loadInfo(storeType);
    }

    public LinksetStatementReaderAndImporter(InputStream inputStream, RDFFormat format, StoreType storeType) throws IDMapperException{
        LinksetStatementReader reader = new LinksetStatementReader(inputStream, format);
        voidStatements = reader.getVoidStatements();
        linkStatements = reader.getLinkStatements();
        loadInfo(storeType);
    }

    @Override
    public Set<Statement> getLinkStatements() {
        return this.linkStatements;
    }

}
