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

import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSCountStatistic;
import com.ibm.websphere.pmi.stat.WSRangeStatistic;
import com.ibm.websphere.pmi.stat.WSStats;
import com.ibm.websphere.pmi.stat.WSTimeStatistic;

/**
 * Convenient methods to access PMI individual stats.
 * 
 * @author Yann Lambret
 *
 */
public abstract class TestUtils {

    public static WSBoundedRangeStatistic getBoundedRangeStats(WSStats stats, int stat) {
        return (WSBoundedRangeStatistic)stats.getStatistic(stat);
    }

    public static WSCountStatistic getCountStats(WSStats stats, int stat) {
        return (WSCountStatistic)stats.getStatistic(stat);
    }

    public static WSRangeStatistic getRangeStats(WSStats stats, int stat) {
        return (WSRangeStatistic)stats.getStatistic(stat);
    }

    public static WSTimeStatistic getTimeStats(WSStats stats, int stat) {
        return (WSTimeStatistic)stats.getStatistic(stat);
    }

    public static long format(long value) {
        return (value/1024);
    }

    /**
     * Removes undesirable characters ('/' and '.')
     * from the WebSphere resource names used in
     * the Graphite schemes.
     * 
     * @param  name
     * @return a cleaned up name
     */
    public static String normalize(String name) {
        return name.replaceAll("[\\./]", "_");
    }

}
