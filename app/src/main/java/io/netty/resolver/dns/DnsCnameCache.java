package io.netty.resolver.dns;

import io.netty.channel.EventLoop;

/* loaded from: classes4.dex */
public interface DnsCnameCache {
    void cache(String str, String str2, long j, EventLoop eventLoop);

    void clear();

    boolean clear(String str);

    String get(String str);
}
