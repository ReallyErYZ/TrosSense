package io.netty.handler.logging;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;

@ChannelHandler.Sharable
/* loaded from: classes4.dex */
public class LoggingHandler extends ChannelDuplexHandler {
    private static final LogLevel DEFAULT_LEVEL = LogLevel.DEBUG;
    private final ByteBufFormat byteBufFormat;
    protected final InternalLogLevel internalLevel;
    private final LogLevel level;
    protected final InternalLogger logger;

    public LoggingHandler() {
        this(DEFAULT_LEVEL);
    }

    public LoggingHandler(ByteBufFormat format) {
        this(DEFAULT_LEVEL, format);
    }

    public LoggingHandler(LogLevel level) {
        this(level, ByteBufFormat.HEX_DUMP);
    }

    public LoggingHandler(LogLevel level, ByteBufFormat byteBufFormat) {
        this.level = (LogLevel) ObjectUtil.checkNotNull(level, "level");
        this.byteBufFormat = (ByteBufFormat) ObjectUtil.checkNotNull(byteBufFormat, "byteBufFormat");
        this.logger = InternalLoggerFactory.getInstance(getClass());
        this.internalLevel = level.toInternalLevel();
    }

    public LoggingHandler(Class<?> clazz) {
        this(clazz, DEFAULT_LEVEL);
    }

    public LoggingHandler(Class<?> clazz, LogLevel level) {
        this(clazz, level, ByteBufFormat.HEX_DUMP);
    }

    public LoggingHandler(Class<?> clazz, LogLevel level, ByteBufFormat byteBufFormat) {
        ObjectUtil.checkNotNull(clazz, "clazz");
        this.level = (LogLevel) ObjectUtil.checkNotNull(level, "level");
        this.byteBufFormat = (ByteBufFormat) ObjectUtil.checkNotNull(byteBufFormat, "byteBufFormat");
        this.logger = InternalLoggerFactory.getInstance(clazz);
        this.internalLevel = level.toInternalLevel();
    }

    public LoggingHandler(String name) {
        this(name, DEFAULT_LEVEL);
    }

    public LoggingHandler(String name, LogLevel level) {
        this(name, level, ByteBufFormat.HEX_DUMP);
    }

    public LoggingHandler(String name, LogLevel level, ByteBufFormat byteBufFormat) {
        ObjectUtil.checkNotNull(name, "name");
        this.level = (LogLevel) ObjectUtil.checkNotNull(level, "level");
        this.byteBufFormat = (ByteBufFormat) ObjectUtil.checkNotNull(byteBufFormat, "byteBufFormat");
        this.logger = InternalLoggerFactory.getInstance(name);
        this.internalLevel = level.toInternalLevel();
    }

    public LogLevel level() {
        return this.level;
    }

    public ByteBufFormat byteBufFormat() {
        return this.byteBufFormat;
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "REGISTERED"));
        }
        ctx.fireChannelRegistered();
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "UNREGISTERED"));
        }
        ctx.fireChannelUnregistered();
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "ACTIVE"));
        }
        ctx.fireChannelActive();
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "INACTIVE"));
        }
        ctx.fireChannelInactive();
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelHandlerAdapter, io.netty.channel.ChannelHandler, io.netty.channel.ChannelInboundHandler
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "EXCEPTION", cause), cause);
        }
        ctx.fireExceptionCaught(cause);
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "USER_EVENT", evt));
        }
        ctx.fireUserEventTriggered(evt);
    }

    @Override // io.netty.channel.ChannelDuplexHandler, io.netty.channel.ChannelOutboundHandler
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "BIND", localAddress));
        }
        ctx.bind(localAddress, promise);
    }

    @Override // io.netty.channel.ChannelDuplexHandler, io.netty.channel.ChannelOutboundHandler
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "CONNECT", remoteAddress, localAddress));
        }
        ctx.connect(remoteAddress, localAddress, promise);
    }

    @Override // io.netty.channel.ChannelDuplexHandler, io.netty.channel.ChannelOutboundHandler
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "DISCONNECT"));
        }
        ctx.disconnect(promise);
    }

    @Override // io.netty.channel.ChannelDuplexHandler, io.netty.channel.ChannelOutboundHandler
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "CLOSE"));
        }
        ctx.close(promise);
    }

    @Override // io.netty.channel.ChannelDuplexHandler, io.netty.channel.ChannelOutboundHandler
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "DEREGISTER"));
        }
        ctx.deregister(promise);
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "READ COMPLETE"));
        }
        ctx.fireChannelReadComplete();
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "READ", msg));
        }
        ctx.fireChannelRead(msg);
    }

    @Override // io.netty.channel.ChannelDuplexHandler, io.netty.channel.ChannelOutboundHandler
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "WRITE", msg));
        }
        ctx.write(msg, promise);
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "WRITABILITY CHANGED"));
        }
        ctx.fireChannelWritabilityChanged();
    }

    @Override // io.netty.channel.ChannelDuplexHandler, io.netty.channel.ChannelOutboundHandler
    public void flush(ChannelHandlerContext ctx) throws Exception {
        if (this.logger.isEnabled(this.internalLevel)) {
            this.logger.log(this.internalLevel, format(ctx, "FLUSH"));
        }
        ctx.flush();
    }

    protected String format(ChannelHandlerContext ctx, String eventName) {
        String chStr = ctx.channel().toString();
        return new StringBuilder(chStr.length() + 1 + eventName.length()).append(chStr).append(' ').append(eventName).toString();
    }

    protected String format(ChannelHandlerContext ctx, String eventName, Object arg) {
        if (arg instanceof ByteBuf) {
            return formatByteBuf(ctx, eventName, (ByteBuf) arg);
        }
        if (arg instanceof ByteBufHolder) {
            return formatByteBufHolder(ctx, eventName, (ByteBufHolder) arg);
        }
        return formatSimple(ctx, eventName, arg);
    }

    protected String format(ChannelHandlerContext ctx, String eventName, Object firstArg, Object secondArg) {
        if (secondArg == null) {
            return formatSimple(ctx, eventName, firstArg);
        }
        String chStr = ctx.channel().toString();
        String arg1Str = String.valueOf(firstArg);
        String arg2Str = secondArg.toString();
        StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + arg1Str.length() + 2 + arg2Str.length());
        buf.append(chStr).append(' ').append(eventName).append(": ").append(arg1Str).append(", ").append(arg2Str);
        return buf.toString();
    }

    private String formatByteBuf(ChannelHandlerContext ctx, String eventName, ByteBuf msg) {
        String chStr = ctx.channel().toString();
        int length = msg.readableBytes();
        if (length == 0) {
            StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 4);
            buf.append(chStr).append(' ').append(eventName).append(": 0B");
            return buf.toString();
        }
        int outputLength = chStr.length() + 1 + eventName.length() + 2 + 10 + 1;
        if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            int rows = (length / 16) + (length % 15 == 0 ? 0 : 1) + 4;
            int hexDumpLength = (rows * 80) + 2;
            outputLength += hexDumpLength;
        }
        StringBuilder buf2 = new StringBuilder(outputLength);
        buf2.append(chStr).append(' ').append(eventName).append(": ").append(length).append('B');
        if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            buf2.append(StringUtil.NEWLINE);
            ByteBufUtil.appendPrettyHexDump(buf2, msg);
        }
        return buf2.toString();
    }

    private String formatByteBufHolder(ChannelHandlerContext ctx, String eventName, ByteBufHolder msg) {
        String chStr = ctx.channel().toString();
        String msgStr = msg.toString();
        ByteBuf content = msg.content();
        int length = content.readableBytes();
        if (length == 0) {
            StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 4);
            buf.append(chStr).append(' ').append(eventName).append(", ").append(msgStr).append(", 0B");
            return buf.toString();
        }
        int outputLength = chStr.length() + 1 + eventName.length() + 2 + msgStr.length() + 2 + 10 + 1;
        if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            int rows = (length / 16) + (length % 15 == 0 ? 0 : 1) + 4;
            int hexDumpLength = (rows * 80) + 2;
            outputLength += hexDumpLength;
        }
        StringBuilder buf2 = new StringBuilder(outputLength);
        buf2.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).append(", ").append(length).append('B');
        if (this.byteBufFormat == ByteBufFormat.HEX_DUMP) {
            buf2.append(StringUtil.NEWLINE);
            ByteBufUtil.appendPrettyHexDump(buf2, content);
        }
        return buf2.toString();
    }

    private static String formatSimple(ChannelHandlerContext ctx, String eventName, Object msg) {
        String chStr = ctx.channel().toString();
        String msgStr = String.valueOf(msg);
        StringBuilder buf = new StringBuilder(chStr.length() + 1 + eventName.length() + 2 + msgStr.length());
        return buf.append(chStr).append(' ').append(eventName).append(": ").append(msgStr).toString();
    }
}
