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

import com.ibm.websphere.pmi.stat.WSJCAConnectionPoolStats;
import com.ibm.websphere.pmi.stat.WSStats;

import net.wait4it.graphite.wasagent.core.WASClientProxy;

/**
 * Gets statistics for JMS connection factories.
 * 
 * The following metrics are available:
 * 
 *   - The JMS connection factory current pool size
 *   - The JMS connection factory maximum pool size
 *   - The active connection count
 *   - The number of threads waiting for an available
 *     connection
 * 
 * @author Yann Lambret
 *
 */
public class JMSTest extends TestUtils implements Test {

    /**
     * WebSphere JMS connection factories stats.
     * 
     * @param proxy   an applicative proxy for the target WAS instance
     * @param params  a comma separated list of factory names, or
     *                a wildcard character (*) for all factories
     * @return output a list of strings for collected data
     */
    public List<String> run(WASClientProxy proxy, String params) {
        List<String> factories = Arrays.asList(params.split(","));
        List<String> output = new ArrayList<String>();
        String name;
        long maximumPoolSize;
        long currentPoolSize;
        long freePoolSize;
        long waitingThreadCount;
        long activeCount;

        try {
            WSStats stats = proxy.getStats(WSJCAConnectionPoolStats.NAME);
            if (stats != null) {
                WSStats[] substats1 = stats.getSubStats(); // JMS provider level
                for (WSStats substat1 : substats1) {
                    if (substat1.getName().equals("SIB JMS Resource Adapter") || substat1.getName().equals("WebSphere MQ JMS Provider")) {
                        WSStats[] substats2 = substat1.getSubStats(); // JCA factory level
                        for (WSStats substat2 : substats2) {
                            if (factories.contains("*") || factories.contains(substat2.getName())) {
                                maximumPoolSize = getBoundedRangeStats(substat2, WSJCAConnectionPoolStats.PoolSize).getUpperBound();
                                currentPoolSize = getBoundedRangeStats(substat2, WSJCAConnectionPoolStats.PoolSize).getCurrent();
                                freePoolSize = getBoundedRangeStats(substat2, WSJCAConnectionPoolStats.FreePoolSize).getCurrent();
                                waitingThreadCount = getRangeStats(substat2, WSJCAConnectionPoolStats.WaitingThreadCount).getCurrent();
                                activeCount = currentPoolSize - freePoolSize;
                                name = normalize(substat2.getName());
                                output.add("jms." + name + ".activeCount " + activeCount);
                                output.add("jms." + name + ".currentPoolSize " + currentPoolSize);
                                output.add("jms." + name + ".maximumPoolSize " + maximumPoolSize);
                                output.add("jms." + name + ".waitingThreadCount " + waitingThreadCount);
                            }
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
