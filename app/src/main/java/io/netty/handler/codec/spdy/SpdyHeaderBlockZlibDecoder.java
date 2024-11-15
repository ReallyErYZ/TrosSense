package io.netty.handler.codec.spdy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/* loaded from: classes4.dex */
final class SpdyHeaderBlockZlibDecoder extends SpdyHeaderBlockRawDecoder {
    private static final int DEFAULT_BUFFER_CAPACITY = 4096;
    private static final SpdyProtocolException INVALID_HEADER_BLOCK = new SpdyProtocolException("Invalid Header Block");
    private ByteBuf decompressed;
    private final Inflater decompressor;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SpdyHeaderBlockZlibDecoder(SpdyVersion spdyVersion, int maxHeaderSize) {
        super(spdyVersion, maxHeaderSize);
        this.decompressor = new Inflater();
    }

    @Override // io.netty.handler.codec.spdy.SpdyHeaderBlockRawDecoder, io.netty.handler.codec.spdy.SpdyHeaderBlockDecoder
    void decode(ByteBufAllocator alloc, ByteBuf headerBlock, SpdyHeadersFrame frame) throws Exception {
        int numBytes;
        int len = setInput(headerBlock);
        do {
            numBytes = decompress(alloc, frame);
        } while (numBytes > 0);
        if (this.decompressor.getRemaining() != 0) {
            throw INVALID_HEADER_BLOCK;
        }
        headerBlock.skipBytes(len);
    }

    private int setInput(ByteBuf compressed) {
        int len = compressed.readableBytes();
        if (compressed.hasArray()) {
            this.decompressor.setInput(compressed.array(), compressed.arrayOffset() + compressed.readerIndex(), len);
        } else {
            byte[] in = new byte[len];
            compressed.getBytes(compressed.readerIndex(), in);
            this.decompressor.setInput(in, 0, in.length);
        }
        return len;
    }

    private int decompress(ByteBufAllocator alloc, SpdyHeadersFrame frame) throws Exception {
        ensureBuffer(alloc);
        byte[] out = this.decompressed.array();
        int off = this.decompressed.arrayOffset() + this.decompressed.writerIndex();
        try {
            int numBytes = this.decompressor.inflate(out, off, this.decompressed.writableBytes());
            if (numBytes == 0 && this.decompressor.needsDictionary()) {
                try {
                    this.decompressor.setDictionary(SpdyCodecUtil.SPDY_DICT);
                    numBytes = this.decompressor.inflate(out, off, this.decompressed.writableBytes());
                } catch (IllegalArgumentException e) {
                    throw INVALID_HEADER_BLOCK;
                }
            }
            if (frame != null) {
                this.decompressed.writerIndex(this.decompressed.writerIndex() + numBytes);
                decodeHeaderBlock(this.decompressed, frame);
                this.decompressed.discardReadBytes();
            }
            return numBytes;
        } catch (DataFormatException e2) {
            throw new SpdyProtocolException("Received invalid header block", e2);
        }
    }

    private void ensureBuffer(ByteBufAllocator alloc) {
        if (this.decompressed == null) {
            this.decompressed = alloc.heapBuffer(4096);
        }
        this.decompressed.ensureWritable(1);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // io.netty.handler.codec.spdy.SpdyHeaderBlockRawDecoder, io.netty.handler.codec.spdy.SpdyHeaderBlockDecoder
    public void endHeaderBlock(SpdyHeadersFrame frame) throws Exception {
        super.endHeaderBlock(frame);
        releaseBuffer();
    }

    @Override // io.netty.handler.codec.spdy.SpdyHeaderBlockRawDecoder, io.netty.handler.codec.spdy.SpdyHeaderBlockDecoder
    public void end() {
        super.end();
        releaseBuffer();
        this.decompressor.end();
    }

    private void releaseBuffer() {
        if (this.decompressed != null) {
            this.decompressed.release();
            this.decompressed = null;
        }
    }
}
