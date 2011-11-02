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

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ConnectionlessBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioDatagramChannelFactory;
import org.trillinux.g2.core.packet.Packet;
import org.trillinux.g2.hub.handler.G2DatagramHandler;
import org.trillinux.g2.hub.handler.G2UDPPacketHandler;

public class UDPTransceiver {
    private static UDPTransceiver instance = new UDPTransceiver();

    private static int seqNum;

    ConnectionlessBootstrap udpServerBootstrap;

    Channel channel;

    private UDPTransceiver() {
        udpServerBootstrap = null;
        channel = null;
    }

    public void start(int port) {
        ChannelFactory factory = new NioDatagramChannelFactory(
                Executors.newCachedThreadPool());

        udpServerBootstrap = new ConnectionlessBootstrap(factory);

        udpServerBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("g2-datagram-handler", new G2DatagramHandler());
                pipeline.addLast("g2-packet-handler", new G2UDPPacketHandler());

                return pipeline;
            }
        });

        channel = udpServerBootstrap.bind(new InetSocketAddress(port));
    }

    public void sendPacket(InetSocketAddress addr, Packet packet) {
        channel.write(packet, addr);
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

    /**
     * @return the instance
     */
    public static UDPTransceiver getInstance() {
        return instance;
    }

}
