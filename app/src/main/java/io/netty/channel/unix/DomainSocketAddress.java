package io.netty.channel.unix;

import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.net.SocketAddress;

/* loaded from: classes4.dex */
public class DomainSocketAddress extends SocketAddress {
    private static final long serialVersionUID = -6934618000832236893L;
    private final String socketPath;

    public DomainSocketAddress(String socketPath) {
        this.socketPath = (String) ObjectUtil.checkNotNull(socketPath, "socketPath");
    }

    public DomainSocketAddress(File file) {
        this(file.getPath());
    }

    public String path() {
        return this.socketPath;
    }

    public String toString() {
        return path();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DomainSocketAddress)) {
            return false;
        }
        return ((DomainSocketAddress) o).socketPath.equals(this.socketPath);
    }

    public int hashCode() {
        return this.socketPath.hashCode();
    }
}