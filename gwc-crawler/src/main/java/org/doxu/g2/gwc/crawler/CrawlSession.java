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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.doxu.g2.gwc.crawler.model.Service;

public class CrawlSession {

    private final Set<String> crawled;
    private final BlockingQueue<String> queue;

    private final List<Service> services;

    private final Object mutex = new Object();

    public CrawlSession() {
        crawled = Collections.synchronizedSet(new HashSet<String>());
        queue = new LinkedBlockingQueue<>();
        services = new ArrayList<>();
    }

    public void addURL(String url) {
        synchronized (mutex) {
            if (!crawled.contains(url)) {
                queue.add(url);
            }
        }
    }

    public String poll() {
        synchronized (mutex) {
            String url = queue.poll();
            if (url != null) {
                crawled.add(url);
            }
            return url;
        }
    }

    public String take() throws InterruptedException {
        String url = queue.take();
        synchronized (mutex) {
            if (!crawled.contains(url)) {
                crawled.add(url);
                return url;
            }
        }

        return take();
    }

    public String peek() {
        synchronized (mutex) {
            return queue.peek();
        }
    }

    public int getCrawlCount() {
        synchronized (mutex) {
            return crawled.size();
        }
    }

    public List<Service> getServices() {
        return services;
    }

    public void addService(Service service) {
        synchronized (services) {
            services.add(service);
        }
    }
}
