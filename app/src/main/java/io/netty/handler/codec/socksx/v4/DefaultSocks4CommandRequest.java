package io.netty.handler.codec.socksx.v4;

import io.netty.handler.codec.DecoderResult;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import java.net.IDN;

/* loaded from: classes4.dex */
public class DefaultSocks4CommandRequest extends AbstractSocks4Message implements Socks4CommandRequest {
    private final String dstAddr;
    private final int dstPort;
    private final Socks4CommandType type;
    private final String userId;

    public DefaultSocks4CommandRequest(Socks4CommandType type, String dstAddr, int dstPort) {
        this(type, dstAddr, dstPort, "");
    }

    public DefaultSocks4CommandRequest(Socks4CommandType type, String dstAddr, int dstPort, String userId) {
        if (dstPort <= 0 || dstPort >= 65536) {
            throw new IllegalArgumentException("dstPort: " + dstPort + " (expected: 1~65535)");
        }
        this.type = (Socks4CommandType) ObjectUtil.checkNotNull(type, "type");
        this.dstAddr = IDN.toASCII((String) ObjectUtil.checkNotNull(dstAddr, "dstAddr"));
        this.userId = (String) ObjectUtil.checkNotNull(userId, "userId");
        this.dstPort = dstPort;
    }

    @Override // io.netty.handler.codec.socksx.v4.Socks4CommandRequest
    public Socks4CommandType type() {
        return this.type;
    }

    @Override // io.netty.handler.codec.socksx.v4.Socks4CommandRequest
    public String dstAddr() {
        return this.dstAddr;
    }

    @Override // io.netty.handler.codec.socksx.v4.Socks4CommandRequest
    public int dstPort() {
        return this.dstPort;
    }

    @Override // io.netty.handler.codec.socksx.v4.Socks4CommandRequest
    public String userId() {
        return this.userId;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(128);
        buf.append(StringUtil.simpleClassName(this));
        DecoderResult decoderResult = decoderResult();
        if (!decoderResult.isSuccess()) {
            buf.append("(decoderResult: ");
            buf.append(decoderResult);
            buf.append(", type: ");
        } else {
            buf.append("(type: ");
        }
        buf.append(type());
        buf.append(", dstAddr: ");
        buf.append(dstAddr());
        buf.append(", dstPort: ");
        buf.append(dstPort());
        buf.append(", userId: ");
        buf.append(userId());
        buf.append(')');
        return buf.toString();
    }
}
