/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import org.bridgedb.metadata.constants.SchemaConstants;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    
    static Map<URI, ResourceMetaData> resourcesByType;
    static Map<Resource, ResourceMetaData> resourcesById = new HashMap<Resource, ResourceMetaData>();
    
    public static ResourceMetaData getResourceByType(Value type) throws MetaDataException{
        Map<URI, ResourceMetaData> theResources = getResources();
        ResourceMetaData resourceMetaData = theResources.get(type);
        if (resourceMetaData == null){
            return null;
        } else {
            return resourceMetaData.getSchemaClone();
        }
    }
   
    public static ResourceMetaData getResourceByID(Resource id) {
        return resourcesById.get(id);
    }
    
    public static void registerResource (ResourceMetaData resourceMetaData){
        resourcesById.put(resourceMetaData.id, resourceMetaData);
    }
            
    private static Map<URI, ResourceMetaData> getResources() throws MetaDataException{
        if (resourcesByType == null){
            String fileName = "resources/metadata.xml";
            Document root = readDomFromFile(fileName);
            List<MetaDataBase> metaDatas = getChildMetaData(root.getDocumentElement());
            resourcesByType = new HashMap<URI, ResourceMetaData>();
            for (MetaData metaData:metaDatas){
                ResourceMetaData resource = (ResourceMetaData)metaData;
                URI type = resource.getType();
                resourcesByType.put(type, resource);
            }
        }
        return resourcesByType;
    }
    
    private static Document readDomFromFile(String fileName) throws MetaDataException  {
        try {
            File xmlFile = new File(fileName);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(xmlFile);
        } catch (IOException ex) {
            throw new MetaDataException("Exception reading MetaData document", ex);
        } catch (SAXException ex) {
            throw new MetaDataException("Unable to Parse MetaData document", ex);
        } catch (ParserConfigurationException ex) {
            throw new MetaDataException("Unable to Parse MetaData document", ex);
        }
    }

    public static List<MetaDataBase> getChildMetaData(Element parent) throws MetaDataException{
        List<Element> childElements = getChildElements(parent);
        return getMetaData(childElements);
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
    
    private static List<MetaDataBase> getMetaData(List<Element> elements) throws MetaDataException{
        ArrayList<MetaDataBase> metaDatas = new ArrayList<MetaDataBase>();
        for (Element element:elements){
            MetaDataBase metaData = createMetaData(element);
            metaDatas.add(metaData);
        }
        return metaDatas;
    }
    
    private static MetaDataBase createMetaData(Element element) throws MetaDataException {
        String tagName = element.getTagName();
        if (tagName.equals(SchemaConstants.RESOURCE)){
            return new ResourceMetaData(element);
        }
        if (tagName.equals(SchemaConstants.PROPERTY)){
            return new PropertyMetaData(element);
        }
        if (tagName.equals(SchemaConstants.GROUP)){
            return new MetaDataGroup(element);
        }
        if (tagName.equals(SchemaConstants.ALTERNATIVES)){
            return new MetaDataAlternatives(element);
        }
        if (tagName.equals(SchemaConstants.LINKED_RESOURCE)){
            return new LinkedResource(element);
        }
        throw new MetaDataException ("Unexpected Element with tagName " + tagName); 
    }

    static public void main(String[] arg) throws SAXException, ParserConfigurationException, IOException, MetaDataException{
        Document doc = readDomFromFile("resources/metadata.xml");
        Element root = doc.getDocumentElement();
        List<Element> elements = getChildElements(root);
        List<MetaDataBase> metaDatas = getMetaData(elements);
        for (MetaData metaData:metaDatas){
            String schema = metaData.toString();
            Reporter.report(schema);
        }
    }

 
}
