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
package org.trillinux.g2.hub.workers;

import java.io.IOException;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.trillinux.g2.core.packet.Packet;
import org.trillinux.g2.hub.packet.QHT;

public class QHTSender extends WorkerTask {

    public QHTSender(ChannelHandlerContext ctx) {
        super(ctx);
    }

    public static void send(Channel ch) {
        try {
            System.out.println("Sending QHT");

            QHT qht = new QHT(12);

            // reset
            Packet qhtReset = new Packet("QHT");
            qhtReset.setPayload(qht.reset());
            byte[] msg = Packet.encode(qhtReset);
            ChannelBuffer buf = ChannelBuffers.dynamicBuffer(msg.length);
            buf.writeBytes(msg);

            // patch
            Packet qhtPatch = new Packet("QHT", qht.patch());
            msg = Packet.encode(qhtPatch);
            System.out.println("QHT patch size: "
                    + qhtPatch.getPayload().length);
            buf.writeBytes(msg);
            ch.write(buf);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void exec() {
        Channel ch = ctx.getChannel();
        send(ch);
    }
}
