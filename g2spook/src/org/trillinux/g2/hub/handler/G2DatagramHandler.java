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
import java.net.InetAddress;
import java.net.InetSocketAddress;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.trillinux.g2.core.datagram.Datagram;
import org.trillinux.g2.core.datagram.DatagramCache;
import org.trillinux.g2.core.packet.Packet;

public class G2DatagramHandler extends SimpleChannelHandler {

    private static DatagramCache cache = new DatagramCache();

    private static int seqNum;

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        parseDatagram(ctx, e);
    }

    private void parseDatagram(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        InetSocketAddress addr = (InetSocketAddress) e.getRemoteAddress();
        InetAddress ip = addr.getAddress();
        int port = addr.getPort();
        ChannelBuffer data = (ChannelBuffer) e.getMessage();

        byte[] tag = new byte[3];

        // a valid datagram is at least 8 bytes long
        if (data.readableBytes() < 8) {
            return;
        }
        data.readBytes(tag);
        if (tag[0] == 'G' && tag[1] == 'N' && tag[2] == 'D') {
            byte flags = data.readByte();
            // TODO this sequence might be decoded wrong due to signs
            short sequence = (short) ((data.readByte() << 8) + (data.readByte()));
            byte part = data.readByte();
            byte count = data.readByte();
            byte[] payload = new byte[data.readableBytes()];
            data.readBytes(payload);

            boolean ack = (flags & 2) == 2;
            boolean deflate = (flags & 1) == 1;
            Datagram dg = cache.findDG(ip, sequence);
            if (dg == null) {
                dg = new Datagram(ip, port, sequence, count, ack, deflate);
                cache.addDG(dg);
            }
            dg.addPart(part, payload);

            if (ack) {
                ackDG(e, dg, part);
            }

            if (count == 0) {
                // this is an acknowledge packet
            } else if (dg.isComplete() && !dg.isHandled()) {
                dg.setHandled();
                Packet p = Packet.decode(new ByteArrayInputStream(dg
                        .getPayload()));
                Channels.fireMessageReceived(ctx, p, e.getRemoteAddress());
            }
        } else {
            System.out.println("Unrecognized packet. Length: "
                    + data.readableBytes());
        }
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

    private void ackDG(MessageEvent e, Datagram dg, byte part) {
        Channel channel = e.getChannel();
        byte[] gnd = makeGND(0, dg.getSequence(), part, 0);

        ChannelBuffer buf = ChannelBuffers.buffer(gnd.length);
        buf.writeBytes(gnd);
        channel.write(buf, e.getRemoteAddress());
    }

    @Override
    public void writeRequested(ChannelHandlerContext ctx, MessageEvent evt) {
        Object msg = evt.getMessage();
        msg.toString();

        // If it's a Packet then wrap it in a GND datagram, otherwise pass it
        // through unchanged assuming that someone else created the datagram
        if (msg instanceof Packet) {
            sendDatagram(evt, (Packet) msg);
        } else {
            ctx.sendDownstream(evt);
        }
    }

    private void sendDatagram(MessageEvent e, Packet packet) {
        byte[] data;
        try {
            byte[] gnd = makeGND(0, getSeqNum(), 1, 1);
            data = Packet.encode(packet);

            byte[] out = new byte[gnd.length + data.length];
            System.arraycopy(gnd, 0, out, 0, gnd.length);
            System.arraycopy(data, 0, out, gnd.length, data.length);

            ChannelBuffer buf = ChannelBuffers.buffer(out.length);
            buf.writeBytes(out);
            Channel channel = e.getChannel();
            channel.write(buf, e.getRemoteAddress());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public short getSeqNum() {
        return (short) ((seqNum++) % 0xffff);
    }

}
