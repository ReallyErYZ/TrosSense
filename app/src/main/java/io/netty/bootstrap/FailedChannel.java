package io.netty.bootstrap;

import io.netty.channel.AbstractChannel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import java.net.SocketAddress;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class FailedChannel extends AbstractChannel {
    private static final ChannelMetadata METADATA = new ChannelMetadata(false);
    private final ChannelConfig config;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FailedChannel() {
        super(null);
        this.config = new DefaultChannelConfig(this);
    }

    @Override // io.netty.channel.AbstractChannel
    protected AbstractChannel.AbstractUnsafe newUnsafe() {
        return new FailedChannelUnsafe();
    }

    @Override // io.netty.channel.AbstractChannel
    protected boolean isCompatible(EventLoop loop) {
        return false;
    }

    @Override // io.netty.channel.AbstractChannel
    protected SocketAddress localAddress0() {
        return null;
    }

    @Override // io.netty.channel.AbstractChannel
    protected SocketAddress remoteAddress0() {
        return null;
    }

    @Override // io.netty.channel.AbstractChannel
    protected void doBind(SocketAddress localAddress) {
        throw new UnsupportedOperationException();
    }

    @Override // io.netty.channel.AbstractChannel
    protected void doDisconnect() {
        throw new UnsupportedOperationException();
    }

    @Override // io.netty.channel.AbstractChannel
    protected void doClose() {
        throw new UnsupportedOperationException();
    }

    @Override // io.netty.channel.AbstractChannel
    protected void doBeginRead() {
        throw new UnsupportedOperationException();
    }

    @Override // io.netty.channel.AbstractChannel
    protected void doWrite(ChannelOutboundBuffer in) {
        throw new UnsupportedOperationException();
    }

    @Override // io.netty.channel.Channel
    public ChannelConfig config() {
        return this.config;
    }

    @Override // io.netty.channel.Channel
    public boolean isOpen() {
        return false;
    }

    @Override // io.netty.channel.Channel
    public boolean isActive() {
        return false;
    }

    @Override // io.netty.channel.Channel
    public ChannelMetadata metadata() {
        return METADATA;
    }

    /* loaded from: classes.dex */
    private final class FailedChannelUnsafe extends AbstractChannel.AbstractUnsafe {
        private FailedChannelUnsafe() {
            super();
        }

        @Override // io.netty.channel.Channel.Unsafe
        public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
            promise.setFailure((Throwable) new UnsupportedOperationException());
        }
    }
}