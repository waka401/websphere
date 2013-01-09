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
package net.wait4it.graphite.wasagent.core;

import java.util.List;
import java.util.Map;

/**
 * This class manages the execution of the
 * performance tests, and builds the required
 * Graphite schemes using data supplied by the
 * user.
 * 
 * @author Yann Lambret
 *
 */
public class WASStatsManager {

    // Plugin overall output
    private StringBuilder output = new StringBuilder();

    // Single test output (for a specific PMI interface)
    private List<String> metrics = null;

    // Optional strings to build the metric scheme
    private String prefix = "";
    private String suffix = "";

    /**
     * Instantiates a WebSphere proxy, and run all
     * the required tests based on the params contents.
     * 
     * @param  params HTTP request params
     * @return the Graphite metrics as plain old text
     */
    public String process(Map<String, String> params) {
        try {
            // Proxy to the target WAS instance
            WASClientProxy proxy = new WASClientProxy(params);
            proxy.init();

            // We get the information we need to build the Graphite scheme
            String serverName = proxy.getServerName(); // WAS instance name
            String hostName = params.get("hostname");  // Target host name

            if (params.get("prefix") != null) {
                prefix = params.get("prefix") + ".";
            }

            if (params.get("suffix") != null) {
                suffix = params.get("suffix") + ".";
            }

            long now = System.currentTimeMillis() / 1000L;

            for (Option option : Option.values()) {
                if (params.containsKey(option.getName())) {
                    metrics = option.getTest().run(proxy, params.get(option.getName()));
                    for (String metric : metrics) {
                        output.append(prefix)
                        .append(hostName).append(".")
                        .append(suffix)
                        .append(serverName).append(".")
                        .append(metric).append(" ")
                        .append(now).append(System.getProperty("line.separator"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();
    }

}
