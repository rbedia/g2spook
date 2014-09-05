/**
 * Copyright 2014 Rafael Bedia
 *
 * This file is part of g2spook.
 *
 * g2spook is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * g2spook is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * g2spook. If not, see <http://www.gnu.org/licenses/>.
 */
package org.doxu.g2.gwc.crawler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.Callable;
import org.doxu.g2.gwc.crawler.model.Host;
import org.doxu.g2.gwc.crawler.model.HostStatus;

public class HostThread implements Callable<HostStatus> {

    private static final int TIMEOUT = 250;

    private final Host host;

    private final String ip;

    private final int port;

    public HostThread(Host host) {
        this.host = host;
        String[] parts = host.getAddress().split(":");
        ip = parts[0];
        port = Integer.parseInt(parts[1]);
    }

    @Override
    public HostStatus call() throws Exception {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(ip, port), TIMEOUT);
            return new HostStatus(host, true);
        } catch (IOException ex) {
            return new HostStatus(host, false);
        }
    }
}
