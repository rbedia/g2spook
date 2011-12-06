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
package org.trillinux.g2.hub.stats;

import java.io.Serializable;

import org.trillinux.g2.hub.packet.Hash;
import org.trillinux.g2.hub.packet.QueryPacket;

public class QueryStats implements Serializable {

    private long queries;

    private long dna;

    private long keyword;

    private long hash;

    private long hashSha1;

    private long hashMd5;

    private long hashTTH;

    private long hashBT;

    private long hashEd2k;

    private long hashBp;

    private long hashOther;

    private long xml;

    public QueryStats() {

    }

    public void record(QueryPacket packet) {
        queries++;
        if (packet.isDna()) {
            dna++;
        }
        if (packet.getDn() != null && !packet.getDn().isEmpty()) {
            keyword++;
        }
        if (packet.getMd() != null && !packet.getMd().isEmpty()) {
            xml++;
        }
        if (packet.getUrns().size() > 0) {
            hash++;
            for (Hash hash : packet.getUrns()) {
                if (hash.getFamily().equals("sha1")) {
                    hashSha1++;
                } else if (hash.getFamily().equals("md5")) {
                    hashMd5++;
                } else if (hash.getFamily().equals("tth")) {
                    hashTTH++;
                } else if (hash.getFamily().equals("btih")) {
                    hashBT++;
                } else if (hash.getFamily().equals("ed2k")) {
                    hashEd2k++;
                } else if (hash.getFamily().equals("bp")) {
                    hashBp++;
                } else {
                    hashOther++;
                }
            }
        }
    }

    /**
     * @return the queries
     */
    public long getQueries() {
        return queries;
    }

    /**
     * @return the dna
     */
    public long getDna() {
        return dna;
    }

    /**
     * @return the keyword
     */
    public long getKeyword() {
        return keyword;
    }

    /**
     * @return the hash
     */
    public long getHash() {
        return hash;
    }

    /**
     * @return the hashSha1
     */
    public long getHashSha1() {
        return hashSha1;
    }

    /**
     * @return the hashMd5
     */
    public long getHashMd5() {
        return hashMd5;
    }

    /**
     * @return the hashTTH
     */
    public long getHashTTH() {
        return hashTTH;
    }

    /**
     * @return the hashBT
     */
    public long getHashBT() {
        return hashBT;
    }

    /**
     * @return the hashEd2k
     */
    public long getHashEd2k() {
        return hashEd2k;
    }

    /**
     * @return the hashBp
     */
    public long getHashBp() {
        return hashBp;
    }

    /**
     * @return the hashOther
     */
    public long getHashOther() {
        return hashOther;
    }

    /**
     * @return the xml
     */
    public long getXml() {
        return xml;
    }

}
