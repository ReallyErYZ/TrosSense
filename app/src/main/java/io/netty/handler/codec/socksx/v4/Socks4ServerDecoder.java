package io.netty.handler.codec.socksx.v4;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.socksx.SocksVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.util.List;

/* loaded from: classes4.dex */
public class Socks4ServerDecoder extends ReplayingDecoder<State> {
    private static final int MAX_FIELD_LENGTH = 255;
    private String dstAddr;
    private int dstPort;
    private Socks4CommandType type;
    private String userId;

    /* loaded from: classes4.dex */
    public enum State {
        START,
        READ_USERID,
        READ_DOMAIN,
        SUCCESS,
        FAILURE
    }

    public Socks4ServerDecoder() {
        super(State.START);
        setSingleDecode(true);
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:3:0x000e. Please report as an issue. */
    @Override // io.netty.handler.codec.ByteToMessageDecoder
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            switch (state()) {
                case START:
                    int version = in.readUnsignedByte();
                    if (version != SocksVersion.SOCKS4a.byteValue()) {
                        throw new DecoderException("unsupported protocol version: " + version);
                    }
                    this.type = Socks4CommandType.valueOf(in.readByte());
                    this.dstPort = ByteBufUtil.readUnsignedShortBE(in);
                    this.dstAddr = NetUtil.intToIpAddress(ByteBufUtil.readIntBE(in));
                    checkpoint(State.READ_USERID);
                case READ_USERID:
                    this.userId = readString("userid", in);
                    checkpoint(State.READ_DOMAIN);
                case READ_DOMAIN:
                    if (!"0.0.0.0".equals(this.dstAddr) && this.dstAddr.startsWith("0.0.0.")) {
                        this.dstAddr = readString("dstAddr", in);
                    }
                    out.add(new DefaultSocks4CommandRequest(this.type, this.dstAddr, this.dstPort, this.userId));
                    checkpoint(State.SUCCESS);
                    break;
                case SUCCESS:
                    int readableBytes = actualReadableBytes();
                    if (readableBytes > 0) {
                        out.add(in.readRetainedSlice(readableBytes));
                        return;
                    }
                    return;
                case FAILURE:
                    in.skipBytes(actualReadableBytes());
                    return;
                default:
                    return;
            }
        } catch (Exception e) {
            fail(out, e);
        }
    }

    private void fail(List<Object> out, Exception cause) {
        if (!(cause instanceof DecoderException)) {
            cause = new DecoderException(cause);
        }
        Socks4CommandRequest m = new DefaultSocks4CommandRequest(this.type != null ? this.type : Socks4CommandType.CONNECT, this.dstAddr != null ? this.dstAddr : "", this.dstPort != 0 ? this.dstPort : 65535, this.userId != null ? this.userId : "");
        m.setDecoderResult(DecoderResult.failure(cause));
        out.add(m);
        checkpoint(State.FAILURE);
    }

    private static String readString(String fieldName, ByteBuf in) {
        int length = in.bytesBefore(256, (byte) 0);
        if (length < 0) {
            throw new DecoderException("field '" + fieldName + "' longer than 255 chars");
        }
        String value = in.readSlice(length).toString(CharsetUtil.US_ASCII);
        in.skipBytes(1);
        return value;
    }
}
