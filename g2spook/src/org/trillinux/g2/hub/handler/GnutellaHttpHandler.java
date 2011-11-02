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

import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.trillinux.g2.core.Node;
import org.trillinux.g2.core.NodeAddress;
import org.trillinux.g2.hub.ConnectionManager;
import org.trillinux.g2.hub.G2Handshake;
import org.trillinux.g2.hub.Hostcache;
import org.trillinux.g2.hub.NodeInfo;
import org.trillinux.g2.settings.Settings;

public class GnutellaHttpHandler extends SimpleChannelHandler {
    private G2Handshake.Type handshakeStage;

    private final ConnectionManager cm = ConnectionManager.getInstance();

    public GnutellaHttpHandler(G2Handshake.Type stage) {
        handshakeStage = stage;
    }

    private G2Context decodeHeaders(G2Handshake handshake) {
        G2Context ctx = new G2Context();

        for (Map.Entry<String, String> entry : handshake.getHeaders()
                .entrySet()) {
            String name = entry.getKey();
            String value = entry.getValue();
            if (name.equalsIgnoreCase("X-Hub")
                    || name.equalsIgnoreCase("X-Ultrapeer")) {
                ctx.setHub(value.equalsIgnoreCase("True"));
            } else if (name.equalsIgnoreCase("X-Hub-Needed")
                    || name.equalsIgnoreCase("X-Ultrapeer-Needed")) {
                ctx.setHubNeeded(value.equalsIgnoreCase("True"));
            } else if (name.equalsIgnoreCase("User-Agent")) {
                ctx.setUserAgent(value);
            } else if (name.equalsIgnoreCase("Remote-IP")) {
                try {
                    ctx.setRemoteIp(InetAddress.getByName(value));
                    NodeInfo.getInstance().setAddress(ctx.getRemoteIp());
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return null;
                }
            } else if (name.equalsIgnoreCase("Listen-IP")
                    || name.equalsIgnoreCase("X-Node")
                    || name.equalsIgnoreCase("X-Node")
                    || name.equalsIgnoreCase("X-My-Address")) {
                try {
                    ctx.setListenIp(new NodeAddress(value));
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return ctx;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // System.out.println("Message received");
        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        byte[] dst = new byte[buf.readableBytes()];
        buf.readBytes(dst);

        G2Handshake handshake = new G2Handshake();
        switch (handshakeStage) {
        case REQUEST: // incoming connection part 1
            if (handshake.decode(G2Handshake.Type.REQUEST, dst)) {
                String accept = handshake.getHeader("Accept");
                if (accept == null || !accept.equals("application/x-gnutella2")) {
                    System.out.println("Host doesn't support G2");
                    // TODO drop connection gracefully
                    e.getChannel().close();
                    return;
                }

                // Debugging
                // printHeaders(handshake.getHeaders());

                G2Context g2ctx = decodeHeaders(handshake);

                // Make sure the connection limits haven't been exceeded
                boolean full;
                if (g2ctx.isHub()) {
                    full = cm.getChannel(ConnectionManager.HUB_HUB).size() >= Settings
                            .getInstance().getSetting("hubToHub").getInt();
                } else {
                    full = cm.getChannel(ConnectionManager.HUB_LEAF).size() >= Settings
                            .getInstance().getSetting("hubToLeaf").getInt();
                }

                G2Handshake response = new G2Handshake();
                response.setType(G2Handshake.Type.RESPONSE);
                if (full) {
                    response.setStatus(503);
                    response.setStatusMessage("Maximum connections reached");
                    response.addHeader("Content-Type",
                            "application/x-gnutella2");
                    response.addHeader("Accept", "application/x-gnutella2");
                    response.addHeader("X-Hub", "True");
                    // TODO add X-Try-Hubs header

                    handshakeStage = G2Handshake.Type.RESPONSE2;

                    sendResponse(response, e);
                    ctx.getChannel().close();
                } else {
                    response.setStatus(200);
                    response.setStatusMessage("OK");
                    response.addHeader("Content-Type",
                            "application/x-gnutella2");
                    response.addHeader("Accept", "application/x-gnutella2");
                    response.addHeader("X-Hub", "True");

                    handshakeStage = G2Handshake.Type.RESPONSE2;

                    sendResponse(response, e);
                }
            } else {
                System.out.println("Bad handshake");
                // TODO drop connection gracefully
                e.getChannel().close();
                return;
            }

            break;
        case RESPONSE2: // incoming connection part 2
            if (handshake.decode(G2Handshake.Type.RESPONSE, dst)) {
                if (handshake.getStatus() != 200) {
                    System.out.println("Error connecting. Status: "
                            + handshake.getStatus() + ": "
                            + handshake.getStatusMessage());

                    String hubs = handshake.getHeader("X-Try-Hubs");
                    parseHubs(hubs);

                    e.getChannel().close();
                    return;
                }

                String contentType = handshake.getHeader("Content-Type");
                if (contentType == null
                        || !contentType.equals("application/x-gnutella2")) {
                    System.out.println("Host doesn't support G2");
                    // TODO drop connection
                }

                G2Context g2ctx = decodeHeaders(handshake);

                // Debugging
                // printHeaders(handshake.getHeaders());

                switchToG2PacketHandlers(ctx, g2ctx);
            }
            break;
        case RESPONSE: // outgoing connection part 2
            if (handshake.decode(G2Handshake.Type.RESPONSE, dst)) {
                if (handshake.getStatus() != 200) {
                    System.out.println("Error connecting. Status: "
                            + handshake.getStatus() + ": "
                            + handshake.getStatusMessage());

                    String hubs = handshake.getHeader("X-Try-Hubs");
                    parseHubs(hubs);

                    e.getChannel().close();
                    return;
                }

                G2Context g2ctx = decodeHeaders(handshake);

                // Debugging
                // printHeaders(handshake.getHeaders());

                String accept = handshake.getHeader("Accept");
                if (accept == null || !accept.equals("application/x-gnutella2")) {
                    System.out.println("Host doesn't support G2");
                    // TODO drop connection gracefully
                    e.getChannel().close();
                    return;
                } else {
                    G2Handshake response = new G2Handshake();
                    response.setType(G2Handshake.Type.RESPONSE);
                    response.setStatus(200);
                    response.setStatusMessage("OK");
                    response.addHeader("Content-Type",
                            "application/x-gnutella2");
                    response.addHeader("X-Hub", "True");

                    sendResponse(response, e);

                    switchToG2PacketHandlers(ctx, g2ctx);
                }
            } else {
                System.out.println("Bad handshake");
                // TODO drop connection gracefully
                e.getChannel().close();
                return;
            }
            break;
        default:
            System.out.println("Unexpected handshake stage");
            // TODO drop connection gracefully
            e.getChannel().close();
            break;
        }

    }

    private void sendResponse(G2Handshake response, MessageEvent e) {
        try {
            byte[] msg = response.encode();
            ChannelBuffer respBuf = ChannelBuffers.buffer(msg.length);
            respBuf.writeBytes(msg);
            Channel ch = e.getChannel();
            ch.write(respBuf);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void switchToG2PacketHandlers(ChannelHandlerContext ctx,
            G2Context g2ctx) {
        // Add the G2 packet handlers
        ctx.getPipeline().addLast("g2decoder", new G2Decoder());
        ctx.getPipeline().addLast("g2handler", new G2ClientHandler(ctx, g2ctx));

        // Remove the handshake handlers
        ctx.getPipeline().remove("http-decoder");
        ctx.getPipeline().remove("http-handler");

        if (g2ctx.isHub()) {
            cm.addHubHub(ctx.getChannel());
        } else {
            cm.addHubLeaf(ctx.getChannel());
        }
    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) {
        if (handshakeStage == G2Handshake.Type.RESPONSE) {
            Channel ch = e.getChannel();

            G2Handshake handshake = new G2Handshake();
            handshake.setType(G2Handshake.Type.REQUEST);
            handshake.addHeader("Accept", "application/x-gnutella2");
            handshake.addHeader("X-Hub", "True");
            handshake.addHeader("User-Agent", "dcat 0.1");

            try {
                byte[] msg = handshake.encode();
                ChannelBuffer buf = ChannelBuffers.buffer(msg.length);
                buf.writeBytes(msg);
                ch.write(buf);
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    private void parseHubs(String hubStr) {
        if (hubStr == null || hubStr.isEmpty()) {
            return;
        }
        String[] units = hubStr.split(",");
        for (String unit : units) {
            String[] parts = unit.split(" ");
            String[] addr = parts[0].split(":");
            try {
                InetAddress ip = InetAddress.getByName(addr[0]);
                int port = Integer.parseInt(addr[1]);
                Node node = new Node(ip, port);
                // System.out.println("Saving hub: " + node);
                Hostcache.getInstance().addHost(node);
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void printHeaders(Map<String, String> headers) {
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
            throws Exception {
        System.out.println("Error connecting: " + e.getCause().getMessage());
        if (!(e.getCause() instanceof ConnectException)) {
            e.getCause().printStackTrace();
        }
    }
}
