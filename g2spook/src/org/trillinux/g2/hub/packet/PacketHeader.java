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
package org.trillinux.g2.hub.packet;

import org.jboss.netty.buffer.ChannelBuffer;
import org.trillinux.g2.core.packet.EndOfChildrenException;
import org.trillinux.g2.core.packet.IncompletePacketException;

public class PacketHeader {
    public boolean be;

    public boolean cb;

    public int totalLength;

    public int length;

    public byte[] name;

    public static PacketHeader decode(ChannelBuffer buf)
            throws IncompletePacketException, EndOfChildrenException {
        PacketHeader p = null;
        p = new PacketHeader();
        if (buf.readableBytes() < 1) {
            throw new IncompletePacketException("no control byte yet");
        }

        int control = (buf.readByte() & 0xFF);
        p.totalLength++;
        if (control == 0)
            throw new EndOfChildrenException();
        int lenlen = (control & 0xC0) >> 6;
        int typelen = ((control & 0x38) >> 3) + 1;
        int flags = (control & 0x07);
        p.be = (flags & 0x02) == 0x02;
        p.cb = (flags & 0x04) == 0x04;

        if (lenlen > buf.readableBytes()) {
            throw new IncompletePacketException(
                    "not enough bytes to satisfy lenlen. needed: " + lenlen);
        }

        byte[] lenArr = new byte[lenlen];
        buf.readBytes(lenArr);
        p.totalLength += lenArr.length;
        p.length = 0;
        for (int i = 0; i < lenlen; i++) {
            int part = 0xFF & lenArr[i];
            p.length += part << (i * 8);
        }

        if (typelen > buf.readableBytes()) {
            throw new IncompletePacketException(
                    "not enough bytes to satisfy typelen. needed: " + typelen);
        }

        p.name = new byte[typelen];
        buf.readBytes(p.name);
        p.totalLength += p.name.length;

        if (p.length > buf.readableBytes()) {
            throw new IncompletePacketException(
                    "not enough bytes to satisfy packet length. needed: "
                            + p.length);
        }
        p.totalLength += p.length;
        return p;
    }

}
