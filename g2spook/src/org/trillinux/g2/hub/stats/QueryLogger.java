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
package org.trillinux.g2.hub.stats;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.trillinux.g2.core.RingBuffer;
import org.trillinux.g2.hub.packet.QueryPacket;

public class QueryLogger {

    private static QueryLogger instance = new QueryLogger();

    private PrintWriter out;

    private final RingBuffer<QueryPacket> buffer;

    private final QueryStats stats;

    private QueryLogger() {
        try {
            out = new PrintWriter(new FileOutputStream("queries.txt"), true);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        buffer = new RingBuffer<QueryPacket>(1000);
        stats = new QueryStats();
    }

    public static QueryLogger getInstance() {
        return instance;
    }

    public synchronized void log(QueryPacket query) {
        out.println(query.getDn());
        buffer.enqueue(query);
        stats.record(query);
    }

    public synchronized List<QueryPacket> getList() {
        List<QueryPacket> queries = new ArrayList<QueryPacket>();
        Iterator<QueryPacket> it = buffer.iterator();
        while (it.hasNext()) {
            queries.add(it.next());
        }
        return queries;
    }

    public synchronized QueryStats getStats() {
        return stats;
    }
}
