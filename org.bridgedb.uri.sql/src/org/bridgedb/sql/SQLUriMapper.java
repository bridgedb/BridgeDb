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
package org.bridgedb.sql;

import org.bridgedb.uri.api.MappingsBySysCodeId;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.bridgedb.DataSource;
import org.bridgedb.Xref;
import org.bridgedb.pairs.CodeMapper;
import org.bridgedb.pairs.IdSysCodePair;
import org.bridgedb.rdf.DataSourceMetaDataProvidor;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.rdf.pairs.RdfBasedCodeMapper;
import static org.bridgedb.sql.SQLUriMapper.scrubUri;
import org.bridgedb.sql.justification.JustificationMaker;
import org.bridgedb.sql.justification.OpsJustificationMaker;
import org.bridgedb.sql.predicate.LoosePredicateMaker;
import org.bridgedb.sql.predicate.PredicateMaker;
import org.bridgedb.sql.transative.ClaimedMapping;
import org.bridgedb.sql.transative.DirectMapping;
import org.bridgedb.sql.transative.MappingsHandlers;
import org.bridgedb.sql.transative.SelfMapping;
import org.bridgedb.statistics.DataSetInfo;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.statistics.OverallStatistics;
import org.bridgedb.statistics.SourceInfo;
import org.bridgedb.statistics.SourceTargetInfo;
import org.bridgedb.uri.api.Mapping;
import org.bridgedb.uri.api.MappingsBySet;
import org.bridgedb.uri.api.UriMapper;
import org.bridgedb.uri.lens.Lens;
import org.bridgedb.uri.lens.LensTools;
import org.bridgedb.uri.tools.GraphResolver;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.bridgedb.uri.tools.UriListener;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;

/**
 * Implements the UriMapper and UriListener interfaces using SQL.
 *
 * Takes into accounts the specific factors for the SQL version being used.
 *
 * @author Christian
 */
public class SQLUriMapper extends SQLIdMapper implements UriMapper, UriListener {

    private static final int CREATED_BY_LENGTH = 150;
    protected static final int JUSTIFICATION_LENGTH = 150;
    private static final int MIMETYPE_LENGTH = 50;
    private static final int POSTFIX_LENGTH = 100;
    protected static final int PREDICATE_LENGTH = 100;
    private static final int PREFIX_LENGTH = 400;
    private static final int REGEX_LENGTH = 400;

    private static final String MIMETYPE_TABLE_NAME = "mimeType";
    private static final String URI_TABLE_NAME = "uri";

    private static final String CREATED_BY_COLUMN_NAME = "createdBy";
    private static final String CREATED_ON_COLUMN_NAME = "createdOn";
    private static final String DATASOURCE_COLUMN_NAME = "dataSource";
    public static final String JUSTIFICATION_COLUMN_NAME = "justification";
    protected static final String PREDICATE_COLUMN_NAME = "predicate";
    private static final String PREFIX_COLUMN_NAME = "prefix";
    private static final String POSTFIX_COLUMN_NAME = "postfix";
    protected static final String MAPPING_LINK_COUNT_COLUMN_NAME = "mappingLinkCount";
    protected static final String MAPPING_RESOURCE_COLUMN_NAME = "resource";
    protected static final String MAPPING_SOURCE_COLUMN_NAME = "source";
    protected static final String MAPPING_SOURCE_COUNT_COLUMN_NAME = "mappingSourceCount";
    protected static final String SYMMETRIC_COLUMN_NAME = "symmetric";
    protected static final String MAPPING_TARGET_COUNT_COLUMN_NAME = "mappingTargetCount";
    private static final String MIMETYPE_COLUMN_NAME = "mimetype";
    private static final String NAME_COLUMN_NAME = "name";
    private static final String REGEX_COLUMN_NAME = "regex";

    private static final boolean INCLUDE_XREF_RESULTS = true;
    private static final boolean EXCLUDE_XREF_RESULTS = false;
    private static final boolean INCLUDE_URI_RESULTS = true;
    private static final boolean EXCLUDE_URI_RESULTS = false;
   
    private static SQLUriMapper mapper = null;

    //Queuries as String saved for speed
    private final HashMap<String, String> directMappingQueries = new HashMap<String, String>();
    private final String uriToIdSysCodePairQuery = "SELECT * FROM " + URI_TABLE_NAME + " WHERE ? LIKE CONCAT(" 
            + PREFIX_COLUMN_NAME + ",'%'," + POSTFIX_COLUMN_NAME + ")";

    /**
     * Stores the Pattern for the source of each mappingSet it is currently loading.
     * 
     * Pattern is added when an mappingSet is Registered.
     * Used to quickly check the URIs and extract the ids in insertUriMappings
     * 
     * Not used during the map functions.
     */
    protected final HashMap<Integer, RegexUriPattern> subjectUriPatterns;
    /**
     * Stores the Pattern for the source of each mappingSet it is currently loading.
     * 
     * Pattern is added when an mappingSet is Registered.
     * Used to quickly check the URIs and extract the ids in insertUriMappings
     * 
     * Not used during the map functions.
     */
    protected final HashMap<Integer, RegexUriPattern> targetUriPatterns;
    private boolean processingRawLinkset = true;

    //Currently there is only one of each of these but could be lens dependent
    private final PredicateMaker predicateMaker;
    private final JustificationMaker justificationMaker;
    private String registerMappingQuery = null;

    private static final Logger logger = Logger.getLogger(SQLUriMapper.class);

    public static SQLUriMapper getExisting() throws BridgeDBException {
        if (mapper == null) {
            CodeMapper codeMapper = new RdfBasedCodeMapper();
            mapper = new SQLUriMapper(false, codeMapper);
        }
        return mapper;
    }

    public static SQLUriMapper createNew() throws BridgeDBException {
        CodeMapper codeMapper = new RdfBasedCodeMapper();
        mapper = new SQLUriMapper(true, codeMapper);
        return mapper;
    }

    /**
     * Creates a new UriMapper including BridgeDB implementation based on a
     * connection to the SQL Database.
     *
     * @param dropTables Flag to determine if any existing tables should be
     * dropped and new empty tables created.
     * @throws BridgeDBException
     */
    protected SQLUriMapper(boolean dropTables, CodeMapper codeMapper) throws BridgeDBException {
        super(dropTables, codeMapper);
        UriPattern.refreshUriPatterns();
        clearUriPatterns();
        Collection<RegexUriPattern> patterns = RegexUriPattern.getUriPatterns();
        for (RegexUriPattern pattern : patterns) {
            this.registerUriPattern(pattern);
        }
        subjectUriPatterns = new HashMap<Integer, RegexUriPattern>();
        targetUriPatterns = new HashMap<Integer, RegexUriPattern>();
        LensTools.init(this);
        LoosePredicateMaker.init();
        predicateMaker = LoosePredicateMaker.getInstance();
        OpsJustificationMaker.init();
        justificationMaker = OpsJustificationMaker.getInstance();
    }

    @Override
    protected void dropSQLTables() throws BridgeDBException {
        super.dropSQLTables();
        dropTable(URI_TABLE_NAME);
        dropTable(MIMETYPE_TABLE_NAME);
    }

    @Override
    protected void createSQLTables() throws BridgeDBException {
        super.createSQLTables();
        Statement sh = null;
        try {
            sh = createStatement();
            sh.execute("CREATE TABLE " + URI_TABLE_NAME
                    + "  (  " + DATASOURCE_COLUMN_NAME + " VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL,   "
                    + "     " + PREFIX_COLUMN_NAME + " VARCHAR(" + PREFIX_LENGTH + ") NOT NULL, "
                    + "     " + REGEX_COLUMN_NAME + " VARCHAR(" + REGEX_LENGTH + "), "
                    + "     " + POSTFIX_COLUMN_NAME + " VARCHAR(" + POSTFIX_LENGTH + ") NOT NULL "
                    + "  ) " + SqlFactory.engineSetting());
            sh.execute("CREATE TABLE " + MIMETYPE_TABLE_NAME
                    + "  (  " + PREFIX_COLUMN_NAME + " VARCHAR(" + PREFIX_LENGTH + ") NOT NULL, "
                    + "     " + POSTFIX_COLUMN_NAME + " VARCHAR(" + POSTFIX_LENGTH + ") NOT NULL, "
                    + "     mimeType VARCHAR(" + MIMETYPE_LENGTH + ") NOT NULL "
                    + "  ) " + SqlFactory.engineSetting());
        } catch (SQLException e) {
            throw new BridgeDBException("Error creating the tables ", e);
        } finally {
            close(sh, null);
        }
    }

    @Override
    protected void createMappingSetTable() throws BridgeDBException {
        //"IF NOT EXISTS " is not supported
        String query = "";
        Statement sh = null;
        try {
            sh = createStatement();
            query = "CREATE TABLE " + MAPPING_SET_TABLE_NAME
                    + " (" + ID_COLUMN_NAME + " INT " + autoIncrement + " PRIMARY KEY, "
                    + SOURCE_DATASOURCE_COLUMN_NAME + " VARCHAR(" + SYSCODE_LENGTH + ") NOT NULL, "
                    + PREDICATE_COLUMN_NAME + " VARCHAR(" + PREDICATE_LENGTH + "), "
                    + JUSTIFICATION_COLUMN_NAME + " VARCHAR(" + JUSTIFICATION_LENGTH + "), "
                    + TARGET_DATASOURCE_COLUMN_NAME + " VARCHAR(" + SYSCODE_LENGTH + "), "
                    //While not use still adding a MAPPING_RESOURCE_COLUMN_NAME column
                    //This avoids haveing to overwrite getDirectMapping
                    + MAPPING_RESOURCE_COLUMN_NAME + " VARCHAR(" + MAPPING_URI_LENGTH + "), "
                    + MAPPING_SOURCE_COLUMN_NAME + " VARCHAR(" + MAPPING_URI_LENGTH + "), "
                    + SYMMETRIC_COLUMN_NAME + " INT, "
                    + MAPPING_LINK_COUNT_COLUMN_NAME + " INT, "
                    + MAPPING_SOURCE_COUNT_COLUMN_NAME + " INT, "
                    + MAPPING_TARGET_COUNT_COLUMN_NAME + " INT"
                    + " ) " + SqlFactory.engineSetting();
            sh.execute(query);
        } catch (SQLException e) {
            throw new BridgeDBException("Error creating the MappingSet table using " + query, e);
        } finally {
            close(sh, null);
        }
    }

    private Set<String> mapUnkownUri(String sourceUri, String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        Set<String> results = new HashSet<String>();
        if (sourceUri == null) {
            return results;
        }
        if (patternMatch(sourceUri, graph, tgtUriPatterns)) {
            results.add(sourceUri);
        }
        return results;
    }

    private Set<Mapping> mappingUnkownUri(String sourceUri, String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        Set<Mapping> results = new HashSet<Mapping>();
        Set<String> targets = mapUnkownUri (sourceUri, graph, tgtUriPatterns);
        if (!targets.isEmpty()){
            results.add(new SelfMapping(sourceUri, targets));
        }
        return results;
    }
    
    protected final boolean patternMatch(String sourceUri, String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        if (graph != null && !graph.isEmpty()) {
            if (tgtUriPatterns == null || tgtUriPatterns.isEmpty()) {
                //graphs only contain known patterns
                return false;
            } else {
                throw new BridgeDBException("Illegal call with both graph " + graph + " and tgtUriPatterns parameters " + tgtUriPatterns);
            }
        } else {
            if (tgtUriPatterns == null || tgtUriPatterns.isEmpty()) {
                //No graph and no pattern so mapto Self
                return true;
            } else {
                for (String tgtUriPattern : tgtUriPatterns) {
                    if (patternMatch(sourceUri, tgtUriPattern)) {
                        return true;
                    }
                }
                //No match found
                return false;
            }
        }
    }

    protected final boolean patternMatch(String uri, String pattern) {
        if (pattern.contains("$id")) {
            if (pattern.endsWith("$id")) {
                pattern = pattern.substring(0, pattern.length() - 3);
            } else {
                String postfix = pattern.substring(pattern.indexOf("$id") + 3);
                if (!uri.endsWith(postfix)) {
                    return false;
                }
                pattern = pattern.substring(0, pattern.indexOf("$id"));
            }
        }
        return uri.startsWith(pattern);
    }

    /**
     * Adds the WHERE clause conditions for ensuring that the returned mappings
     * are from active linksets.
     *
     * @param query Query with WHERE clause started
     * @param lensUri Uri of the lens to use
     * @throws BridgeDbSqlException if the lens does not exist
     */
    private void appendLensClause(StringBuilder query, String lensId, boolean whereAdded) throws BridgeDBException {
        if (lensId == null) {
            lensId = Lens.DEFAULT_LENS_NAME;
        }
        if (!LensTools.isAllLens(lensId)) {
            List<String> justifications = LensTools.getJustificationsbyId(lensId);
            if (justifications.isEmpty()) {
                throw new BridgeDBException("No  justifications found for Lens " + lensId);
            }
            if (whereAdded) {
                query.append(" AND ");
            } else {
                query.append(" WHERE ");
            }
            query.append(JUSTIFICATION_COLUMN_NAME);
            query.append(" IN (");
            for (int i = 0; i < justifications.size() - 1; i++) {
                query.append("'").append(justifications.get(i)).append("', ");
            }
            query.append("'").append(justifications.get(justifications.size() - 1)).append("')");
        }
    }

    @Override
    public boolean uriExists(String uri) throws BridgeDBException {
        uri = scrubUri(uri);
        Xref xref;
        try {
            xref = toXref(uri);
            if (xref == null) {
                return false;
            }
        } catch (BridgeDBException ex) {
            return false;
        }
        return this.xrefExists(xref);
    }

    @Override
    public Set<String> uriSearch(String text, int limit) throws BridgeDBException {
        Set<Xref> xrefs = freeSearch(text, limit);
        Set<String> results = new HashSet<String>();
        for (Xref xref : xrefs) {
            results.addAll(toUris(xref));
            if (results.size() >= limit) {
                break;
            }
        }
        if (results.size() > limit) {
            int count = 0;
            for (Iterator<String> i = results.iterator(); i.hasNext();) {
                String element = i.next();
                count++;
                if (count > limit) {
                    i.remove();
                }
            }
        }
        return results;
    }

    @Override
    public Xref toXref(String uri) throws BridgeDBException {
        IdSysCodePair pair = toIdSysCodePair(uri);
        if (pair == null) {
            return null;
        }
        return codeMapper.toXref(pair);
    }

    private IdSysCodePair getValidPair(String uri, String sysCode, String prefix, String postfix, String regex) {
        String id = uri.substring(prefix.length(), uri.length() - postfix.length());
        if (regex == null) {
            return new IdSysCodePair(id, sysCode);
        } else {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(id);
            if (matcher.matches()) {
                return new IdSysCodePair(id, sysCode);
            }
        }
        return null;
    }
    
    @Override
    public IdSysCodePair toIdSysCodePair(String uri) throws BridgeDBException {
        if (uri == null || uri.isEmpty()) {
            return null;
        }

        PreparedStatement statement = this.createPreparedStatement(uriToIdSysCodePairQuery);
        ResultSet rs = null;
        try {
            statement.setString(1, uri);
            rs = statement.executeQuery();
        } catch (SQLException ex) {
            close(statement, rs);
            throw new BridgeDBException("Unable to run query. " + statement, ex);
        }
        try {
            IdSysCodePair result = null;
            String oldPrefix = "";
            String oldPostfix = "";
            while (rs.next()) {
                String sysCode = rs.getString(DATASOURCE_COLUMN_NAME);
                String prefix = rs.getString(PREFIX_COLUMN_NAME);
                String postfix = rs.getString(POSTFIX_COLUMN_NAME);
                String regex = rs.getString(REGEX_COLUMN_NAME);
                if (result == null){
                    result = this.getValidPair(uri, sysCode, prefix, postfix, regex);
                    if (result != null) {
                        oldPrefix = prefix;
                        oldPostfix = postfix;
                    }                    
                } else {
                    //take the one with the longest prefix
                    if (oldPrefix.length() <= prefix.length()) {
                        IdSysCodePair second = this.getValidPair(uri, sysCode, prefix, postfix, regex);
                        if (second != null) {
                            result = second;
                            oldPrefix = prefix;
                        }
                    //otherwise the one with the shorter prefix
                    } else if (oldPrefix.length() > prefix.length()) {
                    //Take the one with the longer postfix
                    } else if (oldPostfix.length() < postfix.length()){
                        IdSysCodePair second = this.getValidPair(uri, sysCode, prefix, postfix, regex);
                        if (second != null) {
                            result = second;
                            oldPrefix = prefix;
                        }
                    } else {
                        //same prefix so for multiple DataSources 
                        //So we will take the one define in the BIO module first, then one defined in RDF
                        //Ignoring the duplicates from the miriam registry.
                        //Know dulicate prefixes of this type include
                        //http://www.kegg.jp/entry/
                        //http://www.ebi.ac.uk/ontology-lookup/?termId=
                        //http://www.gramene.org/db/ontology/search?id=
                        //http://stke.sciencemag.org/cgi/cm/stkecm;
                        //http://purl.uniprot.org/uniprot/
                        //http://antirrhinum.net/cgi-bin/ace/generic/tree/DragonDB?name=
                        //http://www.uniprot.org/uniprot/
                        //http://www.ncbi.nlm.nih.gov/nucest/
                        //http://arabidopsis.org/servlets/TairObject?accession=
                        //http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?val=
                        //https://www.proteomicsdb.org/#human/proteinDetails/
                        //http://www.ebi.ac.uk/pdbe-srv/pdbechem/chemicalCompound/show/
                        if (DataSourceMetaDataProvidor.compare(result.getSysCode(), sysCode) >= 0) { 
                            IdSysCodePair second = this.getValidPair(uri, sysCode, prefix, postfix, regex);
                            if (second != null) {
                                result = second;
                                oldPrefix = prefix;
                            }
                        }
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new BridgeDBException("Error getting IdSysCodePair using. " + statement, ex);
        } finally {
            close(statement, rs);
        }
    }

    @Override
    public RegexUriPattern toUriPattern(String uri) throws BridgeDBException {
        if (uri == null || uri.isEmpty()) {
            return null;
        }
        PreparedStatement statement = this.createPreparedStatement(uriToIdSysCodePairQuery);
        ResultSet rs = null;
        try {
            statement.setString(1, uri);
            rs = statement.executeQuery();
        } catch (SQLException ex) {
            close(statement, rs);
            throw new BridgeDBException("Unable to run query. " + statement, ex);
        }
        RegexUriPattern result = null;
        try {
            while (rs.next()) {
                String prefix = rs.getString(PREFIX_COLUMN_NAME);
                String postfix = rs.getString(POSTFIX_COLUMN_NAME);
                String regex = rs.getString(REGEX_COLUMN_NAME);
                String sysCode = rs.getString(DATASOURCE_COLUMN_NAME);
                if (regex == null) {
                    return RegexUriPattern.factory(prefix, postfix, sysCode);
                }
                Pattern regexPattern = Pattern.compile(regex);
                String id = uri.substring(prefix.length(), uri.lastIndexOf(postfix));
                Matcher matcher = regexPattern.matcher(id);
                if (matcher.matches()) {
                    if (result != null) {
                        if (DataSourceMetaDataProvidor.compare(result.getSysCode(), sysCode) > 0) {
                            //ystem.out.println("ignoring possible " + result.getSysCode());
                            result = RegexUriPattern.factory(prefix, postfix, sysCode, regexPattern);
                        } else if (DataSourceMetaDataProvidor.compare(result.getSysCode(), sysCode) == 0) {
                            RegexUriPattern second = RegexUriPattern.factory(prefix, postfix, sysCode, regexPattern);
                            throw new BridgeDBException("Uri " + uri + " maps to two different regex patterns "
                                    + result + " and " + second);
                            //} else {
                            //ystem.out.println("ignoring possible " + sysCode);
                        } //if > 0 do nothing as first answer is better
                    } else {
                        result = RegexUriPattern.factory(prefix, postfix, sysCode, regexPattern);
                    }
                }
            }
            return result;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to get uriSpace. " + statement, ex);
        } finally {
            close(statement, rs);
        }
    }

    //@Override 
    public List<Mapping> getSampleMapping() throws BridgeDBException {
        String query = "SELECT * FROM " + MAPPING_TABLE_NAME + ", " + MAPPING_SET_TABLE_NAME 
            + " WHERE " + MAPPING_TABLE_NAME + "." + MAPPING_SET_ID_COLUMN_NAME 
            + " = " + MAPPING_SET_TABLE_NAME + ". " + ID_COLUMN_NAME 
            +  lensClause(Lens.DEFAULT_LENS_NAME)
            + " LIMIT 10";
        Statement statement = this.createStatement();
       
        ResultSet rs = null;
        try {
            List<Mapping> results = new ArrayList<Mapping>();
            rs = statement.executeQuery(query);
            while (rs.next()) {
                String id = rs.getString(SOURCE_ID_COLUMN_NAME);
                String sysCode = rs.getString(SOURCE_DATASOURCE_COLUMN_NAME);
                IdSysCodePair sourceRef = new IdSysCodePair(id, sysCode);
                id = rs.getString(TARGET_ID_COLUMN_NAME);
                sysCode = rs.getString(TARGET_DATASOURCE_COLUMN_NAME);
                IdSysCodePair targetRef = new IdSysCodePair(id, sysCode);
                //stem.out.println(" = " + targetRef);
                Integer mappingSetId = rs.getInt(MAPPING_SET_ID_COLUMN_NAME);
                Integer symmetric = rs.getInt(SYMMETRIC_COLUMN_NAME);
                String predicate = rs.getString(PREDICATE_COLUMN_NAME);
                String justification = rs.getString(JUSTIFICATION_COLUMN_NAME);
                String mappingResource = rs.getString(MAPPING_RESOURCE_COLUMN_NAME);
                String mappingSource = rs.getString(MAPPING_SOURCE_COLUMN_NAME);
                DirectMapping mapping = new DirectMapping(sourceRef, targetRef, mappingSetId, symmetric, predicate, 
                        justification, null, mappingSource, Lens.DEFAULT_LENS_NAME);
                mapping.setSource(codeMapper.toXref(sourceRef));
                mapping.setSourceUri(toUris(sourceRef));
                mapping.setTarget(codeMapper.toXref(targetRef));
                mapping.setTargetUri(toUris(targetRef));
                results.add(mapping);
            }
            //ystem.out.println(results);
            return results;
        } catch (SQLException ex) {
            close(statement, rs);
            throw new BridgeDBException("Error running query " + statement, ex);
        } finally {
            close(null, rs);
        }
    }

    @Override
    public OverallStatistics getOverallStatistics(String lensId) throws BridgeDBException {
        int numberOfLenses;
        if (LensTools.isAllLens(lensId)) {
            numberOfLenses = LensTools.getNumberOfLenses();
        } else {
            numberOfLenses = 1;
        }
        StringBuilder query = new StringBuilder("SELECT count(*) as numberOfMappingSets, ");
        query.append("count(distinct(");
        query.append(SOURCE_DATASOURCE_COLUMN_NAME);
        query.append(")) as numberOfSourceDataSources,");
        query.append(" count(distinct(");
        query.append(PREDICATE_COLUMN_NAME);
        query.append(")) as numberOfPredicates,");
        query.append(" count(distinct(");
        query.append(TARGET_DATASOURCE_COLUMN_NAME);
        query.append(")) as numberOfTargetDataSources,");
        query.append(" sum(");
        query.append(MAPPING_LINK_COUNT_COLUMN_NAME);
        query.append(") as numberOfMappings ");
        query.append(" FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        this.appendLensClause(query, lensId, false);
        Statement statement = this.createStatement();
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(query.toString());
            if (rs.next()) {
                int numberOfMappingSets = rs.getInt("numberOfMappingSets");
                int numberOfSourceDataSources = rs.getInt("numberOfSourceDataSources");
                int numberOfPredicates = rs.getInt("numberOfPredicates");
                int numberOfTargetDataSources = rs.getInt("numberOfTargetDataSources");
                int numberOfMappings = rs.getInt("numberOfMappings");
                return new OverallStatistics(numberOfMappings, numberOfMappingSets,
                        numberOfSourceDataSources, numberOfPredicates,
                        numberOfTargetDataSources, numberOfLenses);
            } else {
                close(statement, rs);
                throw new BridgeDBException("no Results for query. " + query.toString());
            }
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query.toString(), ex);
        } finally {
            close(statement, rs);
        }
    }

    @Override
    public MappingSetInfo getMappingSetInfo(int mappingSetId) throws BridgeDBException {
        StringBuilder query = new StringBuilder("SELECT *");
        query.append(" FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        query.append(" WHERE ");
        query.append(ID_COLUMN_NAME);
        query.append(" = ");
        query.append(mappingSetId);
        Statement statement = this.createStatement();
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(query.toString());
            List<MappingSetInfo> results = resultSetToMappingSetInfos(rs);
            if (results.isEmpty()) {
                return null;
            }
            if (results.size() > 1) {
                throw new BridgeDBException(results.size() + " mappingSets found with id " + mappingSetId);
            }
            return results.get(0);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        } finally {
            close(statement, rs);
        }
    }

    @Override
    public List<MappingSetInfo> getMappingSetInfos(String sourceSysCode, String targetSysCode, String lensUri) throws BridgeDBException {
        if (sourceSysCode == null || sourceSysCode.isEmpty()) {
            throw new BridgeDBException("MappingSetInfos is no longer supported with supplying a sourceSysCOde due to data size.");
        }
        if (targetSysCode == null || targetSysCode.isEmpty()) {
            throw new BridgeDBException("MappingSetInfos is no longer supported with supplying a targetSysCode due to data size.");
        }
        StringBuilder query = new StringBuilder("select *");
        query.append(" FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        boolean whereSet = appendSystemCodes(query, sourceSysCode, targetSysCode);
        appendLensClause(query, lensUri, whereSet);
        Statement statement = this.createStatement();
        ResultSet rs = null;
        List<MappingSetInfo> results;
        try {
            rs = statement.executeQuery(query.toString());
            results = resultSetToMappingSetInfos(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        } finally {
            close(statement, rs);
        }
        return results;
    }

    @Override
    public List<SourceInfo> getSourceInfos(String lensUri) throws BridgeDBException {
        StringBuilder query = new StringBuilder("select ");
        query.append(SOURCE_DATASOURCE_COLUMN_NAME);
        query.append(", count(*) as linksets, count(distinct(");
        query.append(TARGET_DATASOURCE_COLUMN_NAME);
        query.append(")) AS targets, sum(");
        query.append(MAPPING_LINK_COUNT_COLUMN_NAME);
        query.append(") AS links FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        appendLensClause(query, lensUri, false);
        query.append(" GROUP BY ");
        query.append(SOURCE_DATASOURCE_COLUMN_NAME);
        Statement statement = this.createStatement();
        ResultSet rs = null;

        List<SourceInfo> results;
        try {
            rs = statement.executeQuery(query.toString());
            results = resultSetToSourceInfos(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        } finally {
            close(statement, rs);
        }
        return results;
    }

    @Override
    public List<SourceTargetInfo> getSourceTargetInfos(String sourceSysCode, String lensUri) throws BridgeDBException {
        StringBuilder query = new StringBuilder("select ");
        query.append(TARGET_DATASOURCE_COLUMN_NAME);
        query.append(", count(*) as linksets, sum(");
        query.append(MAPPING_LINK_COUNT_COLUMN_NAME);
        query.append(") AS links FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        query.append(" WHERE ");
        query.append(SOURCE_DATASOURCE_COLUMN_NAME);
        query.append(" = ? ");
        appendLensClause(query, lensUri, true);
        query.append(" GROUP BY ");
        query.append(TARGET_DATASOURCE_COLUMN_NAME);
        PreparedStatement statement = createPreparedStatement(query.toString());
        ResultSet rs = null;
        List<SourceTargetInfo> results;
        try {
            statement.setString(1, sourceSysCode);
            rs = statement.executeQuery();
            results = resultSetToSourceTargetInfos(sourceSysCode, rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        } finally {
            close(statement, rs);
        }
        return results;
    }

    @Override
    public Set<String> getUriPatterns(String dataSource) throws BridgeDBException {
        String query = ("SELECT " + PREFIX_COLUMN_NAME + ", " + POSTFIX_COLUMN_NAME + " FROM " + URI_TABLE_NAME
                + " WHERE " + DATASOURCE_COLUMN_NAME + " = '" + dataSource + "'");
        Statement statement = this.createStatement();
        ResultSet rs = null;
        Set<String> results;
        try {
            rs = statement.executeQuery(query.toString());
            results = resultSetToUriPattern(rs);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        } finally {
            close(statement, rs);
        }
        return results;
    }

    @Override
    public int getSqlCompatVersion() throws BridgeDBException {
        String query = ("select " + SCHEMA_VERSION_COLUMN_NAME + " from " + INFO_TABLE_NAME);
        Statement statement = this.createStatement();
        ResultSet rs = null;
        int result;
        try {
            rs = statement.executeQuery(query.toString());
            //should always be there unless something has gone majorly wrong.
            rs.next();
            result = rs.getInt("schemaversion");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        } finally {
            close(statement, rs);
        }
        return result;
    }

    // **** UriListener Methods
    private void registerUriPattern(RegexUriPattern uriPattern) throws BridgeDBException {
        String code = uriPattern.getSysCode();
        String prefix = uriPattern.getPrefix();
        if (prefix.length() > PREFIX_LENGTH) {
            throw new BridgeDBException("Prefix Length ( " + prefix.length() + ") is too long for " + prefix);
        }
        DataSource dataSource = DataSource.getExistingBySystemCode(code);
        Pattern regex = uriPattern.getRegex();
        String postfix = uriPattern.getPostfix();
        if (postfix.length() > POSTFIX_LENGTH) {
            throw new BridgeDBException("Postfix Length ( " + prefix.length() + ") is too long for " + prefix);
        }

        prefix = insertEscpaeCharacters(prefix);
        postfix = insertEscpaeCharacters(postfix);
        checkExistingUriPatterns(uriPattern);
        registerUriPattern(prefix, postfix, code, regex);
    }

    private void registerUriPattern(String prefix, String postfix, String code, Pattern regex) throws BridgeDBException {
        StringBuilder query = new StringBuilder("INSERT INTO ").append(URI_TABLE_NAME).append(" (")
                .append(PREFIX_COLUMN_NAME).append(", ")
                .append(POSTFIX_COLUMN_NAME).append(", ")
                .append(DATASOURCE_COLUMN_NAME).append(", ")
                .append(REGEX_COLUMN_NAME).append(") VALUES ")
                .append(" (?, ?, ?, ?)");
        PreparedStatement statement = this.createPreparedStatement(query.toString());
        String regexSt = null;
        if (regex != null) {
            regexSt = regex.toString();
            if (regexSt.length() > REGEX_LENGTH) {
                throw new BridgeDBException("pattern length ( " + regexSt.length() + ") is too long for " + regex);
            }
        }
        try {
            statement.setString(1, prefix);
            statement.setString(2, postfix);
            statement.setString(3, code);
            statement.setString(4, regexSt);
            int changed = statement.executeUpdate();
        } catch (SQLException ex) {
            throw new BridgeDBException("Error inserting prefix " + prefix + " and postfix " + postfix, ex, statement.toString());
        }
    }
    
    @Override
    public int registerMappingSet(RegexUriPattern sourceUriPattern, String predicate, String justification,
            RegexUriPattern targetUriPattern, Resource mappingSource, boolean symetric) throws BridgeDBException {
        checkUriPattern(sourceUriPattern);
        checkUriPattern(targetUriPattern);
        DataSource source = DataSource.getExistingBySystemCode(sourceUriPattern.getSysCode());
        DataSource target = DataSource.getExistingBySystemCode(targetUriPattern.getSysCode());
        int mappingSetId = registerMappingSet(source, target, predicate, justification, mappingSource, 0);
        if (symetric) {
            int symetricId = registerMappingSet(target, source, predicate, justification, mappingSource, mappingSetId);
            setSymmetric(mappingSetId, symetricId);
        }
        subjectUriPatterns.put(mappingSetId, sourceUriPattern);
        targetUriPatterns.put(mappingSetId, targetUriPattern);
        return mappingSetId;
    }

    @Override
    public int registerMappingSet(RegexUriPattern sourceUriPattern, String predicate, String forwardJustification, String backwardJustification,
            RegexUriPattern targetUriPattern, Resource mappingSource) throws BridgeDBException {
        if (forwardJustification.equals(backwardJustification)){
            return registerMappingSet(sourceUriPattern, predicate, forwardJustification, targetUriPattern, mappingSource, true);
        } else {
            checkUriPattern(sourceUriPattern);
            checkUriPattern(targetUriPattern);
            DataSource source = DataSource.getExistingBySystemCode(sourceUriPattern.getSysCode());
            DataSource target = DataSource.getExistingBySystemCode(targetUriPattern.getSysCode());
            int mappingSetId = registerMappingSet(source, target, predicate, forwardJustification, mappingSource, 0);
            int symetricId = registerMappingSet(target, source, predicate, backwardJustification, mappingSource, 0);
            subjectUriPatterns.put(mappingSetId, sourceUriPattern);
            targetUriPatterns.put(mappingSetId, targetUriPattern);
            //Two linksets are NOT symmetric
            return mappingSetId;
        }
    }
    
    @Override
    public int registerMappingSet(DataSource source, DataSource target, boolean symetric) throws BridgeDBException{
        throw new BridgeDBException ("Not supported in URI Version.");
    }

    /**
     * One way registration of Mapping Set.
     *
     * @param justification
     *
     */
    private int registerMappingSet(DataSource source, DataSource target, String predicate,
            String justification, Resource mappingSource, int symmetric) throws BridgeDBException {
        PreparedStatement statement = null;
        try {
            if (registerMappingQuery == null){
                StringBuilder query = new StringBuilder("INSERT INTO ");
                query.append(MAPPING_SET_TABLE_NAME);
                query.append(" (");
                query.append(SOURCE_DATASOURCE_COLUMN_NAME); //1
                query.append(", ");
                query.append(PREDICATE_COLUMN_NAME); //2
                query.append(", ");
                query.append(JUSTIFICATION_COLUMN_NAME); //3
                query.append(", ");
                query.append(TARGET_DATASOURCE_COLUMN_NAME); //4
                //MAPPING_RESOURCE_COLUMN_NAME is not set by this methods as only used in the IMS
                query.append(", ");
                query.append(MAPPING_SOURCE_COLUMN_NAME); //5
                query.append(", ");
                query.append(SYMMETRIC_COLUMN_NAME); //6
                query.append(") VALUES ( ?, ?, ?, ? , ?, ?)");
                registerMappingQuery = query.toString();
            }
            statement = createPreparedStatement(registerMappingQuery);
            statement.setString(1, getDataSourceKey(source));
            statement.setString(2, predicate);
            statement.setString(3, justification);
            statement.setString(4, getDataSourceKey(target));
            statement.setString(5, mappingSource.stringValue());
            statement.setInt(6, symmetric);
            statement.executeUpdate();
            int autoinc = getAutoInc();
            logger.info("Registered new Mapping " + autoinc + " from " + getDataSourceKey(source) + " to " + getDataSourceKey(target));
            return autoinc;
        } catch (SQLException ex) {
            throw new BridgeDBException ("Error registering mappingSet ", ex);
        } finally {
            close(statement, null);
        }
    }

    protected final void setSymmetric(int mappingSetId, int symetricId) throws BridgeDBException {
        String mappingUri = null;
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(MAPPING_SET_TABLE_NAME);
        query.append(" SET ");
        query.append(SYMMETRIC_COLUMN_NAME);
        query.append(" =  ? WHERE ");
        query.append(ID_COLUMN_NAME);
        query.append(" = ?");
        try {
            PreparedStatement statement = createPreparedStatement(query.toString());
            statement.setInt(1, 0 - symetricId);
            statement.setInt(2, mappingSetId);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new BridgeDBException("Error inserting symmetric with " + query.toString(), ex);
        }
    }

    protected final void checkUriPattern(RegexUriPattern pattern) throws BridgeDBException {
        String postfix = pattern.getPostfix();
        if (postfix == null) {
            postfix = "";
        }
        String query;
        if (pattern.getRegex() == null) {
            query = "SELECT " + DATASOURCE_COLUMN_NAME
                    + " FROM " + URI_TABLE_NAME
                    + " WHERE " + PREFIX_COLUMN_NAME
                    + " = ? AND " + POSTFIX_COLUMN_NAME + " = ? "
                    + " AND " + REGEX_COLUMN_NAME + " is NULL";
        } else {
            query = "SELECT " + DATASOURCE_COLUMN_NAME
                    + " FROM " + URI_TABLE_NAME
                    + " WHERE " + PREFIX_COLUMN_NAME
                    + " = ? AND " + POSTFIX_COLUMN_NAME
                    + " = ? AND " + REGEX_COLUMN_NAME + " = ?";
        }
        PreparedStatement statement = this.createPreparedStatement(query);
        ResultSet rs = null;
        try {
            statement.setString(1, pattern.getPrefix());
            statement.setString(2, postfix);
            if (pattern.getRegex() != null) {
                statement.setString(3, pattern.getRegex().pattern());
            }
            rs = statement.executeQuery();
            if (rs.next()) {
                String storedCode = rs.getString(DATASOURCE_COLUMN_NAME);
                String newCode = pattern.getSysCode();
                if (!storedCode.equals(newCode)) {
                    throw new BridgeDBException(pattern + " has a different Code to what was registered. Expected "
                            + pattern.getSysCode() + " but found " + storedCode);
                }
            } else {
                throw new BridgeDBException("Unregistered pattern. " + pattern);
            }
        } catch (SQLException e) {
            throw new BridgeDBException("Unable to check pattern. " + query, e);
        } finally {
            close(statement, rs);
        }
    }

    //TODO check regex
    @Override
    public void insertUriMapping(String sourceUri, String targetUri, int mappingSetId, boolean symetric) throws BridgeDBException {
        boolean ok = true;
        RegexUriPattern sourceUriPattern = subjectUriPatterns.get(mappingSetId);
        if (sourceUriPattern == null) {
            throw new BridgeDBException(
                "No SourceURIPattern regstered for mappingSetId " + mappingSetId +
                " with URI pattern: " + sourceUriPattern
            );
        }
        int end = sourceUri.length() - sourceUriPattern.getPostfix().length();
        if (!sourceUri.startsWith(sourceUriPattern.getPrefix())) {
            throw new BridgeDBException("SourceUri: " + sourceUri + " does not match the registered pattern " + sourceUriPattern);
        }
        if (!sourceUri.endsWith(sourceUriPattern.getPostfix())) {
            throw new BridgeDBException("SourceUri: " + sourceUri + " does not match the registered pattern " + sourceUriPattern);
        }
        String sourceId = sourceUri.substring(sourceUriPattern.getPrefix().length(), end);

        RegexUriPattern targetUriPattern = targetUriPatterns.get(mappingSetId);
        if (targetUriPattern == null) {
            throw new BridgeDBException("No TargetURIPattern regstered for mappingSetId " + mappingSetId);
        }
        if (!targetUri.startsWith(targetUriPattern.getPrefix())) {
            throw new BridgeDBException("TargetUri: " + targetUri + " does not match the registered pattern " + targetUriPattern);
        }
        if (!targetUri.endsWith(targetUriPattern.getPostfix())) {
            throw new BridgeDBException("TargetUri: " + targetUri + " does not match the registered pattern " + sourceUriPattern);
        }
        end = targetUri.length() - targetUriPattern.getPostfix().length();
        String targetId = targetUri.substring(targetUriPattern.getPrefix().length(), end);

        if (processingRawLinkset) {
            if (sourceUriPattern.getRegex() != null) {
                Matcher matcher = sourceUriPattern.getRegex().matcher(sourceId);
                if (!matcher.matches()) {
                    ok = false;
                }
            }
            if (targetUriPattern.getRegex() != null) {
                Matcher matcher = targetUriPattern.getRegex().matcher(targetId);
                if (!matcher.matches()) {
                    ok = false;
                }
            }
        }

        if (ok) {
            this.insertLink(sourceId, targetId, mappingSetId, symetric);
        }
    }

    @Override
    public void closeInput() throws BridgeDBException {
        super.closeInput();
        countLinks();
        subjectUriPatterns.clear();
        targetUriPatterns.clear();
    }

    private DataSetInfo findDataSetInfo(String sysCode) throws BridgeDBException {
        DataSource ds = DataSource.getExistingBySystemCode(sysCode);
        return new DataSetInfo(sysCode, ds.getFullName());
    }

    /**
     * Generates a set of UriSpaces a ResultSet.
     *
     * This implementation just extracts the URISpace
     *
     * @param rs Result Set holding the information
     * @return UriSpaces generated
     * @throws BridgeDBException
     */
    private Set<String> resultSetToUriPattern(ResultSet rs) throws BridgeDBException {
        try {
            HashSet<String> uriPatterns = new HashSet<String>();
            while (rs.next()) {
                String prefix = rs.getString(PREFIX_COLUMN_NAME);
                String postfix = rs.getString(POSTFIX_COLUMN_NAME);
                uriPatterns.add(prefix + "$id" + postfix);
            }
            return uriPatterns;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
        }
    }

    /**
     * Generates the meta info from the result of a query
     *
     * @param rs
     * @return
     * @throws BridgeDBException
     */
    public List<MappingSetInfo> resultSetToMappingSetInfos(ResultSet rs) throws BridgeDBException {
        ArrayList<MappingSetInfo> results = new ArrayList<MappingSetInfo>();
        try {
            while (rs.next()) {
                int id = rs.getInt(ID_COLUMN_NAME);
                //Set<DataSetInfo> viaSysCodes = getViaCodes(id);
                //Set<Integer> chainIds = getChainIds(id);
                DataSetInfo sourceInfo = findDataSetInfo(rs.getString(SOURCE_DATASOURCE_COLUMN_NAME));
                DataSetInfo targetInfo = findDataSetInfo(rs.getString(TARGET_DATASOURCE_COLUMN_NAME));

                results.add(new MappingSetInfo(id,
                        sourceInfo,
                        rs.getString(PREDICATE_COLUMN_NAME),
                        targetInfo,
                        rs.getString(JUSTIFICATION_COLUMN_NAME),
                        rs.getString(MAPPING_RESOURCE_COLUMN_NAME),
                        rs.getString(MAPPING_SOURCE_COLUMN_NAME),
                        rs.getInt(SYMMETRIC_COLUMN_NAME),
                        rs.getInt(MAPPING_LINK_COUNT_COLUMN_NAME),
                        rs.getInt(MAPPING_SOURCE_COUNT_COLUMN_NAME),
                        rs.getInt(MAPPING_TARGET_COUNT_COLUMN_NAME)));
            }
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
        }
        return results;
    }

    /**
     * Generates the meta info from the result of a query
     *
     * @param rs
     * @return
     * @throws BridgeDBException
     */
    public List<SourceInfo> resultSetToSourceInfos(ResultSet rs) throws BridgeDBException {
        ArrayList<SourceInfo> results = new ArrayList<SourceInfo>();
        try {
            while (rs.next()) {
                DataSetInfo sourceInfo = findDataSetInfo(rs.getString(SOURCE_DATASOURCE_COLUMN_NAME));
                results.add(new SourceInfo(sourceInfo, rs.getInt("targets"), rs.getInt("linksets"), rs.getInt("links")));
            }
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
        }
        return results;
    }

    /**
     * Generates the meta info from the result of a query
     *
     * @param rs
     * @return
     * @throws BridgeDBException
     */
    public List<SourceTargetInfo> resultSetToSourceTargetInfos(String sourceSysCode, ResultSet rs) throws BridgeDBException {
        ArrayList<SourceTargetInfo> results = new ArrayList<SourceTargetInfo>();
        DataSetInfo sourceInfo = findDataSetInfo(sourceSysCode);
        try {
            while (rs.next()) {
                DataSetInfo targetInfo = findDataSetInfo(rs.getString(TARGET_DATASOURCE_COLUMN_NAME));
                results.add(new SourceTargetInfo(sourceInfo, targetInfo, rs.getInt("linksets"), rs.getInt("links")));
            }
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
        }
        return results;
    }

    /**
     * Finds the SysCode of the DataSource which includes this prefix and
     * postfix
     *
     * Should be replaced by a more complex method from identifiers.org
     *
     * @param prefix to find DataSource for
     * @param postfix to find DataSource for
     * @return sysCode of an existig DataSource or null
     * @throws BridgeDBException
     */
    private void checkExistingUriPatterns(RegexUriPattern uriPattern) throws BridgeDBException {
        String code = uriPattern.getSysCode();
        String prefix = uriPattern.getPrefix();
        String postfix = uriPattern.getPostfix();
        if (postfix == null) {
            postfix = "";
        }
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append(DATASOURCE_COLUMN_NAME);
        query.append(" FROM ");
        query.append(URI_TABLE_NAME);
        query.append(" WHERE ");
        query.append(PREFIX_COLUMN_NAME);
        query.append(" = ? AND ");
        query.append(POSTFIX_COLUMN_NAME);
        query.append(" = ? ");
        if (uriPattern.getRegex() != null) {
            query.append(" AND ");
            query.append(REGEX_COLUMN_NAME);
            query.append(" = ? ");
        }
        PreparedStatement statement = this.createPreparedStatement(query.toString());
        ResultSet rs = null;
        try {
            statement.setString(1, prefix);
            statement.setString(2, postfix);
            if (uriPattern.getRegex() != null) {
                statement.setString(3, uriPattern.getRegex().pattern());
            }
            rs = statement.executeQuery();
        } catch (SQLException ex) {
            close(statement, rs);
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
        try {
            if (rs.next()) {
                String dataSourceKey = rs.getString(DATASOURCE_COLUMN_NAME);
                if (!code.equals(dataSourceKey)) {
                    throw new BridgeDBException("UriPattern " + prefix + "$id" + postfix + " already mapped to " + dataSourceKey
                            + " Which does not match " + code);
                }
            }
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to get SysCode. " + statement, ex);
        } finally {
            close(statement, rs);
        }
    }

    private boolean appendSystemCodes(StringBuilder query, String sourceSysCode, String targetSysCode) {
        boolean whereSet = false;
        if (sourceSysCode != null && !sourceSysCode.isEmpty()) {
            query.append(" WHERE ");
            whereSet = true;
            query.append(SOURCE_DATASOURCE_COLUMN_NAME);
            query.append(" = \"");
            query.append(sourceSysCode);
            query.append("\" ");
        }
        if (targetSysCode != null && !targetSysCode.isEmpty()) {
            if (whereSet) {
                query.append(" AND ");
            } else {
                query.append(" WHERE ");
            }
            query.append(TARGET_DATASOURCE_COLUMN_NAME);
            query.append(" = \"");
            query.append(targetSysCode);
            query.append("\" ");
            return true;
        }
        return whereSet;
    }

    public final Set<String> toUris(Xref xref) throws BridgeDBException {
        IdSysCodePair ref = toIdSysCodePair(xref);
        if (ref == null) {
            return new HashSet<String>();
        }
        return toUris(ref);
    }

    private Set<String> toUris(IdSysCodePair ref) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT " + PREFIX_COLUMN_NAME + ", " + POSTFIX_COLUMN_NAME);
        query.append(" FROM ");
        query.append(URI_TABLE_NAME);
        query.append(" WHERE ");
        query.append(DATASOURCE_COLUMN_NAME);
        query.append(" = '");
        query.append(ref.getSysCode());
        query.append("' ");
        Statement statement = this.createStatement();
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(query.toString());
        } catch (SQLException ex) {
            close(statement, rs);
            throw new BridgeDBException("Unable to run query. " + query, ex);
        }
        HashSet<String> results = new HashSet<String>();
        try {
            while (rs.next()) {
                String prefix = rs.getString(PREFIX_COLUMN_NAME);
                String postfix = rs.getString(POSTFIX_COLUMN_NAME);
                String uri = prefix + ref.getId() + postfix;
                results.add(uri);
            }
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to parse results.", ex);
        } finally {
            close(statement, rs);
        }
        return results;
    }

    private void clearUriPatterns() throws BridgeDBException {
        String update = "DELETE FROM " + URI_TABLE_NAME;
        Statement statement = null;
        try {
            statement = createStatement();
            statement.executeUpdate(update);
        } catch (BridgeDBException ex) {
            close(statement, null);
            throw ex;
        } catch (SQLException ex) {
            throw new BridgeDBException("Error clearing uri patterns " + update, ex);
        } finally {
            close(statement, null);
        }
    }

    public final static String scrubUri(String original) {
        if (original == null) {
            return null;
        }
        String result = original.trim();
        if (result.startsWith("<")) {
            result = result.substring(1);
        }
        if (result.endsWith(">")) {
            result = result.substring(0, result.length() - 1);
        }
        return result.trim();
    }

    @Override
    public Set<String> getJustifications() throws BridgeDBException {
        HashSet<String> justifications = new HashSet<String>();
        String lensQuery = "SELECT DISTINCT " + JUSTIFICATION_COLUMN_NAME
                + " FROM " + MAPPING_SET_TABLE_NAME;
        Statement statement = this.createStatement();
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(lensQuery);
            while (rs.next()) {
                justifications.add(rs.getString(JUSTIFICATION_COLUMN_NAME));
            }
        } catch (SQLException ex) {
            throw new BridgeDBException("Error retrieving justifications ", ex);
        } finally {
            close(statement, rs);
        }
        return justifications;
    }

    private int getMaxCounted() throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT MAX(");
        query.append(ID_COLUMN_NAME);
        query.append(") as maxCount FROM ");
        query.append(MAPPING_SET_TABLE_NAME);
        query.append(" WHERE NOT(");
        query.append(MAPPING_LINK_COUNT_COLUMN_NAME);
        query.append(" is NULL)");

        Statement statement = this.createStatement();
        ResultSet rs = null;
        try {
            rs = statement.executeQuery(query.toString());
            if (rs.next()) {
                return (rs.getInt("maxCount"));
            }
            return 0;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run query. " + query, ex);
        } finally {
            close(statement, rs);
        }
    }

    @Override
    public void recover() throws BridgeDBException {
        int max = getMaxCounted();
        deleteUncounted(MAPPING_TABLE_NAME, MAPPING_SET_ID_COLUMN_NAME, max);
        deleteUncounted(MAPPING_SET_TABLE_NAME, ID_COLUMN_NAME, max);
        resetAutoIncrement(max);
    }

    private void deleteUncounted(String tableName, String idColumnName, int max) throws BridgeDBException {
        StringBuilder update = new StringBuilder();
        update.append("DELETE FROM ");
        update.append(tableName);
        update.append(" WHERE ");
        update.append(idColumnName);
        update.append(" > ");
        update.append(max);

        Statement statement = this.createStatement();
        try {
            statement.executeUpdate(update.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run update. " + update, ex);
        } finally {
            close(statement, null);
        }
    }

    private void resetAutoIncrement(int max) throws BridgeDBException {
        StringBuilder update = new StringBuilder();
        update.append("ALTER TABLE ");
        update.append(MAPPING_SET_TABLE_NAME);
        update.append(" AUTO_INCREMENT = ");
        update.append(max + 1);

        Statement statement = this.createStatement();
        try {
            statement.executeUpdate(update.toString());
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run update. " + update, ex);
        } finally {
            close(statement, null);
        }
    }

    /**
     * Updates the count variable for each Mapping Sets.
     * <p>
     * This allows the counts of the mappings in each Mapping Set to be quickly
     * returned.
     *
     * @throws BridgeDBException
     */
    private void countLinks() throws BridgeDBException {
        logger.debug("Updating link counts. Please Wait!");
        Statement countStatement = this.createStatement();
        String query = ("select " + ID_COLUMN_NAME
                + " from " + MAPPING_SET_TABLE_NAME
                + " where " + MAPPING_LINK_COUNT_COLUMN_NAME + " is NULL");
        ResultSet rs = null;
        //ystem.out.println(query);
        try {
            rs = countStatement.executeQuery(query);
            while (rs.next()) {
                int mappingSetId = rs.getInt(ID_COLUMN_NAME);
                int mappings = countLinks(mappingSetId);
            }
            logger.debug("Updating counts finished!");
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        } finally {
            close(countStatement, rs);
        }
    }

    /**
     * Updates the count variable for each Mapping Sets.
     * <p>
     * This allows the counts of the mappings in each Mapping Set to be quickly
     * returned.
     *
     * @throws BridgeDBException
     */
    private int countLinks(int mappingSetId) throws BridgeDBException {
        logger.debug("Updating link count for " + mappingSetId + ". Please Wait!");
        Statement countStatement = this.createStatement();
        Statement updateStatement = this.createStatement();
        StringBuilder query = new StringBuilder("select count(distinct(");
        query.append(SOURCE_ID_COLUMN_NAME);
        query.append("))AS sources,");
        query.append(" COUNT(distinct(");
        query.append(TARGET_ID_COLUMN_NAME);
        query.append(")) as targets,");
        query.append(" COUNT(*) as mappings ");
        query.append(" FROM ");
        query.append(MAPPING_TABLE_NAME);
        addStatsMappingSetIdConditions(query, mappingSetId);
        ResultSet rs = null;
        try {
            rs = countStatement.executeQuery(query.toString());
            logger.debug("Count query run. Updating link count now");
            while (rs.next()) {
                int sources = rs.getInt("sources");
                int targets = rs.getInt("targets");
                int mappings = rs.getInt("mappings");
                StringBuilder update = new StringBuilder("UPDATE ");
                update.append(MAPPING_SET_TABLE_NAME);
                update.append(" SET ");
                update.append(MAPPING_SOURCE_COUNT_COLUMN_NAME);
                update.append(" = ");
                update.append(sources);
                update.append(", ");
                update.append(MAPPING_TARGET_COUNT_COLUMN_NAME);
                update.append(" = ");
                update.append(targets);
                update.append(", ");
                update.append(MAPPING_LINK_COUNT_COLUMN_NAME);
                update.append(" = ");
                update.append(mappings);
                addStatsIdConditions(update, mappingSetId);
                //ystem.out.println(update);
                try {
                    int updateCount = updateStatement.executeUpdate(update.toString());
                    if (updateCount != 1) {
                        throw new BridgeDBException("Updated rows " + updateCount + " <> 1 when running " + update);
                    }
                    logger.debug("Updating counts finished!");
                    return mappings;
                } catch (SQLException ex) {
                    throw new BridgeDBException("Unable to run update. " + update, ex);
                }
            }
            throw new BridgeDBException("No results for " + query);
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new BridgeDBException("Unable to run query. " + query, ex);
        } finally {
            close(updateStatement, null);
            close(countStatement, rs);
        }
    }

    private void addStatsIdConditions(StringBuilder query, int mappingSetId) {
        query.append(" WHERE ");
        query.append(ID_COLUMN_NAME);
        query.append(" = ");
        query.append(mappingSetId);
    }

    private void addStatsMappingSetIdConditions(StringBuilder query, int mappingSetId) {
        query.append(" WHERE ");
        query.append(MAPPING_SET_ID_COLUMN_NAME);
        query.append(" = ");
        query.append(mappingSetId);
    }

    public final Set<RegexUriPattern> findRegexPatternsWithNulls(String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        if (tgtUriPatterns == null || tgtUriPatterns.isEmpty()) {
            return GraphResolver.getUriPatternsForGraph(graph);
        }
        if (graph == null || graph.trim().isEmpty()) {
            HashSet<RegexUriPattern> results = new HashSet<RegexUriPattern>();
            for (String tgtUriPattern:tgtUriPatterns) {
                if (tgtUriPattern == null || tgtUriPattern.isEmpty()) {
                    results.add(null);
                } else if (tgtUriPattern.contains("$id")) {    
                    //todo regex in pattern
                    UriPattern uriPattern = UriPattern.byPattern(tgtUriPattern);
                    if (uriPattern == null){
                        results.add(null);
                    } else {
                        results.addAll(RegexUriPattern.byPattern(uriPattern));
                    }
                } else {
                    results.addAll(getRegexByPartialPrefix(tgtUriPattern));
                }
            }
            return results;
        }
        throw new BridgeDBException("Illegal call with both graph and tgtUriPatterns parameters");
    }

    private List<RegexUriPattern> getRegexByPartialPrefix(String partPrefix) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        query.append("SELECT * FROM ");
        query.append(URI_TABLE_NAME);
        query.append(" WHERE ");
        query.append(PREFIX_COLUMN_NAME);
        query.append(" LIKE CONCAT(?, '%')");
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = this.createPreparedStatement(query.toString());
            statement.setString(1, partPrefix);
            rs = statement.executeQuery();
        } catch (SQLException ex) {
            System.err.println(statement.toString());
            close(statement, rs);
            statement.toString();
            throw new BridgeDBException("Unable to run query. " + query + " with " + partPrefix, ex);
        }
        try {
            ArrayList<RegexUriPattern> results = new ArrayList<RegexUriPattern>();
            while (rs.next()) {
                String prefix = rs.getString(PREFIX_COLUMN_NAME);
                String postfix = rs.getString(POSTFIX_COLUMN_NAME);
                String regex = rs.getString(REGEX_COLUMN_NAME);
                String sysCode = rs.getString(DATASOURCE_COLUMN_NAME);
                results.add(RegexUriPattern.factory(prefix, postfix, sysCode));
            }
            if (results.isEmpty()){
                //Nothimg found so block it from considering no filter
                results.add(null);
            }
            return results;
        } catch (SQLException ex) {
            throw new BridgeDBException("Error getting prefixr using. " + query, ex);
        } finally {
            close(statement, rs);
        }
    }

    //***************
    
    private Set<DirectMapping> getDirectMappings(IdSysCodePair sourceRef, PreparedStatement statement, String lensId) 
            throws BridgeDBException {
//        if (sourceRef == null){
//            return new HashSet<DirectMapping>();
//        }
        ResultSet rs = null;
        try {
            Set<DirectMapping> results = new HashSet<DirectMapping>();
            statement.setString(1, sourceRef.getId());
            statement.setString(2, sourceRef.getSysCode());
            rs = statement.executeQuery();
            while (rs.next()) {
                String id = rs.getString(TARGET_ID_COLUMN_NAME);
                String sysCode = rs.getString(TARGET_DATASOURCE_COLUMN_NAME);
                IdSysCodePair targetRef = new IdSysCodePair(id, sysCode);
                //stem.out.println(" = " + targetRef);
                Integer mappingSetId = rs.getInt(MAPPING_SET_ID_COLUMN_NAME);
                Integer symmetric = rs.getInt(SYMMETRIC_COLUMN_NAME);
                String predicate = rs.getString(PREDICATE_COLUMN_NAME);
                String justification = rs.getString(JUSTIFICATION_COLUMN_NAME);
                String mappingResource = rs.getString(MAPPING_RESOURCE_COLUMN_NAME);
                String mappingSource = rs.getString(MAPPING_SOURCE_COLUMN_NAME);
                DirectMapping mapping = new DirectMapping(sourceRef, targetRef, mappingSetId, symmetric, predicate, 
                        justification, null, mappingSource, lensId);
                results.add(mapping);
            }
            //ystem.out.println(results);
            return results;
        } catch (SQLException ex) {
            close(statement, rs);
            throw new BridgeDBException("Error running query " + statement, ex);
        } finally {
            close(null, rs);
        }
    }

    private static final String DIRECT_MAPPING_QUERY
            = "SELECT "
            + TARGET_ID_COLUMN_NAME + ", "
            + TARGET_DATASOURCE_COLUMN_NAME + ", "
            + MAPPING_SET_ID_COLUMN_NAME + ", "
            + PREDICATE_COLUMN_NAME + ", "
            + JUSTIFICATION_COLUMN_NAME + ", "
            + MAPPING_RESOURCE_COLUMN_NAME + ", "
            + MAPPING_SOURCE_COLUMN_NAME + ", "
            + SYMMETRIC_COLUMN_NAME
            + " FROM " + MAPPING_TABLE_NAME + ", " + MAPPING_SET_TABLE_NAME
            + " WHERE " + MAPPING_SET_ID_COLUMN_NAME + " = " + MAPPING_SET_DOT_ID_COLUMN_NAME
            + " AND " + SOURCE_ID_COLUMN_NAME + " = ? "
            + " AND " + SOURCE_DATASOURCE_COLUMN_NAME + " = ?";

    private String lensClause(String lensId) throws BridgeDBException {
        StringBuilder query = new StringBuilder();
        if (lensId == null) {
            lensId = Lens.DEFAULT_LENS_NAME;
        }
        if (!LensTools.isAllLens(lensId)) {
            List<String> justifications = LensTools.getJustificationsbyId(lensId);
            if (justifications.isEmpty()) {
                throw new BridgeDBException("No  justifications found for Lens " + lensId);
            }
            query.append(" AND ");
            query.append(JUSTIFICATION_COLUMN_NAME);
            query.append(" IN (");
            for (int i = 0; i < justifications.size() - 1; i++) {
                query.append("'").append(justifications.get(i)).append("', ");
            }
            query.append("'").append(justifications.get(justifications.size() - 1)).append("')");
        }
        return query.toString();
    }

    private String directQuery(String lensId) throws BridgeDBException {
        String result = directMappingQueries.get(lensId);
        if (result == null) {
            result = DIRECT_MAPPING_QUERY + lensClause(lensId);
            directMappingQueries.put(lensId, result);
        }
        return result;
    }

    public Set<DirectMapping> getDirectMappings(IdSysCodePair sourceRef, String lensId) throws BridgeDBException {
        PreparedStatement statement = null;
        try {
            statement = createPreparedStatement(directQuery(lensId));
            Set<DirectMapping> results = getDirectMappings(sourceRef, statement, lensId);
            return results;
        } catch (BridgeDBException ex) {
            throw ex;
        } finally {
            close(statement, null);
        }
    }

    public Set<ClaimedMapping> getTransitiveMappings(IdSysCodePair sourceRef, String lensId) throws BridgeDBException {
        PreparedStatement statement = null;
        try {
            if (lensId == null || lensId.isEmpty()){
                lensId = Lens.DEFAULT_LENS_NAME;
            }
            Lens lens = LensTools.byId(lensId);
            statement = createPreparedStatement(directQuery(lensId));
            MappingsHandlers mappingsHandler = new MappingsHandlers(sourceRef, predicateMaker, justificationMaker);
            Set<DirectMapping> direct = getDirectMappings(sourceRef, statement, lensId);
            mappingsHandler.addMappings(direct);
            while (mappingsHandler.moreToCheck()) {
                ClaimedMapping toCheck = mappingsHandler.nextToCheck();
                if (lens.getAllowedMiddleSysCodes().contains(toCheck.getTargetSysCode())){
                    Set<DirectMapping> transitives = getDirectMappings(toCheck.getTargetPair(), statement, lensId);
                    mappingsHandler.addMappings(toCheck, transitives);
                }
            }
            return mappingsHandler.getMappings();
        } catch (BridgeDBException ex) {
            throw ex;
        } finally {
            close(statement, null);
        }
    }

    /*
     * These methods filter thee results to based on the targets System code.
     * Where applicable a mapping to self is added
     *     Either because the applies filter allows this
     *     Or if no filter is applied. (null or empty filter)
     *          A filter with a null is consider a filter
    */
 
    private Set<ClaimedMapping> filterBySysCodes (Set<ClaimedMapping> mappings, 
            IdSysCodePair sourceRef, Set<String> allowedCodes){
        Set<ClaimedMapping> results = new HashSet<ClaimedMapping>();
        for (ClaimedMapping mapping:mappings){
            if (allowedCodes.contains(mapping.getTargetSysCode())){
                results.add(mapping);
            }
        }
        if (allowedCodes.contains(sourceRef.getSysCode())){
            results.add(new SelfMapping(sourceRef));
        }
        return results;
    }
    
    private Set<ClaimedMapping> filterByUriPatterns (Set<ClaimedMapping> mappings, 
            IdSysCodePair sourceRef, Collection<RegexUriPattern> targetUriPatterns){
        if (targetUriPatterns == null || targetUriPatterns.isEmpty()){
            mappings.add(new SelfMapping(sourceRef));
            return mappings;
        }
        Set<String> allowedCodes = new HashSet<String>();
        for (RegexUriPattern targetUriPattern : targetUriPatterns) {
            if (targetUriPattern != null){
                allowedCodes.add(targetUriPattern.getSysCode());
            }
        }        
        return filterBySysCodes (mappings, sourceRef, allowedCodes);
    }
    
    private Set<ClaimedMapping> filterByDataSource (Set<ClaimedMapping> mappings, 
            IdSysCodePair sourceRef, Collection<DataSource> targetDataSources){
        if (targetDataSources == null || targetDataSources.isEmpty()){
            Set<ClaimedMapping> results = new HashSet<ClaimedMapping>(mappings);
            results.add(new SelfMapping(sourceRef));
            return results;
        }
        Set<String> allowedCodes = new HashSet<String>();
        for (DataSource targetDataSource:targetDataSources) {
            if (targetDataSource != null){
                allowedCodes.add(targetDataSource.getSystemCode());
            }
        }        
        return filterBySysCodes (mappings, sourceRef, allowedCodes);
    }
    
    /*
     * These methods do the conversion or addition of uris at the same time as Filtering.
     * This is because only a single TargetUri is normally added rather than all the possible Uris
     */

    private Set<String> filterAndExtractTargetUris(Set<ClaimedMapping> mappings, IdSysCodePair sourceRef, Set<RegexUriPattern> targetUriPatterns) throws BridgeDBException {
        if (targetUriPatterns == null || targetUriPatterns.isEmpty()){
            mappings.add(new SelfMapping(sourceRef));
            return convertToTargetUris(mappings);
        }
        HashSet<String> results = new HashSet<String>();
        for (RegexUriPattern targetUriPattern : targetUriPatterns) {
            if (targetUriPattern != null){
                for (ClaimedMapping mapping : mappings) {
                    if (mapping.getTargetSysCode().equals(targetUriPattern.getSysCode())) {
                        results.add(targetUriPattern.getUri(mapping.getTargetId()));
                    }
                }
                if (targetUriPattern.getSysCode().equals(sourceRef.getSysCode())) {
                    results.add(targetUriPattern.getUri(sourceRef.getId()));
                }
            }
        }
        return results;
    }
    
    /**
     * 
     * @param transitiveMappings
     * @param sourceUri may be null
     * @param sourceRef
     * @param targetUriPatterns
     * @return
     * @throws BridgeDBException 
     */
    private Set<ClaimedMapping> filterAndAddUris(Set<ClaimedMapping> mappings, String sourceUri, IdSysCodePair sourceRef, 
            Set<RegexUriPattern> targetUriPatterns) throws BridgeDBException {
        if (targetUriPatterns == null || targetUriPatterns.isEmpty()){
            mappings.add(new SelfMapping(sourceUri, sourceRef));
            this.addSourceUri(mappings, sourceUri);
            this.addTargetURIs(mappings);
            return mappings;
        }
        HashSet<ClaimedMapping> results = new HashSet<ClaimedMapping>();
        for (RegexUriPattern targetUriPattern : targetUriPatterns) {
            if (targetUriPattern != null){
                for (ClaimedMapping mapping : mappings) {
                    if (mapping.getTargetSysCode().equals(targetUriPattern.getSysCode())) {
                        mapping.addSourceUri(sourceUri);
                        mapping.addTargetUri(targetUriPattern.getUri(mapping.getTargetId()));
                        results.add(mapping);
                    }
                }
                if (targetUriPattern.getSysCode().equals(sourceRef.getSysCode())) {
                    SelfMapping mapping = new SelfMapping(sourceUri, sourceRef);
                    mapping.addTargetUri(targetUriPattern.getUri(sourceRef.getId()));
                    results.add(mapping);
                }
            }
        }
        return results;
    }

    private Set<Xref> convertToXref(Set<ClaimedMapping> mappings) throws BridgeDBException {
        HashSet<Xref> results = new HashSet<Xref>();
        for (ClaimedMapping mapping : mappings) {
            results.add(codeMapper.toXref(mapping.getTargetPair()));
        }    
        return results;
    }

    private Set<String> convertToTargetUris(Set<ClaimedMapping> mappings) throws BridgeDBException {
        HashSet<String> results = new HashSet<String>();
        for (ClaimedMapping mapping : mappings) {
            results.addAll(toUris(mapping.getTargetPair()));
        }
        return results;
    }
                                
    /*
     * Methods in this section add extra information as required.
     * Only adding Xref or URI information as required has two advantages
     * 1. It keeps the results returned cleaner
     * 2. There is a speed advantage of not generating unrequired data
     */

    private void addXrefs(Set<ClaimedMapping> mappings) throws BridgeDBException {
        for (ClaimedMapping mapping : mappings) {
            mapping.setSource(codeMapper.toXref(mapping.getSourcePair()));
            mapping.setTargetXrefs(codeMapper);
        }    
    }

    private void addSourceURIs(ClaimedMapping mapping) throws BridgeDBException {
        Set<String> URIs = toUris(mapping.getSourcePair());
        mapping.addSourceUris(URIs);
    }

    private void addSourceUri(Set<ClaimedMapping> mappings, String sourceUri) throws BridgeDBException {
        for (Mapping mapping : mappings) {
            mapping.addSourceUri(sourceUri);
        }
    }

    private void addSourceUris(Set<ClaimedMapping> mappings) throws BridgeDBException {
        for (ClaimedMapping mapping : mappings) {
            addSourceURIs(mapping);
        }
    }

    private void addTargetURIs(ClaimedMapping mapping) throws BridgeDBException {
        Set<String> URIs = toUris(mapping.getTargetPair());
        mapping.addTargetUris(URIs);
    }

    private void addTargetURIs(Set<ClaimedMapping> mappings) throws BridgeDBException {
        for (ClaimedMapping mapping : mappings) {
            SQLUriMapper.this.addTargetURIs(mapping);
        }
    }
 
    /*
    Methods in this section take the original input and call the correct mapper type
        Scrub the input if required
        Convert the input to IdSysCodePair
        If conversion to IdSysCodePair not possible
            check mapping to self/ return empty
        ElseIf no graph and no patterns provided
            map without filtering
        Else
            convert to RegexUriPattern
            map with filtering
    */
    
    @Override
    public Set<String> mapUri(String sourceUri, String lensUri, String graph, Collection<String> tgtUriPatterns)
            throws BridgeDBException {
        sourceUri = scrubUri(sourceUri);
        IdSysCodePair sourceRef = toIdSysCodePair(sourceUri);
        if (sourceRef == null) {
            return mapUnkownUri(sourceUri, graph, tgtUriPatterns);
        }
        Set<RegexUriPattern> targetUriPatterns = findRegexPatternsWithNulls(graph, tgtUriPatterns);
        Set<ClaimedMapping> mappings = getTransitiveMappings(sourceRef, lensUri);
        return filterAndExtractTargetUris(mappings, sourceRef, targetUriPatterns);
    }

    @Override
    /**
     * @deprecated use mapFull (in fact that is not used under the hood
     */
    public MappingsBySysCodeId mapUriBySysCodeId(String sourceUri, String lensUri, String graph, Collection<String> tgtUriPatterns)
            throws BridgeDBException {
        Set<ClaimedMapping> mappings = mapFullInner(sourceUri, lensUri, false, graph, tgtUriPatterns);
        return toMappingsBySetCodeId(mappings);
    }

    @Override
    public Set<String> mapUri(Xref sourceXref, String lensId, String graph, Collection<String> tgtUriPatterns)
            throws BridgeDBException {
        IdSysCodePair sourceRef = toIdSysCodePair(sourceXref);
        if (sourceRef == null) {
            return new HashSet<String>();
        }
        Set<RegexUriPattern> targetUriPatterns = findRegexPatternsWithNulls(graph, tgtUriPatterns);
        Set<ClaimedMapping> mappings = getTransitiveMappings(sourceRef, lensId);
        return this.filterAndExtractTargetUris(mappings, sourceRef, targetUriPatterns);
    }

    @Override
    public Set<Xref> mapID(Xref sourceXref, String lensId, Collection<DataSource> tgtDataSources) throws BridgeDBException {
        IdSysCodePair sourceRef = toIdSysCodePair(sourceXref);
        if (sourceRef == null) {
            return new HashSet<Xref>();
        }
        Set<ClaimedMapping> mappings = getTransitiveMappings(sourceRef, lensId);
        Set<ClaimedMapping> filteredMappings = filterByDataSource(mappings, sourceRef, tgtDataSources);
        return this.convertToXref(filteredMappings);
    }

    private Set<Mapping> toSuperSet(Set<? extends Mapping> mappings){
        return new HashSet<Mapping>(mappings);
    }
    
    @Override
    public Set<Mapping> mapFull(String sourceUri, String lensId, 
            Collection<DataSource> tgtDataSources) throws BridgeDBException {
        IdSysCodePair sourceRef = toIdSysCodePair(sourceUri);
        if (sourceRef == null) {
            return mappingUnkownUri(sourceUri, null, null);
        }
        Set<ClaimedMapping> mappings = getTransitiveMappings(sourceRef, lensId);
        Set<ClaimedMapping> filteredMappings = filterByDataSource(mappings, sourceRef, tgtDataSources);
        this.addSourceUri(filteredMappings, sourceUri);
        this.addTargetURIs(filteredMappings);
        this.addXrefs(filteredMappings);
        return toSuperSet(filteredMappings);
    }
 
    @Override
    public Set<Mapping> mapFull(Xref sourceXref, String lensId, 
            Boolean includeUriResults, //Boolean allRoutes, boolean showVias, 
            Collection<DataSource> tgtDataSources)
            throws BridgeDBException {
        IdSysCodePair sourceRef = toIdSysCodePair(sourceXref);
        if (sourceRef == null) {
            return new HashSet<Mapping>();
        }
        Set<ClaimedMapping> mappings = getTransitiveMappings(sourceRef, lensId);
        Set<ClaimedMapping> filteredMappings = filterByDataSource(mappings, sourceRef, tgtDataSources);
        this.addXrefs(filteredMappings);
        if (includeUriResults != null && includeUriResults){
            this.addSourceUris(filteredMappings);
            this.addTargetURIs(filteredMappings);
        }
        return toSuperSet(filteredMappings);
    }

    @Override
    public Set<Mapping> mapFull(Xref sourceXref, String lensId, 
            String graph, Collection<String> tgtUriPatterns)
            throws BridgeDBException {
        IdSysCodePair sourceRef = toIdSysCodePair(sourceXref);
        if (sourceRef == null) {
            return new HashSet<Mapping>();
        }
        Set<ClaimedMapping> mappings = getTransitiveMappings(sourceRef, lensId);
        Set<RegexUriPattern> targetUriPatterns = findRegexPatternsWithNulls(graph, tgtUriPatterns);
        Set<ClaimedMapping> filteredMappings = filterAndAddUris(mappings, null, sourceRef, targetUriPatterns);
        addXrefs(filteredMappings);
        return toSuperSet(filteredMappings);
    }

    private Set<ClaimedMapping> mapFullClaimed(Collection<String> sourceUris, String lensId, 
            Boolean includeXrefResults, //Boolean allRoutes, Boolean showVias, 
            String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        Set<ClaimedMapping> results = new HashSet<ClaimedMapping>();
        for (String sourceUri:sourceUris){
            results.addAll(mapFullInner(sourceUri, lensId, includeXrefResults,  
                    graph, tgtUriPatterns));
        }
        return results;
    }
    
    private Set<Mapping> mapFull(Collection<String> sourceUris, String lensId, 
            Boolean includeXrefResults, //Boolean allRoutes, Boolean showVias, 
            String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        Set<Mapping> results = new HashSet<Mapping>();
        for (String sourceUri:sourceUris){
            results.addAll(mapFull(sourceUri, lensId, includeXrefResults,  
                    graph, tgtUriPatterns));
        }
        return results;
    }
    
    private Set<ClaimedMapping> mapFullInner(String sourceUri, String lensId, 
            Boolean includeXrefResults, //Boolean allRoutes, Boolean showVias, 
            String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        IdSysCodePair sourceRef = toIdSysCodePair(sourceUri);
        if (sourceRef == null) {
            return new HashSet<ClaimedMapping>();
        }
        Set<RegexUriPattern> targetUriPatterns = findRegexPatternsWithNulls(graph, tgtUriPatterns);
        Set<ClaimedMapping> mappings = getTransitiveMappings(sourceRef, lensId);
        Set<ClaimedMapping> filteredMappings = filterAndAddUris(mappings, sourceUri, sourceRef, targetUriPatterns);
        if (includeXrefResults != null && includeXrefResults){
            addXrefs(filteredMappings);
        }
        return filteredMappings;
    }

    @Override
    public Set<Mapping> mapFull(String sourceUri, String lensId, 
            Boolean includeXrefResults, //Boolean allRoutes, Boolean showVias, 
            String graph, Collection<String> tgtUriPatterns) throws BridgeDBException {
        IdSysCodePair sourceRef = toIdSysCodePair(sourceUri);
        if (sourceRef == null) {
            return mappingUnkownUri(sourceUri, graph, tgtUriPatterns);
        }
        return toSuperSet(mapFullInner(sourceUri, lensId, includeXrefResults, graph, tgtUriPatterns));
    }

    /*
    These methods combone the results of various sources. 
    Only make sense where the output includes a reference to the original source
        If the collection of sources is empty 
            Return empty results
        ElseIF there is exactly 1 source
            Call the method with a single source
            Return result
        Else
            Set results null
            For each result
                get new result using method with a single source
                Use this result or merge with a pervious omme
    */
    
    @Override
    /**
     * @deprecated use mapFull (in fact that is not used under the hood.
     */
    public MappingsBySysCodeId mapUriBySysCodeId(Collection<String> sourceUris, String lensUri, String graph, Collection<String> tgtUriPatterns)
            throws BridgeDBException {
        Set<ClaimedMapping> mappings = mapFullClaimed(sourceUris, lensUri, false, graph, tgtUriPatterns);
        return toMappingsBySetCodeId(mappings);
    }

    @Override
    /**
     * @deprecated use mapFull (in fact that is not used under the hood.
     */
    public MappingsBySet mapBySet(Collection<String> sourceUris, String lensUri, String graph, Collection<String> tgtUriPatterns)
            throws BridgeDBException {
        Set<Mapping> mappings = mapFull(sourceUris, lensUri, false, graph, tgtUriPatterns);
        return new MappingsBySet(lensUri, mappings);
    }
    
    //Make sure default BridgeDb methods gets all mappings 
    @Override
    public Set<Xref> mapID(Xref sourceXref, DataSource... tgtDataSources) throws BridgeDBException {
        Set<DataSource> targetDataSources = new HashSet<DataSource>();
        if (tgtDataSources != null){
            for (DataSource tgtDataSource:tgtDataSources){
                targetDataSources.add(tgtDataSource);
            }
        }
        return mapID(sourceXref, Lens.ALL_LENS_NAME, targetDataSources);
    }

    protected int getSymmetric(int mappingSetId) throws BridgeDBException{
        String query = "SELECT "
            + SYMMETRIC_COLUMN_NAME
            + " FROM " + MAPPING_SET_TABLE_NAME
            + " WHERE " + ID_COLUMN_NAME + " =  ?";
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            statement = createPreparedStatement(query);
            statement.setInt(1, mappingSetId);
            rs = rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(SYMMETRIC_COLUMN_NAME);
            } else {
                return 0;
            }
       } catch (BridgeDBException ex) {
            throw ex;
        } catch (SQLException ex) {
            throw new BridgeDBException("Unable to run " + query, ex);
        } finally {
            close(statement, rs);
        }
    }

    public static MappingsBySysCodeId toMappingsBySetCodeId(Collection<ClaimedMapping> mappings){
        Map<String,Map<String, Set<String>>> allMappings = new HashMap<String,Map<String, Set<String>>>();
        if (mappings != null){
            for (ClaimedMapping mapping:mappings){
                Map<String, Set<String>> byCode = allMappings.get(mapping.getTargetSysCode());
                if (byCode == null){
                    byCode = new HashMap<String, Set<String>>();
                }
                Set<String> byId = byCode.get(mapping.getTargetId());
                if (byId == null){
                    byId = new HashSet<String>();
                }
                byId.addAll(mapping.getTargetUri());
                byCode.put(mapping.getTargetId(), byId);
                allMappings.put(mapping.getTargetSysCode(), byCode);
            }
        }
        return new MappingsBySysCodeId(allMappings);
    }


           
}


