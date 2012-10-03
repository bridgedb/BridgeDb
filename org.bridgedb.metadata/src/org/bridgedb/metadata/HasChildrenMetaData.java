/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.metadata;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.w3c.dom.Element;

/**
 * 
 * @author Christian
 */
public abstract class HasChildrenMetaData  extends MetaDataBase implements MetaData{
    
    final List<MetaDataBase> childMetaData;

    //HasChildrenMetaData(Element element) throws MetaDataException{
    //    super(element);
    //    childMetaData = MetaDataRegistry.getChildMetaData(element);
    //}

    HasChildrenMetaData(String name, List<MetaDataBase> childMetaData){
        super(name);
        this.childMetaData = childMetaData;
    }
    
    HasChildrenMetaData(HasChildrenMetaData other){
        super(other);
        childMetaData = new ArrayList<MetaDataBase>();
        for (MetaDataBase child:other.childMetaData){
            childMetaData.add(child.getSchemaClone());
        }

    }
    
    // ** MetaData methods 
    
    @Override
    public Set<Value> getValuesByPredicate(URI predicate) {
        for (MetaDataBase child:childMetaData){
            Set<Value> possible = child.getValuesByPredicate(predicate);
            if (possible != null){
                return possible;
            }
        }
        return null;
    }
    
    @Override
    public boolean hasCorrectTypes() {
        for (MetaDataBase child:childMetaData){
            if (!child.hasCorrectTypes()){
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean hasRequiredValues() {
        for (MetaDataBase child:childMetaData){
            if (!child.hasRequiredValues()){
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean allStatementsUsed() {
        //A group has values only if all children do.
        for (MetaDataBase child:childMetaData){
            if (!child.allStatementsUsed()) { 
                return false; 
            }
        }
        return true;
    }

    public Set<Statement> getRDF(){
        HashSet results = new HashSet<Statement>();
        for (MetaDataBase child:childMetaData){
            results.addAll(child.getRDF());
        }
        return results;
     }
    
    // ** MetaDataBase methods 
    @Override
    void loadValues(Resource id, Set<Statement> data, MetaData parent, MetaDataCollection collection) {
        setupValues(id, parent);
        for (MetaDataBase child:childMetaData){
            child.loadValues(id, data, this, collection);
        }
    }

    @Override
    boolean hasValues() {
        for (MetaDataBase child:childMetaData){
            if (!child.hasValues()) { 
                return false; 
            }
        }
        return true;
    }
    
    Set<LeafMetaData> getLeaves() {
        Set<LeafMetaData> leaves = new HashSet<LeafMetaData>();
        for (MetaDataBase child:childMetaData){
            leaves.addAll(child.getLeaves());
        }
        return leaves;
    }

    LeafMetaData getLeafByPredicate(URI predicate) {
        for (MetaDataBase child:childMetaData){
            LeafMetaData leaf = child.getLeafByPredicate(predicate);
            if (leaf != null){
                return leaf;
            }
        }
        return null;
    }
    
    // ** AppendBase methods 
    
    @Override
    void appendSchema(StringBuilder builder, int tabLevel) {
        appendSpecific(builder, tabLevel);
        for (MetaDataBase child:childMetaData){
            child.appendSchema(builder, tabLevel + 1);
        }
    }

   @Override
    void appendShowAll(StringBuilder builder, int tabLevel) {
        appendSpecific(builder, tabLevel);
        for (MetaDataBase child:childMetaData){
            child.appendShowAll(builder, tabLevel + 1);
        }
    }

    @Override
    void appendUnusedStatements(StringBuilder builder) {
        for (MetaDataBase child:childMetaData){
            child.appendUnusedStatements(builder);
        }
    }

    // ** Abstract methods used above
    abstract void appendSpecific(StringBuilder builder, int tabLevel);
    
    /* Methods with no shared implementation 
    @Override
    MetaDataBase getSchemaClone() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    void appendValidityReport(StringBuilder builder, RequirementLevel forceLevel, boolean includeWarnings, int tabLevel) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    */

}
