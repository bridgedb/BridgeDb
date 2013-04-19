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
package org.bridgedb.ws.uri;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.statistics.DataSetInfo;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.impl.URIImpl;

/**
 *
 * @author Christian
 */
public class MappingSetTableMaker implements Comparator<MappingSetInfo>{
    
    private MappingSetInfo[] infos = new MappingSetInfo[0];
    private MappingSetInfo previous;
    private final HttpServletRequest httpServletRequest;
    
    protected final NumberFormat formatter;
    
    static final Logger logger = Logger.getLogger(MappingSetTableMaker.class);

    public static void addTable(StringBuilder sb, List<MappingSetInfo> mappingSetInfos, 
            HttpServletRequest httpServletRequest) throws BridgeDBException{
        MappingSetTableMaker maker = new MappingSetTableMaker(mappingSetInfos, httpServletRequest);
        maker.tableMaker(sb);
    }
    
    private MappingSetTableMaker(List<MappingSetInfo> mappingSetInfos, HttpServletRequest httpServletRequest){
        this.httpServletRequest = httpServletRequest;
        infos = mappingSetInfos.toArray(infos);
        Arrays.sort(infos, this);
        formatter = NumberFormat.getInstance();
        if (formatter instanceof DecimalFormat) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setGroupingSeparator(',');
            ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
        }
    }
    
    private void tableMaker(StringBuilder sb) throws BridgeDBException{
        sb.append(SCRIPT);
        sb.append(TABLE_HEADER);
        newSource(sb, 0);
        for (int i = 1; i < infos.length; i++){
            if (infos[i].getSource().equals(infos[i-1].getSource())){
                if (infos[i].getTarget().equals(infos[i-1].getTarget())){
                    AddMappingSet(sb, i);
                } else {
                    newTarget(sb, i);
                }
            } else {
                newSource(sb, i);
            }
        }
        sb.append("</table>");
    }
    
    private final String TABLE_HEADER = "<table border=\"1\">\n"
            + "\t<tr>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th>Source Data Source</th>\n"
            + "\t\t<th>Targets</th>\n"
            + "\t\t<th>Sum of Mappings</th>\n"
            + "\t\t<th colspan=\"2\"> Id</th>\n"
            + "\t\t<th>Predicate</th>\n"
            + "\t\t<th>Justification</th>\n"
            + "\t\t<th colspan=\"2\"> Transative</th>\n"
            + "\t</tr>\n"
            + "\t<tr>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th></th>\n" //Source Data Source
            + "\t\t<th></th>\n" //Targets
            + "\t\t<th></th>\n" //Sum of Mappings
            + "\t\t<th>Summary</th>\n"
            + "\t\t<th>Rdf</th>\n"
            + "\t\t<th></th>\n" //Predicate
            + "\t\t<th></th>\n" //Justification
            + "\t\t<th>SysCodes</th>\n"
            + "\t\t<th>Ids</th>\n"
            + "\t</tr>\n";

    String SCRIPT = "<script language=\"javascript\">\n"
    + "\n"
    + "function showLevel2(id) {\n"
    + "	tr=document.getElementsByTagName('tr')\n"
    + "	for (i=0;i<tr.length;i++){\n"
    + "	 	if (tr[i].getAttribute(id + '_level2')){\n"
    + "		    tr[i].style.display = '';\n"
    + "		} else if (tr[i].getAttribute(id + '_level1')){\n"
    + "		    tr[i].style.display = 'none';\n"
    + "	    }\n"
    + "	}\n"
    + "}\n"
    + "\n"
    + "function hideLevel2(id) {\n"
    + "	tr=document.getElementsByTagName('tr')\n"
    + "	for (i=0;i<tr.length;i++){\n"
    + "	 	if (tr[i].getAttribute(id + '_level3')){\n"
    + "		    tr[i].style.display = 'none';\n"
    + "	 	} else if (tr[i].getAttribute(id + '_level2')){\n"
    + "		    tr[i].style.display = 'none';\n"
    + "		} else if (tr[i].getAttribute(id + '_level1')){\n"
    + "		    tr[i].style.display = '';\n"
    + "	    }\n"
    + "	}\n"
    + "}\n"
    + "\n"
    + "function showLevel3(id) {\n"
    + "	tr=document.getElementsByTagName('tr')\n"
    + "	for (i=0;i<tr.length;i++){\n"
    + "	 	if (tr[i].getAttribute(id + '_level3')){\n"
    + "		    tr[i].style.display = '';\n"
    + "		} else if (tr[i].getAttribute(id + '_level2')){\n"
    + "		    tr[i].style.display = 'none';\n"
    + "	    }\n"
    + "	}\n"
    + "}\n"
    + "\n"
    + "function hideLevel3(id) {\n"
    + "	tr=document.getElementsByTagName('tr')\n"
    + "	for (i=0;i<tr.length;i++){\n"
    + "	 	if (tr[i].getAttribute(id + '_level3')){\n"
    + "		    tr[i].style.display = 'none';\n"
    + "		} else if (tr[i].getAttribute(id + '_level2')){\n"
    + "		    tr[i].style.display = '';\n"
    + "	    }\n"
    + "	}\n"
    + "}\n"
    + "\n"
    + "</script>\n";
    
    @Override
    public int compare(MappingSetInfo o1, MappingSetInfo o2) {
        int test = o1.getSource().compareTo(o2.getSource());
        if (test != 0) return test;
        return o1.getTarget().compareTo(o2.getTarget());
    }
    
    private void newTarget(StringBuilder sb, int i) throws BridgeDBException {
        if (i == infos.length - 1){
            addSingleTarget(sb ,i);
        } else if (infos[i].getTarget().equals(infos[i+1].getTarget())){
            newTargetMultipleMappings(sb,i);
        } else {
            addSingleTarget(sb ,i);            
        }
    }

    private void newSource(StringBuilder sb, int i) throws BridgeDBException {
        if (i == infos.length - 1){
            addSingleSource(sb ,i);
        } else if (infos[i].getSource().equals(infos[i+1].getSource())){
            newSourceMultipleMappings(sb,i);
        } else {
            addSingleSource(sb ,i);            
        }
    }

    private void newSourceMultipleMappings(StringBuilder sb, int i) throws BridgeDBException {
        logger.debug("newSourceMultipleMappings " + i);
        int j = i;
        int last = infos.length -1;
        int targetCount = 1;
        int mappingCount = 1;
        int numberOfLinks = infos[i].getNumberOfLinks();
        do{
            logger.debug(infos[j].getSource() + " ->" + infos[j+1].getSource());
            j++;
            logger.debug(j);
            mappingCount++;
            numberOfLinks+= infos[j].getNumberOfLinks();
            if (infos[j].getTarget().equals(infos[j-1].getTarget())){
                //same target
            } else {
                targetCount++;
            }
        } while ((j < last) && 
                (infos[j].getSource().equals(infos[j+1].getSource())));
        addSourceSummary(sb, infos[i].getSource(), targetCount, numberOfLinks, mappingCount); 
        addSourceDetail(sb, infos[i].getSource(), targetCount, numberOfLinks, mappingCount); 
        newTarget(sb, i);
    }

    private void newTargetMultipleMappings(StringBuilder sb, int i) throws BridgeDBException {
        int j = i;
        int last = infos.length -1;
        int mappingCount = 1;
        int numberOfLinks = infos[i].getNumberOfLinks();
        do {
            j++;
            mappingCount++;
            numberOfLinks+= infos[j].getNumberOfLinks();
        } while ((j < last) 
                && (infos[j].getSource().equals(infos[j+1].getSource()))
                && (infos[j].getTarget().equals(infos[j+1].getTarget()))); 
        addTargetSummary(sb, infos[i].getSource(), infos[i].getTarget(), numberOfLinks, mappingCount); 
        addTargetDetail(sb, infos[i].getSource(), infos[i].getTarget(), numberOfLinks, mappingCount); 
        AddMappingSet(sb, i);
    }

    private void addSingleSource(StringBuilder sb, int i) 
            throws BridgeDBException {
        sb.append("\t<tr>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addInfo(sb, i);
    }

    private void addInfo(StringBuilder sb, int i) 
            throws BridgeDBException {
        addDataSourceCell(sb, infos[i].getSource());
        addDataSourceCell(sb, infos[i].getTarget());
        addNumberOfLinksCell(sb, infos[i].getNumberOfLinks());
        addMappingSetCells(sb, infos[i].getStringId());
        addPredicateCell(sb, i);
        addJustificationCell(sb,i);
        addTransatives(sb, i);
    }

    private void addSingleTarget(StringBuilder sb, int i) throws BridgeDBException {
        addLevel2Tr(sb, infos[i].getSource());
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addInfo(sb, i);
    }
    
    private void AddMappingSet(StringBuilder sb, int i) throws BridgeDBException {
        addLevel3Tr(sb, infos[i].getSource(), infos[i].getTarget());
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addInfo(sb, i);
    }		
		
    private void addSourceSummary(StringBuilder sb, DataSetInfo source, int targetCount, int numberOfLinks, int mappingCount) 
            throws BridgeDBException {
        sb.append("\t<tr ");
            sb.append(cleanup(source));
            sb.append("_level1=level1>\n");
        sb.append("\t\t<td><span onclick=\"showLevel2('");
            sb.append(cleanup(source));
            sb.append("')\"> &dArr; </span></td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addDataSourceCell(sb, source);
        addTargetCount(sb, targetCount);
        addMappingSummary(sb, numberOfLinks, mappingCount); 
     }
    
    private void addSourceDetail(StringBuilder sb, DataSetInfo source, int targetCount, int numberOfLinks, int mappingCount) 
            throws BridgeDBException {
        addLevel2Tr(sb, source);
        sb.append("\t\t<td><span onclick=\"hideLevel2('");
            sb.append(cleanup(source));
            sb.append("')\"> &uArr; </span></td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addDataSourceCell(sb, source);
        addTargetCount(sb, targetCount);
        addMappingSummary(sb, numberOfLinks, mappingCount);         
     }
    
    private void addTargetSummary(StringBuilder sb, DataSetInfo source, DataSetInfo target, int numberOfLinks, int mappingCount) 
            throws BridgeDBException {
        sb.append("\t<tr ");
            sb.append(cleanup(source));
            sb.append("_level2=level2; ");
            sb.append(cleanup(source));
            sb.append("_");
            sb.append(cleanup(target));
            sb.append("_level2=level2; style=\"display: none;\">\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td><span onclick=\"showLevel3('");
            sb.append(cleanup(source));
            sb.append("_");
            sb.append(cleanup(target));
            sb.append("')\"> &dArr; </span></td>\n");
        addDataSourceCell(sb, source);
        addDataSourceCell(sb, target);
        addMappingSummary(sb, numberOfLinks, mappingCount); 
     }
    
    private void addTargetDetail(StringBuilder sb, DataSetInfo source, DataSetInfo target, int numberOfLinks, int mappingCount) 
            throws BridgeDBException {
        addLevel3Tr(sb, source, target);
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td><span onclick=\"hideLevel3('");
            sb.append(cleanup(source));
            sb.append("_");
            sb.append(cleanup(target));
            sb.append("')\"> &uArr; </span></td>\n");
        addDataSourceCell(sb, source);
        addDataSourceCell(sb, target);
        addMappingSummary(sb, numberOfLinks, mappingCount); 
    }
    
    private void addTargetCount(StringBuilder sb, int targetCount) 
            throws BridgeDBException {
        sb.append("\t\t<td align=\"right\">");
            sb.append(targetCount);
            sb.append(" Targets</td>\n");
    }
    
    private void addMappingSummary(StringBuilder sb, int numberOfLinks, int mappingCount) 
            throws BridgeDBException {
        addNumberOfLinksCell(sb, numberOfLinks);
        sb.append("\t\t<td align=\"right\">");
            sb.append(mappingCount);
            sb.append(" Mappings</td>\n");
        //rdf    
        sb.append("\t\t<td>&nbsp</td>\n");
        //predicate    
        sb.append("\t\t<td>&nbsp</td>\n");
        //Justification
        sb.append("\t\t<td>&nbsp</td>\n");
        //Transative
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t</tr>\n");
    }

    private String cleanup(DataSetInfo original){
        String result = original.getSysCode().replace(" ", "");
        return result;
    }
    
    private void addLevel2Tr(StringBuilder sb, DataSetInfo source) 
            throws BridgeDBException {
        sb.append("\t<tr ");
            sb.append(cleanup(source));
            sb.append("_level2=level2; style=\"display: none;\">\n");
    }

    private void addLevel3Tr(StringBuilder sb, DataSetInfo source, DataSetInfo target) 
            throws BridgeDBException {
         sb.append("\t<tr ");
            sb.append(cleanup(source));
            sb.append("_level3=level3; ");
            sb.append(cleanup(source));
            sb.append("_");
            sb.append(cleanup(target));
            sb.append("_level3=level3; style=\"display: none;\">\n");
   }

   private void addDataSourceCell(StringBuilder sb, DataSetInfo info) throws BridgeDBException {
        sb.append("\t\t<td>");
        addDataSourceLink(sb, info);
        sb.append("</td>\n");
    }
   
   private void addDataSourceLink(StringBuilder sb, DataSetInfo info) throws BridgeDBException{
       addDataSourceLink(sb, info, httpServletRequest);
   }
   
   public final static void addDataSourceLink(StringBuilder sb, DataSetInfo info, HttpServletRequest httpServletRequest) throws BridgeDBException{
        sb.append("<a href=\"");
        sb.append(httpServletRequest.getContextPath());
        sb.append("/dataSource/");
        sb.append(info.getSysCode());
        sb.append("\">");
        sb.append(info.getFullName());
        sb.append("</a>");
   }

    private void addMappingSetCells(StringBuilder sb, String id) throws BridgeDBException {
        sb.append("\t\t<td>");
        addMappingInfoLink(sb, id);
        sb.append("</td>\n");        
        String rdfUri = httpServletRequest.getContextPath() + "/mappingSet/" + id;
        sb.append("\t\t<td><a href=\"");
            sb.append(rdfUri);
            sb.append("\">RDF</a></td>\n");
    }

    private void addMappingInfoLink(StringBuilder sb, String id) throws BridgeDBException{
        String summaryUri = httpServletRequest.getContextPath() + "/getMappingInfo/" + id;
        sb.append("<a href=\"");
            sb.append(summaryUri);
            sb.append("\">");
            sb.append(id);
            sb.append("</a>");        
    }
    
    private void addNumberOfLinksCell(StringBuilder sb, int numberOfLinks) {
        sb.append("\t\t<td align=\"right\">");
            sb.append(formatter.format(numberOfLinks));
            sb.append("</td>\n");
   }

    private void addLinkCell(StringBuilder sb, String uri) throws BridgeDBException {
        sb.append("\t\t<td><a href=\"");
        sb.append(uri);
        sb.append("\"</a>");
        if (uri == null){
            sb.append("null"); 
        } else {
            UriPattern pattern = UriPattern.existingByUri(uri);
            if (pattern == null){
                URIImpl impl = new URIImpl(uri); 
                sb.append(impl.getLocalName());
            } else {
                sb.append(UriPattern.existingByUri(uri).getIdFromUri(uri));        
            }
        }
        sb.append("</a></td>\n");
   }

    private void addPredicateCell(StringBuilder sb, int i) throws BridgeDBException {
        addLinkCell(sb, infos[i].getPredicate());
    }
    
    private void addJustificationCell(StringBuilder sb, int i) throws BridgeDBException {
        addLinkCell(sb, infos[i].getJustification());
    }

    private void addTransatives(StringBuilder sb, int i) throws BridgeDBException {
        sb.append("\t\t<td>");
        Set<DataSetInfo> viaSet = infos[i].getViaDataSets();
        int size = viaSet.size();
        int count = 0;
        for (DataSetInfo dsInfo:viaSet){
            addDataSourceLink(sb, dsInfo);
            count++;
            if (count < size){
                sb.append(", ");
            }
        }
        sb.append("</td>\n");
        sb.append("\t\t<td>");
        Set<Integer> chainSet = infos[i].getChainIds();
        size = chainSet.size();
        count = 0;
        for (Integer chain:chainSet){
            addMappingInfoLink(sb, chain.toString());
            count++;
            if (count < size){
                sb.append(", ");
            }
        }
        sb.append("</td>\n");
        sb.append("\t</tr>\n");
    }

}
