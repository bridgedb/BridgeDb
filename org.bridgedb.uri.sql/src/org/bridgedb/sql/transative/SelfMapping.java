/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.sql.transative;

import java.util.Set;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.uri.api.Mapping;

/**
 *
  @author Christian
 */
public class SelfMapping extends Mapping{
    public SelfMapping (String uri, IdSysCodePair pair){
        super(uri, pair);
    }

    public SelfMapping (String uri, Set<String> targetUris){
        super(uri, targetUris);
    }

    public SelfMapping(IdSysCodePair pair){
        super(pair);
    }
    
    @Override
    public int compareTo(Mapping mapping) {
        int fromSuper = super.compareTo(mapping);
        if (fromSuper != 0){
            return fromSuper;
        }
        if (mapping instanceof SelfMapping){
            return 0;
        } else {
            return 1;
        }
    }

}
