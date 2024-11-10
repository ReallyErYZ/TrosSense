package com.google.common.util.concurrent;

import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class UncheckedExecutionException extends RuntimeException {
    private static final long serialVersionUID = 0;

    protected UncheckedExecutionException() {
    }

    protected UncheckedExecutionException(@Nullable String message) {
        super(message);
    }

    public UncheckedExecutionException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public UncheckedExecutionException(@Nullable Throwable cause) {
        super(cause);
    }
}