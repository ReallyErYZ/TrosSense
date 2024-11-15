package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.io.Serializable;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class ImmutableMapKeySet<K, V> extends ImmutableSet.Indexed<K> {
    private final ImmutableMap<K, V> map;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ImmutableMapKeySet(ImmutableMap<K, V> map) {
        this.map = map;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.map.size();
    }

    @Override // com.google.common.collect.ImmutableSet.Indexed, com.google.common.collect.ImmutableSet, com.google.common.collect.ImmutableCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set, java.util.NavigableSet, com.google.common.collect.SortedIterable
    public UnmodifiableIterator<K> iterator() {
        return this.map.keyIterator();
    }

    @Override // com.google.common.collect.ImmutableSet.Indexed, com.google.common.collect.ImmutableCollection, java.util.Collection, java.lang.Iterable
    public Spliterator<K> spliterator() {
        return this.map.keySpliterator();
    }

    @Override // com.google.common.collect.ImmutableCollection, java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean contains(@Nullable Object object) {
        return this.map.containsKey(object);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableSet.Indexed
    public K get(int index) {
        return this.map.entrySet().asList().get(index).getKey();
    }

    @Override // com.google.common.collect.ImmutableSet.Indexed, java.lang.Iterable
    public void forEach(final Consumer<? super K> action) {
        Preconditions.checkNotNull(action);
        this.map.forEach(new BiConsumer() { // from class: com.google.common.collect.ImmutableMapKeySet$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                action.accept(obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableCollection
    public boolean isPartialView() {
        return true;
    }

    @Override // com.google.common.collect.ImmutableSet, com.google.common.collect.ImmutableCollection
    Object writeReplace() {
        return new KeySetSerializedForm(this.map);
    }

    /* loaded from: classes.dex */
    private static class KeySetSerializedForm<K> implements Serializable {
        private static final long serialVersionUID = 0;
        final ImmutableMap<K, ?> map;

        KeySetSerializedForm(ImmutableMap<K, ?> map) {
            this.map = map;
        }

        Object readResolve() {
            return this.map.keySet();
        }
    }
}
