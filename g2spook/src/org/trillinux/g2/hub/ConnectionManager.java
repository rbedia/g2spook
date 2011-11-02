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

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;

public class ConnectionManager {
    private static ConnectionManager instance = new ConnectionManager();

    private ConnectionManager() {
    }

    public static final String LEAF_HUB = "leaf-hub";
    public static final String HUB_HUB = "hub-hub";
    public static final String HUB_LEAF = "hub-leaf";

    private static final ChannelGroup leafHubChannels = new DefaultChannelGroup(
            LEAF_HUB);
    private static final ChannelGroup hubHubChannels = new DefaultChannelGroup(
            HUB_HUB);
    private static final ChannelGroup hubLeafChannels = new DefaultChannelGroup(
            HUB_LEAF);

    public ChannelGroup getChannel(String name) {
        if (name.equals(LEAF_HUB)) {
            return leafHubChannels;
        } else if (name.equals(HUB_HUB)) {
            return hubHubChannels;
        } else if (name.equals(HUB_LEAF)) {
            return hubLeafChannels;
        }
        return null;
    }

    public void addHubHub(Channel ch) {
        hubHubChannels.add(ch);
    }

    public void addLeafHub(Channel ch) {
        leafHubChannels.add(ch);
    }

    public void addHubLeaf(Channel ch) {
        hubLeafChannels.add(ch);
    }

    /**
     * @return the instance
     */
    public static ConnectionManager getInstance() {
        return instance;
    }

}
