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

public class QHT {
    private final int bits;
    private final byte infinity;

    private final byte[] table;

    public QHT(int bits) {
        this.bits = bits;
        infinity = 1;

        table = new byte[(1 << bits) / 8];

        // create a full table
        for (int i = 0; i < table.length; i++) {
            table[i] = 0;
        }
    }

    public byte[] reset() {
        byte[] data = new byte[6];

        long entries = 1 << bits;

        data[0] = 0; // reset
        data[1] = (byte) ((entries >> 24) & 0xFF);
        data[2] = (byte) ((entries >> 16) & 0xFF);
        data[3] = (byte) ((entries >> 8) & 0xFF);
        data[4] = (byte) (entries & 0xFF);
        data[5] = infinity;

        return data;
    }

    public byte[] patch() {
        long entries = 1 << bits;

        byte[] data = new byte[(int) (5 + (entries / 8))];

        data[0] = 1; // patch
        data[1] = 1; // fragment number
        data[2] = 1; // fragment count
        data[3] = 0; // compression: 0 - none, 1 - deflate
        data[4] = 1; // bits

        // create a full table
        for (int i = 5; i < table.length; i++) {
            data[i] = (byte) (i % 256);
        }

        return data;
    }
}
