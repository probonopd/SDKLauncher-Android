package com.readium.model.epub3.util;

import java.nio.ByteBuffer;

public class ByteBufferUtils {

    private ByteBufferUtils() {
    }

    public static ByteBuffer increaseCapacity(ByteBuffer buffer, int size)
            throws IllegalArgumentException {
        if (buffer == null) {
            throw new IllegalArgumentException("Buffer can't be null");
        }
        if (size < 0) {
            throw new IllegalArgumentException("Size can't be less than 0");
        }

        int capacity = buffer.capacity() + size;
        ByteBuffer result = allocate(capacity, buffer.isDirect());
        buffer.flip();
        result.put(buffer);
        return result;
    }

    public static ByteBuffer allocate(int capacity, boolean direct)
            throws IllegalArgumentException {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity can't be negative");
        }
        return direct ? ByteBuffer.allocateDirect(capacity) : ByteBuffer
                .allocate(capacity);
    }
}
