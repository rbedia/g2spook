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

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.doxu.g2.gwc.crawler.model.Service;

public class Crawler {

    public static final String VERSION = "1.0";
    public static final int GWC_CRAWLER_THREADS = 5;
    private final CrawlSession session;

    private final CountDownLatch crawlCompletedBarrier;

    public Crawler() {
        session = new CrawlSession();
        crawlCompletedBarrier = new CountDownLatch(1);
    }

    public void start() {
        String startUrl = "http://cache.trillinux.org/g2/bazooka.php";
        session.addURL(startUrl);

        CrawlerThreadPoolExecutor executor = new CrawlerThreadPoolExecutor(
                GWC_CRAWLER_THREADS, GWC_CRAWLER_THREADS,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());
        executor.setListener(new IdleListener() {
            @Override
            public void idle() {
                // If the thread pool is idle and the queue of GWCs to crawl is empty
                // the crawl of GWCs is complete
                if (session.peek() == null) {
                    crawlCompletedBarrier.countDown();
                }
            }
        });

        runQueueProcessor(executor);

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        printStats();
    }

    private void runQueueProcessor(CrawlerThreadPoolExecutor executor) {
        Thread producerThread = new QueueProcessorThread(session, executor);
        producerThread.start();

        try {
            crawlCompletedBarrier.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        producerThread.interrupt();
    }

    private void printStats() {
        System.out.println("Total crawled: " + session.getCrawlCount());
        List<Service> services = session.getServices();
        int working = 0;
        int connectError = 0;
        for (Service service : services) {
            switch (service.getStatus()) {
                case WORKING:
                    working++;
                    break;
                default:
                    connectError++;
                    break;
            }
        }
        System.out.println("Working: " + working);
        System.out.println("Error: " + connectError);

        for (Service service : services) {
            System.out.println(service.getUrl() + " - " + service.getStatus());
        }
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        crawler.start();
    }

}
