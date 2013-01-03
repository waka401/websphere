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
import java.util.Set;

import javax.management.ObjectName;

import net.wait4it.graphite.wasagent.core.WASClientProxy;

/**
 * Gets the depth of queues declared on
 * the WebSphere SIB provider. Also display
 * stats for the SIB error queue (*SYSTEM.Exception.Destination*)
 * 
 * @author Yann Lambret
 *
 */
public class SIBQueueTest extends TestUtils implements Test {

    // No statistics for WAS internal components
    private static final List<String> EXCLUSIONS = new ArrayList<String>();

    static {
        EXCLUSIONS.add("^_PSIMP\\.PROXY\\.QUEUE.*");
        EXCLUSIONS.add("^_PSIMP\\.TDRECEIVER.*");
        EXCLUSIONS.add("^_PTRM.*");
    }

    /**
     * WebSphere SIB queues stats.
     * 
     * @param proxy   an applicative proxy for the target WAS instance
     * @param params  a comma separated list of SIB queue names, or
     *                a wildcard character (*) for all SIB queues
     * @return output a list of strings for collected data
     */
    public List<String> run(WASClientProxy proxy, String params) {
        List<String> queues = Arrays.asList(params.split(","));
        List<String> output = new ArrayList<String>();
        String identifier;
        String name;
        long depth;

        try {
            Set<ObjectName> mbeans = proxy.getMBeans("WebSphere:type=SIBQueuePoint,*");
            for (ObjectName mbean : mbeans) {
                identifier = (String)proxy.getAttribute(mbean, "identifier");
                if (skip(identifier))
                    continue;
                if (queues.contains("*") || queues.contains(identifier)) {
                    depth = (Long)proxy.getAttribute(mbean, "depth");
                    name = normalize(identifier);
                    output.add("SIBQueue." + name + ".depth " + depth);
                }
            }
        } catch (Exception ignored) {
            // Don't want to pollute the output.
        }

        Collections.sort(output);
        return output;
    }

    /**
     * 
     * @param  identifier the name of the SIB queue
     * @return true if the given queue belongs to
     *         the EXCLUSIONS list, false otherwise
     */
    private Boolean skip(String identifier) {
        for (String pattern : EXCLUSIONS) {
            if (identifier.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

}
