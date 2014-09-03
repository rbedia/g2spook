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

public class Service {
    private String url;
    private String ip;
    private String client;
    private Status status;

    private List<Host> hosts;
    private List<GWCURL> urls;

    public Service(String url, String ip) {
        this();
        this.url = url;
        this.ip = ip;
    }

    public Service() {
        hosts = new ArrayList<>();
        urls = new ArrayList<>();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<Host> getHosts() {
        return hosts;
    }

    public void addHost(Host host) {
        hosts.add(host);
    }
    public List<GWCURL> getUrls() {
        return urls;
    }

    public void addURL(GWCURL url) {
        urls.add(url);
    }

    public int getDeltaAgeHosts() {
        if (hosts.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (Host host : hosts) {
            sum += host.getAge();
        }
        return sum / hosts.size();
    }

    public int getDeltaAgeUrls() {
        if (urls.isEmpty()) {
            return 0;
        }
        int sum = 0;
        for (GWCURL gwcUrl : urls) {
            sum += gwcUrl.getAge();
        }
        return sum / urls.size();
    }
}
