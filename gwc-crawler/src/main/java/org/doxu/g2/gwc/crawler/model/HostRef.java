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

public class HostRef {

    private final Host host;
    private final long age;

    public HostRef(Host host, long age) {
        this.host = host;
        this.age = age;
    }

    public Host getHost() {
        return host;
    }

    public long getAge() {
        return age;
    }

    public org.doxu.g2.gwc.crawler.xml.Host toXML() {
        org.doxu.g2.gwc.crawler.xml.Host xmlHost = new org.doxu.g2.gwc.crawler.xml.Host();
        xmlHost.setAge(age);
        xmlHost.setOnline(host.isOnline());
        xmlHost.setIp(host.getIp());
        xmlHost.setPort(host.getPort());
        return xmlHost;
    }
}
