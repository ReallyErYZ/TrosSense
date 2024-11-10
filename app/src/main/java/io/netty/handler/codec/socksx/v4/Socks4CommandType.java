package io.netty.handler.codec.socksx.v4;

import io.netty.util.internal.ObjectUtil;

/* loaded from: classes4.dex */
public class Socks4CommandType implements Comparable<Socks4CommandType> {
    private final byte byteValue;
    private final String name;
    private String text;
    public static final Socks4CommandType CONNECT = new Socks4CommandType(1, "CONNECT");
    public static final Socks4CommandType BIND = new Socks4CommandType(2, "BIND");

    public static Socks4CommandType valueOf(byte b) {
        switch (b) {
            case 1:
                return CONNECT;
            case 2:
                return BIND;
            default:
                return new Socks4CommandType(b);
        }
    }

    public Socks4CommandType(int byteValue) {
        this(byteValue, "UNKNOWN");
    }

    public Socks4CommandType(int byteValue, String name) {
        this.name = (String) ObjectUtil.checkNotNull(name, "name");
        this.byteValue = (byte) byteValue;
    }

    public byte byteValue() {
        return this.byteValue;
    }

    public int hashCode() {
        return this.byteValue;
    }

    public boolean equals(Object obj) {
        return (obj instanceof Socks4CommandType) && this.byteValue == ((Socks4CommandType) obj).byteValue;
    }

    @Override // java.lang.Comparable
    public int compareTo(Socks4CommandType o) {
        return this.byteValue - o.byteValue;
    }

    public String toString() {
        String text = this.text;
        if (text == null) {
            String text2 = this.name + '(' + (this.byteValue & 255) + ')';
            this.text = text2;
            return text2;
        }
        return text;
    }
}