/* Copyright (c)  2023  Egon Willighagen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */
package org.bridgedb.bio;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.bridgedb.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UniProtKBTest {

	@BeforeAll
	public static void setUpSources() {
		if (DataSource.getDataSources().size() < 20) DataSourceTxt.init();
	}

	@Test
    public void getBySystemCode() {
        DataSource ds1 = DataSource.getExistingBySystemCode("S");
        assertNotNull(ds1);
        assertEquals("S", ds1.getSystemCode());
        assertEquals("UniProtKB", ds1.getFullName());
    }

	@Test
    public void getByFullName_TrEMBL() {
        DataSource ds1 = DataSource.getExistingByFullName("Uniprot-TrEMBL");
        assertNotNull(ds1);
        assertEquals("S", ds1.getSystemCode());
        assertEquals("UniProtKB", ds1.getFullName());
	}

	@Test
    public void getByFullName_SwissProt() {
        DataSource ds1 = DataSource.getExistingByFullName("Uniprot-SwissProt");
        assertNotNull(ds1);
        assertEquals("S", ds1.getSystemCode());
        assertEquals("UniProtKB", ds1.getFullName());
    }

	@Test
    public void getByFullName_UniProtKB() {
        DataSource ds1 = DataSource.getExistingByFullName("UniProtKB");
        assertNotNull(ds1);
        assertEquals("S", ds1.getSystemCode());
        assertEquals("UniProtKB", ds1.getFullName());
    }
}
