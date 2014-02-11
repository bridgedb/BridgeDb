// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright 2006-2009  BridgeDb developers
// Copyright 2012-2013  Christian Y. A. Brenninkmeijer
// Copyright 2012-2013  OpenPhacts
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
package org.bridgedb.uri.loader;

import java.io.File;
import java.util.Set;
import org.apache.log4j.Logger;
import org.bridgedb.uri.tools.UriListener;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.rio.RDFHandler;

public class LinksetListener {
    
    private final UriListener uriListener;
    private boolean SYMETRIC = true; 
    
    public LinksetListener(UriListener uriListener){
        this.uriListener = uriListener;
    }
    
    static final Logger logger = Logger.getLogger(LinksetListener.class);
    
    public int parse(File file, URI linkPredicate, String justification) throws BridgeDBException{
        URI mappingUri = RdfParser.fileToURL(file);
        LinksetHandler handler = new LinksetHandler(uriListener, linkPredicate, justification, mappingUri, mappingUri); 
        RdfParser parser = getParser(handler);
        parser.parse(mappingUri.stringValue(), file);
        return handler.getMappingsetId();
    }
    
    public int parse(File file, Resource mappingResource, Resource mappingSource, URI linkPredicate, String justification, 
            Boolean symetric, Set<String> viaLabels, Set<Integer> chainedLinkSets) throws BridgeDBException{
        LinksetHandler handler = new LinksetHandler(uriListener, linkPredicate, justification, mappingResource, 
                mappingSource, symetric, viaLabels, chainedLinkSets);
        RdfParser parser = getParser(handler);
        parser.parse(mappingSource.stringValue(), file);
        return handler.getMappingsetId();
    }
    
    public int parse(String uri, Resource mappingResource, Resource  mappingSource, URI linkPredicate, String justification) throws BridgeDBException{
        LinksetHandler handler = new LinksetHandler(uriListener, linkPredicate, justification, mappingResource, mappingSource);
        RdfParser parser = getParser(handler);
        parser.parse(uri);
        return handler.getMappingsetId();
    }

    protected RdfParser getParser(RDFHandler handler){
       return new RdfParser(handler);
    }
 }
