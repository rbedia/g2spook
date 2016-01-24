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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class OutputFiles {

    private static final Map<Id, CrawlerFile> FILES = new HashMap<>();

    static {
        FILES.put(Id.XML, new CrawlerFile("g2_services.xml", "g2_services.xml"));
        FILES.put(Id.SERVICES, new CrawlerFile("g2_services.html", "index.html"));
        FILES.put(Id.DISCOVERY, new CrawlerFile("g2_discovery.html", "discovery.html"));
        FILES.put(Id.STORE, new CrawlerFile("store.txt", "store.dat"));
    }

    public enum Id {

        XML, SERVICES, DISCOVERY, STORE
    }

    public static CrawlerFile get(Id id) {
        return FILES.get(id);
    }
    
    public static Collection<CrawlerFile> list() {
        return Collections.unmodifiableCollection(FILES.values());
    }
}
