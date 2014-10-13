package org.bridgedb.ws.templates;

import java.io.StringWriter;
import java.util.Properties;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;

public class WebTemplates 
{

    public static final String BRIDGEDB_HOME = "bridgeDBHome.vm";
    public static final String FRAME = "frame.vm";
    public static final String GRAPH_INFO_SCRIPT = "uriSpacesPerGraph.vm";
    public static final String LENS = "lensTable.vm";
    public static final String LENS_GROUP = "lensGroup.vm";
    public static final String MAP_URI_FORM = "mapUriForm.vm";
    public static final String MAP_URI_RESULTS = "mapUriResults.vm";
    public static final String SELECTORS_SCRIPTS = "selectorsScripts.vm";
    public static final String DATA_SOURCE_SCRIPT = "dataSource.vm";
    public static final String SOURCE_INFO_SCRIPT = "sourceInfos.vm";
    public static final String SOURCE_TARGET_INFO_SCRIPT = "sourceTargetInfos.vm";
    public static final String MAPPING_SET_INFO_SCRIPT = "mappingSetInfos.vm";
    public static final String MAPPING_SET_SCRIPT = "mappingSet.vm";
    
    private static VelocityEngine velocityEngine = initialize();
    
    static final Logger logger = Logger.getLogger(WebTemplates.class.toString());
    
    private static VelocityEngine initialize(){
        Properties props = new Properties();
    	props.put("resource.loader", "class");
    	props.put("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
    	VelocityEngine ve = new VelocityEngine();
        ve.setProperty( RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
            "org.apache.velocity.runtime.log.Log4JLogChute" );
        ve.setProperty("runtime.log.logsystem.log4j.logger",
                        WebTemplates.class.toString());
    	ve.init(props);  
        return ve;
    }
    
     public static String getForm (VelocityContext context, String formName){
        Template t = velocityEngine.getTemplate(formName);
        StringWriter writer = new StringWriter();
        t.merge( context, writer );
        return writer.toString();       
    }
    
}