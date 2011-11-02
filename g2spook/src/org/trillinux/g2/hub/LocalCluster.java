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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.trillinux.g2.core.NodeAddress;

public class LocalCluster {
    private static LocalCluster instance = new LocalCluster();

    Map<NodeAddress, List<NodeAddress>> neighbors;

    private LocalCluster() {
        neighbors = new HashMap<NodeAddress, List<NodeAddress>>();
    }

    public synchronized void addNeighbor(NodeAddress neighbor,
            List<NodeAddress> peers) {
        neighbors.put(neighbor, peers);
        // TODO remove print call when done testing
        print();
    }

    public synchronized void removeNeighbor(NodeAddress neighbor) {
        neighbors.remove(neighbor);
    }

    public synchronized void print() {
        StringBuilder sb = new StringBuilder();
        sb.append("Local cluster:\n");
        sb.append("  Neighbors: ").append(neighbors.size()).append(" ");

        Set<NodeAddress> nodes = new HashSet<NodeAddress>();

        int nonUnique = 0;
        for (Map.Entry<NodeAddress, List<NodeAddress>> entry : neighbors
                .entrySet()) {
            nodes.add(entry.getKey());
            nodes.addAll(entry.getValue());
            nonUnique++;
            nonUnique += entry.getValue().size();
        }

        sb.append("Unique: ").append(nodes.size()).append(" Total: ")
                .append(nonUnique).append("\n");
        // for (Map.Entry<NodeAddress, List<NodeAddress>> entry : neighbors
        // .entrySet()) {
        // sb.append(entry.getKey()).append("\n");
        // for (NodeAddress peer : entry.getValue()) {
        // sb.append(" - ").append(peer).append("\n");
        // }
        // }
        System.out.println(sb);
    }

    /**
     * @return the instance
     */
    public static LocalCluster getInstance() {
        return instance;
    }
}
