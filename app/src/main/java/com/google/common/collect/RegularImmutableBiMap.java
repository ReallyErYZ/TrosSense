package com.google.common.collect;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.Serializable;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class RegularImmutableBiMap<K, V> extends ImmutableBiMap<K, V> {
    static final RegularImmutableBiMap<Object, Object> EMPTY = new RegularImmutableBiMap<>(null, null, ImmutableMap.EMPTY_ENTRY_ARRAY, 0, 0);
    static final double MAX_LOAD_FACTOR = 1.2d;
    private final transient Map.Entry<K, V>[] entries;
    private final transient int hashCode;

    @LazyInit
    private transient ImmutableBiMap<V, K> inverse;
    private final transient ImmutableMapEntry<K, V>[] keyTable;
    private final transient int mask;
    private final transient ImmutableMapEntry<K, V>[] valueTable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> RegularImmutableBiMap<K, V> fromEntries(Map.Entry<K, V>... entries) {
        return fromEntryArray(entries.length, entries);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static <K, V> RegularImmutableBiMap<K, V> fromEntryArray(int n, Map.Entry<K, V>[] entryArray) {
        Map.Entry<K, V>[] entries;
        int tableSize;
        ImmutableMapEntry<K, V> newEntry;
        int i = n;
        Map.Entry<K, V>[] entryArr = entryArray;
        Preconditions.checkPositionIndex(i, entryArr.length);
        int tableSize2 = Hashing.closedTableSize(i, MAX_LOAD_FACTOR);
        int mask = tableSize2 - 1;
        ImmutableMapEntry<K, V>[] keyTable = ImmutableMapEntry.createEntryArray(tableSize2);
        ImmutableMapEntry<K, V>[] valueTable = ImmutableMapEntry.createEntryArray(tableSize2);
        if (i == entryArr.length) {
            entries = entryArray;
        } else {
            Map.Entry<K, V>[] entries2 = ImmutableMapEntry.createEntryArray(n);
            entries = entries2;
        }
        int i2 = 0;
        int hashCode = 0;
        while (i2 < i) {
            Map.Entry<K, V> entry = entryArr[i2];
            K key = entry.getKey();
            V value = entry.getValue();
            CollectPreconditions.checkEntryNotNull(key, value);
            int keyHash = key.hashCode();
            int valueHash = value.hashCode();
            int keyBucket = Hashing.smear(keyHash) & mask;
            int valueBucket = Hashing.smear(valueHash) & mask;
            ImmutableMapEntry<K, V> nextInKeyBucket = keyTable[keyBucket];
            RegularImmutableMap.checkNoConflictInKeyBucket(key, entry, nextInKeyBucket);
            ImmutableMapEntry<K, V> nextInValueBucket = valueTable[valueBucket];
            checkNoConflictInValueBucket(value, entry, nextInValueBucket);
            if (nextInValueBucket != null || nextInKeyBucket != null) {
                tableSize = tableSize2;
                newEntry = new ImmutableMapEntry.NonTerminalImmutableBiMapEntry<>(key, value, nextInKeyBucket, nextInValueBucket);
            } else {
                tableSize = tableSize2;
                boolean reusable = (entry instanceof ImmutableMapEntry) && ((ImmutableMapEntry) entry).isReusable();
                newEntry = reusable ? (ImmutableMapEntry) entry : new ImmutableMapEntry<>(key, value);
            }
            keyTable[keyBucket] = newEntry;
            valueTable[valueBucket] = newEntry;
            entries[i2] = newEntry;
            hashCode += keyHash ^ valueHash;
            i2++;
            i = n;
            entryArr = entryArray;
            tableSize2 = tableSize;
        }
        return new RegularImmutableBiMap<>(keyTable, valueTable, entries, mask, hashCode);
    }

    private RegularImmutableBiMap(ImmutableMapEntry<K, V>[] keyTable, ImmutableMapEntry<K, V>[] valueTable, Map.Entry<K, V>[] entries, int mask, int hashCode) {
        this.keyTable = keyTable;
        this.valueTable = valueTable;
        this.entries = entries;
        this.mask = mask;
        this.hashCode = hashCode;
    }

    private static void checkNoConflictInValueBucket(Object value, Map.Entry<?, ?> entry, @Nullable ImmutableMapEntry<?, ?> valueBucketHead) {
        while (valueBucketHead != null) {
            checkNoConflict(!value.equals(valueBucketHead.getValue()), "value", entry, valueBucketHead);
            valueBucketHead = valueBucketHead.getNextInValueBucket();
        }
    }

    @Override // com.google.common.collect.ImmutableMap, java.util.Map
    @Nullable
    public V get(@Nullable Object obj) {
        if (this.keyTable == null) {
            return null;
        }
        return (V) RegularImmutableMap.get(obj, this.keyTable, this.mask);
    }

    @Override // com.google.common.collect.ImmutableMap
    ImmutableSet<Map.Entry<K, V>> createEntrySet() {
        return isEmpty() ? ImmutableSet.of() : new ImmutableMapEntrySet.RegularEntrySet(this, this.entries);
    }

    @Override // java.util.Map
    public void forEach(BiConsumer<? super K, ? super V> biConsumer) {
        Preconditions.checkNotNull(biConsumer);
        for (Map.Entry<K, V> entry : this.entries) {
            biConsumer.accept(entry.getKey(), entry.getValue());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableMap
    public boolean isHashCodeFast() {
        return true;
    }

    @Override // com.google.common.collect.ImmutableMap, java.util.Map
    public int hashCode() {
        return this.hashCode;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableMap
    public boolean isPartialView() {
        return false;
    }

    @Override // java.util.Map
    public int size() {
        return this.entries.length;
    }

    @Override // com.google.common.collect.ImmutableBiMap, com.google.common.collect.BiMap
    public ImmutableBiMap<V, K> inverse() {
        if (isEmpty()) {
            return ImmutableBiMap.of();
        }
        ImmutableBiMap<V, K> result = this.inverse;
        if (result != null) {
            return result;
        }
        Inverse inverse = new Inverse();
        this.inverse = inverse;
        return inverse;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class Inverse extends ImmutableBiMap<V, K> {
        private Inverse() {
        }

        @Override // java.util.Map
        public int size() {
            return inverse().size();
        }

        @Override // com.google.common.collect.ImmutableBiMap, com.google.common.collect.BiMap
        public ImmutableBiMap<K, V> inverse() {
            return RegularImmutableBiMap.this;
        }

        @Override // java.util.Map
        public void forEach(final BiConsumer<? super V, ? super K> action) {
            Preconditions.checkNotNull(action);
            RegularImmutableBiMap.this.forEach(new BiConsumer() { // from class: com.google.common.collect.RegularImmutableBiMap$Inverse$$ExternalSyntheticLambda0
                @Override // java.util.function.BiConsumer
                public final void accept(Object obj, Object obj2) {
                    action.accept(obj2, obj);
                }
            });
        }

        @Override // com.google.common.collect.ImmutableMap, java.util.Map
        public K get(@Nullable Object value) {
            if (value != null && RegularImmutableBiMap.this.valueTable != null) {
                int bucket = Hashing.smear(value.hashCode()) & RegularImmutableBiMap.this.mask;
                for (ImmutableMapEntry<K, V> entry = RegularImmutableBiMap.this.valueTable[bucket]; entry != null; entry = entry.getNextInValueBucket()) {
                    if (value.equals(entry.getValue())) {
                        return entry.getKey();
                    }
                }
                return null;
            }
            return null;
        }

        @Override // com.google.common.collect.ImmutableMap
        ImmutableSet<Map.Entry<V, K>> createEntrySet() {
            return new InverseEntrySet();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public final class InverseEntrySet extends ImmutableMapEntrySet<V, K> {
            InverseEntrySet() {
            }

            @Override // com.google.common.collect.ImmutableMapEntrySet
            ImmutableMap<V, K> map() {
                return Inverse.this;
            }

            @Override // com.google.common.collect.ImmutableMapEntrySet, com.google.common.collect.ImmutableSet
            boolean isHashCodeFast() {
                return true;
            }

            @Override // com.google.common.collect.ImmutableMapEntrySet, com.google.common.collect.ImmutableSet, java.util.Collection, java.util.Set
            public int hashCode() {
                return RegularImmutableBiMap.this.hashCode;
            }

            @Override // com.google.common.collect.ImmutableSet, com.google.common.collect.ImmutableCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set, java.util.NavigableSet, com.google.common.collect.SortedIterable
            public UnmodifiableIterator<Map.Entry<V, K>> iterator() {
                return asList().iterator();
            }

            @Override // java.lang.Iterable
            public void forEach(Consumer<? super Map.Entry<V, K>> action) {
                asList().forEach(action);
            }

            @Override // com.google.common.collect.ImmutableSet
            ImmutableList<Map.Entry<V, K>> createAsList() {
                return new ImmutableAsList<Map.Entry<V, K>>() { // from class: com.google.common.collect.RegularImmutableBiMap.Inverse.InverseEntrySet.1
                    @Override // java.util.List
                    public Map.Entry<V, K> get(int index) {
                        Map.Entry<K, V> entry = RegularImmutableBiMap.this.entries[index];
                        return Maps.immutableEntry(entry.getValue(), entry.getKey());
                    }

                    @Override // com.google.common.collect.ImmutableAsList
                    ImmutableCollection<Map.Entry<V, K>> delegateCollection() {
                        return InverseEntrySet.this;
                    }
                };
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        @Override // com.google.common.collect.ImmutableMap
        public boolean isPartialView() {
            return false;
        }

        @Override // com.google.common.collect.ImmutableBiMap, com.google.common.collect.ImmutableMap
        Object writeReplace() {
            return new InverseSerializedForm(RegularImmutableBiMap.this);
        }
    }

    /* loaded from: classes.dex */
    private static class InverseSerializedForm<K, V> implements Serializable {
        private static final long serialVersionUID = 1;
        private final ImmutableBiMap<K, V> forward;

        InverseSerializedForm(ImmutableBiMap<K, V> forward) {
            this.forward = forward;
        }

        Object readResolve() {
            return this.forward.inverse();
        }
    }
}
