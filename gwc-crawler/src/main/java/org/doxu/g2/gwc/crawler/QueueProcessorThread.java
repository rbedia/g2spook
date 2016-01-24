/**
 * Copyright 2016 Rafael Bedia
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

import java.util.concurrent.ExecutorService;

public class QueueProcessorThread extends Thread {

    private final CrawlSession session;
    
    private final CrawlThreadFactory factory;

    private final ExecutorService executor;

    public QueueProcessorThread(CrawlSession session, CrawlThreadFactory factory, ExecutorService executor) {
        this.session = session;
        this.factory = factory;
        this.executor = executor;
    }

    @Override
    public void run() {
        try {
            while (true) {
                String gwcUrl = session.take();
                executor.execute(factory.createThread(gwcUrl));
            }
        } catch (InterruptedException ex) {
            // terminate
        }
    }

}
