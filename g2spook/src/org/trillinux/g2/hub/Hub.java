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
package org.trillinux.g2.hub;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.trillinux.g2.core.Node;

public class Hub extends Node implements Serializable {
    private static final long serialVersionUID = 492315369061718953L;

    private Date lasttried;
    private Date firstseen;
    private Date lastseen;
    private int tries;
    private int answers;
    private boolean tried;
    private List<Hub> neighbors;

    protected int leafCount;
    protected int maxLeaves;

    public Hub(InetAddress ip, int port) {
        super(ip, port);
        tries = 0;
        answers = 0;
        tried = false;
        neighbors = new ArrayList<Hub>();

        leafCount = 0;
        maxLeaves = 0;
    }

    public Hub() throws UnknownHostException {
        super(InetAddress.getByName("0.0.0.0"), 0);
    }

    public Hub(Node n) {
        super(n);
    }

    /**
     * Copies the core information about a hub. Notably excluded from copying is
     * time related information which should usually be preserved as is.
     * 
     * @param hub
     */
    public void copy(Hub hub) {
        setNeighbors(hub.getNeighbors());
        setLeafCount(hub.getLeafCount());
        setVendor(hub.getVendor());
        setVersion(hub.getVersion());
        setLat(hub.getLat());
        setLon(hub.getLon());
        setName(hub.getName());
        setCountry(hub.getCountry());
    }

    public void markTried() {
        setLasttried(new Date());
        tries++;
        tried = true;
    }

    public void markSeen() {
        if (firstseen == null) {
            markFirstSeen();
        }
        setLastseen(new Date());
        answers++;
    }

    public void markFirstSeen() {
        setFirstseen(new Date());
    }

    public void resetTried() {
        tried = false;
    }

    public boolean wasTried() {
        return tried;
    }

    public int getFailures() {
        return tries - answers;
    }

    public int getTries() {
        return tries;
    }

    public void setLasttried(Date lasttried) {
        this.lasttried = lasttried;
    }

    public Date getLasttried() {
        return lasttried;
    }

    public Date getFirstseen() {
        return firstseen;
    }

    public void setFirstseen(Date firstseen) {
        this.firstseen = firstseen;
    }

    public void setLastseen(Date lastseen) {
        this.lastseen = lastseen;
    }

    public Date getLastseen() {
        return lastseen;
    }

    public boolean wasSeen() {
        return answers > 0;
    }

    public List<Hub> getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(List<Hub> neighbors) {
        this.neighbors.clear();
        this.neighbors.addAll(neighbors);
    }

    public void clearNeighbors() {
        neighbors.clear();
    }

    public void addNeighbor(Hub h) {
        neighbors.add(h);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int getLeafCount() {
        return leafCount;
    }

    public void setLeafCount(int leaves) {
        this.leafCount = leaves;
    }

    public int getMaxLeaves() {
        return maxLeaves;
    }

    public void setMaxLeaves(int maxLeaves) {
        this.maxLeaves = maxLeaves;
    }

    public long getUptime() {
        if (getFirstseen() != null) {
            return (System.currentTimeMillis() - getFirstseen().getTime()) / 1000;
        } else {
            return 0;
        }
    }

}
