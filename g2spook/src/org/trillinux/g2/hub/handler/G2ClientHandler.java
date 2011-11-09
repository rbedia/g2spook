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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
import org.trillinux.g2.core.TimerManager;
import org.trillinux.g2.core.packet.BadPacketException;
import org.trillinux.g2.core.packet.EndOfChildrenException;
import org.trillinux.g2.core.packet.EndOfStreamException;
import org.trillinux.g2.core.packet.Packet;
import org.trillinux.g2.hub.Hostcache;
import org.trillinux.g2.hub.LocalCluster;
import org.trillinux.g2.hub.NodeInfo;
import org.trillinux.g2.hub.QueryLogger;
import org.trillinux.g2.hub.UDPTransceiver;
import org.trillinux.g2.hub.packet.PingPacket;
import org.trillinux.g2.hub.packet.QueryPacket;
import org.trillinux.g2.hub.routecache.GUIDCache;
import org.trillinux.g2.hub.util.BigNumUtil;
import org.trillinux.g2.hub.workers.LniSender;
import org.trillinux.g2.hub.workers.PingSender;
import org.trillinux.g2.hub.workers.UprocSender;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class G2ClientHandler extends SimpleChannelHandler {

    boolean qhtSent;

    G2Context g2ctx;

    public G2ClientHandler(ChannelHandlerContext ctx, G2Context g2ctx) {
        this.g2ctx = g2ctx;

        TimerManager.schedule(new PingSender(ctx), 1000, 15 * 1000);
        TimerManager.schedule(new LniSender(ctx), 3000, 10 * 1000);
        // only send UPROC once
        TimerManager.scheduleOnce(new UprocSender(ctx), 10 * 1000);
        // TimerManager.schedule(new QHTSender(ctx), 5000);

        qhtSent = false;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // System.out.println("G2 Message received");

        ChannelBuffer buf = (ChannelBuffer) e.getMessage();
        byte[] dst = new byte[buf.readableBytes()];
        buf.readBytes(dst);

        try {
            Packet p = Packet.decode(new ByteArrayInputStream(dst));
            if (p.getName().equals("KHL")) {
                handleKHL(e, p);
                // sendQuery(e.getChannel()); // TODO temporary
            } else if (p.getName().equals("LNI")) {
                handleLNI(p);
            } else if (p.getName().equals("PO")) {
                // silently drop it
                // System.out.println("PO received.");
            } else if (p.getName().equals("PI")) {
                handlePI(e, p);
            } else if (p.getName().equals("Q2")) {
                // TODO handle
                // System.out.println("Q2 received.");
                handleQ2(e, p);
            } else if (p.getName().equals("UPROC")) {
                // TODO handle
                System.out.println("UPROC received.");
            } else if (p.getName().equals("UPROD")) {
                System.out.println("UPROD received.");
                // p.print();
                handleUPROD(p);
            } else if (p.getName().equals("QA")) {
                // TODO handle
                System.out.println("QA received.");
            } else if (p.getName().equals("QH2")) {
                // TODO handle
                System.out.println("QH2 received.");
                // p.print();
            } else if (p.getName().equals("QHT")) {
                // TODO handle
            } else if (p.getName().equals("HAW")) {
                handleHAW(p);
            } else {
                p.print();
            }
        } catch (BadPacketException e1) {
            e1.printStackTrace();
        } catch (EndOfChildrenException e1) {
            e1.printStackTrace();
        } catch (EndOfStreamException e1) {
            e1.printStackTrace();
        }

        if (!qhtSent) {
            qhtSent = true;
            // QHTSender.send(ctx.getChannel());
        }
    }

    private void handleQ2(MessageEvent e, Packet p) {
        for (Packet child : p.getChildren()) {
            if (child.getName().equals("DN")) {
                // child.print();
                QueryLogger.getInstance().log(new String(child.getPayload()));
            }
        }
        QueryPacket qp = new QueryPacket();
        qp.decode(p);
        // if (qp.isDna() && qp.getDn() != null) {
        // System.out.println(qp);
        // }
        // System.out.println(qp);
    }

    private void handleUPROD(Packet p) {
        for (Packet child : p.getChildren()) {
            if (child.getName().equals("XML")) {
                byte[] xml = child.getPayload();
                Document doc = parseXml(xml);
                if (doc != null) {
                    Element elem = doc.getDocumentElement();
                    NodeList nodes = elem.getElementsByTagName("handle");
                    Element childElem = (Element) nodes.item(0);
                    if (childElem != null) {
                        String name = childElem.getAttribute("primary");
                        System.out.println("Username: " + name);
                    }
                }
            }
        }
    }

    private Document parseXml(byte[] xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new ByteArrayInputStream(xml));
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void handlePI(MessageEvent e, Packet p) {
        PingPacket ping = new PingPacket();
        ping.decode(p);
        // System.out.println(ping);
        if (!ping.isRelay() && ping.getUdpAddress() == null) {
            sendPO(e.getChannel());
        } else if (ping.isRelay() && ping.getUdpAddress() != null) {
            // System.out.println("Relay ping: " + ping.getUdpAddress());
            InetSocketAddress addr = ping.getUdpAddress().getSocketAddress();
            Packet po = new Packet("PO");
            po.addChild(new Packet("RELAY"));
            UDPTransceiver.getInstance().sendPacket(addr, po);
        } else if (!ping.isRelay() && ping.getUdpAddress() != null) {
            // TODO forward to all neighbors
        } else {
            System.out.println(ping);
        }
    }

    private void sendQuery(Channel channel) {
        System.out.println("Sending query");
        Packet q2 = new Packet("Q2");
        byte[] guid = asByteArray(UUID.randomUUID());
        q2.setPayload(guid);

        Packet dn = new Packet("DN");
        dn.setPayload("potter".getBytes());
        q2.addChild(dn);

        try {
            byte[] data = Packet.encode(q2);
            ChannelBuffer buf = ChannelBuffers.buffer(data.length);
            buf.writeBytes(data);
            channel.write(buf);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static byte[] asByteArray(UUID uuid) {
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        byte[] buffer = new byte[16];

        for (int i = 0; i < 8; i++) {
            buffer[i] = (byte) (msb >>> 8 * (7 - i));
        }
        for (int i = 8; i < 16; i++) {
            buffer[i] = (byte) (lsb >>> 8 * (7 - i));
        }

        return buffer;

    }

    private void sendPO(Channel channel) {
        System.out.println("Sending PO");
        Packet po = new Packet("PO");
        try {
            byte[] data = Packet.encode(po);
            ChannelBuffer buf = ChannelBuffers.buffer(data.length);
            buf.writeBytes(data);
            channel.write(buf);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleLNI(Packet p) {
        NodeInfo nodeInfo = new NodeInfo();
        NodeAddress nodeAddr = null;

        for (Packet child : p.getChildren()) {
            if (child.getName().equals("NA")) {
                if (child.getPayload().length == 6) {
                    byte[] addr = new byte[6];
                    System.arraycopy(child.getPayload(), 0, addr, 0,
                            addr.length);
                    nodeAddr = new NodeAddress(addr);
                    nodeInfo.setIp(nodeAddr.getIp());
                    nodeInfo.setPort(nodeAddr.getPort());
                }
            } else if (child.getName().equals("GU")) {
                byte[] payload = child.getPayload();
                if (payload.length == 16) {
                    byte[] guid = payload;
                    nodeInfo.setGuid(guid);
                }
            } else if (child.getName().equals("V")) {
                String vendor = new String(child.getPayload());
                nodeInfo.setVendor(vendor);
            } else if (child.getName().equals("LS")) {
                byte[] payload = child.getPayload();
                if (payload.length >= 8) {
                    long files = ((payload[3] & 0xFF) << 24)
                            + ((payload[2] & 0xFF) << 16)
                            + ((payload[1] & 0xFF) << 8) + (payload[0] & 0xFF);
                    BigInteger size = BigNumUtil.getBigInteger(payload, 4, 4);
                    nodeInfo.setFiles(files);
                    nodeInfo.setLibrarySize(size.longValue());
                } else {
                    // System.out.println("/LNI/LS payload size unexpected: "
                    // + payload.length);
                }
            } else if (child.getName().equals("HS")) {
                byte[] payload = child.getPayload();
                int leaves = ((payload[1] & 0xFF) << 8) + (payload[0] & 0xFF);
                int maxLeaves = ((payload[3] & 0xFF) << 8)
                        + (payload[2] & 0xFF);

                nodeInfo.setLeaves(leaves);
                nodeInfo.setMaxLeaves(maxLeaves);
            } else if (child.getName().equals("QK")) {
                nodeInfo.setQk(true);
            } else if (child.getName().equals("FW")) {
                nodeInfo.setFw(true);
            } else {
                System.out.println("Unknown LNI child: " + child.getName());
            }
        }

        System.out.println("Leaves: " + nodeInfo.getLeaves() + ", "
                + nodeInfo.getMaxLeaves());
        System.out.println("Library: " + nodeInfo.getFiles() + ", "
                + getScaledSize(nodeInfo.getLibrarySize()));

        if (nodeInfo.getGuid() != null && nodeAddr != null) {
            GUIDCache.getInstance().addRoute(nodeInfo.getGuid(), nodeAddr);
        }
    }

    /**
     * Takes a quantity in KB and scales it down to between 0 and 1000 with the
     * appropriate unit appended.
     * 
     * @param amount
     * @return
     */
    private String getScaledSize(long amount) {
        double size = amount;
        int scale = 0;
        while (size > 1000) {
            scale++;
            size /= 1000;
        }
        String unit = "";
        if (scale == 0) {
            unit = "KB";
        } else if (scale == 1) {
            unit = "MB";
        } else if (scale == 2) {
            unit = "GB";
        } else if (scale == 3) {
            unit = "TB";
        } else if (scale == 4) {
            unit = "PB";
        } else {
            // Really big and unexpected unit so fall back on KB
            return amount + " KB";
        }
        return String.format("%3.2f %s", size, unit);
    }

    private NodeAddress readNodeAddress(byte[] payload) {
        byte[] addr = new byte[6];
        System.arraycopy(payload, 0, addr, 0, addr.length);
        NodeAddress nodeAddr = new NodeAddress(addr);
        return nodeAddr;
    }

    private void handleKHL(MessageEvent e, Packet p) {
        NodeAddress neighbor = new NodeAddress(
                (InetSocketAddress) e.getRemoteAddress());

        List<NodeAddress> peers = new ArrayList<NodeAddress>();

        for (Packet child : p.getChildren()) {
            if (child.getName().equals("CH")) {
                if (child.getPayload().length == 10) {
                    NodeAddress nodeAddr = readNodeAddress(child.getPayload());
                    Node node = new Node(nodeAddr);
                    Hostcache.getInstance().addHost(node);
                    // TODO handle timestamp
                }
            } else if (child.getName().equals("NH")) {
                if (child.getPayload().length == 6) {
                    NodeAddress nodeAddr = readNodeAddress(child.getPayload());
                    Node node = new Node(nodeAddr);
                    Hostcache.getInstance().addHost(node);
                    peers.add(nodeAddr);
                }
            } else if (child.getName().equals("TS")) {
                // TODO handle timestamp
            } else {
                System.out.println("Unknown KHL child: " + child.getName());
            }
        }

        if (g2ctx.isHub()) {
            // For now only add hubs to the local cluster
            LocalCluster.getInstance().addNeighbor(neighbor, peers);
        } else {
            LocalCluster.getInstance().removeNeighbor(neighbor);
        }

        System.out.println("Host count: "
                + Hostcache.getInstance().getHostCount());
    }

    private void handleHAW(Packet p) {
        for (Packet child : p.getChildren()) {
            if (child.getName().equals("NA") && child.getPayload().length >= 6) {
                byte[] addr = new byte[6];
                System.arraycopy(child.getPayload(), 0, addr, 0, addr.length);
                NodeAddress nodeAddr = new NodeAddress(addr);
                Node node = new Node(nodeAddr);
                Hostcache.getInstance().addHost(node);
            }
        }
        byte[] payload = p.getPayload();
        if (payload.length < 2 + 16) {
            return;
        }
        byte ttl = payload[0];
        byte hops = payload[1];
        if (ttl > 0) {
            payload[0] = (byte) (ttl - 1);
            payload[1] = (byte) (hops + 1);
            // TODO forward p to a random hub that hasn't seen it before
        }
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx,
            ChannelStateEvent e) throws Exception {
        InetSocketAddress addr = (InetSocketAddress) e.getChannel()
                .getRemoteAddress();
        if (addr != null) {
            NodeAddress neighbor = new NodeAddress(addr);
            LocalCluster.getInstance().removeNeighbor(neighbor);
        } else {
            System.out.println("Address is null when disconnected!");
        }
        super.channelDisconnected(ctx, e);
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
