// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
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
package org.bridgedb.ws.bean;

import org.bridgedb.Xref;
import org.bridgedb.url.URLMapping;

/**
 *
 * @author Christian
 */
public class URLMappingBeanFactory {
    
    public static URLMapping asURLMapping(URLMappingBean bean){
        Xref source = XrefBeanFactory.asXref(bean.getSource());
        Xref target = XrefBeanFactory.asXref(bean.getTarget());
        return new URLMapping (bean.getId(), bean.getSourceURL(),source, bean.getPredicate(), 
                bean.getTargetURL(), target, bean.getMappingSetId());      
    }

    public static URLMappingBean asBean(URLMapping urlMapping) {
        XrefBean source = XrefBeanFactory.asBean(urlMapping.getSource());
        XrefBean target = XrefBeanFactory.asBean(urlMapping.getTarget());
        return new URLMappingBean (urlMapping.getId(), urlMapping.getSourceURLs(), source, urlMapping.getPredicate(), 
                urlMapping.getTargetURLs(), target, urlMapping.getMappingSetId());              
    }
}
