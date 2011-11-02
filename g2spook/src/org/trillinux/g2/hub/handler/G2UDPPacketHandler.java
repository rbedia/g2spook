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
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.trillinux.g2.core.NodeAddress;
import org.trillinux.g2.core.Packet;
import org.trillinux.g2.hub.ConnectionManager;
import org.trillinux.g2.hub.Hostcache;
import org.trillinux.g2.hub.Hub;

public class G2UDPPacketHandler extends SimpleChannelHandler {

    private static int seqNum;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) {
        // System.out.println("G2 UDP Message received");

        Packet p = (Packet) e.getMessage();

        if (p.getName().equals("KHLR")) {
            handleKHLR(e, p);
        } else if (p.getName().equals("QKR")) {
            handleQKR(e, p);
        } else if (p.getName().equals("PI")) {
            handlePI(e, p);
        } else if (p.getName().equals("CRAWLR")) {
            handleCRAWLA(e, p);
        } else if (p.getName().equals("QKA")) {
            // TODO handle
        } else {
            p.print();
        }
    }

    private void handleCRAWLA(MessageEvent e, Packet crawlr) {
        ConnectionManager cm = ConnectionManager.getInstance();
        Packet crawla = new Packet("CRAWLA");

        Packet self = new Packet("SELF");
        self.addChild(new Packet("HUB"));
        self.addChild(new Packet("V", new byte[] { 'D', 'C', 'A', 'T' }));
        self.addChild(new Packet("CV", new byte[] { 'D', 'C', 'A', 'T', '/',
                '0', '.', '1' }));
        self.addChild(new Packet("NAME", new byte[] { 'd', 'c', 'a', 't' }));
        self.addChild(new Packet("GPS", new byte[] { 0, 0, 0, 0 }));
        self.addChild(new Packet("HS", new byte[] {
                (byte) cm.getChannel(ConnectionManager.HUB_LEAF).size(), 0, 25,
                0 }));

        crawla.addChild(self);

        sendDatagram(e, crawla);
        crawla.print();
    }

    private void handlePI(MessageEvent e, Packet pi) {
        Packet packet = new Packet("PO");
        sendDatagram(e, packet);
    }

    private void handleKHLR(MessageEvent e, Packet khlr) {
        System.out.println("Received KHLR");
        Packet khla = new Packet("KHLA");

        int khlCount = 20;
        List<Hub> hubs = Hostcache.getInstance().getRandomHubs(khlCount);
        for (Hub hub : hubs) {
            Packet ch = new Packet("CH");
            ch.setPayload(hub.getAddress().toBytes());
            khla.addChild(ch);
        }

        sendDatagram(e, khla);
    }

    public void sendDatagram(MessageEvent e, Packet packet) {
        Channel channel = e.getChannel();
        channel.write(packet, e.getRemoteAddress());
    }

    private void handleQKR(MessageEvent e, Packet qkr) {

        InetSocketAddress senderAddress = (InetSocketAddress) e
                .getRemoteAddress();
        NodeAddress rna = new NodeAddress(senderAddress.getAddress(),
                senderAddress.getPort());
        InetAddress sna = senderAddress.getAddress();
        boolean dna = false;
        for (Packet child : qkr.getChildren()) {
            if (child.getName().equals("RNA")) {
                byte[] addr = new byte[6];
                System.arraycopy(child.getPayload(), 0, addr, 0, addr.length);
                rna = new NodeAddress(addr);
            } else if (child.getName().equals("dna")) {
                dna = true;
            } else if (child.getName().equals("SNA")) {
                if (child.getPayload().length == 4) {
                    byte[] addr = new byte[4];
                    System.arraycopy(child.getPayload(), 0, addr, 0,
                            addr.length);
                    try {
                        sna = InetAddress.getByAddress(addr);
                    } catch (UnknownHostException ex) {
                        // TODO Auto-generated catch block
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("Unexpected SNA size: "
                            + child.getPayload().length);
                }
            } else {
                System.out.println("Unknown QKR child: " + child.getName());
            }
        }
        // System.out.println("QKR RNA: " + rna + " SNA: " + sna + " dna? "
        // + dna);
    }

    /**
     * Creates the 8-byte GND header.
     * 
     * @param flags
     *            currently unused
     * @param seq
     *            2-byte sequence number
     * @param part
     *            1-byte part
     * @param count
     *            1-byte count
     * @return the 8-byte header
     */
    public byte[] makeGND(int flags, short seq, int part, int count) {
        byte[] gnd = new byte[8];
        gnd[0] = 'G';
        gnd[1] = 'N';
        gnd[2] = 'D';
        gnd[3] = (byte) flags;
        gnd[4] = (byte) (seq >> 8);
        gnd[5] = (byte) seq;
        gnd[6] = (byte) part;
        gnd[7] = (byte) count;
        return gnd;
    }

    public short getSeqNum() {
        return (short) ((seqNum++) % 0xffff);
    }

}
