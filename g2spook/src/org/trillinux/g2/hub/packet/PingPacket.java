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
package org.trillinux.g2.hub.packet;

import org.trillinux.g2.core.NodeAddress;
import org.trillinux.g2.core.Packet;

public class PingPacket {
    boolean relay = false;

    NodeAddress udpAddress = null;

    public void decode(Packet p) {
        for (Packet child : p.getChildren()) {
            if (child.getName().equals("RELAY")) {
                relay = true;
            } else if (child.getName().equals("UDP")) {
                if (child.getPayload().length == 6) {
                    udpAddress = new NodeAddress(child.getPayload());
                }
            }
        }
    }

    public boolean isRelay() {
        return relay;
    }

    public void setRelay(boolean relay) {
        this.relay = relay;
    }

    public NodeAddress getUdpAddress() {
        return udpAddress;
    }

    public void setUdpAddress(NodeAddress udpAddress) {
        this.udpAddress = udpAddress;
    }

    @Override
    public String toString() {
        return "PingPacket [relay=" + relay + ", udpAddress=" + udpAddress
                + "]";
    }
}
