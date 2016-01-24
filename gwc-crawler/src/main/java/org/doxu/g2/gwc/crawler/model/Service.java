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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.doxu.g2.gwc.crawler.xml.Hosts;

public class Service {

    private final String url;
    private String ip;
    private String client;
    private Status status;
    private Date timestamp;

    private final List<HostRef> hosts;
    private final List<ServiceRef> urls;

    public Service(String url) {
        this.url = url;
        timestamp = new Date();
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
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

    public List<HostRef> getOnlineHosts() {
        List<HostRef> online = new ArrayList<>();
        for (HostRef host : hosts) {
            if (host.getHost().isOnline()) {
                online.add(host);
            }
        }
        return online;
    }

    public List<ServiceRef> getWorkingUrls() {
        List<ServiceRef> online = new ArrayList<>();
        for (ServiceRef service : urls) {
            if (service.getService().isWorking()) {
                online.add(service);
            }
        }
        return online;
    }

    public double getScore() {
        double score = 0;
        double totalUrls = urls.size();
        if (totalUrls > 1) {
            double workingUrls = getWorkingUrls().size();
            score += (1 / Math.log10(totalUrls)) * workingUrls * (workingUrls / totalUrls);
        }
        double totalHosts = hosts.size();
        if (totalHosts > 1) {
            double workingHosts = getOnlineHosts().size();
            score += (1 / Math.log10(totalHosts)) * workingHosts * (workingHosts / totalHosts);
        }
        return score;
    }

    public long getDeltaAge() {
        int hostCount = hosts.size();
        if (hostCount > 0) {
            long minAge = hosts.get(0).getAge();
            long maxAge = hosts.get(0).getAge();
            for (HostRef host : hosts) {
                long hostAge = host.getAge();
                if (hostAge < minAge) {
                    minAge = hostAge;
                }
                if (hostAge > maxAge) {
                    maxAge = hostAge;
                }
            }
            return (maxAge - minAge) / hostCount;
        }
        return Long.MIN_VALUE;
    }

    public org.doxu.g2.gwc.crawler.xml.Service toXML() throws DatatypeConfigurationException {
        org.doxu.g2.gwc.crawler.xml.Service xmlService = new org.doxu.g2.gwc.crawler.xml.Service();
        xmlService.setUrl(url);
        xmlService.setIp(ip);
        xmlService.setClient(client);
        long deltaAge = getDeltaAge();
        if (deltaAge != Long.MIN_VALUE) {
            xmlService.setDeltaAge(Long.toString(deltaAge));
        } else {
            xmlService.setDeltaAge("");
        }
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(timestamp);
        xmlService.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));
        xmlService.setStatus(status.toString());
        xmlService.setHosts(new Hosts());
        for (HostRef hostRef : hosts) {
            org.doxu.g2.gwc.crawler.xml.Host xmlHost = hostRef.toXML();
            xmlService.getHosts().getContent().add(xmlHost);
        }
        int onlineHosts = getOnlineHosts().size();
        int hostCount = hosts.size();
        if (hostCount > 0) {
            String hostSummary = String.format("%d/%d (%1.0f%%)", onlineHosts, hostCount, onlineHosts / (float) hostCount * 100.0);
            // Add the summary to the beginning so it is picked up by the
            // stylesheet.
            xmlService.getHosts().getContent().add(0, hostSummary);
        }
        int onlineUrls = getWorkingUrls().size();
        int urlCount = urls.size();
        if (urlCount > 0) {
            String urlSummary = String.format("%d/%d (%1.0f%%)", onlineUrls, urlCount, onlineUrls / (float) urlCount * 100.0);
            xmlService.setUrls(urlSummary);
        } else {
            xmlService.setUrls("0");
        }
        xmlService.setScore(getScore());
        return xmlService;
    }
}
