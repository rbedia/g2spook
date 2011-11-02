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
package org.trillinux.g2.core;

/**
 * Thrown when a packet can not be parsed. The reason the packet is bad will be
 * included in the message.
 * 
 * @author Rafael Bedia
 * 
 */
public class BadPacketException extends Exception {

    public BadPacketException(String string) {
        super(string);
    }

    private static final long serialVersionUID = -749939229502229822L;

}
