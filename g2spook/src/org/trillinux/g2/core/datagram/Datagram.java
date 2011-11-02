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
package org.trillinux.g2.core.datagram;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.BitSet;
import java.util.Date;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import org.trillinux.g2.core.packet.IncompletePacketException;

/**
 * A single GND datagram. Each datagram can be looked up by (ip,sequence). Once
 * a datagram has all parts it is complete and its payload can be retrieved. If
 * it was compressed then the payload will be decompressed automatically before
 * it is returned.
 * 
 * @author Rafael Bedia
 * 
 */
public class Datagram {
    private final InetAddress ip;
    private final int port;
    private final short sequence;
    private final byte count;
    private final boolean ack;
    private final boolean deflate;
    private final byte[][] parts;
    private boolean handled;
    private final BitSet received;
    private Date lastUpdate;

    public Datagram(InetAddress ip, int port, short sequence, byte count,
            boolean ack, boolean deflate) {
        super();
        this.ip = ip;
        this.port = port;
        this.sequence = sequence;
        this.count = count;
        this.ack = ack;
        this.deflate = deflate;
        this.parts = new byte[count][];
        this.handled = false;
        received = new BitSet(count);
        lastUpdate = new Date();
    }

    public boolean match(InetAddress inIp, short inSequence) {
        return ip.equals(inIp) && sequence == inSequence;
    }

    public byte[] getPayload() throws IOException, IncompletePacketException,
            DataFormatException {
        if (!isComplete())
            throw new IncompletePacketException();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int i = 0; i < count; i++) {
            out.write(parts[i]);
        }
        if (deflate) {
            Inflater decomp = new Inflater();
            decomp.setInput(out.toByteArray());
            byte[] result = new byte[out.size() * 3];

            int length = decomp.inflate(result);
            byte[] data = new byte[length];
            System.arraycopy(result, 0, data, 0, data.length);
            return data;
        }
        return out.toByteArray();
    }

    public boolean isComplete() {
        return received.cardinality() == count && received.length() == count;
    }

    public void addPart(byte part, byte[] data) {
        if (isComplete())
            return;
        if (part > 0 && part <= count) {
            part--;
            parts[part] = data;
            received.set(part);
            touch();
        }
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled() {
        this.handled = true;
    }

    public void touch() {
        lastUpdate = new Date();
    }

    public Date getDate() {
        return lastUpdate;
    }

    public InetAddress getIp() {
        return ip;
    }

    public short getSequence() {
        return sequence;
    }

    public int getPort() {
        return port;
    }

    public boolean isAck() {
        return ack;
    }

    public byte getCount() {
        return count;
    }

}
