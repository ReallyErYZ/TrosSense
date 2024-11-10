package io.netty.handler.codec.http.websocketx.extensions.compression;

import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionData;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionDecoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionEncoder;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketExtensionFilterProvider;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension;
import io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker;
import io.netty.util.internal.ObjectUtil;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: classes4.dex */
public final class PerMessageDeflateServerExtensionHandshaker implements WebSocketServerExtensionHandshaker {
    static final String CLIENT_MAX_WINDOW = "client_max_window_bits";
    static final String CLIENT_NO_CONTEXT = "client_no_context_takeover";
    public static final int MAX_WINDOW_SIZE = 15;
    public static final int MIN_WINDOW_SIZE = 8;
    static final String PERMESSAGE_DEFLATE_EXTENSION = "permessage-deflate";
    static final String SERVER_MAX_WINDOW = "server_max_window_bits";
    static final String SERVER_NO_CONTEXT = "server_no_context_takeover";
    private final boolean allowServerNoContext;
    private final boolean allowServerWindowSize;
    private final int compressionLevel;
    private final WebSocketExtensionFilterProvider extensionFilterProvider;
    private final boolean preferredClientNoContext;
    private final int preferredClientWindowSize;

    public PerMessageDeflateServerExtensionHandshaker() {
        this(6, ZlibCodecFactory.isSupportingWindowSizeAndMemLevel(), 15, false, false);
    }

    public PerMessageDeflateServerExtensionHandshaker(int compressionLevel, boolean allowServerWindowSize, int preferredClientWindowSize, boolean allowServerNoContext, boolean preferredClientNoContext) {
        this(compressionLevel, allowServerWindowSize, preferredClientWindowSize, allowServerNoContext, preferredClientNoContext, WebSocketExtensionFilterProvider.DEFAULT);
    }

    public PerMessageDeflateServerExtensionHandshaker(int compressionLevel, boolean allowServerWindowSize, int preferredClientWindowSize, boolean allowServerNoContext, boolean preferredClientNoContext, WebSocketExtensionFilterProvider extensionFilterProvider) {
        if (preferredClientWindowSize > 15 || preferredClientWindowSize < 8) {
            throw new IllegalArgumentException("preferredServerWindowSize: " + preferredClientWindowSize + " (expected: 8-15)");
        }
        if (compressionLevel < 0 || compressionLevel > 9) {
            throw new IllegalArgumentException("compressionLevel: " + compressionLevel + " (expected: 0-9)");
        }
        this.compressionLevel = compressionLevel;
        this.allowServerWindowSize = allowServerWindowSize;
        this.preferredClientWindowSize = preferredClientWindowSize;
        this.allowServerNoContext = allowServerNoContext;
        this.preferredClientNoContext = preferredClientNoContext;
        this.extensionFilterProvider = (WebSocketExtensionFilterProvider) ObjectUtil.checkNotNull(extensionFilterProvider, "extensionFilterProvider");
    }

    @Override // io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtensionHandshaker
    public WebSocketServerExtension handshakeExtension(WebSocketExtensionData extensionData) {
        if (!PERMESSAGE_DEFLATE_EXTENSION.equals(extensionData.name())) {
            return null;
        }
        boolean deflateEnabled = true;
        int clientWindowSize = 15;
        int serverWindowSize = 15;
        boolean serverNoContext = false;
        boolean clientNoContext = false;
        Iterator<Map.Entry<String, String>> parametersIterator = extensionData.parameters().entrySet().iterator();
        while (deflateEnabled && parametersIterator.hasNext()) {
            Map.Entry<String, String> parameter = parametersIterator.next();
            if (CLIENT_MAX_WINDOW.equalsIgnoreCase(parameter.getKey())) {
                clientWindowSize = this.preferredClientWindowSize;
            } else if (SERVER_MAX_WINDOW.equalsIgnoreCase(parameter.getKey())) {
                if (this.allowServerWindowSize) {
                    serverWindowSize = Integer.parseInt(parameter.getValue());
                    if (serverWindowSize > 15 || serverWindowSize < 8) {
                        deflateEnabled = false;
                    }
                } else {
                    deflateEnabled = false;
                }
            } else if (CLIENT_NO_CONTEXT.equalsIgnoreCase(parameter.getKey())) {
                clientNoContext = this.preferredClientNoContext;
            } else if (SERVER_NO_CONTEXT.equalsIgnoreCase(parameter.getKey())) {
                if (this.allowServerNoContext) {
                    serverNoContext = true;
                } else {
                    deflateEnabled = false;
                }
            } else {
                deflateEnabled = false;
            }
        }
        if (!deflateEnabled) {
            return null;
        }
        return new PermessageDeflateExtension(this.compressionLevel, serverNoContext, serverWindowSize, clientNoContext, clientWindowSize, this.extensionFilterProvider);
    }

    /* loaded from: classes4.dex */
    private static class PermessageDeflateExtension implements WebSocketServerExtension {
        private final boolean clientNoContext;
        private final int clientWindowSize;
        private final int compressionLevel;
        private final WebSocketExtensionFilterProvider extensionFilterProvider;
        private final boolean serverNoContext;
        private final int serverWindowSize;

        PermessageDeflateExtension(int compressionLevel, boolean serverNoContext, int serverWindowSize, boolean clientNoContext, int clientWindowSize, WebSocketExtensionFilterProvider extensionFilterProvider) {
            this.compressionLevel = compressionLevel;
            this.serverNoContext = serverNoContext;
            this.serverWindowSize = serverWindowSize;
            this.clientNoContext = clientNoContext;
            this.clientWindowSize = clientWindowSize;
            this.extensionFilterProvider = extensionFilterProvider;
        }

        @Override // io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension
        public int rsv() {
            return 4;
        }

        @Override // io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension
        public WebSocketExtensionEncoder newExtensionEncoder() {
            return new PerMessageDeflateEncoder(this.compressionLevel, this.serverWindowSize, this.serverNoContext, this.extensionFilterProvider.encoderFilter());
        }

        @Override // io.netty.handler.codec.http.websocketx.extensions.WebSocketExtension
        public WebSocketExtensionDecoder newExtensionDecoder() {
            return new PerMessageDeflateDecoder(this.clientNoContext, this.extensionFilterProvider.decoderFilter());
        }

        @Override // io.netty.handler.codec.http.websocketx.extensions.WebSocketServerExtension
        public WebSocketExtensionData newReponseData() {
            HashMap<String, String> parameters = new HashMap<>(4);
            if (this.serverNoContext) {
                parameters.put(PerMessageDeflateServerExtensionHandshaker.SERVER_NO_CONTEXT, null);
            }
            if (this.clientNoContext) {
                parameters.put(PerMessageDeflateServerExtensionHandshaker.CLIENT_NO_CONTEXT, null);
            }
            if (this.serverWindowSize != 15) {
                parameters.put(PerMessageDeflateServerExtensionHandshaker.SERVER_MAX_WINDOW, Integer.toString(this.serverWindowSize));
            }
            if (this.clientWindowSize != 15) {
                parameters.put(PerMessageDeflateServerExtensionHandshaker.CLIENT_MAX_WINDOW, Integer.toString(this.clientWindowSize));
            }
            return new WebSocketExtensionData(PerMessageDeflateServerExtensionHandshaker.PERMESSAGE_DEFLATE_EXTENSION, parameters);
        }
    }
}