package io.netty.handler.codec.dns;

import io.netty.util.internal.StringUtil;

/* loaded from: classes4.dex */
public class DefaultDnsQuestion extends AbstractDnsRecord implements DnsQuestion {
    public DefaultDnsQuestion(String name, DnsRecordType type) {
        super(name, type, 0L);
    }

    public DefaultDnsQuestion(String name, DnsRecordType type, int dnsClass) {
        super(name, type, dnsClass, 0L);
    }

    @Override // io.netty.handler.codec.dns.AbstractDnsRecord
    public String toString() {
        StringBuilder buf = new StringBuilder(64);
        buf.append(StringUtil.simpleClassName(this)).append('(').append(name()).append(' ');
        DnsMessageUtil.appendRecordClass(buf, dnsClass()).append(' ').append(type().name()).append(')');
        return buf.toString();
    }
}
