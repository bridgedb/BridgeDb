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
import org.bridgedb.statistics.DataSetInfo;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.uri.api.SetMappings;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.impl.URIImpl;

/**
 * @deprecated Take to long with lots of data
 * @author Christian
 */
public class MappingSetTableMaker implements Comparator<MappingSetInfo>{
    
    private MappingSetInfo[] infos = new MappingSetInfo[0];
    private MappingSetInfo previous;
    protected final HttpServletRequest httpServletRequest;
    
    protected final NumberFormat formatter;
    
    private static final Logger logger = Logger.getLogger(MappingSetTableMaker.class);

    public MappingSetTableMaker(List<MappingSetInfo> mappingSetInfos, HttpServletRequest httpServletRequest){
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
    
    public void tableMaker(StringBuilder sb) throws BridgeDBException{
        sb.append(SCRIPT);
        sb.append(FREQUENCY_INFO);
        sb.append(TABLE_HEADER);
        newSource(sb, 0);
        logger.info("tableMaker");        
        for (int i = 1; i < infos.length; i++){
            try{
                if (infos[i].getSource().equals(infos[i-1].getSource())){
                    if (infos[i].getTarget().equals(infos[i-1].getTarget())){
                        AddMappingSet(sb, i);
                    } else {
                        newTarget(sb, i);
                    }
                } else {
                    newSource(sb, i);
                }
            } catch (Exception ex){
                throw new BridgeDBException ("info = " + i, ex);
            }
        }
        sb.append("</table>");
        logger.info("done");        
    }
    
    private final String FREQUENCY_INFO = "<span id=\"showFrequency\" onclick=\"showFrequency()\">"
            + "<h3>Click Here to show columns with the number of Targets per Source</h3></span>\n"
            + "<span id=\"hideFrequency\" style=\"display: none;\" onclick=\"hideFrequency()\">"
            + "  <h3>Click Here to hide columns with the number of Targets per Source</h3>\n"
            + "  <p><dl>\n"
            + "     <dt>Avg</dt>"
            + "        <dd>Average number links per source (rounded down). </dd>\n"
            + "     <dt>X % / Mean (50%)</dt>"
            + "         <dd>At least X % of all unique sources map to this number or less targets.</dd>\n"
            + "     <dt>Max</dt>"
            + "         <dd>The largest number of targets a unique sources maps to.</dd>\n"
            + " </dl></p>\n"
            + "</span>\n";
    
    private final String TABLE_HEADER = "<table border=\"1\">\n"
            + "<col/>\n" //Level 1
            + "<col/>\n" //level2
            + "<col/>\n" //Source Data Source
            + "<col/>\n" //Targets
            + "<col/>\n" //Sum of Mappings
            + "<col/>\n" //Summary
            + "<col/>\n" //Mapping Resource
            + "<col/>\n" //Mapping Source
            + "<col/>\n" //Source Count
            + "<col/>\n" //Target Count
            + "<col/>\n" //Predicate
            + "<col/>\n" //Justification
            + "<col class=\"Frequency\" style=\"visibility:collapse\" />\n" //Avg
		    + "<col class=\"Frequency\" style=\"visibility:collapse\" />\n" //Mean
		    + "<col class=\"Frequency\" style=\"visibility:collapse\" />\n" //75%
		    + "<col class=\"Frequency\" style=\"visibility:collapse\" />\n" //90%
		    + "<col class=\"Frequency\" style=\"visibility:collapse\" />\n" //99%
		    + "<col class=\"Frequency\" style=\"visibility:collapse\" />\n" //Max
            + "\t<tr>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th>Source Data Source</th>\n"
            + "\t\t<th>Targets</th>\n"
            + "\t\t<th>Sum of Mappings</th>\n"
            + "\t\t<th colspan=\"5\"> Mapping Set</th>\n"
            + "\t\t<th>Predicate</th>\n"
            + "\t\t<th>Justification</th>\n"
            + "\t\t<th colspan=\"6\"> Target Frequency</th>\n"
            + "\t\t<th colspan=\"2\"> Transative</th>\n"
            + "\t</tr>\n"
            + "\t<tr>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th></th>\n" //Source Data Source
            + "\t\t<th></th>\n" //Targets
            + "\t\t<th></th>\n" //Sum of Mappings
            + "\t\t<th>Summary</th>\n"
            + "\t\t<th>Mapping Resource</th>\n"
            + "\t\t<th>Mapping Source</th>\n"
            + "\t\t<th>Source Count</th>\n"
            + "\t\t<th>Target Count</th>\n"
            + "\t\t<th></th>\n" //Predicate
            + "\t\t<th></th>\n" //Justification
            + "\t\t<th>Avg</th>\n"
		    + "\t\t<th>Mean</th>\n"
		    + "\t\t<th>75%</th>\n"
		    + "\t\t<th>90%</th>\n"
		    + "\t\t<th>99%</th>\n"
		    + "\t\t<th>Max</th>\n"
            + "\t\t<th>Data Source(s)</th>\n"
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
    + "function showFrequency() {\n"
	+ "  freqCol=document.getElementsByClassName('Frequency')\n"
	+ "  for (i=0;i<freqCol.length;i++){\n"
	+ "    freqCol[i].style.visibility = 'visible';\n"
	+ "  }\n"
	+ "  document.getElementById('hideFrequency').style.display = '';\n"
	+ "  document.getElementById('showFrequency').style.display = 'none';\n"
    + "}\n"
    + "function hideFrequency() {\n"
	+ "  freqCol=document.getElementsByClassName('Frequency')\n"
	+ "  for (i=0;i<freqCol.length;i++){\n"
	+ "      freqCol[i].style.visibility = 'collapse';\n"
	+ "  }\n"
	+ "  document.getElementById('hideFrequency').style.display = 'none';\n"
	+ "  document.getElementById('showFrequency').style.display = '';\n"
    + "}\n"
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
        MappingSetInfo info = infos[i];
        addDataSourceCell(sb, info.getSource());
        addDataSourceCell(sb, info.getTarget());
        addNumberOfLinksCell(sb, info.getNumberOfLinks());
        //Summary
        sb.append("\t\t<td>");
        addMappingInfoLinkByLocation(sb, i);
        sb.append("</td>\n");  
        addResourceLinkCell(sb, infos[i].getMappingResource());
        addLinkCell(sb, infos[i].getMappingSource());
                //SourceCount
        addNumberCell(sb, info.getNumberOfSources());
        addNumberCell(sb, info.getNumberOfTargets());
        addPredicateCell(sb, i);
        addJustificationCell(sb,i);
        if (info.getNumberOfSources() > 0){
            addNumberCell(sb, info.getNumberOfLinks() / info.getNumberOfSources());
        } else {
            sb.append("\t\t<td><td>"); 
        }
        addNumberCell(sb, info.getFrequencyMedium());
        addNumberCell(sb, info.getFrequency75());
        addNumberCell(sb, info.getFrequency90());
        addNumberCell(sb, info.getFrequency99());
        addNumberCell(sb, info.getFrequencyMax());
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
            sb.append("')\"> + </span></td>\n");
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
            sb.append("')\"> - </span></td>\n");
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
            sb.append("')\"> + </span></td>\n");
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
            sb.append("')\"> - </span></td>\n");
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
        //resource   
        sb.append("\t\t<td>&nbsp</td>");
        //source
        sb.append("\t\t<td>&nbsp</td>");
        //Source Count   
        sb.append("<td>&nbsp</td>");
        //Target Count
        sb.append("<td>&nbsp</td>");        
        //predicate    
        sb.append("<td>&nbsp</td>");
        //Justification
        sb.append("<td>&nbsp</td>");
        //Frequency
        sb.append("<td>&nbsp</td>");
        sb.append("<td>&nbsp</td>");
        sb.append("<td>&nbsp</td>");
        sb.append("<td>&nbsp</td>");
        sb.append("<td>&nbsp</td>");
        sb.append("<td>&nbsp</td>");
        //Transative
        sb.append("<td>&nbsp</td>");
        sb.append("<td>&nbsp</td>\n");
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

    private void addMappingInfoLinkByLocation(StringBuilder sb, int i) {
        int id;
        if (i > 0){
            id = i;
        } else {
            id = 0-i;
        }
        addMappingInfoLinkById(sb, infos[id].getStringId());
    }
    
    private void addMappingInfoLinkById(StringBuilder sb, String id){
         try{
            String summaryUri = httpServletRequest.getContextPath() + "/" + SetMappings.METHOD_NAME + "/" + id;
            sb.append("<a href=\"");
            sb.append(summaryUri);
            sb.append("\">");
            sb.append(id);
            sb.append("</a>");        
        } catch (Exception ex){
            sb.append(ex);
        }
    }
    
    private void addNumberCell(StringBuilder sb, Integer number) {
        if (number == null){
           sb.append("\t\t<td><td>"); 
        } else {
           addNumberOfLinksCell(sb, number); 
        }
    }
    
    private void addNumberOfLinksCell(StringBuilder sb, int numberOfLinks) {
        sb.append("\t\t<td align=\"right\">");
            sb.append(formatter.format(numberOfLinks));
            sb.append("</td>\n");
   }

    /**
     * This allows project extentions that store information on resources to change the link.
     * 
     * @param sb
     * @param resource
     * @throws BridgeDBException 
     */
   protected void addResourceLinkCell(StringBuilder sb, String resource) throws BridgeDBException{
       addLinkCell(sb, resource);
   } 
   
   private void addLinkCell(StringBuilder sb, String uri) throws BridgeDBException {
        sb.append("\t\t<td><a href=\"");
        sb.append(uri);
        sb.append("\">");
        if (uri == null){
            sb.append("null"); 
        } else {
            URIImpl impl = new URIImpl(uri); 
            sb.append(impl.getLocalName());
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
            if (chain >= 0){
                addMappingInfoLinkById(sb, "" + chain);
            } else {
                addMappingInfoLinkById(sb, "" + (0-chain));
            }
            count++;
            if (count < size){
                sb.append(", ");
            }
        }
        sb.append("</td>\n");
        sb.append("\t</tr>\n");
    }

}
