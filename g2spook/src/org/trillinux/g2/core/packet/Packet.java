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
package org.trillinux.g2.core.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.trillinux.util.Base64;
import org.trillinux.util.HexDump;

import com.generationjava.io.xml.PrettyPrinterXmlWriter;
import com.generationjava.io.xml.SimpleXmlWriter;
import com.generationjava.io.xml.XmlWriter;

/**
 * A G2 packet parser and container. Every G2 packet can have a payload and more
 * packets as children.
 * 
 * @author Rafael Bedia
 * 
 */
public class Packet implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 6025082704365474019L;

    private boolean be;
    private boolean cb;
    private int length;
    private byte[] name;
    private final ArrayList<Packet> children;
    byte[] payload;

    public Packet() {
        children = new ArrayList<Packet>();
        payload = new byte[0];
        name = new byte[0];
    }

    public Packet(String name) {
        children = new ArrayList<Packet>();
        setName(name);
        this.payload = new byte[0];
    }

    public Packet(String name, byte[] payload) {
        children = new ArrayList<Packet>();
        setName(name);
        this.payload = payload;
    }

    public static byte[] encode(Packet p) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream childOut = new ByteArrayOutputStream();

        Iterator<Packet> it = p.children.iterator();
        while (it.hasNext()) {
            Packet child = it.next();
            byte[] childData = Packet.encode(child);
            childOut.write(childData);
        }
        byte[] childData = childOut.toByteArray();
        int childLen = childData.length;
        int length = childLen + p.payload.length;
        if (childLen > 0) {
            length++; // child terminator
        }

        int lenlen = 0;
        if (length >= 256 * 256) {
            lenlen = 3;
        } else if (length >= 256) {
            lenlen = 2;
        } else if (length >= 0) {
            lenlen = 1;
        }
        int typelen = p.name.length - 1;
        int flags = (lenlen << 6) + (typelen << 3);
        if (p.cb) { // TODO derive from length of children?
            flags |= 0x04;
        }
        out.write(flags);
        if (length >= 256 * 256) {
            out.write(length >> 16);
        }
        if (length >= 256) {
            out.write(length >> 8);
        }
        if (length >= 0) {
            out.write(length);
        }
        out.write(p.name);
        if (childLen > 0) {
            out.write(childData);
            out.write(0); // child terminator
        }
        out.write(p.payload);
        return out.toByteArray();
    }

    public static void dump(byte[] data) {
        HexDump.dumpHexData(System.out, "Test HexDump", data, data.length);
    }

    public String toXml() {
        Writer writer = new java.io.StringWriter();
        XmlWriter simplexml = new SimpleXmlWriter(writer);
        PrettyPrinterXmlWriter xmlwriter = new PrettyPrinterXmlWriter(simplexml);
        try {
            xml(xmlwriter);

            xmlwriter.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return writer.toString();
    }

    public void xml(XmlWriter xmlwriter) throws IOException {
        xmlwriter.writeEntity(getName());
        xmlwriter.writeText(Base64.encodeBytes(getPayload()));
        Iterator<Packet> it = children.iterator();
        while (it.hasNext()) {
            it.next().xml(xmlwriter);
        }
        xmlwriter.endEntity();
    }

    public void print() {
        print(0);
    }

    public void print(int level) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < level * 3; i++) {
            out.append(' ');
        }
        String spacer = out.toString();
        System.out.println(spacer + getName());
        System.out.println(spacer + "paylen: " + payload.length);
        System.out.print(spacer + "payload: ");
        for (int i = 0; i < payload.length; i++) {
            System.out.print((char) payload[i]);
        }
        System.out.println();
        System.out.println(spacer + "children: " + children.size());
        Iterator<Packet> it = children.iterator();
        while (it.hasNext()) {
            it.next().print(level + 1);
        }
    }

    public static Packet decodeHeader(InputStream in)
            throws BadPacketException, EndOfChildrenException {
        Packet p = null;
        try {
            p = new Packet();
            if (in.available() == 0) {
                throw new BadPacketException("no bytes left");
            }
            int control = in.read();
            if (control == 0)
                throw new EndOfChildrenException();
            int lenlen = (control & 0xC0) >> 6;
            int typelen = ((control & 0x38) >> 3) + 1;
            int flags = (control & 0x07);
            p.be = (flags & 0x02) == 0x02;
            p.cb = (flags & 0x04) == 0x04;

            if (in.available() < lenlen)
                throw new BadPacketException(
                        "not enough bytes to satisfy lenlen");
            byte[] lenArr = new byte[lenlen];
            in.read(lenArr);
            p.length = 0;
            for (int i = 0; i < lenlen; i++) {
                int part = 0xFF & lenArr[i];
                // System.out.println("lenArr[" + i + "] = " + part);
                p.length += part << (i * 8);
            }
            System.out.println("length before: " + p.length);
            if (in.available() < typelen)
                throw new BadPacketException(
                        "not enough bytes to satisfy typelen");
            p.name = new byte[typelen];
            in.read(p.name);

        } catch (IOException e) {
            e.printStackTrace();
            throw new BadPacketException("IOException");
        }
        return p;
    }

    public boolean hasChildren() {
        return cb;
    }

    public boolean hasChildrenLeft(ByteArrayInputStream in) {
        in.mark(0);
        int control = in.read();
        in.reset();
        return control != 0;
    }

    public static Packet decode(InputStream in) throws BadPacketException,
            EndOfChildrenException, EndOfStreamException {
        Packet p = null;
        try {
            p = new Packet();
            int control = in.read();
            if (control == -1)
                throw new EndOfStreamException();
            if (control == 0)
                throw new EndOfChildrenException();
            int lenlen = (control & 0xC0) >> 6;
            int typelen = ((control & 0x38) >> 3) + 1;
            int flags = (control & 0x07);
            p.be = (flags & 0x02) == 0x02;
            p.cb = (flags & 0x04) == 0x04;

            byte[] lenArr = new byte[lenlen];
            int len = in.read(lenArr);
            if (len < lenlen)
                throw new BadPacketException(
                        "not enough bytes to satisfy lenlen. needed: " + lenlen
                                + ". got: " + len);
            p.length = 0;
            for (int i = 0; i < lenlen; i++) {
                int part = 0xFF & lenArr[i];
                p.length += part << (i * 8);
            }
            p.name = new byte[typelen];
            len = in.read(p.name);
            if (len < typelen)
                throw new BadPacketException(
                        "not enough bytes to satisfy typelen. needed: "
                                + typelen + ". got: " + len);

            // trim excess stuff that belongs to another packet
            /*
             * byte[] rest = new byte[p.length]; in.read(rest);
             * ByteArrayInputStream in2 = new ByteArrayInputStream(rest);
             */
            int consumed = 0;
            int marker = in.available();
            if (p.cb) {
                while (in.available() > 0 && consumed < p.length) {
                    try {
                        Packet child = Packet.decode(in);
                        p.children.add(child);
                        consumed = marker - in.available();
                    } catch (EndOfChildrenException e) {
                        consumed++;
                        break;
                    }
                }
            }
            if (p.length - consumed > 0) {
                p.payload = new byte[p.length - consumed];
                in.read(p.payload);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new BadPacketException("IOException");
        }

        return p;
    }

    public void addChild(Packet p) {
        children.add(p);
        cb = true;
    }

    public ArrayList<Packet> getChildren() {
        return children;
    }

    public void setPayload(byte[] data) {
        payload = data;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setName(String name) {
        char[] chars = name.toCharArray();
        if (chars.length > 8) {
            throw new IllegalArgumentException(
                    "Packet name can't be greater than 8 characters");
        }
        this.name = new byte[chars.length];
        for (int i = 0; i < chars.length; i++) {
            this.name[i] = (byte) chars[i];
        }
    }

    public String getName() {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < name.length; i++) {
            out.append((char) name[i]);
        }
        return out.toString();
    }

    public boolean isBe() {
        return be;
    }

    @Override
    public String toString() {
        return "Packet [name=" + getName() + ", children=" + children.size()
                + "]";
    }
}
