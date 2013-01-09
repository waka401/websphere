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

import net.wait4it.graphite.wasagent.tests.*;

/**
 * Details the available parameters when sending HTTP
 * queries to the agent. If one of the values matches
 * the query parameter, the corresponding test is run.
 * 
 * @author Yann Lambret
 *
 */
public enum Option {

    JVM         ( "jvm", new JVMTest() ),
    THREADPOOL  ( "thread-pool", new ThreadPoolTest() ),
    JTA         ( "jta", new JTATest() ),
    JDBC        ( "jdbc", new JDBCTest() ),
    JMS         ( "jms", new JMSTest() ),
    SIBQUEUE    ( "sib-queue", new SIBQueueTest() ),
    APPLICATION ( "application", new ApplicationTest() ),
    SERVLET     ( "servlet", new ServletTest() );

    private final String name;
    private final Test test;

    private Option(String name, Test test) {
        this.name = name;
        this.test = test;
    }

    public String getName() {
        return name;
    }

    public Test getTest() {
        return test;
    }

}
