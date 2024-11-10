package io.netty.handler.ssl;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.internal.tcnative.SSL;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.KeyStoreSpi;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.KeyManagerFactorySpi;
import javax.net.ssl.ManagerFactoryParameters;
import javax.net.ssl.X509KeyManager;

/* loaded from: classes4.dex */
public final class OpenSslX509KeyManagerFactory extends KeyManagerFactory {
    private final OpenSslKeyManagerFactorySpi spi;

    public OpenSslX509KeyManagerFactory() {
        this(newOpenSslKeyManagerFactorySpi(null));
    }

    public OpenSslX509KeyManagerFactory(Provider provider) {
        this(newOpenSslKeyManagerFactorySpi(provider));
    }

    public OpenSslX509KeyManagerFactory(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        this(newOpenSslKeyManagerFactorySpi(algorithm, provider));
    }

    private OpenSslX509KeyManagerFactory(OpenSslKeyManagerFactorySpi spi) {
        super(spi, spi.kmf.getProvider(), spi.kmf.getAlgorithm());
        this.spi = spi;
    }

    private static OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(Provider provider) {
        try {
            return newOpenSslKeyManagerFactorySpi(null, provider);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static OpenSslKeyManagerFactorySpi newOpenSslKeyManagerFactorySpi(String algorithm, Provider provider) throws NoSuchAlgorithmException {
        KeyManagerFactory keyManagerFactory;
        if (algorithm == null) {
            algorithm = KeyManagerFactory.getDefaultAlgorithm();
        }
        if (provider == null) {
            keyManagerFactory = KeyManagerFactory.getInstance(algorithm);
        } else {
            keyManagerFactory = KeyManagerFactory.getInstance(algorithm, provider);
        }
        return new OpenSslKeyManagerFactorySpi(keyManagerFactory);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public OpenSslKeyMaterialProvider newProvider() {
        return this.spi.newProvider();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class OpenSslKeyManagerFactorySpi extends KeyManagerFactorySpi {
        final KeyManagerFactory kmf;
        private volatile ProviderFactory providerFactory;

        OpenSslKeyManagerFactorySpi(KeyManagerFactory kmf) {
            this.kmf = (KeyManagerFactory) ObjectUtil.checkNotNull(kmf, "kmf");
        }

        @Override // javax.net.ssl.KeyManagerFactorySpi
        protected synchronized void engineInit(KeyStore keyStore, char[] chars) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
            if (this.providerFactory != null) {
                throw new KeyStoreException("Already initialized");
            }
            if (!keyStore.aliases().hasMoreElements()) {
                throw new KeyStoreException("No aliases found");
            }
            this.kmf.init(keyStore, chars);
            this.providerFactory = new ProviderFactory(ReferenceCountedOpenSslContext.chooseX509KeyManager(this.kmf.getKeyManagers()), password(chars), Collections.list(keyStore.aliases()));
        }

        private static String password(char[] password) {
            if (password == null || password.length == 0) {
                return null;
            }
            return new String(password);
        }

        @Override // javax.net.ssl.KeyManagerFactorySpi
        protected void engineInit(ManagerFactoryParameters managerFactoryParameters) throws InvalidAlgorithmParameterException {
            throw new InvalidAlgorithmParameterException("Not supported");
        }

        @Override // javax.net.ssl.KeyManagerFactorySpi
        protected KeyManager[] engineGetKeyManagers() {
            ProviderFactory providerFactory = this.providerFactory;
            if (providerFactory == null) {
                throw new IllegalStateException("engineInit(...) not called yet");
            }
            return new KeyManager[]{providerFactory.keyManager};
        }

        OpenSslKeyMaterialProvider newProvider() {
            ProviderFactory providerFactory = this.providerFactory;
            if (providerFactory == null) {
                throw new IllegalStateException("engineInit(...) not called yet");
            }
            return providerFactory.newProvider();
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static final class ProviderFactory {
            private final Iterable<String> aliases;
            private final X509KeyManager keyManager;
            private final String password;

            ProviderFactory(X509KeyManager keyManager, String password, Iterable<String> aliases) {
                this.keyManager = keyManager;
                this.password = password;
                this.aliases = aliases;
            }

            OpenSslKeyMaterialProvider newProvider() {
                return new OpenSslPopulatedKeyMaterialProvider(this.keyManager, this.password, this.aliases);
            }

            /* JADX INFO: Access modifiers changed from: private */
            /* loaded from: classes4.dex */
            public static final class OpenSslPopulatedKeyMaterialProvider extends OpenSslKeyMaterialProvider {
                private final Map<String, Object> materialMap;

                OpenSslPopulatedKeyMaterialProvider(X509KeyManager keyManager, String password, Iterable<String> aliases) {
                    super(keyManager, password);
                    this.materialMap = new HashMap();
                    boolean initComplete = false;
                    try {
                        for (String alias : aliases) {
                            if (alias != null && !this.materialMap.containsKey(alias)) {
                                try {
                                    this.materialMap.put(alias, super.chooseKeyMaterial(UnpooledByteBufAllocator.DEFAULT, alias));
                                } catch (Exception e) {
                                    this.materialMap.put(alias, e);
                                }
                            }
                        }
                        initComplete = true;
                        ObjectUtil.checkNonEmpty(this.materialMap, "materialMap");
                    } finally {
                        if (!initComplete) {
                            destroy();
                        }
                    }
                }

                /* JADX INFO: Access modifiers changed from: package-private */
                @Override // io.netty.handler.ssl.OpenSslKeyMaterialProvider
                public OpenSslKeyMaterial chooseKeyMaterial(ByteBufAllocator allocator, String alias) throws Exception {
                    Object value = this.materialMap.get(alias);
                    if (value == null) {
                        return null;
                    }
                    if (value instanceof OpenSslKeyMaterial) {
                        return ((OpenSslKeyMaterial) value).retain();
                    }
                    throw ((Exception) value);
                }

                /* JADX INFO: Access modifiers changed from: package-private */
                @Override // io.netty.handler.ssl.OpenSslKeyMaterialProvider
                public void destroy() {
                    for (Object material : this.materialMap.values()) {
                        ReferenceCountUtil.release(material);
                    }
                    this.materialMap.clear();
                }
            }
        }
    }

    public static OpenSslX509KeyManagerFactory newEngineBased(File certificateChain, String password) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return newEngineBased(SslContext.toX509Certificates(certificateChain), password);
    }

    public static OpenSslX509KeyManagerFactory newEngineBased(X509Certificate[] x509CertificateArr, String str) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        ObjectUtil.checkNotNull(x509CertificateArr, "certificateChain");
        OpenSslKeyStore openSslKeyStore = new OpenSslKeyStore((X509Certificate[]) x509CertificateArr.clone(), false);
        openSslKeyStore.load(null, null);
        OpenSslX509KeyManagerFactory openSslX509KeyManagerFactory = new OpenSslX509KeyManagerFactory();
        openSslX509KeyManagerFactory.init(openSslKeyStore, str != null ? str.toCharArray() : null);
        return openSslX509KeyManagerFactory;
    }

    public static OpenSslX509KeyManagerFactory newKeyless(File chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return newKeyless(SslContext.toX509Certificates(chain));
    }

    public static OpenSslX509KeyManagerFactory newKeyless(InputStream chain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        return newKeyless(SslContext.toX509Certificates(chain));
    }

    public static OpenSslX509KeyManagerFactory newKeyless(X509Certificate... certificateChain) throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
        ObjectUtil.checkNotNull(certificateChain, "certificateChain");
        KeyStore store = new OpenSslKeyStore((X509Certificate[]) certificateChain.clone(), true);
        store.load(null, null);
        OpenSslX509KeyManagerFactory factory = new OpenSslX509KeyManagerFactory();
        factory.init(store, null);
        return factory;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class OpenSslKeyStore extends KeyStore {
        private OpenSslKeyStore(final X509Certificate[] certificateChain, final boolean keyless) {
            super(new KeyStoreSpi() { // from class: io.netty.handler.ssl.OpenSslX509KeyManagerFactory.OpenSslKeyStore.1
                private final Date creationDate = new Date();

                @Override // java.security.KeyStoreSpi
                public Key engineGetKey(String alias, char[] password) throws UnrecoverableKeyException {
                    long privateKeyAddress;
                    String str = null;
                    if (!engineContainsAlias(alias)) {
                        return null;
                    }
                    if (keyless) {
                        privateKeyAddress = 0;
                    } else {
                        if (password != null) {
                            try {
                                str = new String(password);
                            } catch (Exception e) {
                                UnrecoverableKeyException keyException = new UnrecoverableKeyException("Unable to load key from engine");
                                keyException.initCause(e);
                                throw keyException;
                            }
                        }
                        privateKeyAddress = SSL.loadPrivateKeyFromEngine(alias, str);
                    }
                    return new OpenSslPrivateKey(privateKeyAddress);
                }

                @Override // java.security.KeyStoreSpi
                public Certificate[] engineGetCertificateChain(String alias) {
                    if (engineContainsAlias(alias)) {
                        return (X509Certificate[]) certificateChain.clone();
                    }
                    return null;
                }

                @Override // java.security.KeyStoreSpi
                public Certificate engineGetCertificate(String alias) {
                    if (engineContainsAlias(alias)) {
                        return certificateChain[0];
                    }
                    return null;
                }

                @Override // java.security.KeyStoreSpi
                public Date engineGetCreationDate(String alias) {
                    if (engineContainsAlias(alias)) {
                        return this.creationDate;
                    }
                    return null;
                }

                @Override // java.security.KeyStoreSpi
                public void engineSetKeyEntry(String alias, Key key, char[] password, Certificate[] chain) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }

                @Override // java.security.KeyStoreSpi
                public void engineSetKeyEntry(String alias, byte[] key, Certificate[] chain) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }

                @Override // java.security.KeyStoreSpi
                public void engineSetCertificateEntry(String alias, Certificate cert) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }

                @Override // java.security.KeyStoreSpi
                public void engineDeleteEntry(String alias) throws KeyStoreException {
                    throw new KeyStoreException("Not supported");
                }

                @Override // java.security.KeyStoreSpi
                public Enumeration<String> engineAliases() {
                    return Collections.enumeration(Collections.singleton("key"));
                }

                @Override // java.security.KeyStoreSpi
                public boolean engineContainsAlias(String alias) {
                    return "key".equals(alias);
                }

                @Override // java.security.KeyStoreSpi
                public int engineSize() {
                    return 1;
                }

                @Override // java.security.KeyStoreSpi
                public boolean engineIsKeyEntry(String alias) {
                    return engineContainsAlias(alias);
                }

                @Override // java.security.KeyStoreSpi
                public boolean engineIsCertificateEntry(String alias) {
                    return engineContainsAlias(alias);
                }

                @Override // java.security.KeyStoreSpi
                public String engineGetCertificateAlias(Certificate cert) {
                    if (cert instanceof X509Certificate) {
                        for (X509Certificate x509Certificate : certificateChain) {
                            if (x509Certificate.equals(cert)) {
                                return "key";
                            }
                        }
                        return null;
                    }
                    return null;
                }

                @Override // java.security.KeyStoreSpi
                public void engineStore(OutputStream stream, char[] password) {
                    throw new UnsupportedOperationException();
                }

                @Override // java.security.KeyStoreSpi
                public void engineLoad(InputStream stream, char[] password) {
                    if (stream != null && password != null) {
                        throw new UnsupportedOperationException();
                    }
                }
            }, null, "native");
            OpenSsl.ensureAvailability();
        }
    }
}