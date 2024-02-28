/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2012 Egon Willighagen <egonw@users.sf.net>
 *Copyright (c) 2012 OpenPhacts
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */

package org.bridgedb;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/**
 * Tests the {@link org.bridgedb.DataSourcePatterns} class
 * @author youphendriks
 */
public class DataSourcePatternsTest {

  protected static DataSource key;

  @Test
  @DisplayName("Test getDataSourceMatches class, valid input")
  public void testgetDataSourceMatchesValid() {
    assertNotNull(DataSourcePatterns.getDataSourceMatches("F"));
  }

  @Test
  @DisplayName("Test getDataSourceMatches class, invalid input")
  public void testgetDataSourceMatchesInvalid() {
    Assertions.assertThrows(IllegalArgumentException.class,
      () -> {
        DataSourcePatterns.getDataSourceMatches("");
      });
  }

}
