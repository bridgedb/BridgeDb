/*
 * Loads example data for the BridgeDb URI web service: the mappings of one
 * Wikidata item, taken from a BridgeDb Derby database (.bridge file).
 *
 * Run it with: start-uri-server.sh --load <file.bridge> <Wikidata ID>
 *
 * WARNING: this drops and recreates all tables in the configured MySQL or
 * MariaDB database (SqlDatabase in BridgeDb.properties or local.properties).
 */
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.Xref;
import org.bridgedb.rdb.SimpleGdbFactory;
import org.bridgedb.rdf.UriPattern;
import org.bridgedb.sql.SQLUriMapper;
import org.bridgedb.uri.tools.RegexUriPattern;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;

public class LoadUriExample {

    static final String EXACT_MATCH = "http://www.w3.org/2004/02/skos/core#exactMatch";
    // "database cross-reference", one of the justifications of the Default lens
    static final String JUSTIFICATION = "http://semanticscience.org/resource/SIO_001171";
    static final String WIKIDATA = "http://www.wikidata.org/entity/";

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: LoadUriExample <file.bridge> <Wikidata ID, like Q47512>");
            System.exit(1);
        }
        File bridgeFile = new File(args[0]).getAbsoluteFile();
        String wikidataId = args[1];

        // creates empty tables, and loads the data sources and their URI patterns
        SQLUriMapper uriMapper = SQLUriMapper.createNew();

        IDMapper derby = SimpleGdbFactory.createInstance("example",
            "jdbc:derby:jar:(" + bridgeFile.getPath() + ")database");
        Xref item = new Xref(wikidataId, DataSource.getExistingBySystemCode("Wd"));
        Set<Xref> mapped = derby.mapID(item);

        String sourceUri = WIKIDATA + wikidataId;
        RegexUriPattern sourcePattern = uriMapper.toUriPattern(sourceUri);
        Resource mappingSource = SimpleValueFactory.getInstance().createIRI(bridgeFile.toURI().toString());

        Map<String, Integer> mappingSets = new HashMap<>();
        List<String> skipped = new ArrayList<>();
        int loaded = 0;
        for (Xref xref : mapped) {
            if (xref.equals(item)) continue;
            String targetUri = toUri(uriMapper, xref);
            if (targetUri == null) {
                skipped.add(xref.toString());
                continue;
            }
            RegexUriPattern targetPattern = uriMapper.toUriPattern(targetUri);
            String key = targetPattern.getPrefix() + "$id" + targetPattern.getPostfix();
            Integer mappingSet = mappingSets.get(key);
            if (mappingSet == null) {
                mappingSet = uriMapper.registerMappingSet(sourcePattern, EXACT_MATCH, JUSTIFICATION,
                    targetPattern, mappingSource, true);
                mappingSets.put(key, mappingSet);
            }
            uriMapper.insertUriMapping(sourceUri, targetUri, mappingSet, true);
            System.out.println(sourceUri + " -> " + targetUri);
            loaded++;
        }
        uriMapper.closeInput();
        System.out.println("Loaded " + loaded + " mappings for " + sourceUri + " from " + bridgeFile);
        if (!skipped.isEmpty()) System.out.println("Skipped, as no URI pattern accepts the identifier: " + skipped);
    }

    /**
     * The first URI for the identifier that the URI mapper accepts, trying the
     * identifiers.org patterns first and then the others, in alphabetical order.
     */
    static String toUri(SQLUriMapper uriMapper, Xref xref) throws Exception {
        List<String> patterns = new ArrayList<>();
        for (UriPattern pattern : UriPattern.byCode(xref.getDataSource().getSystemCode())) {
            patterns.add(pattern.getUriPattern());
        }
        patterns.sort((a, b) -> {
            boolean aId = a.startsWith("http://identifiers.org/");
            boolean bId = b.startsWith("http://identifiers.org/");
            return aId != bId ? (aId ? -1 : 1) : a.compareTo(b);
        });
        for (String pattern : patterns) {
            String uri = pattern.replace("$id", xref.getId());
            if (uriMapper.toUriPattern(uri) != null) return uri;
        }
        return null;
    }
}
