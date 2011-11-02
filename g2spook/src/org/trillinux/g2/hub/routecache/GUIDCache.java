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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.trillinux.g2.core.NodeAddress;

public class GUIDCache {
    private static final GUIDCache instance = new GUIDCache();

    private final Map<byte[], Route> table;

    private final Timer timer;

    private static final long PURGE_FREQUENCY = 1000 * 60 * 5; // 5 minutes

    private static final long ROUTE_EXPIRE = 1000 * 60 * 20; // 20 minutes

    private GUIDCache() {
        table = new HashMap<byte[], Route>();
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                int purged = purge();
                System.out.println(String.format(
                        "Purge %d routes from GUIDCache", purged));
            }
        }, PURGE_FREQUENCY, PURGE_FREQUENCY);
    }

    public synchronized int purge() {
        int count = 0;
        Iterator<Map.Entry<byte[], Route>> it = table.entrySet().iterator();
        long now = System.currentTimeMillis();
        while (it.hasNext()) {
            Map.Entry<byte[], Route> entry = it.next();
            long diff = now - entry.getValue().getTimestamp();
            if (diff > ROUTE_EXPIRE) {
                it.remove();
                count++;
            }
        }
        return count;
    }

    public synchronized void addRoute(byte[] guid, NodeAddress address) {
        Route route = new Route(guid, System.currentTimeMillis(), address);
        table.put(guid, route);
    }

    public synchronized Route getRoute(byte[] guid) {
        return table.get(guid);
    }

    /**
     * @return the instance
     */
    public static GUIDCache getInstance() {
        return instance;
    }
}
