package org.bridgedb.linkset.transative;

import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandlerException;

/**
 *
 * @author Christian
 */
public class PredicateMaker {

    public static Value combine(Value left, Value right) throws RDFHandlerException{
        if (left.equals(right)){
            return left;
        }
        throw new RDFHandlerException("unable to combine " + left + " with " + right);
    }
}
