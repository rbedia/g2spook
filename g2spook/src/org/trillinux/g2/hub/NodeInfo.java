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

import org.trillinux.g2.core.NodeAddress;

public class NodeInfo {

    private NodeAddress address;

    private byte[] guid;

    private String vendor;

    private boolean qk;

    private boolean fw;

    private int leaves;

    private int maxLeaves;

    private long files;

    private long librarySize;

    public NodeInfo() {
    }

    /**
     * @return the address
     */
    public NodeAddress getAddress() {
        return address;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(NodeAddress address) {
        this.address = address;
    }

    public byte[] getGuid() {
        return guid;
    }

    public void setGuid(byte[] guid) {
        this.guid = guid;
    }

    /**
     * @return the vendor
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @param vendor
     *            the vendor to set
     */
    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    /**
     * @return the qk
     */
    public boolean isQk() {
        return qk;
    }

    /**
     * @param qk
     *            the qk to set
     */
    public void setQk(boolean qk) {
        this.qk = qk;
    }

    /**
     * @return the fw
     */
    public boolean isFw() {
        return fw;
    }

    /**
     * @param fw
     *            the fw to set
     */
    public void setFw(boolean fw) {
        this.fw = fw;
    }

    /**
     * @return the leaves
     */
    public int getLeaves() {
        return leaves;
    }

    /**
     * @param leaves
     *            the leaves to set
     */
    public void setLeaves(int leaves) {
        this.leaves = leaves;
    }

    /**
     * @return the maxLeaves
     */
    public int getMaxLeaves() {
        return maxLeaves;
    }

    /**
     * @param maxLeaves
     *            the maxLeaves to set
     */
    public void setMaxLeaves(int maxLeaves) {
        this.maxLeaves = maxLeaves;
    }

    /**
     * @return the files
     */
    public long getFiles() {
        return files;
    }

    /**
     * @param files
     *            the files to set
     */
    public void setFiles(long files) {
        this.files = files;
    }

    /**
     * @return the librarySize
     */
    public long getLibrarySize() {
        return librarySize;
    }

    /**
     * @param librarySize
     *            the librarySize to set
     */
    public void setLibrarySize(long librarySize) {
        this.librarySize = librarySize;
    }
}
