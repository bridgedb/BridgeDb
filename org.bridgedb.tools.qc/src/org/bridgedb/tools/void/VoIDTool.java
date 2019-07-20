package org.bridgedb.tools.qc;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.bridgedb.bio.DataSourceTxt;
import org.bridgedb.rdb.SimpleGdb;
import org.bridgedb.rdb.SimpleGdbFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class VoIDTool {
    private PrintStream out;
    private final File oldDb;

    private SimpleGdb oldGdb;
    public FileWriter file ;

    String fileName = "rdf.txt";
    public VoIDTool(File f1) throws IDMapperException, IOException {
        this(f1, System.out);
        file = new FileWriter("rdf.txt", true);
    }
    public VoIDTool(File f1, OutputStream out) throws IOException {
        oldDb= f1;
        this.out = new PrintStream(out);
    }
    public void appendStrToFile(String fileName,
                                       String str)
    {
        try {
            // Open given file in append mode.
            BufferedWriter out = new BufferedWriter(
                    new FileWriter(fileName, true));
            out.write(str);
            out.close();
        }
        catch (IOException e) {
            System.out.println("exception occoured" + e);
        }
    }

    public void initDatabases() throws IDMapperException{
        String url = "jdbc:derby:jar:(" + oldDb+ ")database";
        oldGdb = SimpleGdbFactory.createInstance("db1", url);
        }
        
    public void run() throws IDMapperException, IOException {
        appendStrToFile(fileName,"@prefix void:  <http://rdfs.org/ns/void#> .\n");
        appendStrToFile(fileName,"@prefix pav:   <http://purl.org/pav/> .\n");
        appendStrToFile(fileName,"@prefix freq:  <http://purl.org/cld/freq/> .\n");
        appendStrToFile(fileName,"@prefix biopax: <http://www.biopax.org/release/biopax-level3.owl#> .\n");
        appendStrToFile(fileName,"@prefix skos:  <http://www.w3.org/2004/02/skos/core#> .\n");
        appendStrToFile(fileName,"@prefix rdfs:  <http://www.w3.org/2000/01/rdf-schema#> .\n");
        appendStrToFile(fileName,"@prefix ncbigene: <http://identifiers.org/ncbigene/> .\n");
        appendStrToFile(fileName,"@prefix pubmed: <http://www.ncbi.nlm.nih.gov/pubmed/> .\n");
        appendStrToFile(fileName,"@prefix hmdb:  <http://identifiers.org/hmdb/> .\n");
        appendStrToFile(fileName,"@prefix rdf:   <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n");
        appendStrToFile(fileName,"@prefix gpml:  <http://vocabularies.wikipathways.org/gpml#> .\n");
        appendStrToFile(fileName,"@prefix wp:    <http://vocabularies.wikipathways.org/wp#> .\n");
        appendStrToFile(fileName,"@prefix dcterms: <http://purl.org/dc/terms/> .\n");
        appendStrToFile(fileName,"@prefix dcat:  <http://www.w3.org/ns/dcat#> .\n");
        appendStrToFile(fileName,"@prefix prov:  <http://www.w3.org/ns/prov#> .\n");
        appendStrToFile(fileName,"@prefix foaf:  <http://xmlns.com/foaf/0.1/> .\n");
        appendStrToFile(fileName,"\n");
        initDatabases();
        for (DataSource ds : oldGdb.getCapabilities().getSupportedSrcDataSources()) {
            this.out.println(ds.getFullName());
            createVoid(ds.getFullName());
        }
    }
    public void createVoid(String db1)throws IOException{
        db1 = db1.toLowerCase();
        switch(db1) {
            case "gmpl":
                appendStrToFile(fileName,"<http://rdf.wikipathways.org/20190713/rdf/gpml> \n");
                appendStrToFile(fileName,"        a                 dcat:Distribution ;\n");
                appendStrToFile(fileName,"void:dataDump     <https://jenkins.bigcat.unimaas.nl/job/GPML%20to%20GPML%20+%20WP%20RDF/ws/WP2RDF/output/gpml/*zip*/gpml.zip> ;\n");
                appendStrToFile(fileName,"dcat:downloadURL  <https://jenkins.bigcat.unimaas.nl/job/GPML%20to%20GPML%20+%20WP%20RDF/ws/WP2RDF/output/gpml/*zip*/gpml.zip> ;\n");
                appendStrToFile(fileName,"\n");
                break;
            case "wikipathways":
                appendStrToFile(fileName,"http://rdf.wikipathways.org/20190713/datasetDescription/");
                appendStrToFile(fileName,"        a                           void:Dataset ;\n");
                appendStrToFile(fileName," void:DatasetDescription ;\n");
                appendStrToFile(fileName," dcterms:description  \"This is the VoID description for this WikiPathways RDF dataset created on 20190713.\"@en \n");
                appendStrToFile(fileName," dcterms:issued \"2019-07-13T12:48:33.25Z\"^^xsd:dateTime ;\n");
                appendStrToFile(fileName," dcterms:modified \"2019-07-13T12:48:33.25Z\"^^xsd:dateTime ;\n");
                appendStrToFile(fileName," dcterms:title \"WikiPathways RDF VoID Description\"@en ;\n");
                appendStrToFile(fileName," foaf:primaryTopic    <http://rdf.wikipathways.org/20190713/rdf/> .\n");
                appendStrToFile(fileName," <http://rdf.wikipathways.org/20190713/linkset/wikidata>. \n");
                appendStrToFile(fileName,"        a                    void:Linkset ;");
                appendStrToFile(fileName," dcterms:title        \"WPRDF to Wikidata Linkset\"; \n");
                appendStrToFile(fileName," void:linkPredicate   wp:bdbWikidata ;\n");
                appendStrToFile(fileName," void:objectsTarget   <https://www.wikidata.org/entity/Q2013> ; \n");
                appendStrToFile(fileName," void:subjectsTarget  <http://rdf.wikipathways.org/20190713/rdf/>. \n");
                appendStrToFile(fileName,"\n");
                break;
            case "hmdb":
                appendStrToFile(fileName,"void:DatasetDescription ; \n");
                appendStrToFile(fileName,"dcterms:title \"The Human Metabolome Database;\" \n");
                appendStrToFile(fileName,"dcterms:description \"The Human Metabolome Database (HMDB) is a database containing detailed information about small molecule metabolites found in the human body.It contains or links 1) chemical 2) clinical and 3) molecular biology/biochemistry data.\" \n");
                appendStrToFile(fileName,"foaf:homepage <http://www.hmdb.ca> \n");
                appendStrToFile(fileName,"dcterms:publisher \"University of Alberta\" \n");
                appendStrToFile(fileName,"dcterms:license <http://www.hmdb.ca/about#cite> \n");
                appendStrToFile(fileName,"\n");
                break;
            case "chebi":
                appendStrToFile(fileName,"void:DatasetDescription ; \n");
                appendStrToFile(fileName,"dcterms:title \"Chemical Entities of Biological Interest (ChEBI)\"@en ; \n");
                appendStrToFile(fileName,"dcterms:description \"Chemical Entities of Biological Interest (ChEBI) is a freely available dictionary of molecular entities focused on &#39;small&#39; chemical compounds.\"@en ; \n");
                appendStrToFile(fileName,"foaf:homepage <http://www.ebi.ac.uk/chebi/> ; \n");
                appendStrToFile(fileName,"dcterms:publisher <http://www.ebi.ac.uk> ;");
                appendStrToFile(fileName,"void:dataDump <ftp://ftp.ebi.ac.uk/pub/databases/chebi/ontology/chebi.owl> ; \n");
                appendStrToFile(fileName,"dcterms:license <http://creativecommons.org/licenses/by-sa/3.0/> ; \n");
                appendStrToFile(fileName,"\n");
                break;
            case "chembl compound":
                appendStrToFile(fileName,"void:DatasetDescription ; \n");
                appendStrToFile(fileName,"dcterms:title \"The ChEMBL Database\" ;\n");
                appendStrToFile(fileName,"dcterms:description \"ChEMBL is a database of bioactive drug-like small molecules, it contains 2-D structures, calculated properties (e.g. logP, Molecular Weight, Lipinski Parameters, etc.) and abstracted bioactivities (e.g. binding constants, pharmacology and ADMET data). The data is abstracted and curated from the primary scientific literature, and cover a significant fraction of the SAR and discovery of modern drugs.\"\n");
                appendStrToFile(fileName,"foaf:page <ftp://ftp.ebi.ac.uk/pub/databases/chembl/> ;\n");
                appendStrToFile(fileName,"dcterms:publisher <http://www.ebi.ac.uk> ;");
                appendStrToFile(fileName,"dcterms:license <http://creativecommons.org/licenses/by-sa/3.0/> ;\n");
                appendStrToFile(fileName,"void:dataDump <ftp://ftp.ebi.ac.uk/pub/databases/chembl/ChEMBL-RDF/20.0/chembl_20.0_targetrel.ttl.gz> .\n");
                appendStrToFile(fileName,"\n");
                break;
            case "ensembl":
                appendStrToFile(fileName,"void:DatasetDescription ; \n");
                appendStrToFile(fileName,"dcterms:description \"Ensembl is a joint project between EMBL - EBI and the Wellcome Trust Sanger Institute to develop a software system which produces and maintains automatic annotation on selected eukaryotic genomes.\"@en ;\n");
                appendStrToFile(fileName,"dcterms:license <http://www.ebi.ac.uk/Information/termsofuse.html> ;");
                appendStrToFile(fileName,"dcterms:publisher <http://www.ebi.ac.uk> ;\n");
                appendStrToFile(fileName,"dcterms:title \"Ensembl RDF\"@en ;\n");
                appendStrToFile(fileName,"foaf:page <ftp://ftp.ebi.ac.uk/pub/databases/ensembl/> ;\n");
                appendStrToFile(fileName,"void:dataDump <ftp://ftp.ebi.ac.uk/pub/databases/RDF/ensembl/83> ;\n");
                appendStrToFile(fileName,"\n");
                break;
        }
    }
    public static void printUsage(){
        System.out.println ("Expected 1 argument: <database>");
    }
    public static void main(String[] args) throws IOException, IDMapperException, SQLException {
        if (args.length != 1) {
            printUsage(); return;
        }
        VoIDTool main = new VoIDTool(new File(args[0]));
        DataSourceTxt.init();
        main.run();
    }
}
