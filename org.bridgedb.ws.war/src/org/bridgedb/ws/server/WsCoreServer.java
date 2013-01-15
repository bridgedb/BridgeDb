// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.ws.server;

import java.io.UnsupportedEncodingException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.log4j.Logger;
import org.bridgedb.IDMapperException;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLIdMapper;
import org.bridgedb.sql.SqlFactory;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.bridgedb.ws.WSCoreService;

/**
 *
 * @author Christian
 */
public class WsCoreServer extends WSCoreService {
        
    static final Logger logger = Logger.getLogger(WsCoreServer.class);

    public WsCoreServer() throws IDMapperException {
        idMapper = new SQLIdMapper(false, StoreType.LIVE);
        logger.info("BridgeDB Server setup");
    }
            
    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response welcomeMessage() throws IDMapperException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        StringBuilder sbInnerPure;
        StringBuilder sbInnerEncoded;

        sb.append("<?xml version=\"1.0\"?>");
        sb.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" "
                + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        sb.append("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\">");
        sb.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=ISO-8859-1\"/>");
        sb.append("<head><title>OPS IMS</title></head><body>");
        sb.append("<h1>Open PHACTS Core BridgeDB</h1>");
        sb.append("<p>Welcome to the prototype of an BridgeDb Mapping Service. </p>");
              
        sb.append("<p>The links where last updated ");
        sb.append(idMapper.getCapabilities().getProperty("LastUpdates"));
        sb.append("</p>");
                
        sb.append("</body></html>");
        return Response.ok(sb.toString(), MediaType.TEXT_HTML).build();
    }

}


