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

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.TimerTask;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.trillinux.g2.core.Node;
import org.trillinux.g2.core.NodeAddress;
import org.trillinux.g2.core.TimerManager;
import org.trillinux.g2.core.gwc.GWCManager;
import org.trillinux.g2.core.gwc.GWCQueryResponse;
import org.trillinux.g2.hub.handler.GnutellaHttpHandler;
import org.trillinux.g2.hub.handler.HttpDecoder;
import org.trillinux.g2.settings.Settings;

public class G2HubMain {
    ClientBootstrap tcpClientBootstrap = null;
    ServerBootstrap tcpServerBootstrap = null;
    UDPTransceiver transceiver = null;

    GWCManager gwcManager;

    // This is my GWC so I'm comfortable hardcoding it.
    private static final String DEFAULT_GWC = "http://cache.trillinux.org/g2/bazooka.php";

    private void createConnectTimer() {
        TimerManager.schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    connect();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }, 1000, 5000);
    }

    public void start() {
        gwcManager = new GWCManager();

        createConnectTimer();

        startTcpServer();
        startTcpClient();
        transceiver = UDPTransceiver.getInstance();
        transceiver.start(NodeInfo.getInstance().getPort());

        // String host = "localhost";
        // int port = 6346;
        //
        // try {
        // InetAddress ip = InetAddress.getByName(host);
        // Hostcache.getInstance().addHost(new Node(ip, port));
        // } catch (UnknownHostException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
    }

    private void startTcpClient() {
        ChannelFactory factory = new NioClientSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        tcpClientBootstrap = new ClientBootstrap(factory);

        tcpClientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("http-decoder", HttpDecoder.getDecoder());
                pipeline.addLast("http-handler", new GnutellaHttpHandler(
                        G2Handshake.Type.RESPONSE));

                return pipeline;
            }
        });

        tcpClientBootstrap.setOption("tcpNoDelay", true);
        tcpClientBootstrap.setOption("keepAlive", true);
    }

    private void startTcpServer() {
        ChannelFactory factory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool());

        tcpServerBootstrap = new ServerBootstrap(factory);

        tcpServerBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("http-decoder", HttpDecoder.getDecoder());
                pipeline.addLast("http-handler", new GnutellaHttpHandler(
                        G2Handshake.Type.REQUEST));

                return pipeline;
            }
        });

        tcpServerBootstrap.setOption("tcpNoDelay", true);
        tcpServerBootstrap.setOption("keepAlive", true);

        tcpServerBootstrap.bind(new InetSocketAddress(NodeInfo.getInstance()
                .getPort()));
    }

    private void connect() throws InterruptedException {
        ConnectionManager cm = ConnectionManager.getInstance();
        System.out
                .println("Connected to "
                        + cm.getChannel(ConnectionManager.HUB_HUB).size()
                        + " hubs, "
                        + cm.getChannel(ConnectionManager.HUB_LEAF).size()
                        + " leaves.");
        if (cm.getChannel(ConnectionManager.HUB_HUB).size() >= Settings
                .getInstance().getSetting("hubToHub").getInt()) {
            return;
        }

        NodeAddress addr = Hostcache.getInstance().getRandomAddress();
        if (addr == null) {
            queryGWC(DEFAULT_GWC);
            return;
        }

        System.out.println("Found hub: " + addr);

        InetAddress host = addr.getIp();
        int port = addr.getPort();

        InetSocketAddress socket = new InetSocketAddress(host, port);
        ChannelFuture future = tcpClientBootstrap.connect(socket);
        future.addListener(new ConnectionListener(socket));
    }

    private void queryGWC(String gwc) {
        try {
            System.out.println("Query GWC: " + gwc);
            GWCQueryResponse response = gwcManager.get(gwc);
            for (String host : response.getHosts()) {
                Node node = new Node(new NodeAddress(host));
                Hostcache.getInstance().addHost(node);
            }
            System.out.println(String.format("Found %d hosts, %d GWCs",
                    response.getHosts().size(), response.getGwcs().size()));
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class ConnectionListener implements ChannelFutureListener {

        private final InetSocketAddress socket;

        public ConnectionListener(InetSocketAddress socket) {
            this.socket = socket;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            if (future.isSuccess()) {
                System.out.println("Connected.");
                // ConnectionManager.leafHubChannels.add(future.getChannel());
            } else {
                System.out.println("Failed.");

                InetAddress ip = socket.getAddress();
                int port = socket.getPort();

                NodeAddress nodeAddr = new NodeAddress(ip, port);
                Hostcache.getInstance().removeHost(nodeAddr);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        G2HubMain client = new G2HubMain();
        client.start();
    }
}
