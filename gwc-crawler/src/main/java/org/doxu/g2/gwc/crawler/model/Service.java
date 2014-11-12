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
package org.doxu.g2.gwc.crawler.model;

import java.util.ArrayList;
import java.util.List;
import org.doxu.g2.gwc.crawler.xml.Hosts;

public class Service {

    private final String url;
    private String ip;
    private String client;
    private Status status;

    private final List<HostRef> hosts;
    private final List<ServiceRef> urls;

    public Service(String url) {
        this.url = url;
        hosts = new ArrayList<>();
        urls = new ArrayList<>();
    }

    public String getUrl() {
        return url;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public boolean isWorking() {
        return status == Status.WORKING;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<HostRef> getHosts() {
        return hosts;
    }

    public void addHost(HostRef host) {
        hosts.add(host);
    }

    public List<ServiceRef> getUrls() {
        return urls;
    }

    public void addURL(ServiceRef url) {
        urls.add(url);
    }

    public int getDeltaAgeHosts() {
        if (hosts.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (HostRef host : hosts) {
            sum += host.getAge();
        }
        return sum / hosts.size();
    }

    public int getDeltaAgeUrls() {
        if (urls.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (ServiceRef gwcUrl : urls) {
            sum += gwcUrl.getAge();
        }
        return sum / urls.size();
    }

    public org.doxu.g2.gwc.crawler.xml.Service toXML() {
        org.doxu.g2.gwc.crawler.xml.Service xmlService = new org.doxu.g2.gwc.crawler.xml.Service();
        xmlService.setUrl(url);
        xmlService.setIp(ip);
        xmlService.setClient(client);
        xmlService.setStatus(status.toString());
        xmlService.setHosts(new Hosts());
        int onlineHosts = 0;
        for (HostRef hostRef : hosts) {
            org.doxu.g2.gwc.crawler.xml.Host xmlHost = hostRef.toXML();
            xmlService.getHosts().getHost().add(xmlHost);
            if (hostRef.getHost().isOnline()) {
                onlineHosts++;
            }
        }
        int hostCount = hosts.size();
        if (hostCount > 0) {
            xmlService.getHosts().setSummary(String.format("%d/%d (%1.0f%%)", onlineHosts, hostCount, onlineHosts / (float) hostCount * 100.0));
        }
        int onlineUrls = 0;
        for (ServiceRef serviceRef : urls) {
            if (serviceRef.getService().isWorking()) {
                onlineUrls++;
            }
        }
        int urlCount = urls.size();
        if (urlCount > 0) {
            xmlService.setUrls(String.format("%d/%d (%1.0f%%)", onlineUrls, urlCount, onlineUrls / (float) urlCount * 100.0));
        } else {
            xmlService.setUrls("0");
        }
        return xmlService;
    }
}
