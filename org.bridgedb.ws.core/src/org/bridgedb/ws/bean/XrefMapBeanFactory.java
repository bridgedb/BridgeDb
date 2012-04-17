package org.bridgedb.ws.bean;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.bridgedb.IDMapperException;
import org.bridgedb.Xref;
import org.bridgedb.provenance.XrefProvenance;

public class XrefMapBeanFactory {
    public static XrefMapBean asBean(XrefBean source, List<XrefProvenanceBean> target){
        XrefMapBean bean = new XrefMapBean();
        bean.source = source;
        bean.target = target;
        return bean;
    }

    public static XrefMapBean asBean(Xref source, Set<XrefProvenance> tgtXrefs){
        XrefMapBean bean = new XrefMapBean();
        bean.source = XrefBeanFactory.asBean(source);
        bean.target = new ArrayList<XrefProvenanceBean>();
        for (XrefProvenance tgt:tgtXrefs){
           bean.target.add(XrefProvenanceBeanFactory.asBean(tgt));
        }
        return bean;
    }

    public static XrefMapBean asBean(Xref source, XrefProvenance tgtXref){
        XrefMapBean bean = new XrefMapBean();
        bean.source = XrefBeanFactory.asBean(source);
        bean.target = new ArrayList<XrefProvenanceBean>();
        bean.target.add(XrefProvenanceBeanFactory.asBean(tgtXref));
        return bean;
    }

    public static Xref getKey(XrefMapBean bean){
        return XrefBeanFactory.asXref(bean.source);
    }
    
    public static Set<Xref> getXrefMappedSet(XrefMapBean bean) throws IDMapperException {
        HashSet<Xref> results = new HashSet<Xref>();
        for (XrefProvenanceBean trg:bean.target){
            results.add(XrefProvenanceBeanFactory.asXref(trg));
        }
        return results;
    }
    
    public static Set<XrefProvenance> getXrefProvenanceMappedSet(XrefMapBean bean) throws IDMapperException {
        HashSet<XrefProvenance> results = new HashSet<XrefProvenance>();
        for (XrefProvenanceBean trg:bean.target){
            results.add(XrefProvenanceBeanFactory.asXrefProvenance(trg));
        }
        return results;
    }
}
