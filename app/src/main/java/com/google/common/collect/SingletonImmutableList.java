package com.google.common.collect;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.Spliterator;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public final class SingletonImmutableList<E> extends ImmutableList<E> {
    final transient E element;

    /* JADX INFO: Access modifiers changed from: package-private */
    public SingletonImmutableList(E e) {
        this.element = (E) Preconditions.checkNotNull(e);
    }

    @Override // java.util.List
    public E get(int index) {
        Preconditions.checkElementIndex(index, 1);
        return this.element;
    }

    @Override // com.google.common.collect.ImmutableList, com.google.common.collect.ImmutableCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set, java.util.NavigableSet, com.google.common.collect.SortedIterable
    public UnmodifiableIterator<E> iterator() {
        return Iterators.singletonIterator(this.element);
    }

    @Override // com.google.common.collect.ImmutableList, com.google.common.collect.ImmutableCollection, java.util.Collection, java.lang.Iterable
    public Spliterator<E> spliterator() {
        return Collections.singleton(this.element).spliterator();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return 1;
    }

    @Override // com.google.common.collect.ImmutableList, java.util.List
    public ImmutableList<E> subList(int fromIndex, int toIndex) {
        Preconditions.checkPositionIndexes(fromIndex, toIndex, 1);
        return fromIndex == toIndex ? ImmutableList.of() : this;
    }

    @Override // java.util.AbstractCollection
    public String toString() {
        return '[' + this.element.toString() + ']';
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.common.collect.ImmutableCollection
    public boolean isPartialView() {
        return false;
    }
}