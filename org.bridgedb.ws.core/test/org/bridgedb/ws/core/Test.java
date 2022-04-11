package org.bridgedb.ws.core;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bridgedb.BridgeDb;
import org.bridgedb.DataSource;
import org.bridgedb.IDMapper;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.ws.bean.CapabilitiesBean;
import org.bridgedb.ws.bean.DataSourceBean;
import org.bridgedb.ws.bean.DataSourceMapBean;
import org.bridgedb.ws.bean.FreeSearchSupportedBean;
import org.bridgedb.ws.bean.PropertyBean;
import org.bridgedb.ws.bean.XrefBean;
import org.bridgedb.ws.bean.XrefMapBean;
import org.bridgedb.ws.bean.XrefsBean;

import org.junit.Before;

public class Test {

	@Before
	public void setUp() throws Exception {
	}
	
	@org.junit.jupiter.api.Test
	public void testCapabilitiesBean() throws ClassNotFoundException, IDMapperException {
		Class.forName("org.bridgedb.file.IDMapperText");

		String fileName = "yeast_id_mapping";
		String fullName = fileName + ".txt";
		ClassLoader classLoader = this.getClass().getClassLoader();
		URL url = classLoader.getResource(fullName);
		IDMapper m = BridgeDb.connect("idmapper-text:" + url);

		CapabilitiesBean cb = new CapabilitiesBean(m.getCapabilities());
		CapabilitiesBean cbNull = new CapabilitiesBean();

		DataSource sourceEnsembl = DataSource.register("dsEnsembl", "Ensembl").asDataSource();
		DataSource sourceUniprot = DataSource.register("S", "Uniprot-TrEMBL")
				.urlPattern("http://www.uniprot.org/uniprot/$id")
				.idExample("S")
				.urnBase("urn:miriam:uniprot")
				.mainUrl("http://www.uniprot.org/")
			    .asDataSource();
		DataSource sourceEG = DataSource.register("dsEntrez Gene", "Entrez Gene").asDataSource();
		DataSource sourceDrugBank = DataSource.register("Dr", "DrugBank").asDataSource();

		assertFalse(cb.isFreeSearchSupported());
		assertNull(cbNull.getSourceDataSource());
		assertNull(cbNull.getTargetDataSource());

		assertTrue(cb.getSupportedSrcDataSources().toString().contains("Ensembl") && cb.getSupportedTgtDataSources().toString().contains("Ensembl"));
		assertTrue(cb.getSupportedSrcDataSources().toString().contains("EMBL") && cb.getSupportedTgtDataSources().toString().contains("EMBL"));
		assertTrue(cb.getSupportedSrcDataSources().toString().contains("Entrez Gene") && cb.getSupportedTgtDataSources().toString().contains("Entrez Gene"));
		assertTrue(cb.getSupportedSrcDataSources().toString().contains("UniProt-TrEMBL") && cb.getSupportedTgtDataSources().toString().contains("UniProt-TrEMBL"));

		assertTrue(cb.isMappingSupported(sourceEnsembl, sourceEG));
		assertFalse(cb.isMappingSupported(sourceEnsembl, sourceDrugBank));

		// Create a PropertyBean object and test the PropertyBean functions
		List<PropertyBean> property;
		property = new ArrayList<PropertyBean>();
		PropertyBean propertybean;
		PropertyBean sourceDbProperty;
		propertybean = new PropertyBean();
		sourceDbProperty = new PropertyBean();
		assertTrue(propertybean.isEmpty());
		propertybean = new PropertyBean("Species", "SaccharomycesCerevisiae");
		assertFalse(propertybean.isEmpty());
		sourceDbProperty.setKey("SourceDatabase");
		sourceDbProperty.setValue("yeast_id_mapping.txt");
		property.add(propertybean);
		property.add(sourceDbProperty);
		cb.setProperty(property);
		assertEquals("SaccharomycesCerevisiae", cb.getProperty("Species"));
		assertNull(cb.getProperty("Version"));
		HashSet<String> keys = new HashSet<String>();
		keys.add("Species");
		keys.add("SourceDatabase");

		assertEquals(keys,cb.getKeys());
		System.out.println(cb.getKeys());

		cb.setIsFreeSearchSupported(true);

		// Create DataSourceBean object
		DataSourceBean dsBean = new DataSourceBean(sourceUniprot);
		dsBean.setType("protein");
		DataSourceBean testdsBean = new DataSourceBean();
		DataSourceBean testdsBean2 = new DataSourceBean();

		testdsBean = DataSourceBean.asBean("dsEnsembl");
		testdsBean2 = DataSourceBean.asBean("Ensembl");
		testdsBean2.setSysCode(null);
		testdsBean2.setFullName("Ensembl");
		testdsBean2.setUrlPattern("http://www.uniprot.org/uniprot/$id");
		testdsBean2.setIdExample("En");
		assertFalse(testdsBean2.isIsPrimary());
		testdsBean2.setIsPrimary(true);
		assertTrue(testdsBean2.isIsPrimary());
		testdsBean2.setUrnBase("urn:miriam:ensembl");
		testdsBean2.setMainUrl("http://www.ensembl.org/");
		assertEquals("urn:miriam:ensembl", testdsBean2.getUrnBase());
		assertEquals("http://www.ensembl.org/", testdsBean2.getMainUrl());

		assertNotNull(testdsBean);

		assertNotEquals(sourceEG, DataSourceBean.asDataSource(dsBean));
		sourceEG = DataSourceBean.asDataSource(dsBean);
		assertEquals(sourceEG, DataSourceBean.asDataSource(dsBean));
		assertNull(DataSourceBean.asDataSource(null));

		dsBean.asDataSource();

		List<DataSourceBean> sourceDataSource;
		sourceDataSource = new ArrayList<DataSourceBean>();
		sourceDataSource.add(testdsBean);
		cb.setSourceDataSource(sourceDataSource);
		cb.setTargetDataSource(sourceDataSource);
		assertEquals(cb.getSourceDataSource(),sourceDataSource);
		assertEquals(cb.getTargetDataSource(),sourceDataSource);

		assertEquals("class org.bridgedb.ws.bean.DataSourceBean",dsBean.getClass().toString());
		assertEquals("S",dsBean.getIdExample());
		assertEquals("S",dsBean.getSysCode());
		assertEquals("Uniprot-TrEMBL",dsBean.getFullName());
		assertEquals("http://www.uniprot.org/",dsBean.getMainUrl());
		assertEquals("protein",dsBean.getType().toString());
		assertEquals("http://www.uniprot.org/uniprot/$id", dsBean.getUrlPattern());
		assertEquals("urn:miriam:uniprot", dsBean.getUrnBase());
		assertEquals("S:Uniprot-TrEMBL", dsBean.toString());
		assertEquals("sysCode = dsEnsembl", testdsBean.toString());
		assertEquals("FullName = Ensembl", testdsBean2.toString());

		List<DataSourceMapBean> dsMapBeanList = new ArrayList<DataSourceMapBean>();
		List<DataSourceMapBean> supportedMapping = new ArrayList<DataSourceMapBean>();
		DataSourceMapBean dsMapBean = new DataSourceMapBean();
		DataSourceMapBean dsMapBean2 = new DataSourceMapBean();
		dsMapBeanList = cb.getSupportedMapping();
		assertNotNull(cb.getSupportedMapping());
		dsMapBean = dsMapBeanList.get(0);
		dsMapBean2 = dsMapBeanList.get(1);
		supportedMapping.add(dsMapBeanList.get(0));
		cb.setSupportedMapping(supportedMapping);
		assertNotNull(cb.getSupportedMapping());
		dsMapBean.getSource();
		dsMapBean.getTarget();
		dsMapBean.getTarget();
		dsMapBean.setSource(testdsBean2);
		dsMapBean.setTarget(sourceDataSource);
		DataSourceMapBean.getMappedSet(dsMapBean2);
		assertNotNull(DataSourceMapBean.getMappedSet(dsMapBean2));

		dsMapBean2.setSource(null);
		DataSource dsFromDsMapBean = DataSourceMapBean.AsDataSource(dsMapBean2);
		assertNull(dsFromDsMapBean);
	}
	
	@org.junit.jupiter.api.Test
	public void testFreeSearchSupportedBean() {
		FreeSearchSupportedBean fssBean = new FreeSearchSupportedBean();
		assertNotNull(fssBean);
		FreeSearchSupportedBean fssBeanTrue = new FreeSearchSupportedBean(true);
		assertTrue(fssBeanTrue.getIsFreeSearchSupported());
		assertTrue(fssBeanTrue.isFreeSearchSupported());
		fssBeanTrue.setIsFreeSearchSupported(false);
		assertFalse(fssBeanTrue.isFreeSearchSupported());
	}

	@org.junit.jupiter.api.Test
	public void testXrefMapBean() {
		XrefBean xrefBean = new XrefBean();
		XrefMapBean xrefMapBean = new XrefMapBean();
		assertNotNull(xrefMapBean);
		DataSource sourceEG = DataSource.register("dsEntrez Gene", "Entrez Gene").asDataSource();
		DataSource sourceDrugBank = DataSource.register("Dr", "DrugBank").asDataSource();
		System.out.println(sourceDrugBank.getFullName());
		DataSourceBean sourceDrugBankAsBean = new DataSourceBean(sourceDrugBank);
		sourceDrugBankAsBean.setFullName("DrugBank");

		Xref source = new Xref("id1000", sourceEG);
		Xref target = new Xref("id2000", sourceDrugBank);
		XrefBean xrefBeanSource = XrefBean.asBean(source);
		XrefBean xrefBeanTarget = XrefBean.asBean(target);
		XrefMapBean xrefMapBean2 = XrefMapBean.asBean(source, target);
		assertNotNull(xrefMapBean2);
		xrefMapBean2.setSource(xrefBeanSource);
		xrefMapBean2.setTarget(xrefBeanTarget);

		assertEquals(xrefBeanSource, xrefMapBean2.getSource());
		assertEquals(xrefBeanTarget, xrefMapBean2.getTarget());
		assertEquals("id1000:dsEntrez Gene:Entrez Gene -> id2000:Dr:DrugBank",xrefMapBean2.toString());

		XrefBean xrefBeanTest =  new XrefBean("id2000", "Dr");
		assertNotNull(xrefBeanTest);
		xrefBeanTest.setDataSource(sourceDrugBankAsBean);
		XrefBean xrefBeanNull = XrefBean.asBean(null);
		assertNull(xrefBeanNull);

		assertNotNull(xrefBeanTest.getId());
		assertFalse(xrefBeanTest.isEmpty());
		xrefBeanTest.setId(null);
		assertTrue(xrefBeanTest.isEmpty());

		assertNotNull(xrefBeanTest.getDataSource());

		Xref test = xrefBeanTest.asXref();
		assertNotNull(test);

		XrefsBean xrefsBean = new XrefsBean();
		Set<Xref> xrefsList = new HashSet<Xref>();
		Set<XrefBean> xrefsBeanList = new HashSet<XrefBean>(); 
		xrefsList.add(source);
		xrefsList.add(target);
		xrefsList.add(null);
		XrefsBean xrefsBean2 = new XrefsBean(xrefsList);
		assertNotNull(xrefsBean2);
		Set<Xref> xrefsList2 = xrefsBean2.asXrefs();
		assertNotNull(xrefsList2);
		assertNotNull(xrefsBean2.getXref());

		xrefsBean2.setXref(xrefsBeanList);
		System.out.println(xrefsBeanList);
	}
}
