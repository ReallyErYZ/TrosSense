package com.google.common.util.concurrent;

import com.google.common.util.concurrent.ForwardingListenableFuture;
import java.lang.Exception;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/* loaded from: classes.dex */
public abstract class AbstractCheckedFuture<V, X extends Exception> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V> implements CheckedFuture<V, X> {
    protected abstract X mapException(Exception exc);

    /* JADX INFO: Access modifiers changed from: protected */
    public AbstractCheckedFuture(ListenableFuture<V> delegate) {
        super(delegate);
    }

    @Override // com.google.common.util.concurrent.CheckedFuture
    public V checkedGet() throws Exception {
        try {
            return get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw mapException(e);
        } catch (CancellationException e2) {
            throw mapException(e2);
        } catch (ExecutionException e3) {
            throw mapException(e3);
        }
    }

    @Override // com.google.common.util.concurrent.CheckedFuture
    public V checkedGet(long timeout, TimeUnit unit) throws TimeoutException, Exception {
        try {
            return get(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw mapException(e);
        } catch (CancellationException e2) {
            throw mapException(e2);
        } catch (ExecutionException e3) {
            throw mapException(e3);
        }
    }
}
