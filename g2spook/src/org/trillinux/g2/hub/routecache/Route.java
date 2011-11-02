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
package org.trillinux.g2.hub.routecache;

import org.trillinux.g2.core.NodeAddress;

public class Route {
    private byte[] guid;

    private long timestamp;

    private NodeAddress address;

    /**
     * @param guid
     * @param timestamp
     * @param address
     */
    public Route(byte[] guid, long timestamp, NodeAddress address) {
        super();
        this.guid = guid;
        this.timestamp = timestamp;
        this.address = address;
    }

    /**
     * @return the guid
     */
    public byte[] getGuid() {
        return guid;
    }

    /**
     * @param guid
     *            the guid to set
     */
    public void setGuid(byte[] guid) {
        this.guid = guid;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * @return the address
     */
    public NodeAddress getAddress() {
        return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(NodeAddress address) {
        this.address = address;
    }
}
