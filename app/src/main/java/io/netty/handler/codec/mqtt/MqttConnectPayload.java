package io.netty.handler.codec.mqtt;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;
import java.util.Arrays;

/* loaded from: classes4.dex */
public final class MqttConnectPayload {
    private final String clientIdentifier;
    private final byte[] password;
    private final String userName;
    private final byte[] willMessage;
    private final MqttProperties willProperties;
    private final String willTopic;

    @Deprecated
    public MqttConnectPayload(String clientIdentifier, String willTopic, String willMessage, String userName, String password) {
        this(clientIdentifier, MqttProperties.NO_PROPERTIES, willTopic, willMessage.getBytes(CharsetUtil.UTF_8), userName, password.getBytes(CharsetUtil.UTF_8));
    }

    public MqttConnectPayload(String clientIdentifier, String willTopic, byte[] willMessage, String userName, byte[] password) {
        this(clientIdentifier, MqttProperties.NO_PROPERTIES, willTopic, willMessage, userName, password);
    }

    public MqttConnectPayload(String clientIdentifier, MqttProperties willProperties, String willTopic, byte[] willMessage, String userName, byte[] password) {
        this.clientIdentifier = clientIdentifier;
        this.willProperties = MqttProperties.withEmptyDefaults(willProperties);
        this.willTopic = willTopic;
        this.willMessage = willMessage;
        this.userName = userName;
        this.password = password;
    }

    public String clientIdentifier() {
        return this.clientIdentifier;
    }

    public MqttProperties willProperties() {
        return this.willProperties;
    }

    public String willTopic() {
        return this.willTopic;
    }

    @Deprecated
    public String willMessage() {
        if (this.willMessage == null) {
            return null;
        }
        return new String(this.willMessage, CharsetUtil.UTF_8);
    }

    public byte[] willMessageInBytes() {
        return this.willMessage;
    }

    public String userName() {
        return this.userName;
    }

    @Deprecated
    public String password() {
        if (this.password == null) {
            return null;
        }
        return new String(this.password, CharsetUtil.UTF_8);
    }

    public byte[] passwordInBytes() {
        return this.password;
    }

    public String toString() {
        return StringUtil.simpleClassName(this) + "[clientIdentifier=" + this.clientIdentifier + ", willTopic=" + this.willTopic + ", willMessage=" + Arrays.toString(this.willMessage) + ", userName=" + this.userName + ", password=" + Arrays.toString(this.password) + ']';
    }
}
