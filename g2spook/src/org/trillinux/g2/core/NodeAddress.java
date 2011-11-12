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
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * A simple container for gluing the IP and port of a node together. It supports
 * being compared for equality and hashed.
 * 
 * @author Rafael Bedia
 */
public class NodeAddress implements Serializable {
    private static final long serialVersionUID = 7259811352355235438L;

    private InetAddress ip;
    private int port;

    public NodeAddress(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public NodeAddress(String host) throws UnknownHostException {
        String[] parts = host.split(":", 2);
        ip = InetAddress.getByName(parts[0]);
        port = Integer.parseInt(parts[1]);
    }

    public NodeAddress(byte[] addr) {
        byte[] rawIP = new byte[4];
        System.arraycopy(addr, 0, rawIP, 0, rawIP.length);
        try {
            ip = InetAddress.getByAddress(rawIP);
        } catch (UnknownHostException e) {
            ip = null;
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (addr.length == 6) {
            port = ((addr[5] & 0xFF) << 8) + (addr[4] & 0xFF);
        } else {
            port = 0;
        }
    }

    public NodeAddress(InetSocketAddress remoteAddress) {
        ip = remoteAddress.getAddress();
        port = remoteAddress.getPort();
    }

    public InetSocketAddress getSocketAddress() {
        return new InetSocketAddress(ip, port);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + port;
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
        NodeAddress other = (NodeAddress) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return ip.getHostAddress() + ":" + port;
    }

    public InetAddress getIp() {
        return ip;
    }

    /**
     * @param ip
     *            the ip to set
     */
    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    public byte[] toBytes() {
        byte[] addr = new byte[6];

        System.arraycopy(ip.getAddress(), 0, addr, 0, 4);
        addr[4] = (byte) (port & 0xFF);
        addr[5] = (byte) ((port >> 8) & 0xFF);

        return addr;
    }
}
