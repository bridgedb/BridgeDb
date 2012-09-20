/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class RdfController {
    
    private static final Resource CONTROLLER_RESOURCE = new URIImpl(RepositoryFactory.getBaseURI());
    private static final URI NEXT_CONTEXT = new URIImpl(RepositoryFactory.getBaseURI() + "#nextContext");
    private static final boolean CAN_BE_NEW = false;
    
    public static String getNextContext(RdfStoreType rdfStoreType) throws RdfException{
        WrappedRepository repository = RepositoryFactory.getRepository(rdfStoreType, CAN_BE_NEW);
        int nextContext = repository.getAndIncrementValue(CONTROLLER_RESOURCE, NEXT_CONTEXT, CONTROLLER_RESOURCE);
        return RepositoryFactory.getBaseURI() + "void/" + nextContext;
    }

    public static String getValidateBase() throws RdfException{
        return RepositoryFactory.getBaseURI() + "void/validate/";
    }
}
