package io.netty.handler.codec.memcache;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.FileRegion;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.memcache.MemcacheMessage;
import io.netty.util.internal.StringUtil;
import java.util.List;

/* loaded from: classes4.dex */
public abstract class AbstractMemcacheObjectEncoder<M extends MemcacheMessage> extends MessageToMessageEncoder<Object> {
    private boolean expectingMoreContent;

    protected abstract ByteBuf encodeMessage(ChannelHandlerContext channelHandlerContext, M m);

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // io.netty.handler.codec.MessageToMessageEncoder
    public void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        if (msg instanceof MemcacheMessage) {
            if (this.expectingMoreContent) {
                throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
            }
            out.add(encodeMessage(ctx, (MemcacheMessage) msg));
        }
        if ((msg instanceof MemcacheContent) || (msg instanceof ByteBuf) || (msg instanceof FileRegion)) {
            int contentLength = contentLength(msg);
            if (contentLength > 0) {
                out.add(encodeAndRetain(msg));
            } else {
                out.add(Unpooled.EMPTY_BUFFER);
            }
            this.expectingMoreContent = !(msg instanceof LastMemcacheContent);
        }
    }

    @Override // io.netty.handler.codec.MessageToMessageEncoder
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return (msg instanceof MemcacheObject) || (msg instanceof ByteBuf) || (msg instanceof FileRegion);
    }

    private static int contentLength(Object msg) {
        if (msg instanceof MemcacheContent) {
            return ((MemcacheContent) msg).content().readableBytes();
        }
        if (msg instanceof ByteBuf) {
            return ((ByteBuf) msg).readableBytes();
        }
        if (msg instanceof FileRegion) {
            return (int) ((FileRegion) msg).count();
        }
        throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
    }

    private static Object encodeAndRetain(Object msg) {
        if (msg instanceof ByteBuf) {
            return ((ByteBuf) msg).retain();
        }
        if (msg instanceof MemcacheContent) {
            return ((MemcacheContent) msg).content().retain();
        }
        if (msg instanceof FileRegion) {
            return ((FileRegion) msg).retain();
        }
        throw new IllegalStateException("unexpected message type: " + StringUtil.simpleClassName(msg));
    }
}
