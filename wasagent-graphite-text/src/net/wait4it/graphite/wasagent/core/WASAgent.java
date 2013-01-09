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

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Instantiates an embedded Jetty container and
 * registers a servlet to handle monitoring requests.
 * 
 * @author Yann Lambret
 *
 */
public class WASAgent {

    public static void main(String[] args) throws Exception {
        // Jetty server connector is created from plugin arguments
        String host = args[0];
        int port = Integer.parseInt(args[1]);

        // Jetty server setup
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setHost(host);
        connector.setPort(port);
        // Timeout for HTTP incoming requests (milliseconds)
        connector.setMaxIdleTime(15000);
        server.setConnectors(new Connector[] { connector });

        // We add the 'WASServlet' as a unique entry point
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        handler.setContextPath("/wasagent");
        server.setHandler(handler);
        handler.addServlet(new ServletHolder(new WASServlet()), "/*");

        server.start();
        server.join();
    }

}
