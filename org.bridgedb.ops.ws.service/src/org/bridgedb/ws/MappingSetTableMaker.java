/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.ws;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bridgedb.IDMapperException;
import org.bridgedb.rdf.RdfConfig;
import org.bridgedb.statistics.MappingSetInfo;

/**
 *
 * @author Christian
 */
public class MappingSetTableMaker implements Comparator<MappingSetInfo>{
    
    private MappingSetInfo[] infos = new MappingSetInfo[0];
    private MappingSetInfo previous;
    
    protected final NumberFormat formatter;
    
    private MappingSetTableMaker(List<MappingSetInfo> mappingSetInfos){
        infos = mappingSetInfos.toArray(infos);
        Arrays.sort(infos, this);
        formatter = NumberFormat.getInstance();
        if (formatter instanceof DecimalFormat) {
            DecimalFormatSymbols dfs = new DecimalFormatSymbols();
            dfs.setGroupingSeparator(',');
            ((DecimalFormat) formatter).setDecimalFormatSymbols(dfs);
        }
    }
    
    private void tableMaker(StringBuilder sb) throws IDMapperException{
        sb.append(TABLE_HEADER);
        newSource(sb, 0);
        for (int i = 1; i < infos.length; i++){
            if (infos[i].getSourceSysCode().equals(infos[i-1].getSourceSysCode())){
                if (infos[i].getTargetSysCode().equals(infos[i-1].getTargetSysCode())){
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
    
    String TABLE_HEADER = "<table border=\"1\">\n"
            + "\t<tr>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th></th>\n"
            + "\t\t<th>Source Data Source</th>\n"
            + "\t\t<th>Targets</th>\n"
            + "\t\t<th>Sum of Mappings</th>\n"
            + "\t\t<th>Id</th>\n"
            + "\t\t<th>Transative</th>\n"
            + "\t</tr>\n";

    @Override
    public int compare(MappingSetInfo o1, MappingSetInfo o2) {
        int test = o1.getSourceSysCode().compareTo(o2.getSourceSysCode());
        if (test != 0) return test;
        return o1.getTargetSysCode().compareTo(o2.getTargetSysCode());
    }
    
    private void newTarget(StringBuilder sb, int i) throws IDMapperException {
        if (i == infos.length - 1){
            addSingleTarget(sb ,i);
        } else if (infos[i].getTargetSysCode().equals(infos[i+1].getTargetSysCode())){
            newTargetMultipleMappings(sb,i);
        } else {
            addSingleTarget(sb ,i);            
        }
    }

    private void newSource(StringBuilder sb, int i) throws IDMapperException {
        if (i == infos.length - 1){
            addSingleSource(sb ,i);
        } else if (infos[i].getSourceSysCode().equals(infos[i+1].getSourceSysCode())){
            newSourceMultipleMappings(sb,i);
        } else {
            addSingleSource(sb ,i);            
        }
    }

    private void newSourceMultipleMappings(StringBuilder sb, int i) throws IDMapperException {
        int j = i + 1;
        int last = infos.length -1;
        int targetCount = 1;
        int mappingCount = 1;
        int numberOfLinks = infos[i].getNumberOfLinks();
        do {
            mappingCount++;
            numberOfLinks+= infos[j].getNumberOfLinks();
            if (infos[j].getTargetSysCode().equals(infos[j-1])){
                //same target
            } else {
                targetCount++;
            }
            j++;
        } while ((j < last) && 
                (infos[j].getSourceSysCode().equals(infos[j+1].getSourceSysCode()))); 
		
        addSourceSummary(sb, infos[i].getSourceSysCode(), targetCount, numberOfLinks, mappingCount); 
        addSourceDetail(sb, infos[i].getSourceSysCode(), targetCount, numberOfLinks, mappingCount); 
        newTarget(sb, i);
    }

    private void newTargetMultipleMappings(StringBuilder sb, int i) throws IDMapperException {
        int j = i + 1;
        int last = infos.length -1;
        int mappingCount = 1;
        int numberOfLinks = infos[i].getNumberOfLinks();
        do {
            mappingCount++;
            numberOfLinks+= infos[j].getNumberOfLinks();
            j++;
        } while ((j < last) && 
                (infos[j].getSourceSysCode().equals(infos[j+1].getSourceSysCode()))); 
        addTargetSummary(sb, infos[i].getSourceSysCode(), infos[i].getTargetSysCode(), numberOfLinks, mappingCount); 
        addTargetDetail(sb, infos[i].getSourceSysCode(), infos[i].getTargetSysCode(), numberOfLinks, mappingCount); 
        AddMappingSet(sb, i);
    }

    private void addSingleSource(StringBuilder sb, int i) 
            throws IDMapperException {
        sb.append("\t<tr>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addInfo(sb, i);
    }

    private void addInfo(StringBuilder sb, int i) 
            throws IDMapperException {
        addDataSourceCell(sb, infos[i].getSourceSysCode());
        addDataSourceCell(sb, infos[i].getTargetSysCode());
        addNumberOfLinksCell(sb, infos[i].getNumberOfLinks());
        addMappingSetCell(sb, infos[i].getId());
        addTransative(sb, i);
    }

    private void addSingleTarget(StringBuilder sb, int i) throws IDMapperException {
        addLevel2Tr(sb, infos[i].getSourceSysCode());
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addInfo(sb, i);
    }
    
    private void AddMappingSet(StringBuilder sb, int i) throws IDMapperException {
        addLevel3Tr(sb, infos[i].getSourceSysCode(), infos[i].getTargetSysCode());
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addInfo(sb, i);
    }		
		
    private void addSourceSummary(StringBuilder sb, String source, int targetCount, int numberOfLinks, int mappingCount) 
            throws IDMapperException {
        sb.append("\t<tr ");
            sb.append(source);
            sb.append("_level1=level1>\n");
        sb.append("\t\t<td><span onclick=\"showLevel2('");
            sb.append(source);
            sb.append("')\"> &dArr; </span></td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addDataSourceCell(sb, source);
        addTargetCount(sb, targetCount);
        addMappingSummary(sb, numberOfLinks, mappingCount); 
     }
    
    private void addSourceDetail(StringBuilder sb, String source, int targetCount, int numberOfLinks, int mappingCount) 
            throws IDMapperException {
        addLevel2Tr(sb, source);
        sb.append("\t\t<td><span onclick=\"hideLevel2('");
            sb.append(source);
            sb.append("')\"> &uArr; </span></td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        addDataSourceCell(sb, source);
        addTargetCount(sb, targetCount);
        addMappingSummary(sb, numberOfLinks, mappingCount);         
     }
    
    private void addTargetSummary(StringBuilder sb, String source, String target, int numberOfLinks, int mappingCount) 
            throws IDMapperException {
        sb.append("\t<tr ");
            sb.append(source);
            sb.append("_level2=level2; ");
            sb.append(source);
            sb.append("_");
            sb.append(target);
            sb.append("_level2=level2; style=\"display: none;\">\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td><span onclick=\"showLevel3('");
            sb.append(source);
            sb.append("_");
            sb.append(target);
            sb.append("')\"> &dArr; </span></td>\n");
        addDataSourceCell(sb, source);
        addDataSourceCell(sb, target);
        addMappingSummary(sb, numberOfLinks, mappingCount); 
     }
    
    private void addTargetDetail(StringBuilder sb, String source, String target, int numberOfLinks, int mappingCount) 
            throws IDMapperException {
        addLevel3Tr(sb, source, target);
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t\t<td><span onclick=\"hideLevel3('");
            sb.append(source);
            sb.append("_");
            sb.append(target);
            sb.append("')\"> &uArr; </span></td>\n");
        addDataSourceCell(sb, source);
        addDataSourceCell(sb, target);
        addMappingSummary(sb, numberOfLinks, mappingCount); 
    }
    
    private void addTargetCount(StringBuilder sb, int targetCount) 
            throws IDMapperException {
        sb.append("\t\t<td align=\"right\">");
            sb.append(targetCount);
            sb.append(" Targets</td>\n");
    }
    
    private void addMappingSummary(StringBuilder sb, int numberOfLinks, int mappingCount) 
            throws IDMapperException {
        addNumberOfLinksCell(sb, numberOfLinks);
        sb.append("\t\t<td align=\"right\">");
            sb.append(mappingCount);
            sb.append(" Mappings</td>\n");
        sb.append("\t\t<td>&nbsp</td>\n");
        sb.append("\t</tr>\n");
    }

    private void addLevel2Tr(StringBuilder sb, String source) 
            throws IDMapperException {
        sb.append("\t<tr ");
            sb.append(source);
            sb.append("_level2=level2; style=\"display: none;\">\n");
    }

    private void addLevel3Tr(StringBuilder sb, String source, String target) 
            throws IDMapperException {
         sb.append("\t<tr ");
            sb.append(source);
            sb.append("_level3=level3; ");
            sb.append(source);
            sb.append("_");
            sb.append(target);
            sb.append("_level3=level3; style=\"display: none;\">\n");
   }

   private void addDataSourceCell(StringBuilder sb, String sysCode) throws IDMapperException {
        sb.append("\t\t<td><a href=\"");
            sb.append(RdfConfig.getTheBaseURI());
            sb.append("dataSource/");
            sb.append(sysCode);
            sb.append("\">");
            sb.append(sysCode);
            sb.append("</a></td>\n");
    }

    private void addMappingSetCell(StringBuilder sb, String id) throws IDMapperException {
        sb.append("\t\t<td><a href=\"");
            sb.append(RdfConfig.getTheBaseURI());
            sb.append("mappingSet/");
            sb.append(id);
            sb.append("\">");
            sb.append(id);
            sb.append("</a></td>\n");
    }

    private void addNumberOfLinksCell(StringBuilder sb, int numberOfLinks) {
        sb.append("\t\t<td align=\"right\">");
            sb.append(formatter.format(numberOfLinks));
            sb.append("</td>\n");
   }

    private void addTransative(StringBuilder sb, int i) {
        sb.append("\t\t<td>");
        if (infos[i].isTransitive()){
            sb.append("true");
        } else {
            sb.append("false");            
        }
        sb.append("</td>\n");
        sb.append("\t</tr>\n");
    }

    public static void main(String[] args) throws IDMapperException {
        List<MappingSetInfo> data = new ArrayList<MappingSetInfo>();
        data.add(new MappingSetInfo("1", "S1", "p1", "T1", 25000, false));
        data.add(new MappingSetInfo("2", "S1", "p1", "T1", 50000, false));
        data.add(new MappingSetInfo("3", "S1", "p1", "T2", 50001, false));
        data.add(new MappingSetInfo("4", "S1", "p1", "T2", 25, false));
        data.add(new MappingSetInfo("5", "S2", "p1", "T1", 25, false));
        data.add(new MappingSetInfo("6", "S3", "p1", "T1", 12325, false));
        data.add(new MappingSetInfo("7", "S3", "p1", "T2", 234525, false));
        MappingSetTableMaker maker = new MappingSetTableMaker(data);
        StringBuilder sb = new StringBuilder();
        maker.tableMaker(sb);
        System.out.println(sb);
    }

}
