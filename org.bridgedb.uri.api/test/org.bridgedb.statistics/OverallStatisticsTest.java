/*
 *BridgeDb,
 *An abstraction layer for identifier mapping services, both local and online.
 *Copyright (c) 2019, Manas Awasthi <marvex17@gmail.com>
 *
 *Licensed under the Apache License, Version 2.0 (the "License");
 *you may not use this file except in compliance with the License.
 *You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 *Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 *WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *See the License for the specific language governing permissions and limitations under the License.
 */

package org.bridgedb.statistics;

import static org.junit.jupiter.api.Assertions.*;

class OverallStatisticsTest {

    @org.junit.jupiter.api.Test
    public void getNumberOfMappings() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        assertEquals(1, overallStatistics.getNumberOfMappings());
        assertNotEquals(0, overallStatistics.getNumberOfMappings());
    }

    @org.junit.jupiter.api.Test
    public void getNumberOfMappingSets() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        assertEquals(1, overallStatistics.getNumberOfMappingSets());
        assertNotEquals(0, overallStatistics.getNumberOfMappingSets());
    }

    @org.junit.jupiter.api.Test
    public void getNumberOfSourceDataSources() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        assertEquals(1, overallStatistics.getNumberOfSourceDataSources());
        assertNotEquals(0, overallStatistics.getNumberOfTargetDataSources());
    }

    @org.junit.jupiter.api.Test
    public void getNumberOfTargetDataSources() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        assertEquals(1, overallStatistics.getNumberOfTargetDataSources());
        assertNotEquals(0, overallStatistics.getNumberOfTargetDataSources());
    }

    @org.junit.jupiter.api.Test
    public void getNumberOfPredicates() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        assertEquals(1, overallStatistics.getNumberOfPredicates());
        assertNotEquals(0, overallStatistics.getNumberOfPredicates());
    }

    @org.junit.jupiter.api.Test
    public void getNumberOfLenses() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        assertEquals(1, overallStatistics.getNumberOfLenses());
        assertNotEquals(0, overallStatistics.getNumberOfLenses());
    }

    @org.junit.jupiter.api.Test
    public void setNumberOfMappings() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        overallStatistics.setNumberOfMappings(2);
        assertEquals(2, overallStatistics.getNumberOfMappings());
        assertNotEquals(1, overallStatistics.getNumberOfMappings());
    }

    @org.junit.jupiter.api.Test
    public void setNumberOfMappingSets() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        overallStatistics.setNumberOfMappingSets(2);
        assertEquals(2, overallStatistics.getNumberOfMappingSets());
        assertNotEquals(1, overallStatistics.getNumberOfMappingSets());
    }

    @org.junit.jupiter.api.Test
    public void setNumberOfSourceDataSources() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        overallStatistics.setNumberOfSourceDataSources(2);
        assertEquals(2, overallStatistics.getNumberOfSourceDataSources());
        assertNotEquals(1, overallStatistics.getNumberOfSourceDataSources());
    }

    @org.junit.jupiter.api.Test
    public void setNumberOfPredicates() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        overallStatistics.setNumberOfPredicates(2);
        assertEquals(2, overallStatistics.getNumberOfPredicates());
        assertNotEquals(1, overallStatistics.getNumberOfPredicates());
    }

    @org.junit.jupiter.api.Test
    public void setNumberOfTargetDataSources() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        overallStatistics.setNumberOfTargetDataSources(2);
        assertEquals(2, overallStatistics.getNumberOfTargetDataSources());
        assertNotEquals(1, overallStatistics.getNumberOfTargetDataSources());
    }

    @org.junit.jupiter.api.Test
    public void toString1() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        StringBuilder builder = new StringBuilder();
        builder.append("numberOfMappings: ");
        builder.append(overallStatistics.getNumberOfMappings());
        builder.append("\n");
        builder.append("numberOfMappingSets: ");
        builder.append(overallStatistics.getNumberOfMappingSets());
        builder.append("\n");
        builder.append("numberOfSourceDataSources: ");
        builder.append(overallStatistics.getNumberOfSourceDataSources());
        builder.append("\n");
        builder.append("numberOfPredicates: ");
        builder.append(overallStatistics.getNumberOfPredicates());
        builder.append("\n");
        builder.append("numberOfTargetDataSources: ");
        builder.append(overallStatistics.getNumberOfTargetDataSources());
        builder.append("\n");
        builder.append("numberOfLenses: ");
        builder.append(overallStatistics.getNumberOfLenses());
        builder.append("\n");
        String stringbuilder= builder.toString();
        assertEquals(stringbuilder, overallStatistics.toString());
    }

    @org.junit.jupiter.api.Test
    public void setNumberOfLenses() {
        OverallStatistics overallStatistics = new OverallStatistics(1,1,1,1,1,1);
        overallStatistics.setNumberOfLenses(2);
        assertEquals(2, overallStatistics.getNumberOfLenses());
        assertNotEquals(1, overallStatistics.getNumberOfLenses());
    }
}
