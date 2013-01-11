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

import com.ibm.websphere.pmi.stat.WSSessionManagementStats;
import com.ibm.websphere.pmi.stat.WSStats;

import net.wait4it.graphite.wasagent.core.WASClientProxy;

/**
 * Gets the current HTTP sessions count
 * for a web application.
 * 
 * @author Yann Lambret
 *
 */
public class ApplicationTest extends TestUtils implements Test {

    // No statistics for WAS internal components
    private static final List<String> EXCLUSIONS = new ArrayList<String>();

    static {
        EXCLUSIONS.add("ibmasyncrsp#ibmasyncrsp.war");
    }

    /**
     * WebSphere applications stats.
     * 
     * @param proxy   an applicative proxy for the target WAS instance
     * @param params  a comma separated list of web application names, or
     *                a wildcard character (*) for all web applications
     * @return output a list of strings for collected data
     */
    public List<String> run(WASClientProxy proxy, String params) {
        List<String> applications = Arrays.asList(params.split(","));
        List<String> output = new ArrayList<String>();
        String name;
        long liveCount;

        try {
            WSStats stats = proxy.getStats(WSSessionManagementStats.NAME);
            if (stats != null) {
                WSStats[] substats = stats.getSubStats();
                for (WSStats substat : substats) {
                    if (EXCLUSIONS.contains(substat.getName())) {
                        continue;
                    }
                    if (applications.contains("*") || applications.contains(substat.getName())) {
                        liveCount = getRangeStats(stats, WSSessionManagementStats.LiveCount).getCurrent();
                        name = normalize(substat.getName());
                        output.add("application." + name + ".liveCount " + liveCount);
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
