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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.trillinux.g2.core.Node;
import org.trillinux.g2.core.NodeAddress;

public class Hostcache {
    private static Hostcache instance = new Hostcache();

    private final Map<NodeAddress, Hub> nodes;

    private final Timer timer;

    private Hostcache() {
        nodes = new HashMap<NodeAddress, Hub>();
        timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    int count = purgeOldHubs();
                    System.out.println("Purged " + count + " hosts.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }, 10 * 1000, 5 * 60 * 1000);
    }

    public static Hostcache getInstance() {
        return instance;
    }

    public synchronized void removeHost(NodeAddress addr) {
        nodes.remove(addr);
    }

    public synchronized void addHost(Node host) {
        if (nodes.containsKey(host.getAddress())) {
            Hub hub = nodes.get(host.getAddress());
            hub.markSeen();
        } else {
            Hub hub = new Hub(host);
            hub.markSeen();
            nodes.put(host.getAddress(), hub);
        }
    }

    private static final long EXPIRATION = 1000 * 60 * 30; // 30 minutes

    /**
     * Removes hubs from the cache if they haven't been seen for a while.
     * Returns the number of hubs removed.
     */
    public synchronized int purgeOldHubs() {
        Date now = new Date();
        int count = 0;

        Iterator<Map.Entry<NodeAddress, Hub>> it = nodes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<NodeAddress, Hub> entry = it.next();
            Hub hub = entry.getValue();
            if (now.getTime() - hub.getLastseen().getTime() > EXPIRATION) {
                count++;
                it.remove();
            }
        }
        return count;
    }

    public synchronized int getHostCount() {
        return nodes.size();
    }

    public synchronized NodeAddress getRandomAddress() {
        Set<NodeAddress> addrs = nodes.keySet();
        int size = addrs.size();

        if (size == 0) {
            return null;
        }

        int randIndex = (int) (Math.random() * size);
        Iterator<NodeAddress> it = addrs.iterator();
        NodeAddress addr = it.next();
        while (randIndex > 0) {
            addr = it.next();
            randIndex--;
        }

        return addr;
    }

    public synchronized List<Hub> getRandomHubs(int khlCount) {
        List<Hub> hubs = new ArrayList<Hub>();

        Iterator<Hub> it = nodes.values().iterator();
        while (it.hasNext() && khlCount > 0) {
            hubs.add(it.next());
            khlCount--;
        }

        return hubs;
    }
}
