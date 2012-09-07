/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

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
import org.openrdf.model.URI;
import org.openrdf.model.Value;
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
public class MetaDataClassFactory {
    
    static Map<URI, ResourceMetaData> resources;
    
    public static ResourceMetaData getResourceByType(Value type) throws MetaDataException{
        Map<URI, ResourceMetaData> theResources = getResources();
        ResourceMetaData resourceMetaData = theResources.get(type);
        if (resourceMetaData == null){
            return null;
        } else {
            return resourceMetaData.getSchemaClone();
        }
    }
    
    private static Map<URI, ResourceMetaData> getResources() throws MetaDataException{
        if (resources == null){
            String fileName = "resources/metadata.xml";
            try {        
                Document root = readDomFromFile(fileName);
                List<MetaDataClass> metaDatas = getChildMetaData(root.getDocumentElement());
                resources = new HashMap<URI, ResourceMetaData>();
                for (MetaDataClass metaData:metaDatas){
                    ResourceMetaData resource = (ResourceMetaData)metaData;
                    URI type = resource.getType();
                    resources.put(type, resource);
                }
            } catch (Exception ex) {
                throw new MetaDataException ("Unable to read schema description from " + fileName, ex);
            }
        }
        return resources;
    }
    
    private static Document readDomFromFile(String fileName) throws SAXException, ParserConfigurationException, IOException{
        File xmlFile = new File(fileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlFile);        
    }

     public static List<MetaDataClass> getChildMetaData(Element parent) throws MetaDataException{
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
            } else {
                throw new MetaDataException("found non element" + node);            
            }
        }
        return children;
    }
    
    private static List<MetaDataClass> getMetaData(List<Element> elements) throws MetaDataException{
        ArrayList<MetaDataClass> metaDatas = new ArrayList<MetaDataClass>();
        for (Element element:elements){
            MetaDataClass metaData = createMetaData(element);
            metaDatas.add(metaData);
        }
        return metaDatas;
    }
    
    private static MetaDataClass createMetaData(Element element) throws MetaDataException {
        String tagName = element.getTagName();
        if (tagName.equals(Schema.RESOURCE)){
            return new ResourceMetaData(element);
        }
        if (tagName.equals(Schema.PROPERTY)){
            return new Property(element);
        }
        throw new MetaDataException ("Unexpected Element with tagName " + tagName); 
    }

    static public void main(String[] arg) throws SAXException, ParserConfigurationException, IOException, MetaDataException{
        Document doc = readDomFromFile("resources/metadata.xml");
        Element root = doc.getDocumentElement();
        List<Element> elements = getChildElements(root);
        List<MetaDataClass> metaDatas = getMetaData(elements);
        for (MetaDataClass metaData:metaDatas){
            String schema = metaData.schema();
            System.out.println(schema);
        }
    }

 
}
