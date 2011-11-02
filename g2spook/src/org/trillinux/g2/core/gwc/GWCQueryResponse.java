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
package org.trillinux.g2.core.gwc;

import java.util.ArrayList;
import java.util.List;

public class GWCQueryResponse {
    private List<String> hosts;
    private List<String> gwcs;

    public GWCQueryResponse() {
        hosts = new ArrayList<String>();
        gwcs = new ArrayList<String>();
    }

    public List<String> getHosts() {
        return hosts;
    }

    public void setHosts(List<String> hosts) {
        this.hosts = hosts;
    }

    public List<String> getGwcs() {
        return gwcs;
    }

    public void setGwcs(List<String> gwcs) {
        this.gwcs = gwcs;
    }

}
