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
import java.util.List;

import com.ibm.websphere.pmi.stat.WSJVMStats;
import com.ibm.websphere.pmi.stat.WSStats;

import net.wait4it.graphite.wasagent.core.WASClientProxy;

/**
 * Gets statistics for the the target
 * WAS instance JVM.
 * 
 * The following metrics are available:
 * 
 *   - The JVM current heap size (MB)
 *   - The JVM maximum heap size (MB)
 *   - The current amount of memory used by the JVM (MB)
 *   - The amount of CPU resources used by the JVM (%)
 * 
 * @author Yann Lambret
 *
 */
public class JVMTest extends TestUtils implements Test {

    /**
     * WebSphere JVM stats.
     * 
     * @param proxy   an applicative proxy for the target WAS instance
     * @param params  null for this test
     * @return output a list of strings for collected data
     */
    public List<String> run(WASClientProxy proxy, String params) {
        List<String> output = new ArrayList<String>();
        long cpuUsage;
        long currentHeapSize;
        long currentHeapUsed;
        long maximumHeapSize;

        try {
            WSStats stats = proxy.getStats(WSJVMStats.NAME);
            cpuUsage = getCountStats(stats, WSJVMStats.cpuUsage).getCount();
            currentHeapSize = format(getBoundedRangeStats(stats, WSJVMStats.HeapSize).getCurrent());
            currentHeapUsed = format(getCountStats(stats, WSJVMStats.UsedMemory).getCount());
            maximumHeapSize = format(getBoundedRangeStats(stats, WSJVMStats.HeapSize).getUpperBound());
            output.add("jvm.cpuUsage " + cpuUsage);
            output.add("jvm.currentHeapSize " + currentHeapSize);
            output.add("jvm.currentHeapUsed " + currentHeapUsed);
            output.add("jvm.maximumHeapSize " + maximumHeapSize);
        } catch (Exception ignored) {
            // stats object may be null, or PMI settings may be wrong.
            // Anyway, we don't want to pollute the output.
        }

        return output;
    }

}
