package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

/* loaded from: classes4.dex */
public abstract class HttpContentEncoder extends MessageToMessageCodec<HttpRequest, HttpObject> {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private EmbeddedChannel encoder;
    private static final CharSequence ZERO_LENGTH_HEAD = "HEAD";
    private static final CharSequence ZERO_LENGTH_CONNECT = "CONNECT";
    private final Queue<CharSequence> acceptEncodingQueue = new ArrayDeque();
    private State state = State.AWAIT_HEADERS;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public enum State {
        PASS_THROUGH,
        AWAIT_HEADERS,
        AWAIT_CONTENT
    }

    protected abstract Result beginEncode(HttpResponse httpResponse, String str) throws Exception;

    @Override // io.netty.handler.codec.MessageToMessageCodec
    protected /* bridge */ /* synthetic */ void decode(ChannelHandlerContext channelHandlerContext, HttpRequest httpRequest, List list) throws Exception {
        decode2(channelHandlerContext, httpRequest, (List<Object>) list);
    }

    @Override // io.netty.handler.codec.MessageToMessageCodec
    protected /* bridge */ /* synthetic */ void encode(ChannelHandlerContext channelHandlerContext, HttpObject httpObject, List list) throws Exception {
        encode2(channelHandlerContext, httpObject, (List<Object>) list);
    }

    @Override // io.netty.handler.codec.MessageToMessageCodec
    public boolean acceptOutboundMessage(Object msg) throws Exception {
        return (msg instanceof HttpContent) || (msg instanceof HttpResponse);
    }

    /* renamed from: decode, reason: avoid collision after fix types in other method */
    protected void decode2(ChannelHandlerContext ctx, HttpRequest msg, List<Object> out) throws Exception {
        String acceptEncoding;
        List<String> acceptEncodingHeaders = msg.headers().getAll(HttpHeaderNames.ACCEPT_ENCODING);
        switch (acceptEncodingHeaders.size()) {
            case 0:
                acceptEncoding = HttpContentDecoder.IDENTITY;
                break;
            case 1:
                acceptEncoding = acceptEncodingHeaders.get(0);
                break;
            default:
                acceptEncoding = StringUtil.join(",", acceptEncodingHeaders);
                break;
        }
        HttpMethod method = msg.method();
        if (HttpMethod.HEAD.equals(method)) {
            acceptEncoding = ZERO_LENGTH_HEAD;
        } else if (HttpMethod.CONNECT.equals(method)) {
            acceptEncoding = ZERO_LENGTH_CONNECT;
        }
        this.acceptEncodingQueue.add(acceptEncoding);
        out.add(ReferenceCountUtil.retain(msg));
    }

    /* renamed from: encode, reason: avoid collision after fix types in other method */
    protected void encode2(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
        CharSequence acceptEncoding;
        boolean isFull = (msg instanceof HttpResponse) && (msg instanceof LastHttpContent);
        switch (this.state) {
            case AWAIT_HEADERS:
                ensureHeaders(msg);
                if (this.encoder != null) {
                    throw new AssertionError();
                }
                HttpResponse res = (HttpResponse) msg;
                int code = res.status().code();
                HttpStatusClass codeClass = res.status().codeClass();
                if (codeClass == HttpStatusClass.INFORMATIONAL) {
                    acceptEncoding = null;
                } else {
                    acceptEncoding = this.acceptEncodingQueue.poll();
                    if (acceptEncoding == null) {
                        throw new IllegalStateException("cannot send more responses than requests");
                    }
                }
                if (isPassthru(res.protocolVersion(), code, acceptEncoding)) {
                    if (isFull) {
                        out.add(ReferenceCountUtil.retain(res));
                        return;
                    } else {
                        out.add(ReferenceCountUtil.retain(res));
                        this.state = State.PASS_THROUGH;
                        return;
                    }
                }
                if (isFull && !((ByteBufHolder) res).content().isReadable()) {
                    out.add(ReferenceCountUtil.retain(res));
                    return;
                }
                Result result = beginEncode(res, acceptEncoding.toString());
                if (result == null) {
                    if (isFull) {
                        out.add(ReferenceCountUtil.retain(res));
                        return;
                    } else {
                        out.add(ReferenceCountUtil.retain(res));
                        this.state = State.PASS_THROUGH;
                        return;
                    }
                }
                this.encoder = result.contentEncoder();
                res.headers().set(HttpHeaderNames.CONTENT_ENCODING, result.targetContentEncoding());
                if (isFull) {
                    HttpResponse newRes = new DefaultHttpResponse(res.protocolVersion(), res.status());
                    newRes.headers().set(res.headers());
                    out.add(newRes);
                    ensureContent(res);
                    encodeFullResponse(newRes, (HttpContent) res, out);
                    return;
                }
                res.headers().remove(HttpHeaderNames.CONTENT_LENGTH);
                res.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
                out.add(ReferenceCountUtil.retain(res));
                this.state = State.AWAIT_CONTENT;
                if (!(msg instanceof HttpContent)) {
                    return;
                }
                break;
            case AWAIT_CONTENT:
                break;
            case PASS_THROUGH:
                ensureContent(msg);
                out.add(ReferenceCountUtil.retain(msg));
                if (msg instanceof LastHttpContent) {
                    this.state = State.AWAIT_HEADERS;
                    return;
                }
                return;
            default:
                return;
        }
        ensureContent(msg);
        if (encodeContent((HttpContent) msg, out)) {
            this.state = State.AWAIT_HEADERS;
        } else if (out.isEmpty()) {
            out.add(new DefaultHttpContent(Unpooled.EMPTY_BUFFER));
        }
    }

    private void encodeFullResponse(HttpResponse newRes, HttpContent content, List<Object> out) {
        int existingMessages = out.size();
        encodeContent(content, out);
        if (HttpUtil.isContentLengthSet(newRes)) {
            int messageSize = 0;
            for (int i = existingMessages; i < out.size(); i++) {
                Object item = out.get(i);
                if (item instanceof HttpContent) {
                    messageSize += ((HttpContent) item).content().readableBytes();
                }
            }
            HttpUtil.setContentLength(newRes, messageSize);
            return;
        }
        newRes.headers().set(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
    }

    private static boolean isPassthru(HttpVersion version, int code, CharSequence httpMethod) {
        return code < 200 || code == 204 || code == 304 || httpMethod == ZERO_LENGTH_HEAD || (httpMethod == ZERO_LENGTH_CONNECT && code == 200) || version == HttpVersion.HTTP_1_0;
    }

    private static void ensureHeaders(HttpObject msg) {
        if (!(msg instanceof HttpResponse)) {
            throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpResponse.class.getSimpleName() + ')');
        }
    }

    private static void ensureContent(HttpObject msg) {
        if (!(msg instanceof HttpContent)) {
            throw new IllegalStateException("unexpected message type: " + msg.getClass().getName() + " (expected: " + HttpContent.class.getSimpleName() + ')');
        }
    }

    private boolean encodeContent(HttpContent c, List<Object> out) {
        ByteBuf content = c.content();
        encode(content, out);
        if (c instanceof LastHttpContent) {
            finishEncode(out);
            LastHttpContent last = (LastHttpContent) c;
            HttpHeaders headers = last.trailingHeaders();
            if (headers.isEmpty()) {
                out.add(LastHttpContent.EMPTY_LAST_CONTENT);
                return true;
            }
            out.add(new ComposedLastHttpContent(headers, DecoderResult.SUCCESS));
            return true;
        }
        return false;
    }

    @Override // io.netty.channel.ChannelHandlerAdapter, io.netty.channel.ChannelHandler
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        cleanupSafely(ctx);
        super.handlerRemoved(ctx);
    }

    @Override // io.netty.channel.ChannelInboundHandlerAdapter, io.netty.channel.ChannelInboundHandler
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        cleanupSafely(ctx);
        super.channelInactive(ctx);
    }

    private void cleanup() {
        if (this.encoder != null) {
            this.encoder.finishAndReleaseAll();
            this.encoder = null;
        }
    }

    private void cleanupSafely(ChannelHandlerContext ctx) {
        try {
            cleanup();
        } catch (Throwable cause) {
            ctx.fireExceptionCaught(cause);
        }
    }

    private void encode(ByteBuf in, List<Object> out) {
        this.encoder.writeOutbound(in.retain());
        fetchEncoderOutput(out);
    }

    private void finishEncode(List<Object> out) {
        if (this.encoder.finish()) {
            fetchEncoderOutput(out);
        }
        this.encoder = null;
    }

    private void fetchEncoderOutput(List<Object> out) {
        while (true) {
            ByteBuf buf = (ByteBuf) this.encoder.readOutbound();
            if (buf != null) {
                if (!buf.isReadable()) {
                    buf.release();
                } else {
                    out.add(new DefaultHttpContent(buf));
                }
            } else {
                return;
            }
        }
    }

    /* loaded from: classes4.dex */
    public static final class Result {
        private final EmbeddedChannel contentEncoder;
        private final String targetContentEncoding;

        public Result(String targetContentEncoding, EmbeddedChannel contentEncoder) {
            this.targetContentEncoding = (String) ObjectUtil.checkNotNull(targetContentEncoding, "targetContentEncoding");
            this.contentEncoder = (EmbeddedChannel) ObjectUtil.checkNotNull(contentEncoder, "contentEncoder");
        }

        public String targetContentEncoding() {
            return this.targetContentEncoding;
        }

        public EmbeddedChannel contentEncoder() {
            return this.contentEncoder;
        }
    }
}
