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

import java.text.DecimalFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.ibm.websphere.pmi.stat.WSStats;
import com.ibm.websphere.pmi.stat.WSWebAppStats;

import net.wait4it.graphite.wasagent.core.WASClientProxy;

/**
 * Gets servlet service method execution time (ms).
 * 
 * The metric name is of the form:
 * 
 *   app_name#web_module_name.servlet_name
 * 
 * @author Yann Lambret
 *
 */
public class ServletTest extends TestUtils implements Test {

    // No statistics for WAS internal components
    private static final List<String> EXCLUSIONS = new ArrayList<String>();

    // Servlet response time format
    private static final DecimalFormat DF = new DecimalFormat("0.00");

    static {
        EXCLUSIONS.add("rspservlet");
    }

    /**
     * WebSphere servlets stats.
     * 
     * @param proxy   an applicative proxy for the target WAS instance
     * @param params  a comma separated list of servlet names, or
     *                a wildcard character (*) for all servlets
     * @return output a list of strings for collected data
     */
    public List<String> run(WASClientProxy proxy, String params) {
        List<String> servlets = Arrays.asList(params.split(","));
        List<String> output = new ArrayList<String>();
        String archiveName;
        String servletName;
        String name;
        double serviceTime;

        try {
            WSStats stats = proxy.getStats(WSWebAppStats.NAME);
            if (stats != null) {          
                WSStats[] substats1 = stats.getSubStats(); // WEB module level
                for (WSStats substat1 : substats1) {
                    archiveName = normalize(substat1.getName());
                    WSStats[] substats2 = substat1.getSubStats(); // Servlets module level
                    for (WSStats substat2 : substats2) {
                        WSStats[] substats3 = substat2.getSubStats(); // Servlet level
                        for (WSStats substat3 : substats3) {
                            if (EXCLUSIONS.contains(substat3.getName())) {
                                continue;
                            }
                            if (servlets.contains("*") || servlets.contains(substat3.getName())) {
                                serviceTime = getTimeStats(substat3, WSWebAppStats.ServletStats.ServiceTime).getMean();
                                servletName = normalize(substat3.getName());
                                name = archiveName + "." + servletName;
                                output.add("servlet." + name + ".serviceTime " + DF.format(serviceTime));
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
