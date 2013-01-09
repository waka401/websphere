/**
 * This file is part of Wasagent.
 *
 * Wasagent is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Wasagent is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Wasagent. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package net.wait4it.graphite.wasagent.tests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ibm.websphere.pmi.stat.WSJDBCConnectionPoolStats;
import com.ibm.websphere.pmi.stat.WSStats;

import net.wait4it.graphite.wasagent.core.WASClientProxy;

/**
 * Gets statistics for JDBC datasources.
 * 
 * The following metrics are available:
 * 
 *   - The datasource current pool size
 *   - The datasource maximum pool size
 *   - The active connection count
 *   - The number of threads waiting for
 *     a connection from the pool
 * 
 * @author Yann Lambret
 *
 */
public class JDBCTest extends TestUtils implements Test {

    // No statistics for WAS internal datasources
    private static final List<String> EXCLUSIONS = new ArrayList<String>();

    static {
        EXCLUSIONS.add("jdbc/DefaultEJBTimerDataSource");
    }

    /**
     * WebSphere JDBC datasources stats.
     * 
     * @param proxy   an applicative proxy for the target WAS instance
     * @param params  a comma separated list of datasource names, or
     *                a wildcard character (*) for all datasources
     * @return output a list of strings for collected data
     */
    public List<String> run(WASClientProxy proxy, String params) {
        List<String> datasources = Arrays.asList(params.split(","));
        List<String> output = new ArrayList<String>();
        String name;
        long maximumPoolSize;
        long currentPoolSize;
        long freePoolSize;
        long waitingThreadCount;
        long activeCount;

        try {
            WSStats stats = proxy.getStats(WSJDBCConnectionPoolStats.NAME);
            if (stats != null) {
                WSStats[] substats1 = stats.getSubStats(); // JDBC Provider level
                for (WSStats substat1 : substats1) {
                    WSStats[] substats2 = substat1.getSubStats(); // DataSource level
                    for (WSStats substat2 : substats2) {
                        if (EXCLUSIONS.contains(substat2.getName())) {
                            continue;
                        }
                        if (datasources.contains("*") || datasources.contains(substat2.getName())) {
                            maximumPoolSize = getBoundedRangeStats(substat2, WSJDBCConnectionPoolStats.PoolSize).getUpperBound();
                            currentPoolSize = getBoundedRangeStats(substat2, WSJDBCConnectionPoolStats.PoolSize).getCurrent();
                            freePoolSize = getBoundedRangeStats(substat2, WSJDBCConnectionPoolStats.FreePoolSize).getCurrent();
                            waitingThreadCount = getRangeStats(substat2, WSJDBCConnectionPoolStats.WaitingThreadCount).getCurrent();
                            activeCount = currentPoolSize - freePoolSize;
                            name = normalize(substat2.getName());
                            output.add("jdbc." + name + ".activeCount " + activeCount);
                            output.add("jdbc." + name + ".currentPoolSize " + currentPoolSize);
                            output.add("jdbc." + name + ".maximumPoolSize " + maximumPoolSize);
                            output.add("jdbc." + name + ".waitingThreadCount " + waitingThreadCount);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // PMI settings may be wrong.
            // Anyway, we don't want to pollute the output.
        }

        Collections.sort(output);
        return output;
    }

}
