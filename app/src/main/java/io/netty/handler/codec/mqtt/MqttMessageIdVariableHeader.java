package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

/* loaded from: classes4.dex */
public class MqttMessageIdVariableHeader {
    private final int messageId;

    public static MqttMessageIdVariableHeader from(int messageId) {
        if (messageId < 1 || messageId > 65535) {
            throw new IllegalArgumentException("messageId: " + messageId + " (expected: 1 ~ 65535)");
        }
        return new MqttMessageIdVariableHeader(messageId);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public MqttMessageIdVariableHeader(int messageId) {
        this.messageId = messageId;
    }

    public int messageId() {
        return this.messageId;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "[messageId=" + this.messageId + ']';
    }

    public MqttMessageIdAndPropertiesVariableHeader withEmptyProperties() {
        return new MqttMessageIdAndPropertiesVariableHeader(this.messageId, MqttProperties.NO_PROPERTIES);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public MqttMessageIdAndPropertiesVariableHeader withDefaultEmptyProperties() {
        return withEmptyProperties();
    }
}
