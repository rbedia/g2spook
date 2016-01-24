/**
 * Copyright 2014 Rafael Bedia
 *
 * This file is part of g2spook.
 *
 * g2spook is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * g2spook is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * g2spook. If not, see <http://www.gnu.org/licenses/>.
 */
package org.doxu.g2.gwc.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.doxu.g2.gwc.crawler.model.Host;
import org.doxu.g2.gwc.crawler.model.HostStatus;

public class HostChecker {

    CrawlSession session;

    private static final int HOST_THREADS = 10;

    public HostChecker(CrawlSession session) {
        this.session = session;
    }

    public void start() {
        Map<String, Host> hosts = session.getHosts();
        // create thread pool executor
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                HOST_THREADS, HOST_THREADS,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        // execute host checks
        List<Future<HostStatus>> futures = new ArrayList<>(hosts.keySet().size());
        for (Host host : hosts.values()) {
            futures.add(executor.submit(new HostThread(host)));
        }

        // collect up/down status
        for (Future<HostStatus> future : futures) {
            try {
                HostStatus status = future.get();
                Host host = status.getHost();
                host.setOnline(status.isOnline());
            } catch (InterruptedException | ExecutionException ex) {
                Logger.getLogger(HostChecker.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
