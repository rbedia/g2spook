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
package org.trillinux.g2.core.datagram;

import java.net.InetAddress;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimerTask;

import org.trillinux.g2.core.TimerManager;

/**
 * Stores datagrams as they are received from the network.
 * 
 * @author Rafael Bedia
 * 
 */
public class DatagramCache {
    private final Hashtable<String, Datagram> table;
    private int expireAfter; // milliseconds
    private final PurgeTask purgeTask;
    private final int cleanupPeriod;

    public DatagramCache() {
        table = new Hashtable<String, Datagram>();
        expireAfter = 15 * 1000;
        cleanupPeriod = 60 * 1000;
        purgeTask = new PurgeTask();
        TimerManager.schedule(purgeTask, cleanupPeriod);
    }

    public Datagram findDG(InetAddress ip, short sequence) {
        String key = ip.toString() + "-" + sequence;
        return table.get(key);
    }

    public void addDG(Datagram dg) {
        String key = dg.getIp().toString() + "-" + dg.getSequence();
        synchronized (table) {
            table.put(key, dg);
        }
    }

    /**
     * Call this once a minute or so to empty the table of old entries.
     */
    public void purge() {
        Date now = new Date();
        long time = now.getTime();
        // if dg is handled then remove after X time has passed
        // if dg is not handled then remove after 2X time has passed
        // implement a hard limit on size
        synchronized (table) {
            Iterator<Datagram> it = table.values().iterator();
            while (it.hasNext()) {
                Datagram dg = it.next();
                long dgTime = dg.getDate().getTime();
                long difference = (time - dgTime); // milliseconds
                if (dg.isHandled() && difference > expireAfter) {
                    it.remove();
                }
                if (!dg.isHandled() && difference > expireAfter * 2) {
                    it.remove();
                }
            }
        }
    }

    public int size() {
        return table.size();
    }

    public int getExpireAfter() {
        return expireAfter;
    }

    public void setExpireAfter(int expireAfter) {
        this.expireAfter = expireAfter;
    }

    class PurgeTask extends TimerTask {
        @Override
        public void run() {
            try {
                purge();
            } catch (ConcurrentModificationException e) {
                System.out.println("FIXME: This is bad!");
                e.printStackTrace();
            }
        }

    }
}
