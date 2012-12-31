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

import com.ibm.websphere.pmi.stat.WSStats;
import com.ibm.websphere.pmi.stat.WSThreadPoolStats;

import net.wait4it.graphite.wasagent.core.WASClientProxy;

/**
 * Gets statistics for WebSphere thread pools.
 * 
 * The following metrics are available:
 * 
 *   - The thread pool current size
 *   - The thread pool maximum size
 *   - The active thread count
 * 
 * @author Yann Lambret
 *      
 */
public class ThreadPoolTest extends TestUtils implements Test {

    /**
     * WebSphere thread pools stats.
     * 
     * @param proxy   an applicative proxy for the target WAS instance
     * @param params  a comma separated list of thread pool names, or
     *                a wildcard character (*) for all thread pools
     * @return output a list of strings for collected data
     */
    public List<String> run(WASClientProxy proxy, String params) {
        List<String> pools = Arrays.asList(params.split(","));
        List<String> output = new ArrayList<String>();
        String name;
        long activeCount;
        long currentPoolSize;
        long maximumPoolSize;

        try {
            WSStats stats = proxy.getStats(WSThreadPoolStats.NAME);
            if (stats != null) {
                WSStats[] substats = stats.getSubStats();
                for (WSStats substat : substats) {
                    if (pools.contains("*") || pools.contains(substat.getName())) {
                        activeCount = getBoundedRangeStats(substat, WSThreadPoolStats.ActiveCount).getCurrent();
                        currentPoolSize = getBoundedRangeStats(substat, WSThreadPoolStats.PoolSize).getCurrent();
                        maximumPoolSize = getBoundedRangeStats(substat, WSThreadPoolStats.PoolSize).getUpperBound();
                        name = normalize(substat.getName());
                        output.add("threadPool." + name + ".activeCount " + activeCount);
                        output.add("threadPool." + name + ".currentPoolSize " + currentPoolSize);
                        output.add("threadPool." + name + ".maximumPoolSize " + maximumPoolSize);
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
