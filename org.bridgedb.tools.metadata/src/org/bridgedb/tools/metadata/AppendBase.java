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
package org.bridgedb.tools.metadata;

import org.bridgedb.utils.BridgeDBException;

/**
 *
 * @author Christian
 */
public abstract class AppendBase implements MetaData{
    
    public static final String CLEAR_REPORT = "No issues found";
    static final boolean CHECK_ALL_PRESENT = true;
    
    @Override
    public synchronized String toString(){
         StringBuilder builder = new StringBuilder();
         appendShowAll(builder, 0);
         return builder.toString();
    }
    
    String showAll() {
         StringBuilder builder = new StringBuilder();
         appendShowAll(builder, 0);
         return builder.toString();
    }

    @Override
    public String Schema(){
        StringBuilder builder = new StringBuilder();
        appendSchema(builder, 0);
        return builder.toString();
    }

    abstract void appendSchema(StringBuilder builder, int tabLevel);
    
    abstract void appendShowAll(StringBuilder builder, int tabLevel);
    
    @Override
    public String validityReport(boolean includeWarnings) throws BridgeDBException {
         StringBuilder builder = new StringBuilder();
         appendValidityReport(builder, CHECK_ALL_PRESENT, includeWarnings, 0);
         return builder.toString();
    }
    
    public void validate() throws BridgeDBException {
        String report = this.validityReport(false);
        if (report.contains("ERROR")){
            throw new BridgeDBException(report);
        }
    }

   abstract void appendValidityReport(StringBuilder builder, boolean checkAllpresent, boolean includeWarnings, 
           int tabLevel) throws BridgeDBException;

    @Override
    public String unusedStatements(){
        StringBuilder builder = new StringBuilder();
        appendUnusedStatements(builder);
        return builder.toString();        
    }
    
    abstract void appendUnusedStatements(StringBuilder builder);

    static public void tab(StringBuilder builder, int tab){
        for (int i = 0; i < tab; i++){
            builder.append("\t");
        }
    }
    
    static public void newLine(StringBuilder builder){
        builder.append("\n");
    }

    static public void newLine(StringBuilder builder, int tab){
        builder.append("\n");
        for (int i = 0; i < tab; i++){
            builder.append("\t");
        }        
    }

}
