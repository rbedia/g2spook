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
package org.trillinux.g2.hub;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LocalInfo {

    private static LocalInfo instance = new LocalInfo();

    private final NodeInfo node;

    private LocalInfo() {
        node = new NodeInfo();
        try {
            node.setAddress(InetAddress.getLocalHost());
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        node.setPort(1234);
        byte[] guid = new byte[16];
        for (int i = 0; i < guid.length; i++) {
            guid[i] = (byte) i;
        }
        node.setGuid(guid);
    }

    public static LocalInfo getInstance() {
        return instance;
    }

    /**
     * @return the node
     */
    public NodeInfo getNode() {
        return node;
    }

}
