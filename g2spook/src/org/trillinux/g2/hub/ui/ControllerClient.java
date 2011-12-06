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
package org.trillinux.g2.hub.ui;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.serialization.ObjectDecoder;
import org.jboss.netty.handler.codec.serialization.ObjectEncoder;

public class ControllerClient {

    private static ControllerClient instance = new ControllerClient();

    private static final String HOST = "localhost";

    private static final int PORT = 3636;

    private ClientBootstrap bootstrap;

    /**
     * @return the instance
     */
    public static ControllerClient getInstance() {
        return instance;
    }

    private ControllerClient() {
        bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool()));

        // Set up the pipeline factory.
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return Channels.pipeline(new ObjectEncoder(),
                        new ObjectDecoder(), new ControllerClientHandler());
            }
        });
    }

    public void sendMessage(final Serializable message,
            final ClientCallback<?> callback) {
        // Start the connection attempt.
        ChannelFuture future = bootstrap.connect(new InetSocketAddress(HOST,
                PORT));
        future.addListener(new ChannelFutureListener() {

            @Override
            public void operationComplete(ChannelFuture future)
                    throws Exception {
                future.getChannel().getPipeline()
                        .get(ControllerClientHandler.class)
                        .setCallback(callback);
                future.getChannel().write(message);
            }
        });
    }
}
