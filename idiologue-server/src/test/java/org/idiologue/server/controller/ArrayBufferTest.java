package org.idiologue.server.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@DisplayName("An array buffer")
public class ArrayBufferTest {

    private DataBufferFactory factory = new DefaultDataBufferFactory();

    @Test
    @DisplayName("should be able to write a data buffer")
    void testWriteBuffer() {
        ArrayBuffer buffer = new ArrayBuffer(10);
        DataBuffer dataBuffer = factory.wrap(new byte[] { 1, 2, 3, 4 });
        buffer.write(dataBuffer);
        assertEquals(4, buffer.available());
    }

    @Test
    @DisplayName("should be able to find a complete match in buffered data")
    void testCompleteMatch() {
        ArrayBuffer buffer = new ArrayBuffer(10);
        DataBuffer dataBuffer = factory.wrap(new byte[] { 1, 2, 3, 4 });
        buffer.write(dataBuffer);
        byte[] pattern = new byte[] { 1, 2, 3 };
        ArrayBuffer.MatchResult result = buffer.match(0, pattern);
        assertInstanceOf(ArrayBuffer.CompleteMatchResult.class, result);
        ArrayBuffer.CompleteMatchResult complete = (ArrayBuffer.CompleteMatchResult) result;
        assertEquals(0, complete.startPosition);
        assertEquals(2, complete.endPosition);
    }

    @Test
    @DisplayName("should be able to find a complete match in buffered data when the read position is not 0")
    void testCompleteMatchNonZero() {
        ArrayBuffer buffer = new ArrayBuffer(10);
        DataBuffer dataBuffer = factory.wrap(new byte[] { 1, 2, 3, 4, 5, 6 });
        buffer.write(dataBuffer);
        buffer.skip(2);
        byte[] pattern = new byte[] { 4, 5, 6 };
        ArrayBuffer.MatchResult result = buffer.match(0, pattern);
        assertInstanceOf(ArrayBuffer.CompleteMatchResult.class, result);
        ArrayBuffer.CompleteMatchResult complete = (ArrayBuffer.CompleteMatchResult) result;
        assertEquals(1, complete.startPosition);
        assertEquals(3, complete.endPosition);
    }

    @Test
    @DisplayName("should be able to find a partial match in buffered data")
    void testPartialMatch() {
        ArrayBuffer buffer = new ArrayBuffer(10);
        DataBuffer dataBuffer = factory.wrap(new byte[] { 1, 2, 3, 4 });
        buffer.write(dataBuffer);
        byte[] pattern = new byte[] { 3, 4, 5, 6 };
        ArrayBuffer.MatchResult result = buffer.match(0, pattern);
        assertInstanceOf(ArrayBuffer.PartialMatchResult.class, result);
        ArrayBuffer.PartialMatchResult partial = (ArrayBuffer.PartialMatchResult) result;
        assertEquals(2, partial.startPosition);
    }

    @Test
    @DisplayName("should be able to find a partial match in buffered data when the read position is not 0")
    void testPartialMatchNonZero() {
        ArrayBuffer buffer = new ArrayBuffer(10);
        DataBuffer dataBuffer = factory.wrap(new byte[] { 1, 2, 3, 4, 5, 6 });
        buffer.write(dataBuffer);
        buffer.skip(2);
        byte[] pattern = new byte[] { 5, 6, 7, 8 };
        ArrayBuffer.MatchResult result = buffer.match(0, pattern);
        assertInstanceOf(ArrayBuffer.PartialMatchResult.class, result);
        ArrayBuffer.PartialMatchResult partial = (ArrayBuffer.PartialMatchResult) result;
        assertEquals(2, partial.startPosition);
    }

    @Test
    @DisplayName("should be able to find no match in buffered data")
    void testNoMatch() {
        ArrayBuffer buffer = new ArrayBuffer(10);
        DataBuffer dataBuffer = factory.wrap(new byte[] { 1, 2, 3, 4 });
        buffer.write(dataBuffer);
        byte[] pattern = new byte[] { 3, 5 };
        ArrayBuffer.MatchResult result = buffer.match(0, pattern);
        assertInstanceOf(ArrayBuffer.NoMatchResult.class, result);
    }

}
