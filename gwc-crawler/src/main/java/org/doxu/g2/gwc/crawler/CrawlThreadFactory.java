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

import org.apache.http.impl.client.CloseableHttpClient;

public class CrawlThreadFactory {
    
    private final CrawlSession session;
    
    private final CloseableHttpClient httpClient;

    private CrawlThreadFactory(CrawlSession session, CloseableHttpClient httpClient) {
        this.session = session;
        this.httpClient = httpClient;
    }
    
    public static CrawlThreadFactory newFactory(CrawlSession session, CloseableHttpClient httpClient) {
        return new CrawlThreadFactory(session, httpClient);
    }
    
    public CrawlThread createThread(String gwcUrl) {
        return new CrawlThread(session, httpClient, gwcUrl);
    }
}
