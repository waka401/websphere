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

import java.util.List;

import net.wait4it.graphite.wasagent.core.WASClientProxy;

/**
 * The common interface for all tests.
 * 
 * @author Yann Lambret
 *
 */
public interface Test {

    /**
     * 
     * @param  proxy an applicative proxy for
     *         a WebSphere instance
     * @param  params specific params for the test,
     *         if needed. This string can be null
     * @return a list of Graphite metrics
     */
    List<String> run(WASClientProxy proxy, String params);

}
