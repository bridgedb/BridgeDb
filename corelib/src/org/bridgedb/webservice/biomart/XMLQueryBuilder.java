// BridgeDb,
// An abstraction layer for identifer mapping services, both local and online.
// Copyright 2006-2009 BridgeDb developers
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

package org.bridgedb.webservice.biomart;

import java.io.StringWriter;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 */
public final class XMLQueryBuilder {
	private static DocumentBuilderFactory factory;
	private static DocumentBuilder builder;

	static {
		factory = DocumentBuilderFactory.newInstance();

		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

        /**
         * Prevent from instantializing.
         */
        private XMLQueryBuilder() {}

	/**
         *
         * @param dataset dataset
         * @param attrs attributes
         * @param queryFilters filters
         * @return quering strings
         */
	public static String getQueryString(String dataset, Attribute[] attrs, 
                    Map<String, String> queryFilters) {
            final Document doc = builder.newDocument();
            Element query = doc.createElement("Query");
            query.setAttribute("virtualSchemaName", "default");
            query.setAttribute("header", "1");
            query.setAttribute("uniqueRows", "1");
            query.setAttribute("count", "");
            query.setAttribute("datasetConfigVersion", "0.6");
            query.setAttribute("formatter", "TSV");

            doc.appendChild(query);

            Element ds = doc.createElement("Dataset");
            ds.setAttribute("name", dataset);
            query.appendChild(ds);

            for (Attribute attr : attrs) {
                Element at = doc.createElement("Attribute");
                at.setAttribute("name", attr.getName());
                ds.appendChild(at);
            }

            if ((queryFilters != null) && (!queryFilters.isEmpty())) {
                for (Map.Entry<String, String> filter : queryFilters.entrySet()) {
                    Element ft = doc.createElement("Filter");
                    ft.setAttribute("name", filter.getKey());
                    if(filter.getValue() == null) {
                        ft.setAttribute("excluded", "0");
                    } else {
                        ft.setAttribute("value", filter.getValue());
                    }
                    ds.appendChild(ft);
                }
            }

            TransformerFactory tff = TransformerFactory.newInstance();
            Transformer tf;
            String result = null;

            try {
                tf = tff.newTransformer();
                tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                StringWriter strWtr = new StringWriter();
                StreamResult strResult = new StreamResult(strWtr);

                tf.transform(new DOMSource(doc.getDocumentElement()), strResult);

                result = strResult.getWriter().toString();
            } catch (TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }

            return result;
	}
}
