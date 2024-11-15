package io.netty.handler.codec.http2;

/* loaded from: classes4.dex */
public interface Http2PushPromiseFrame extends Http2StreamFrame {
    Http2Headers http2Headers();

    int padding();

    int promisedStreamId();

    Http2FrameStream pushStream();

    Http2StreamFrame pushStream(Http2FrameStream http2FrameStream);

    @Override // io.netty.handler.codec.http2.Http2StreamFrame
    Http2PushPromiseFrame stream(Http2FrameStream http2FrameStream);
}
