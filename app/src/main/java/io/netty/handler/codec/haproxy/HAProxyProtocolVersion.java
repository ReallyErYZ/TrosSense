package io.netty.handler.codec.haproxy;

/* loaded from: classes4.dex */
public enum HAProxyProtocolVersion {
    V1((byte) 16),
    V2((byte) 32);

    private static final byte VERSION_MASK = -16;
    private final byte byteValue;

    HAProxyProtocolVersion(byte byteValue) {
        this.byteValue = byteValue;
    }

    public static HAProxyProtocolVersion valueOf(byte verCmdByte) {
        int version = verCmdByte & VERSION_MASK;
        switch ((byte) version) {
            case 16:
                return V1;
            case 32:
                return V2;
            default:
                throw new IllegalArgumentException("unknown version: " + version);
        }
    }

    public byte byteValue() {
        return this.byteValue;
    }
}
