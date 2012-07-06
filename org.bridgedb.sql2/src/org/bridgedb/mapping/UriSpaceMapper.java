/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.mapping;

import java.util.HashMap;
import java.util.Map;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;

/**
 *
 * @author Christian
 */
public class UriSpaceMapper {
    
    public static Map<String,DataSource> getUriSpaceMappings() throws IDMapperException{
       HashMap <String,DataSource> map = new HashMap <String,DataSource>();
       DataSource dataSource = DataSource.register("TestDS1", "TestDS1"). urlPattern("http://www.foo.com/$id")
                .idExample("123").asDataSource();
       map.put("http://www.foo.com/", dataSource);
       dataSource = DataSource.register("TestDS2", "TestDS2").urlPattern("http://www.example.com/$id")
                .idExample("123").asDataSource();
       map.put("http://www.example.com/", dataSource);
       dataSource = DataSource.register("TestDS3", "TestDS3").URISpace("http://www.example.org#")
                .idExample("123").asDataSource();
       map.put("http://www.example.org#", dataSource);
       return map;
    }
}
