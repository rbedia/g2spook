/**
 * Copyright 2011 Rafael Bedia
 * 
 * This file is part of g2spook.
 * 
 * g2spook is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * g2spook is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with g2spook.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.trillinux.g2.core;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * A node on the network. Could be either a hub or a leaf.
 * 
 * @author Rafael Bedia
 * 
 */
public class Node implements Serializable {
    private static final long serialVersionUID = 5181448345780649467L;

    protected NodeAddress address;
    protected String vendor;
    protected String version;
    protected String name;
    protected double lat;
    protected double lon;
    protected String country;

    public Node(NodeAddress addr) {
        this.address = addr;
        country = "XX";
    }

    public Node(InetAddress ip, int port) {
        this.address = new NodeAddress(ip, port);
        country = "XX";
    }

    public Node(Node n) {
        address = n.address;
        copy(n);
    }

    public void copy(Node n) {
        // address = n.address; // don't need to copy this, it is the
        // "primary key"
        vendor = n.vendor;
        version = n.version;
        name = n.name;
        lat = n.lat;
        lon = n.lon;
        country = n.country;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(byte[] vendor) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < vendor.length; i++) {
            out.append((char) vendor[i]);
        }
        this.vendor = out.toString();
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(byte[] version) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < version.length; i++) {
            out.append((char) version[i]);
        }
        this.version = out.toString();
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(byte[] name) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < name.length; i++) {
            out.append((char) name[i]);
        }
        this.name = out.toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public NodeAddress getAddress() {
        return address;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Node other = (Node) obj;
        if (address == null) {
            if (other.address != null)
                return false;
        } else if (!address.equals(other.address))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return address.toString();
    }
}
