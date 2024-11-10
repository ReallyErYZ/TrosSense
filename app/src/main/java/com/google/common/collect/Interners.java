package com.google.common.collect;

import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import java.util.concurrent.ConcurrentMap;

/* loaded from: classes.dex */
public final class Interners {
    private Interners() {
    }

    /* loaded from: classes.dex */
    public static class InternerBuilder {
        private final MapMaker mapMaker;
        private boolean strong;

        private InternerBuilder() {
            this.mapMaker = new MapMaker();
            this.strong = true;
        }

        public InternerBuilder strong() {
            this.strong = true;
            return this;
        }

        public InternerBuilder weak() {
            this.strong = false;
            return this;
        }

        public InternerBuilder concurrencyLevel(int concurrencyLevel) {
            this.mapMaker.concurrencyLevel(concurrencyLevel);
            return this;
        }

        public <E> Interner<E> build() {
            return this.strong ? new StrongInterner(this.mapMaker) : new WeakInterner(this.mapMaker);
        }
    }

    public static InternerBuilder newBuilder() {
        return new InternerBuilder();
    }

    public static <E> Interner<E> newStrongInterner() {
        return newBuilder().strong().build();
    }

    public static <E> Interner<E> newWeakInterner() {
        return newBuilder().weak().build();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class StrongInterner<E> implements Interner<E> {
        final ConcurrentMap<E, E> map;

        private StrongInterner(MapMaker mapMaker) {
            this.map = mapMaker.makeMap();
        }

        @Override // com.google.common.collect.Interner
        public E intern(E e) {
            E e2 = (E) this.map.putIfAbsent(Preconditions.checkNotNull(e), e);
            return e2 == null ? e : e2;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class WeakInterner<E> implements Interner<E> {
        final MapMakerInternalMap<E, Dummy, ?, ?> map;

        /* loaded from: classes.dex */
        private enum Dummy {
            VALUE
        }

        private WeakInterner(MapMaker mapMaker) {
            this.map = mapMaker.weakKeys().keyEquivalence(Equivalence.equals()).makeCustomMap();
        }

        /* JADX WARN: Type inference failed for: r0v1, types: [com.google.common.collect.MapMakerInternalMap$InternalEntry] */
        @Override // com.google.common.collect.Interner
        public E intern(E e) {
            E e2;
            do {
                ?? entry = this.map.getEntry(e);
                if (entry != 0 && (e2 = (E) entry.getKey()) != null) {
                    return e2;
                }
            } while (this.map.putIfAbsent(e, Dummy.VALUE) != null);
            return e;
        }
    }

    public static <E> Function<E, E> asFunction(Interner<E> interner) {
        return new InternerFunction((Interner) Preconditions.checkNotNull(interner));
    }

    /* loaded from: classes.dex */
    private static class InternerFunction<E> implements Function<E, E> {
        private final Interner<E> interner;

        public InternerFunction(Interner<E> interner) {
            this.interner = interner;
        }

        @Override // com.google.common.base.Function, java.util.function.Function
        public E apply(E input) {
            return this.interner.intern(input);
        }

        public int hashCode() {
            return this.interner.hashCode();
        }

        @Override // com.google.common.base.Function
        public boolean equals(Object other) {
            if (other instanceof InternerFunction) {
                InternerFunction<?> that = (InternerFunction) other;
                return this.interner.equals(that.interner);
            }
            return false;
        }
    }
}