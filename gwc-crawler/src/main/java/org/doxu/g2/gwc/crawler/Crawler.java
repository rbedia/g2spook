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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import net.sf.saxon.s9api.Processor;
import net.sf.saxon.s9api.SaxonApiException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.doxu.g2.gwc.crawler.model.Service;
import org.doxu.g2.gwc.crawler.transform.XSLTProcessor;

public class Crawler {

    public static final int GWC_CRAWLER_THREADS = 10;
    public static final int CONNECT_TIMEOUT = 7 * 1000;

    private final CrawlSession session;

    private final CountDownLatch crawlCompletedBarrier;

    public Crawler(String startUrl) {
        session = new CrawlSession();
        session.addURL(startUrl);
        crawlCompletedBarrier = new CountDownLatch(1);
    }

    public void crawl() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Crawler.CONNECT_TIMEOUT)
                .setSocketTimeout(Crawler.CONNECT_TIMEOUT)
                .build();
        try (CloseableHttpClient httpClient = HttpClients.custom()
                .setUserAgent("doxu/" + AppInfo.VERSION)
                .setDefaultRequestConfig(requestConfig)
                .disableAutomaticRetries()
                .build()) {
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

            CrawlThreadFactory factory = CrawlThreadFactory.newFactory(session, httpClient);
            runQueueProcessor(factory, executor);

            executor.shutdown();
            try {
                executor.awaitTermination(30, TimeUnit.SECONDS);
            } catch (InterruptedException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        HostChecker hostChecker = new HostChecker(session);
        hostChecker.start();

        session.filter();
    }

    private void runQueueProcessor(CrawlThreadFactory factory, CrawlerThreadPoolExecutor executor) {
        Thread producerThread = new QueueProcessorThread(session, factory, executor);
        producerThread.start();

        try {
            crawlCompletedBarrier.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }

        producerThread.interrupt();
    }

    public void writeOutput(File outputDir) {
        try {
            if (!outputDir.exists()) {
                boolean success = outputDir.mkdirs();
                if (!success) {
                    throw new IOException("Failed to create output directory: " + outputDir.getPath());
                }
            } else if (!outputDir.isDirectory()) {
                throw new IllegalArgumentException(outputDir.getPath() + " must be a directory.");
            }
            
            File xml = new File(outputDir, OutputFiles.get(OutputFiles.Id.XML).local);
            session.toXMLFile(xml);

            Processor proc = new Processor(false);

            File servicesHtml = new File(outputDir, OutputFiles.get(OutputFiles.Id.SERVICES).local);
            XSLTProcessor.transform(proc, XSLTProcessor.SERVICES_XSL, xml, servicesHtml);

            File discoveryHtml = new File(outputDir, OutputFiles.get(OutputFiles.Id.DISCOVERY).local);
            XSLTProcessor.transform(proc, XSLTProcessor.DISCOVERY_XSL, xml, discoveryHtml);

            File storeTxt = new File(outputDir, OutputFiles.get(OutputFiles.Id.STORE).local);
            XSLTProcessor.transform(proc, XSLTProcessor.STORE_XSL, xml, storeTxt);
        } catch (DatatypeConfigurationException | SaxonApiException | IOException | JAXBException ex) {
            Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void printStats() {
        System.out.println("Total crawled: " + session.getCrawlCount());
        Collection<Service> services = session.getServices().values();
        int working = 0;
        int error = 0;
        for (Service service : services) {
            if (service.isWorking()) {
                working++;
            } else {
                error++;
            }
        }
        System.out.println("Working: " + working);
        System.out.println("Error: " + error);

        for (Service service : services) {
            int hostsOnline = service.getOnlineHosts().size();
            int urlsOnline = service.getWorkingUrls().size();
            int hosts = service.getHosts().size();
            int urls = service.getUrls().size();
            int deltaAgeHosts = service.getDeltaAgeHosts();
            int deltaAgeUrls = service.getDeltaAgeUrls();
            System.out.println(service.getUrl() + " - " + service.getStatus()
                    + " - " + service.getClient()
                    + " - " + hostsOnline + "/" + hosts + ", " + deltaAgeHosts
                    + " - " + urlsOnline + "/" + urls + ", " + deltaAgeUrls);
        }
    }

    public static void main(String[] args) {
        Crawler crawler = new Crawler("http://cache.trillinux.org/g2/bazooka.php");
        crawler.crawl();

        crawler.printStats();
        File outputDir = new File("target");
        if (!outputDir.isDirectory()) {
            outputDir = new File(".");
        }
        crawler.writeOutput(outputDir);
    }

}
