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
package org.bridgedb.linkset;

import java.io.InputStream;
import org.bridgedb.tools.metadata.validator.ValidationType;
import org.bridgedb.utils.BridgeDBException;
import org.bridgedb.utils.StoreType;
import org.openrdf.rio.RDFFormat;

/**
 *
 * @author Christian
 */
public interface LinksetInterface extends LinksetInterfaceMinimal{
    @Override
    public String validateString(String source, String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws BridgeDBException;
    
    @Override
    public String validateInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType, boolean includeWarnings) throws BridgeDBException;
    
    public String validateFile(String fileName, StoreType storeType, ValidationType type, boolean includeWarnings) 
            throws BridgeDBException;
    
    @Override
    public String loadString(String source, String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws BridgeDBException;
    
    public void loadFile(String fileName, StoreType storeType, ValidationType type) throws BridgeDBException;
    
    @Override
    public String loadInputStream(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws BridgeDBException;

    @Override
    public void checkStringValid(String source, String info, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws BridgeDBException;
    
    @Override
    public void checkInputStreamValid(String source, InputStream inputStream, RDFFormat format, StoreType storeType, 
            ValidationType validationType) throws BridgeDBException;

    public void checkFileValid(String fileName, StoreType storeType, ValidationType type) throws BridgeDBException;
    
    public void clearExistingData (StoreType storeType) throws BridgeDBException;
    
}
