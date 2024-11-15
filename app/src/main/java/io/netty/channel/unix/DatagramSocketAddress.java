package io.netty.channel.unix;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/* loaded from: classes4.dex */
public final class DatagramSocketAddress extends InetSocketAddress {
    private static final long serialVersionUID = 3094819287843178401L;
    private final DatagramSocketAddress localAddress;
    private final int receivedAmount;

    DatagramSocketAddress(byte[] addr, int scopeId, int port, int receivedAmount, DatagramSocketAddress local) throws UnknownHostException {
        super(newAddress(addr, scopeId), port);
        this.receivedAmount = receivedAmount;
        this.localAddress = local;
    }

    public DatagramSocketAddress localAddress() {
        return this.localAddress;
    }

    public int receivedAmount() {
        return this.receivedAmount;
    }

    private static InetAddress newAddress(byte[] bytes, int scopeId) throws UnknownHostException {
        if (bytes.length == 4) {
            return InetAddress.getByAddress(bytes);
        }
        return Inet6Address.getByAddress((String) null, bytes, scopeId);
    }
}
