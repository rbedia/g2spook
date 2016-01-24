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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.doxu.g2.gwc.crawler.model.ServiceRef;
import org.doxu.g2.gwc.crawler.model.Host;
import org.doxu.g2.gwc.crawler.model.HostRef;
import org.doxu.g2.gwc.crawler.model.Service;
import org.doxu.g2.gwc.crawler.model.Status;

public class CrawlThread implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(CrawlThread.class.getName());

    private final CrawlSession session;

    private final String gwcUrl;

    private final CloseableHttpClient httpClient;

    public CrawlThread(CrawlSession session, CloseableHttpClient httpClient, String gwcUrl) {
        this.session = session;
        this.httpClient = httpClient;
        this.gwcUrl = gwcUrl;
    }

    @Override
    public void run() {
        crawlGWC(gwcUrl, httpClient);
    }

    private void crawlGWC(String gwcUrl, final CloseableHttpClient httpclient) {
        URI uri = basicGet(gwcUrl);
        InetAddress address = getIPAddress(uri);
        String ip = null;
        if (address != null) {
            ip = address.getHostAddress();
        }

        Service service = session.addService(gwcUrl);
        service.setIp(ip);
        if (ip == null) {
            service.setStatus(Status.BAD_DNS);
        } else if (isAddressBlocked(address)) {
            service.setStatus(Status.BAD_IP);
        } else if (uri != null) {
            HttpGet httpget = new HttpGet(uri);
            try (CloseableHttpResponse response = httpclient.execute(httpget)) {
                if (response.getStatusLine().getStatusCode() == 200) {
                    parseResponse(service, response);
                    service.setStatus(Status.WORKING);
                } else {
                    service.setStatus(Status.HTTP_ERROR);
                }
            } catch (IOException ex) {
                service.setStatus(Status.CONNECT_ERROR);
                LOGGER.log(Level.FINE, null, ex);
            }
        }
        System.out.println("Service: " + service.getUrl());
        System.out.println("  Status: " + service.getStatus());
        System.out.println("  IP: " + service.getIp());
        System.out.println("  Hosts: " + service.getHosts().size());
        System.out.println("  URLs: " + service.getUrls().size());
        System.out.println();
    }

    private URI basicGet(String host) {
        try {
            URI uri = new URIBuilder(host)
                    .setParameter("client", "DOXU" + Crawler.VERSION)
                    .setParameter("get", "1")
                    .setParameter("ping", "1")
                    .setParameter("net", "gnutella2")
                    .build();
            return uri;
        } catch (URISyntaxException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private InetAddress getIPAddress(URI uri) {
        try {
            InetAddress address = InetAddress.getByName(uri.getHost());
            return address;
        } catch (UnknownHostException ex) {
            return null;
        }
    }

    private void parseResponse(final Service service, final HttpResponse response) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] tokens = line.split("\\|");
                if (tokens.length > 0) {
                    switch (tokens[0]) {
                        case "i":
                        case "I":
                            parseInfo(tokens, service);
                            break;
                        case "h":
                        case "H":
                            parseHost(tokens, service);
                            break;
                        case "u":
                        case "U":
                            parseUrl(tokens, service);
                            break;
                        default:
                            LOGGER.log(Level.INFO, "Unrecognized line: {0}", line);
                            break;
                    }
                }
            }
        }
    }

    private void parseInfo(String[] tokens, final Service service) {
        if (tokens.length > 2 && tokens[1].equalsIgnoreCase("pong")) {
            String server = tokens[2];
            service.setClient(server);
        }
    }

    private void parseHost(String[] tokens, final Service service) throws NumberFormatException {
        if (tokens.length > 1) {
            String address = tokens[1];
            // TODO validate address is well-formed
            int age = 0;
            if (tokens.length > 2) {
                age = convertToInt(tokens[2]);
            }
            Host host = session.addHost(address);
            HostRef hostRef = new HostRef(host, age);
            service.addHost(hostRef);
        }
    }

    private void parseUrl(String[] tokens, final Service service) throws NumberFormatException {
        if (tokens.length > 1) {
            String address = tokens[1];
            // TODO validate address is well-formed
            int age = 0;
            if (tokens.length > 2) {
                age = convertToInt(tokens[2]);
            }
            Service recvService = session.addService(address);
            ServiceRef serviceRef = new ServiceRef(recvService, age);
            service.addURL(serviceRef);
            session.addURL(address);
        }
    }

    private int convertToInt(String num) throws NumberFormatException {
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private boolean isAddressBlocked(InetAddress ip) {
        return ip.isLinkLocalAddress() || ip.isLoopbackAddress()
                || ip.isSiteLocalAddress() || ip.isMulticastAddress();
    }

}
