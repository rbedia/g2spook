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
package org.trillinux.g2.hub.controller;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;
import org.trillinux.g2.core.NodeAddress;
import org.trillinux.g2.hub.packet.QueryPacket;
import org.trillinux.g2.hub.stats.QueryLogger;

/**
 * Server that can be used for controlling the hub and getting status
 * information about the hub. Communication happens through serialized java
 * objects.
 * 
 * @author Rafael Bedia
 */
public class ControllerServer {

    public static final int PORT = 3636;

    public void start() {
        // Configure the server.
        ServerBootstrap bootstrap = new ServerBootstrap(
                new NioServerSocketChannelFactory(
                        Executors.newCachedThreadPool(),
                        Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new ObjectEncoder(),
                        new ObjectDecoder(), new ControllerServerHandler());
            }
        });

        // Bind and start to accept incoming connections.
        bootstrap.bind(new InetSocketAddress(PORT));
    }

    public static void main(String[] args) throws UnknownHostException {
        ControllerServer server = new ControllerServer();
        server.start();

        // Test data
        QueryPacket query = new QueryPacket();
        query.setDn("test");
        query.setRetAddress(new NodeAddress("localhost:1234"));
        QueryLogger.getInstance().log(query);
    }
}
