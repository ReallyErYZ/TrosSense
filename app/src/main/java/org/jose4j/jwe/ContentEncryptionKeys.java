package org.jose4j.jwe;

import org.jose4j.lang.ByteUtil;

/* loaded from: classes5.dex */
public class ContentEncryptionKeys {
    private final byte[] contentEncryptionKey;
    private final byte[] encryptedKey;

    public ContentEncryptionKeys(byte[] contentEncryptionKey, byte[] encryptedKey) {
        this.contentEncryptionKey = contentEncryptionKey;
        this.encryptedKey = encryptedKey == null ? ByteUtil.EMPTY_BYTES : encryptedKey;
    }

    public byte[] getContentEncryptionKey() {
        return this.contentEncryptionKey;
    }

    public byte[] getEncryptedKey() {
        return this.encryptedKey;
    }
}
