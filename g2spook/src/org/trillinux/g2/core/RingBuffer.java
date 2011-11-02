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

import java.util.Iterator;
import java.util.NoSuchElementException;

/*************************************************************************
 * Copied from: http://www.cs.princeton.edu/introcs/43stack/RingBuffer.java.html
 * 
 * Ring buffer (fixed size queue) implementation using a circular array (array
 * with wrap-around).
 * 
 *************************************************************************/
public class RingBuffer<Item> implements Iterable<Item> {
    private final Item[] a; // queue elements
    private int N = 0; // number of elements on queue
    private int first = 0; // index of first element of queue
    private int last = 0; // index of next available slot

    // cast needed since no generic array creation in Java
    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        a = (Item[]) new Object[capacity];
    }

    public boolean isEmpty() {
        return N == 0;
    }

    public int size() {
        return N;
    }

    /**
     * Add a new item to the buffer. If the buffer is full then the least
     * recently added item will be overwritten.
     * 
     * @param item
     */
    public void enqueue(Item item) {
        // This is a modification from the original because the objective is to
        // always overwrite the oldest when enqueuing and the buffer is already
        // full.
        if (N < a.length) {
            N++;
        }
        a[last] = item;
        last = (last + 1) % a.length; // wrap-around
    }

    /**
     * Remove the least recently added item.
     * 
     * @return
     */
    public Item dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Ring buffer underflow");
        }
        Item item = a[first];
        a[first] = null; // to help with garbage collection
        N--;
        first = (first + 1) % a.length; // wrap-around
        return item;
    }

    @Override
    public Iterator<Item> iterator() {
        return new RingBufferIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class RingBufferIterator implements Iterator<Item> {
        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < N;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Item next() {
            if (!hasNext())
                throw new NoSuchElementException();
            return a[i++];
        }
    }
}
