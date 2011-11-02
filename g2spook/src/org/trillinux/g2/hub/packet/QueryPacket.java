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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.trillinux.g2.core.NodeAddress;
import org.trillinux.g2.core.Packet;
import org.trillinux.g2.hub.util.BigNumUtil;

public class QueryPacket {
    private byte[] guid;

    private String dn;

    private NodeAddress retAddress;

    private byte[] querykey;

    private List<Hash> urns;

    private boolean dna;

    private BigInteger min;

    private BigInteger max;

    private String md;

    private String[] interests;

    public void decode(Packet p) {
        dna = false;
        urns = new ArrayList<Hash>();
        guid = p.getPayload();

        for (Packet child : p.getChildren()) {
            if (child.getName().equals("UDP")) {
                if (child.getPayload().length == 10) {
                    byte[] addr = new byte[6];
                    System.arraycopy(child.getPayload(), 0, addr, 0,
                            addr.length);
                    retAddress = new NodeAddress(addr);
                    querykey = new byte[4];
                    System.arraycopy(child.getPayload(), 6, querykey, 0,
                            querykey.length);
                }
            } else if (child.getName().equals("DN")) {
                dn = new String(child.getPayload());
            } else if (child.getName().equals("URN")) {
                byte[] payload = child.getPayload();
                int end = 0;
                for (int i = 0; i < payload.length; i++) {
                    if (payload[i] == 0) {
                        end = i;
                        break;
                    }
                }
                byte[] sub = Arrays.copyOf(payload, end);
                byte[] hash = new byte[payload.length - end - 1];
                System.arraycopy(payload, end + 1, hash, 0, hash.length);
                String family = new String(sub);
                if (family.equals("sha1") || family.equals("bp")
                        || family.equals("ed2k") || family.equals("md5")
                        || family.equals("btih")) {
                    // System.out.println(family + " " + hash.length);
                } else {
                    System.out.println("Unknown hash: " + family);
                }
                urns.add(new Hash(family, hash));
            } else if (child.getName().equals("dna")) {
                dna = true;
            } else if (child.getName().equals("MD")) {
                md = new String(child.getPayload());
            } else if (child.getName().equals("I")) {
                byte[] payload = child.getPayload();
                String str = new String(payload);
                interests = str.split("\0");
            } else if (child.getName().equals("SZR")) {
                byte[] payload = child.getPayload();
                if (payload.length == 8) {
                    min = BigNumUtil.getBigInteger(payload, 0, 4);
                    max = BigNumUtil.getBigInteger(payload, 4, 4);
                } else if (payload.length == 16) {
                    min = BigNumUtil.getBigInteger(payload, 0, 8);
                    max = BigNumUtil.getBigInteger(payload, 8, 8);
                }
            } else if (child.getName().equals("NAT")) {
            } else if (child.getName().equals("HKEY")) {
                // this is something Shareaza Plus proprietary
            } else if (child.getName().equals("HURN")) {
                // this is something Shareaza Plus proprietary
            } else {
                child.print();
            }
        }
        if (interests == null) {
            interests = new String[0];
        }
    }

    /**
     * @return the guid
     */
    public byte[] getGuid() {
        return guid;
    }

    /**
     * @param guid
     *            the guid to set
     */
    public void setGuid(byte[] guid) {
        this.guid = guid;
    }

    /**
     * @return the dn
     */
    public String getDn() {
        return dn;
    }

    /**
     * @param dn
     *            the dn to set
     */
    public void setDn(String dn) {
        this.dn = dn;
    }

    /**
     * @return the retAddress
     */
    public NodeAddress getRetAddress() {
        return retAddress;
    }

    /**
     * @param retAddress
     *            the retAddress to set
     */
    public void setRetAddress(NodeAddress retAddress) {
        this.retAddress = retAddress;
    }

    /**
     * @return the querykey
     */
    public byte[] getQuerykey() {
        return querykey;
    }

    /**
     * @param querykey
     *            the querykey to set
     */
    public void setQuerykey(byte[] querykey) {
        this.querykey = querykey;
    }

    /**
     * @return the urns
     */
    public List<Hash> getUrns() {
        return urns;
    }

    /**
     * @param urns
     *            the urns to set
     */
    public void setUrns(List<Hash> urns) {
        this.urns = urns;
    }

    /**
     * @return the dna
     */
    public boolean isDna() {
        return dna;
    }

    /**
     * @param dna
     *            the dna to set
     */
    public void setDna(boolean dna) {
        this.dna = dna;
    }

    /**
     * @return the min
     */
    public BigInteger getMin() {
        return min;
    }

    /**
     * @param min
     *            the min to set
     */
    public void setMin(BigInteger min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public BigInteger getMax() {
        return max;
    }

    /**
     * @param max
     *            the max to set
     */
    public void setMax(BigInteger max) {
        this.max = max;
    }

    /**
     * @return the md
     */
    public String getMd() {
        return md;
    }

    /**
     * @param md
     *            the md to set
     */
    public void setMd(String md) {
        this.md = md;
    }

    /**
     * @return the interests
     */
    public String[] getInterests() {
        return interests;
    }

    /**
     * @param interests
     *            the interests to set
     */
    public void setInterests(String[] interests) {
        this.interests = interests;
    }

    @Override
    public String toString() {
        return "QueryPacket [dn=" + dn + ", urns=" + urns.size()
                + ", retAddress=" + retAddress + ", min=" + min + ", max="
                + max + ", i=" + Arrays.toString(interests) + ", dna=" + dna
                + "]";
    }
}
