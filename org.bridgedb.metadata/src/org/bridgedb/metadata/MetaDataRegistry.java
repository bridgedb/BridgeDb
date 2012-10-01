/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.io.FileNotFoundException;
import org.bridgedb.metadata.constants.SchemaConstants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.bridgedb.metadata.utils.Reporter;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 *
 * @author Christian
 */
public class MetaDataRegistry {
    
    Map<URI, ResourceMetaData> resourcesByType;
   // Map<Resource, ResourceMetaData> resourcesById = new HashMap<Resource, ResourceMetaData>();
    static String documentationRoot = "";
    
    public ResourceMetaData getResourceByType(Value type) throws MetaDataException{
        ResourceMetaData resourceMetaData = resourcesByType.get(type);
        if (resourceMetaData == null){
            return null;
        } else {
            return resourceMetaData.getSchemaClone();
        }
    }
   
    //public ResourceMetaData getResourceByID(Resource id) {
    //    return resourcesById.get(id);
    //}
    
    //public void registerResource (ResourceMetaData resourceMetaData){
    //    resourcesById.put(resourceMetaData.id, resourceMetaData);
    //}
            
    
    public static String getDocumentationRoot(){
        return documentationRoot;
    }
    
    private static List<Element> getChildElements(Element parent) throws MetaDataException{
        ArrayList<Element> children = new ArrayList<Element>();
        NodeList list = parent.getChildNodes();
        for (int i=0; i<list.getLength(); i++) {
            Node node = list.item(i);
            if (node instanceof Element){
                children.add((Element)node);
            } else if (node instanceof Text) {
                String check = ((Text)node).getWholeText();
                check = check.trim();
                if (!check.isEmpty()){
                    throw new MetaDataException("found non empty text " + check); 
                }
            } else if (node instanceof Comment) {
                //comments can of course be ignored
            } else {
                throw new MetaDataException("found non element" + node + node.getClass());            
            }
        }
        return children;
    }
    
    /*private static List<MetaDataBase> getMetaData(List<Element> elements) throws MetaDataException{
        ArrayList<MetaDataBase> metaDatas = new ArrayList<MetaDataBase>();
        for (Element element:elements){
            String tagName = element.getTagName();
            if (tagName.equals(SchemaConstants.RESOURCE)){
                metaDatas.add(new ResourceMetaData(element));
            } else if (tagName.equals(SchemaConstants.PROPERTY)){
                metaDatas.add(new PropertyMetaData(element));
            } else if (tagName.equals(SchemaConstants.GROUP)){
                metaDatas.add(new MetaDataGroup(element));
            } else if (tagName.equals(SchemaConstants.ALTERNATIVES)){
                metaDatas.add(new MetaDataAlternatives(element));
            } else if (tagName.equals(SchemaConstants.LINKED_RESOURCE)){
                metaDatas.add(new LinkedResource(element));
            } else if (tagName.equals(SchemaConstants.DOCUMENTATION_ROOT)){
                documentationRoot = element.getFirstChild().getTextContent();
            } else {
                throw new MetaDataException ("Unexpected Element with tagName " + tagName); 
            }
        }
        return metaDatas;
    }*/

    //TODO work this out more including looking in configs
    private static InputStream findXmlStream() throws MetaDataException {
        InputStream test = getInputStreamFromPath("metadata.xml");
        if (test != null) { 
            return test;
        }
        test = getInputStreamFromPath("resources/metadata.xml");
        if (test != null) { 
            return test;
        }
        test = getInputStreamFromResource("metadata.xml");
        if (test != null) { 
            return test;
        }
        test = getInputStreamFromJar("metadata.xml");
        if (test != null) { 
            return test;
        }
        throw new MetaDataException("Unable to find the metadata.xml file");
    }
    
    private static InputStream getInputStreamFromPath(String filePath){
        File file = new File(filePath);
        if (file.isFile()) {
            try {
                InputStream stream = new FileInputStream(file);
                return stream;
            } catch (FileNotFoundException ex) {
                Reporter.report(ex.toString());
            }
        }
        return null;
    }

    private static InputStream getInputStreamFromResource(String resourcePath){
        java.net.URL url = MetaDataRegistry.class.getResource(resourcePath);
        if (url != null){
            String fileName = url.getFile();
            return getInputStreamFromPath(fileName);
        }
        return null;
    }

    private static InputStream getInputStreamFromJar(String name){
        ZipInputStream zip = null;
        try {
            CodeSource src = MetaDataRegistry.class.getProtectionDomain().getCodeSource();
            URL jar = src.getLocation();
            zip = new ZipInputStream( jar.openStream());
            ZipEntry ze = null;
            while( ( ze = zip.getNextEntry() ) != null ) {
                if (name.equals(ze.getName())){
                    return zip;
                }
            }
            return null;
        } catch (IOException ex) {
            Logger.getLogger(MetaDataRegistry.class.getName()).log(Level.SEVERE, null, ex);
        }
        //NOTE: Stream must be left OPEN!
        return null;
    }
}
