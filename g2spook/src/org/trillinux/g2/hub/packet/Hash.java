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

import java.io.Serializable;

public class Hash implements Serializable {
    private String family;

    private byte[] hash;

    /**
     * @param family
     * @param hash
     */
    public Hash(String family, byte[] hash) {
        super();
        this.family = family;
        this.hash = hash;
    }

    /**
     * @return the family
     */
    public String getFamily() {
        return family;
    }

    /**
     * @param family
     *            the family to set
     */
    public void setFamily(String family) {
        this.family = family;
    }

    /**
     * @return the hash
     */
    public byte[] getHash() {
        return hash;
    }

    /**
     * @param hash
     *            the hash to set
     */
    public void setHash(byte[] hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Hash [family=" + family + ", len=" + hash.length + "]";
    }
}
