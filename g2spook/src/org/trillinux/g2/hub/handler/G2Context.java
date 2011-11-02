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
package org.trillinux.g2.hub.handler;

import java.net.InetAddress;

import org.trillinux.g2.core.NodeAddress;

public class G2Context {
    private boolean hub;

    private boolean hubNeeded;

    private String userAgent;

    private NodeAddress listenIp;

    private InetAddress remoteIp;

    /**
     * @return the hub
     */
    public boolean isHub() {
        return hub;
    }

    /**
     * @param hub
     *            the hub to set
     */
    public void setHub(boolean hub) {
        this.hub = hub;
    }

    /**
     * @return the hubNeeded
     */
    public boolean isHubNeeded() {
        return hubNeeded;
    }

    /**
     * @param hubNeeded
     *            the hubNeeded to set
     */
    public void setHubNeeded(boolean hubNeeded) {
        this.hubNeeded = hubNeeded;
    }

    /**
     * @return the userAgent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * @param userAgent
     *            the userAgent to set
     */
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    /**
     * @return the listenIp
     */
    public NodeAddress getListenIp() {
        return listenIp;
    }

    /**
     * @param listenIp
     *            the listenIp to set
     */
    public void setListenIp(NodeAddress listenIp) {
        this.listenIp = listenIp;
    }

    /**
     * @return the remoteIp
     */
    public InetAddress getRemoteIp() {
        return remoteIp;
    }

    /**
     * @param remoteIp
     *            the remoteIp to set
     */
    public void setRemoteIp(InetAddress remoteIp) {
        this.remoteIp = remoteIp;
    }

}
