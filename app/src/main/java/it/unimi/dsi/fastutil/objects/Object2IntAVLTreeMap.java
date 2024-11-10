package it.unimi.dsi.fastutil.objects;

import androidx.core.view.InputDeviceCompat;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntListIterator;
import it.unimi.dsi.fastutil.objects.AbstractObject2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;

/* loaded from: classes4.dex */
public class Object2IntAVLTreeMap<K> extends AbstractObject2IntSortedMap<K> implements Serializable, Cloneable {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    private static final long serialVersionUID = -7046029254386353129L;
    protected transient Comparator<? super K> actualComparator;
    protected int count;
    private transient boolean[] dirPath;
    protected transient ObjectSortedSet<Object2IntMap.Entry<K>> entries;
    protected transient Entry<K> firstEntry;
    protected transient ObjectSortedSet<K> keys;
    protected transient Entry<K> lastEntry;
    protected transient boolean modified;
    protected Comparator<? super K> storedComparator;
    protected transient Entry<K> tree;
    protected transient IntCollection values;

    /* JADX WARN: Multi-variable type inference failed */
    @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
    public /* bridge */ /* synthetic */ SortedMap headMap(Object obj) {
        return headMap((Object2IntAVLTreeMap<K>) obj);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
    public /* bridge */ /* synthetic */ SortedMap tailMap(Object obj) {
        return tailMap((Object2IntAVLTreeMap<K>) obj);
    }

    public Object2IntAVLTreeMap() {
        allocatePaths();
        this.tree = null;
        this.count = 0;
    }

    private void setActualComparator() {
        this.actualComparator = this.storedComparator;
    }

    public Object2IntAVLTreeMap(Comparator<? super K> c) {
        this();
        this.storedComparator = c;
        setActualComparator();
    }

    public Object2IntAVLTreeMap(Map<? extends K, ? extends Integer> m) {
        this();
        putAll(m);
    }

    public Object2IntAVLTreeMap(SortedMap<K, Integer> m) {
        this(m.comparator());
        putAll(m);
    }

    public Object2IntAVLTreeMap(Object2IntMap<? extends K> m) {
        this();
        putAll(m);
    }

    public Object2IntAVLTreeMap(Object2IntSortedMap<K> m) {
        this(m.comparator());
        putAll(m);
    }

    public Object2IntAVLTreeMap(K[] k, int[] v, Comparator<? super K> c) {
        this(c);
        if (k.length != v.length) {
            throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
        }
        for (int i = 0; i < k.length; i++) {
            put((Object2IntAVLTreeMap<K>) k[i], v[i]);
        }
    }

    public Object2IntAVLTreeMap(K[] k, int[] v) {
        this(k, v, null);
    }

    final int compare(K k1, K k2) {
        return this.actualComparator == null ? ((Comparable) k1).compareTo(k2) : this.actualComparator.compare(k1, k2);
    }

    final Entry<K> findKey(K k) {
        Entry<K> e = this.tree;
        while (e != null) {
            int cmp = compare(k, e.key);
            if (cmp == 0) {
                break;
            }
            e = cmp < 0 ? e.left() : e.right();
        }
        return e;
    }

    final Entry<K> locateKey(K k) {
        Entry<K> e = this.tree;
        Entry<K> last = this.tree;
        int cmp = 0;
        while (e != null) {
            int compare = compare(k, e.key);
            cmp = compare;
            if (compare == 0) {
                break;
            }
            last = e;
            e = cmp < 0 ? e.left() : e.right();
        }
        return cmp == 0 ? e : last;
    }

    private void allocatePaths() {
        this.dirPath = new boolean[48];
    }

    public int addTo(K k, int incr) {
        Entry<K> e = add(k);
        int oldValue = e.value;
        e.value += incr;
        return oldValue;
    }

    @Override // it.unimi.dsi.fastutil.objects.Object2IntFunction
    public int put(K k, int v) {
        Entry<K> e = add(k);
        int oldValue = e.value;
        e.value = v;
        return oldValue;
    }

    private Entry<K> add(K k) {
        Entry<K> e;
        Entry<K> w;
        Objects.requireNonNull(k);
        this.modified = false;
        if (this.tree == null) {
            this.count++;
            Entry<K> e2 = new Entry<>(k, this.defRetValue);
            this.firstEntry = e2;
            this.lastEntry = e2;
            this.tree = e2;
            this.modified = true;
            return e2;
        }
        Entry<K> p = this.tree;
        Entry<K> q = null;
        Entry<K> y = this.tree;
        Entry<K> z = null;
        int i = 0;
        while (true) {
            int cmp = compare(k, p.key);
            if (cmp == 0) {
                return p;
            }
            if (p.balance() != 0) {
                i = 0;
                z = q;
                y = p;
            }
            boolean[] zArr = this.dirPath;
            int i2 = i + 1;
            boolean z2 = cmp > 0;
            zArr[i] = z2;
            if (z2) {
                if (p.succ()) {
                    this.count++;
                    e = new Entry<>(k, this.defRetValue);
                    this.modified = true;
                    if (p.right == null) {
                        this.lastEntry = e;
                    }
                    e.left = p;
                    e.right = p.right;
                    p.right(e);
                } else {
                    q = p;
                    p = p.right;
                    i = i2;
                }
            } else if (p.pred()) {
                this.count++;
                e = new Entry<>(k, this.defRetValue);
                this.modified = true;
                if (p.left == null) {
                    this.firstEntry = e;
                }
                e.right = p;
                e.left = p.left;
                p.left(e);
            } else {
                q = p;
                p = p.left;
                i = i2;
            }
        }
        Entry<K> p2 = y;
        int i3 = 0;
        while (p2 != e) {
            if (this.dirPath[i3]) {
                p2.incBalance();
            } else {
                p2.decBalance();
            }
            int i4 = i3 + 1;
            p2 = this.dirPath[i3] ? p2.right : p2.left;
            i3 = i4;
        }
        if (y.balance() == -2) {
            Entry<K> x = y.left;
            if (x.balance() == -1) {
                w = x;
                if (x.succ()) {
                    x.succ(false);
                    y.pred(x);
                } else {
                    y.left = x.right;
                }
                x.right = y;
                x.balance(0);
                y.balance(0);
            } else {
                if (x.balance() != 1) {
                    throw new AssertionError();
                }
                Entry<K> w2 = x.right;
                x.right = w2.left;
                w2.left = x;
                y.left = w2.right;
                w2.right = y;
                if (w2.balance() == -1) {
                    x.balance(0);
                    y.balance(1);
                } else if (w2.balance() == 0) {
                    x.balance(0);
                    y.balance(0);
                } else {
                    x.balance(-1);
                    y.balance(0);
                }
                w2.balance(0);
                if (w2.pred()) {
                    x.succ(w2);
                    w2.pred(false);
                }
                if (w2.succ()) {
                    y.pred(w2);
                    w2.succ(false);
                }
                w = w2;
            }
        } else if (y.balance() == 2) {
            Entry<K> x2 = y.right;
            if (x2.balance() == 1) {
                w = x2;
                if (x2.pred()) {
                    x2.pred(false);
                    y.succ(x2);
                } else {
                    y.right = x2.left;
                }
                x2.left = y;
                x2.balance(0);
                y.balance(0);
            } else {
                if (x2.balance() != -1) {
                    throw new AssertionError();
                }
                Entry<K> w3 = x2.left;
                x2.left = w3.right;
                w3.right = x2;
                y.right = w3.left;
                w3.left = y;
                if (w3.balance() == 1) {
                    x2.balance(0);
                    y.balance(-1);
                } else if (w3.balance() == 0) {
                    x2.balance(0);
                    y.balance(0);
                } else {
                    x2.balance(1);
                    y.balance(0);
                }
                w3.balance(0);
                if (w3.pred()) {
                    y.succ(w3);
                    w3.pred(false);
                }
                if (w3.succ()) {
                    x2.pred(w3);
                    w3.succ(false);
                }
                w = w3;
            }
        } else {
            return e;
        }
        if (z == null) {
            this.tree = w;
        } else if (z.left == y) {
            z.left = w;
        } else {
            z.right = w;
        }
        return e;
    }

    private Entry<K> parent(Entry<K> e) {
        if (e == this.tree) {
            return null;
        }
        Entry<K> y = e;
        Entry<K> x = e;
        while (!y.succ()) {
            if (x.pred()) {
                Entry<K> p = x.left;
                if (p == null || p.right != e) {
                    while (!y.succ()) {
                        y = y.right;
                    }
                    return y.right;
                }
                return p;
            }
            x = x.left;
            y = y.right;
        }
        Entry<K> p2 = y.right;
        if (p2 == null || p2.left != e) {
            while (!x.pred()) {
                x = x.left;
            }
            return x.left;
        }
        return p2;
    }

    /* JADX WARN: Code restructure failed: missing block: B:80:0x02b9, code lost:            r12.modified = true;        r12.count--;     */
    /* JADX WARN: Code restructure failed: missing block: B:81:0x02c2, code lost:            return r1.value;     */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // it.unimi.dsi.fastutil.objects.Object2IntFunction
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int removeInt(java.lang.Object r13) {
        /*
            Method dump skipped, instructions count: 739
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap.removeInt(java.lang.Object):int");
    }

    @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, it.unimi.dsi.fastutil.objects.Object2IntMap
    public boolean containsValue(int v) {
        Object2IntAVLTreeMap<K>.ValueIterator i = new ValueIterator(this, null);
        int ev = this.count;
        while (true) {
            int j = ev - 1;
            if (ev != 0) {
                int ev2 = i.nextInt();
                if (ev2 == v) {
                    return true;
                }
                ev = j;
            } else {
                return false;
            }
        }
    }

    @Override // it.unimi.dsi.fastutil.Function, it.unimi.dsi.fastutil.ints.Int2ObjectMap, java.util.Map
    public void clear() {
        this.count = 0;
        this.tree = null;
        this.entries = null;
        this.values = null;
        this.keys = null;
        this.lastEntry = null;
        this.firstEntry = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class Entry<K> extends AbstractObject2IntMap.BasicEntry<K> implements Cloneable {
        private static final int BALANCE_MASK = 255;
        private static final int PRED_MASK = 1073741824;
        private static final int SUCC_MASK = Integer.MIN_VALUE;
        int info;
        Entry<K> left;
        Entry<K> right;

        Entry() {
            super((Object) null, 0);
        }

        Entry(K k, int v) {
            super(k, v);
            this.info = -1073741824;
        }

        Entry<K> left() {
            if ((this.info & 1073741824) != 0) {
                return null;
            }
            return this.left;
        }

        Entry<K> right() {
            if ((this.info & Integer.MIN_VALUE) != 0) {
                return null;
            }
            return this.right;
        }

        boolean pred() {
            return (this.info & 1073741824) != 0;
        }

        boolean succ() {
            return (this.info & Integer.MIN_VALUE) != 0;
        }

        void pred(boolean pred) {
            if (!pred) {
                this.info &= -1073741825;
            } else {
                this.info |= 1073741824;
            }
        }

        void succ(boolean succ) {
            if (!succ) {
                this.info &= Integer.MAX_VALUE;
            } else {
                this.info |= Integer.MIN_VALUE;
            }
        }

        void pred(Entry<K> pred) {
            this.info |= 1073741824;
            this.left = pred;
        }

        void succ(Entry<K> succ) {
            this.info |= Integer.MIN_VALUE;
            this.right = succ;
        }

        void left(Entry<K> left) {
            this.info &= -1073741825;
            this.left = left;
        }

        void right(Entry<K> right) {
            this.info &= Integer.MAX_VALUE;
            this.right = right;
        }

        int balance() {
            return (byte) this.info;
        }

        void balance(int level) {
            this.info &= InputDeviceCompat.SOURCE_ANY;
            this.info |= level & 255;
        }

        void incBalance() {
            this.info = (this.info & InputDeviceCompat.SOURCE_ANY) | ((((byte) this.info) + 1) & 255);
        }

        protected void decBalance() {
            this.info = (this.info & InputDeviceCompat.SOURCE_ANY) | ((((byte) this.info) - 1) & 255);
        }

        Entry<K> next() {
            Entry<K> next = this.right;
            if ((this.info & Integer.MIN_VALUE) == 0) {
                while ((next.info & 1073741824) == 0) {
                    next = next.left;
                }
            }
            return next;
        }

        Entry<K> prev() {
            Entry<K> prev = this.left;
            if ((this.info & 1073741824) == 0) {
                while ((prev.info & Integer.MIN_VALUE) == 0) {
                    prev = prev.right;
                }
            }
            return prev;
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap.BasicEntry, it.unimi.dsi.fastutil.objects.Object2IntMap.Entry
        public int setValue(int value) {
            int oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        /* renamed from: clone, reason: merged with bridge method [inline-methods] */
        public Entry<K> m257clone() {
            try {
                Entry<K> c = (Entry) super.clone();
                c.key = this.key;
                c.value = this.value;
                c.info = this.info;
                return c;
            } catch (CloneNotSupportedException e) {
                throw new InternalError();
            }
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap.BasicEntry, java.util.Map.Entry
        public boolean equals(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<K, Integer> e = (Map.Entry) o;
            return Objects.equals(this.key, e.getKey()) && this.value == e.getValue().intValue();
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap.BasicEntry, java.util.Map.Entry
        public int hashCode() {
            return this.key.hashCode() ^ this.value;
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap.BasicEntry
        public String toString() {
            return this.key + "=>" + this.value;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, it.unimi.dsi.fastutil.Function
    public boolean containsKey(Object obj) {
        return (obj == 0 || findKey(obj) == null) ? false : true;
    }

    @Override // it.unimi.dsi.fastutil.Function, it.unimi.dsi.fastutil.ints.Int2ObjectMap, java.util.Map
    public int size() {
        return this.count;
    }

    @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, java.util.Map
    public boolean isEmpty() {
        return this.count == 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // it.unimi.dsi.fastutil.objects.Object2IntFunction
    public int getInt(Object obj) {
        Entry<K> e = findKey(obj);
        return e == null ? this.defRetValue : e.value;
    }

    @Override // java.util.SortedMap
    public K firstKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.firstEntry.key;
    }

    @Override // java.util.SortedMap
    public K lastKey() {
        if (this.tree == null) {
            throw new NoSuchElementException();
        }
        return this.lastEntry.key;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class TreeIterator {
        Entry<K> curr;
        int index = 0;
        Entry<K> next;
        Entry<K> prev;

        TreeIterator() {
            this.next = Object2IntAVLTreeMap.this.firstEntry;
        }

        TreeIterator(K k) {
            Entry<K> locateKey = Object2IntAVLTreeMap.this.locateKey(k);
            this.next = locateKey;
            if (locateKey != null) {
                if (Object2IntAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                    this.prev = this.next;
                    this.next = this.next.next();
                } else {
                    this.prev = this.next.prev();
                }
            }
        }

        public boolean hasNext() {
            return this.next != null;
        }

        public boolean hasPrevious() {
            return this.prev != null;
        }

        void updateNext() {
            this.next = this.next.next();
        }

        Entry<K> nextEntry() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            Entry<K> entry = this.next;
            this.prev = entry;
            this.curr = entry;
            this.index++;
            updateNext();
            return this.curr;
        }

        void updatePrevious() {
            this.prev = this.prev.prev();
        }

        Entry<K> previousEntry() {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            Entry<K> entry = this.prev;
            this.next = entry;
            this.curr = entry;
            this.index--;
            updatePrevious();
            return this.curr;
        }

        public int nextIndex() {
            return this.index;
        }

        public int previousIndex() {
            return this.index - 1;
        }

        public void remove() {
            if (this.curr == null) {
                throw new IllegalStateException();
            }
            if (this.curr == this.prev) {
                this.index--;
            }
            Entry<K> entry = this.curr;
            this.prev = entry;
            this.next = entry;
            updatePrevious();
            updateNext();
            Object2IntAVLTreeMap.this.removeInt(this.curr.key);
            this.curr = null;
        }

        public int skip(int n) {
            int i;
            int i2 = n;
            while (true) {
                i = i2 - 1;
                if (i2 == 0 || !hasNext()) {
                    break;
                }
                nextEntry();
                i2 = i;
            }
            return (n - i) - 1;
        }

        public int back(int n) {
            int i;
            int i2 = n;
            while (true) {
                i = i2 - 1;
                if (i2 == 0 || !hasPrevious()) {
                    break;
                }
                previousEntry();
                i2 = i;
            }
            return (n - i) - 1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class EntryIterator extends Object2IntAVLTreeMap<K>.TreeIterator implements ObjectListIterator<Object2IntMap.Entry<K>> {
        EntryIterator() {
            super();
        }

        EntryIterator(K k) {
            super(k);
        }

        @Override // java.util.Iterator, java.util.ListIterator
        public Object2IntMap.Entry<K> next() {
            return nextEntry();
        }

        @Override // it.unimi.dsi.fastutil.BidirectionalIterator, java.util.ListIterator
        public Object2IntMap.Entry<K> previous() {
            return previousEntry();
        }

        @Override // it.unimi.dsi.fastutil.objects.ObjectListIterator, java.util.ListIterator
        public void set(Object2IntMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }

        @Override // it.unimi.dsi.fastutil.objects.ObjectListIterator, java.util.ListIterator
        public void add(Object2IntMap.Entry<K> ok) {
            throw new UnsupportedOperationException();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap$1, reason: invalid class name */
    /* loaded from: classes4.dex */
    public class AnonymousClass1 extends AbstractObjectSortedSet<Object2IntMap.Entry<K>> {
        final Comparator<? super Object2IntMap.Entry<K>> comparator;

        AnonymousClass1() {
            this.comparator = Object2IntAVLTreeMap.this.actualComparator == null ? new Comparator() { // from class: it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap$1$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    int compareTo;
                    compareTo = ((Comparable) ((Object2IntMap.Entry) obj).getKey()).compareTo(((Object2IntMap.Entry) obj2).getKey());
                    return compareTo;
                }
            } : new Comparator() { // from class: it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap$1$$ExternalSyntheticLambda1
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return Object2IntAVLTreeMap.AnonymousClass1.this.m256lambda$$1$itunimidsifastutilobjectsObject2IntAVLTreeMap$1((Object2IntMap.Entry) obj, (Object2IntMap.Entry) obj2);
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* renamed from: lambda$$1$it-unimi-dsi-fastutil-objects-Object2IntAVLTreeMap$1, reason: not valid java name */
        public /* synthetic */ int m256lambda$$1$itunimidsifastutilobjectsObject2IntAVLTreeMap$1(Object2IntMap.Entry x, Object2IntMap.Entry y) {
            return Object2IntAVLTreeMap.this.actualComparator.compare(x.getKey(), y.getKey());
        }

        @Override // java.util.SortedSet
        public Comparator<? super Object2IntMap.Entry<K>> comparator() {
            return this.comparator;
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet, it.unimi.dsi.fastutil.objects.AbstractObjectSet, it.unimi.dsi.fastutil.objects.AbstractObjectCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, it.unimi.dsi.fastutil.objects.ObjectCollection, it.unimi.dsi.fastutil.objects.ObjectIterable
        public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> iterator() {
            return new EntryIterator();
        }

        @Override // it.unimi.dsi.fastutil.objects.ObjectSortedSet
        public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> iterator(Object2IntMap.Entry<K> from) {
            return new EntryIterator(from.getKey());
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean contains(Object o) {
            if (o == null || !(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            if (e.getKey() == null || e.getValue() == null || !(e.getValue() instanceof Integer)) {
                return false;
            }
            Entry<K> f = Object2IntAVLTreeMap.this.findKey(e.getKey());
            return e.equals(f);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public boolean remove(Object o) {
            Entry<K> f;
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry<?, ?> e = (Map.Entry) o;
            if (e.getKey() == null || !(e.getValue() instanceof Integer) || (f = Object2IntAVLTreeMap.this.findKey(e.getKey())) == null || f.getIntValue() != ((Integer) e.getValue()).intValue()) {
                return false;
            }
            Object2IntAVLTreeMap.this.removeInt(f.key);
            return true;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return Object2IntAVLTreeMap.this.count;
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            Object2IntAVLTreeMap.this.clear();
        }

        @Override // java.util.SortedSet
        public Object2IntMap.Entry<K> first() {
            return Object2IntAVLTreeMap.this.firstEntry;
        }

        @Override // java.util.SortedSet
        public Object2IntMap.Entry<K> last() {
            return Object2IntAVLTreeMap.this.lastEntry;
        }

        @Override // it.unimi.dsi.fastutil.objects.ObjectSortedSet, java.util.SortedSet
        public ObjectSortedSet<Object2IntMap.Entry<K>> subSet(Object2IntMap.Entry<K> from, Object2IntMap.Entry<K> to) {
            return Object2IntAVLTreeMap.this.subMap((Object) from.getKey(), (Object) to.getKey()).object2IntEntrySet();
        }

        @Override // it.unimi.dsi.fastutil.objects.ObjectSortedSet, java.util.SortedSet
        public ObjectSortedSet<Object2IntMap.Entry<K>> headSet(Object2IntMap.Entry<K> to) {
            return Object2IntAVLTreeMap.this.headMap((Object2IntAVLTreeMap) to.getKey()).object2IntEntrySet();
        }

        @Override // it.unimi.dsi.fastutil.objects.ObjectSortedSet, java.util.SortedSet
        public ObjectSortedSet<Object2IntMap.Entry<K>> tailSet(Object2IntMap.Entry<K> from) {
            return Object2IntAVLTreeMap.this.tailMap((Object2IntAVLTreeMap) from.getKey()).object2IntEntrySet();
        }
    }

    @Override // it.unimi.dsi.fastutil.objects.Object2IntMap, it.unimi.dsi.fastutil.objects.Object2IntSortedMap
    public ObjectSortedSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
        if (this.entries == null) {
            this.entries = new AnonymousClass1();
        }
        return this.entries;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class KeyIterator extends Object2IntAVLTreeMap<K>.TreeIterator implements ObjectListIterator<K> {
        public KeyIterator() {
            super();
        }

        public KeyIterator(K k) {
            super(k);
        }

        @Override // java.util.Iterator, java.util.ListIterator
        public K next() {
            return nextEntry().key;
        }

        @Override // it.unimi.dsi.fastutil.BidirectionalIterator, java.util.ListIterator
        public K previous() {
            return previousEntry().key;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class KeySet extends AbstractObject2IntSortedMap<K>.KeySet {
        private KeySet() {
            super();
        }

        /* synthetic */ KeySet(Object2IntAVLTreeMap x0, AnonymousClass1 x1) {
            this();
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntSortedMap.KeySet, it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet, it.unimi.dsi.fastutil.objects.AbstractObjectSet, it.unimi.dsi.fastutil.objects.AbstractObjectCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, it.unimi.dsi.fastutil.objects.ObjectCollection, it.unimi.dsi.fastutil.objects.ObjectIterable
        public ObjectBidirectionalIterator<K> iterator() {
            return new KeyIterator();
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntSortedMap.KeySet, it.unimi.dsi.fastutil.objects.ObjectSortedSet
        public ObjectBidirectionalIterator<K> iterator(K from) {
            return new KeyIterator(from);
        }
    }

    @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntSortedMap, it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, it.unimi.dsi.fastutil.objects.Object2IntMap, java.util.Map
    public ObjectSortedSet<K> keySet() {
        if (this.keys == null) {
            this.keys = new KeySet(this, null);
        }
        return this.keys;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class ValueIterator extends Object2IntAVLTreeMap<K>.TreeIterator implements IntListIterator {
        private ValueIterator() {
            super();
        }

        /* synthetic */ ValueIterator(Object2IntAVLTreeMap x0, AnonymousClass1 x1) {
            this();
        }

        @Override // it.unimi.dsi.fastutil.ints.IntIterator, java.util.PrimitiveIterator.OfInt
        public int nextInt() {
            return nextEntry().value;
        }

        @Override // it.unimi.dsi.fastutil.ints.IntBidirectionalIterator
        public int previousInt() {
            return previousEntry().value;
        }
    }

    @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntSortedMap, it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, it.unimi.dsi.fastutil.objects.Object2IntMap, java.util.Map
    /* renamed from: values */
    public Collection<Integer> values2() {
        if (this.values == null) {
            this.values = new AbstractIntCollection() { // from class: it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap.2
                @Override // it.unimi.dsi.fastutil.ints.AbstractIntCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, it.unimi.dsi.fastutil.ints.IntCollection, it.unimi.dsi.fastutil.ints.IntIterable, it.unimi.dsi.fastutil.ints.IntSet, java.util.Set
                public IntIterator iterator() {
                    return new ValueIterator(Object2IntAVLTreeMap.this, null);
                }

                @Override // it.unimi.dsi.fastutil.ints.AbstractIntCollection, it.unimi.dsi.fastutil.ints.IntCollection
                public boolean contains(int k) {
                    return Object2IntAVLTreeMap.this.containsValue(k);
                }

                @Override // java.util.AbstractCollection, java.util.Collection
                public int size() {
                    return Object2IntAVLTreeMap.this.count;
                }

                @Override // java.util.AbstractCollection, java.util.Collection
                public void clear() {
                    Object2IntAVLTreeMap.this.clear();
                }
            };
        }
        return this.values;
    }

    @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
    public Comparator<? super K> comparator() {
        return this.actualComparator;
    }

    @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
    public Object2IntSortedMap<K> headMap(K to) {
        return new Submap(null, true, to, false);
    }

    @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
    public Object2IntSortedMap<K> tailMap(K from) {
        return new Submap(from, false, null, true);
    }

    @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
    public Object2IntSortedMap<K> subMap(K from, K to) {
        return new Submap(from, false, to, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class Submap extends AbstractObject2IntSortedMap<K> implements Serializable {
        private static final long serialVersionUID = -7046029254386353129L;
        boolean bottom;
        protected transient ObjectSortedSet<Object2IntMap.Entry<K>> entries;
        K from;
        protected transient ObjectSortedSet<K> keys;
        K to;
        boolean top;
        protected transient IntCollection values;

        @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
        public /* bridge */ /* synthetic */ SortedMap headMap(Object obj) {
            return headMap((Submap) obj);
        }

        @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
        public /* bridge */ /* synthetic */ SortedMap tailMap(Object obj) {
            return tailMap((Submap) obj);
        }

        public Submap(K from, boolean bottom, K to, boolean top) {
            if (!bottom && !top && Object2IntAVLTreeMap.this.compare(from, to) > 0) {
                throw new IllegalArgumentException("Start key (" + from + ") is larger than end key (" + to + ")");
            }
            this.from = from;
            this.bottom = bottom;
            this.to = to;
            this.top = top;
            this.defRetValue = Object2IntAVLTreeMap.this.defRetValue;
        }

        @Override // it.unimi.dsi.fastutil.Function, it.unimi.dsi.fastutil.ints.Int2ObjectMap, java.util.Map
        public void clear() {
            Object2IntAVLTreeMap<K>.Submap.SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                i.nextEntry();
                i.remove();
            }
        }

        final boolean in(K k) {
            return (this.bottom || Object2IntAVLTreeMap.this.compare(k, this.from) >= 0) && (this.top || Object2IntAVLTreeMap.this.compare(k, this.to) < 0);
        }

        @Override // it.unimi.dsi.fastutil.objects.Object2IntMap, it.unimi.dsi.fastutil.objects.Object2IntSortedMap
        public ObjectSortedSet<Object2IntMap.Entry<K>> object2IntEntrySet() {
            if (this.entries == null) {
                this.entries = new AbstractObjectSortedSet<Object2IntMap.Entry<K>>() { // from class: it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap.Submap.1
                    @Override // it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet, it.unimi.dsi.fastutil.objects.AbstractObjectSet, it.unimi.dsi.fastutil.objects.AbstractObjectCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, it.unimi.dsi.fastutil.objects.ObjectCollection, it.unimi.dsi.fastutil.objects.ObjectIterable
                    public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> iterator() {
                        return new SubmapEntryIterator();
                    }

                    @Override // it.unimi.dsi.fastutil.objects.ObjectSortedSet
                    public ObjectBidirectionalIterator<Object2IntMap.Entry<K>> iterator(Object2IntMap.Entry<K> from) {
                        return new SubmapEntryIterator(from.getKey());
                    }

                    @Override // java.util.SortedSet
                    public Comparator<? super Object2IntMap.Entry<K>> comparator() {
                        return Object2IntAVLTreeMap.this.object2IntEntrySet().comparator();
                    }

                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                    public boolean contains(Object o) {
                        Entry<K> f;
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry<?, ?> e = (Map.Entry) o;
                        return e.getValue() != null && (e.getValue() instanceof Integer) && (f = Object2IntAVLTreeMap.this.findKey(e.getKey())) != null && Submap.this.in(f.key) && e.equals(f);
                    }

                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                    public boolean remove(Object o) {
                        if (!(o instanceof Map.Entry)) {
                            return false;
                        }
                        Map.Entry<?, ?> e = (Map.Entry) o;
                        if (e.getValue() == null || !(e.getValue() instanceof Integer)) {
                            return false;
                        }
                        Entry<K> f = Object2IntAVLTreeMap.this.findKey(e.getKey());
                        if (f != null && Submap.this.in(f.key)) {
                            Submap.this.removeInt(f.key);
                        }
                        return f != null;
                    }

                    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                    public int size() {
                        int c = 0;
                        Iterator<?> i = iterator();
                        while (i.hasNext()) {
                            c++;
                            i.next();
                        }
                        return c;
                    }

                    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                    public boolean isEmpty() {
                        return !new SubmapIterator().hasNext();
                    }

                    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
                    public void clear() {
                        Submap.this.clear();
                    }

                    @Override // java.util.SortedSet
                    public Object2IntMap.Entry<K> first() {
                        return Submap.this.firstEntry();
                    }

                    @Override // java.util.SortedSet
                    public Object2IntMap.Entry<K> last() {
                        return Submap.this.lastEntry();
                    }

                    @Override // it.unimi.dsi.fastutil.objects.ObjectSortedSet, java.util.SortedSet
                    public ObjectSortedSet<Object2IntMap.Entry<K>> subSet(Object2IntMap.Entry<K> from, Object2IntMap.Entry<K> to) {
                        return Submap.this.subMap((Object) from.getKey(), (Object) to.getKey()).object2IntEntrySet();
                    }

                    @Override // it.unimi.dsi.fastutil.objects.ObjectSortedSet, java.util.SortedSet
                    public ObjectSortedSet<Object2IntMap.Entry<K>> headSet(Object2IntMap.Entry<K> to) {
                        return Submap.this.headMap((Submap) to.getKey()).object2IntEntrySet();
                    }

                    @Override // it.unimi.dsi.fastutil.objects.ObjectSortedSet, java.util.SortedSet
                    public ObjectSortedSet<Object2IntMap.Entry<K>> tailSet(Object2IntMap.Entry<K> from) {
                        return Submap.this.tailMap((Submap) from.getKey()).object2IntEntrySet();
                    }
                };
            }
            return this.entries;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public class KeySet extends AbstractObject2IntSortedMap<K>.KeySet {
            private KeySet() {
                super();
            }

            /* synthetic */ KeySet(Submap x0, AnonymousClass1 x1) {
                this();
            }

            @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntSortedMap.KeySet, it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet, it.unimi.dsi.fastutil.objects.AbstractObjectSet, it.unimi.dsi.fastutil.objects.AbstractObjectCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, it.unimi.dsi.fastutil.objects.ObjectCollection, it.unimi.dsi.fastutil.objects.ObjectIterable
            public ObjectBidirectionalIterator<K> iterator() {
                return new SubmapKeyIterator();
            }

            @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntSortedMap.KeySet, it.unimi.dsi.fastutil.objects.ObjectSortedSet
            public ObjectBidirectionalIterator<K> iterator(K from) {
                return new SubmapKeyIterator(from);
            }
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntSortedMap, it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, it.unimi.dsi.fastutil.objects.Object2IntMap, java.util.Map
        public ObjectSortedSet<K> keySet() {
            if (this.keys == null) {
                this.keys = new KeySet(this, null);
            }
            return this.keys;
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntSortedMap, it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, it.unimi.dsi.fastutil.objects.Object2IntMap, java.util.Map
        /* renamed from: values */
        public Collection<Integer> values2() {
            if (this.values == null) {
                this.values = new AbstractIntCollection() { // from class: it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap.Submap.2
                    @Override // it.unimi.dsi.fastutil.ints.AbstractIntCollection, java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, it.unimi.dsi.fastutil.ints.IntCollection, it.unimi.dsi.fastutil.ints.IntIterable, it.unimi.dsi.fastutil.ints.IntSet, java.util.Set
                    public IntIterator iterator() {
                        return new SubmapValueIterator(Submap.this, null);
                    }

                    @Override // it.unimi.dsi.fastutil.ints.AbstractIntCollection, it.unimi.dsi.fastutil.ints.IntCollection
                    public boolean contains(int k) {
                        return Submap.this.containsValue(k);
                    }

                    @Override // java.util.AbstractCollection, java.util.Collection
                    public int size() {
                        return Submap.this.size();
                    }

                    @Override // java.util.AbstractCollection, java.util.Collection
                    public void clear() {
                        Submap.this.clear();
                    }
                };
            }
            return this.values;
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, it.unimi.dsi.fastutil.Function
        public boolean containsKey(Object k) {
            return k != null && in(k) && Object2IntAVLTreeMap.this.containsKey(k);
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, it.unimi.dsi.fastutil.objects.Object2IntMap
        public boolean containsValue(int v) {
            Object2IntAVLTreeMap<K>.Submap.SubmapIterator i = new SubmapIterator();
            while (i.hasNext()) {
                int ev = i.nextEntry().value;
                if (ev == v) {
                    return true;
                }
            }
            return false;
        }

        @Override // it.unimi.dsi.fastutil.objects.Object2IntFunction
        public int getInt(Object k) {
            Entry<K> e;
            return (!in(k) || (e = Object2IntAVLTreeMap.this.findKey(k)) == null) ? this.defRetValue : e.value;
        }

        @Override // it.unimi.dsi.fastutil.objects.Object2IntFunction
        public int put(K k, int v) {
            Object2IntAVLTreeMap.this.modified = false;
            if (!in(k)) {
                throw new IllegalArgumentException("Key (" + k + ") out of range [" + (this.bottom ? "-" : String.valueOf(this.from)) + ", " + (this.top ? "-" : String.valueOf(this.to)) + ")");
            }
            int oldValue = Object2IntAVLTreeMap.this.put((Object2IntAVLTreeMap) k, v);
            return Object2IntAVLTreeMap.this.modified ? this.defRetValue : oldValue;
        }

        @Override // it.unimi.dsi.fastutil.objects.Object2IntFunction
        public int removeInt(Object k) {
            Object2IntAVLTreeMap.this.modified = false;
            if (!in(k)) {
                return this.defRetValue;
            }
            int oldValue = Object2IntAVLTreeMap.this.removeInt(k);
            return Object2IntAVLTreeMap.this.modified ? oldValue : this.defRetValue;
        }

        @Override // it.unimi.dsi.fastutil.Function, it.unimi.dsi.fastutil.ints.Int2ObjectMap, java.util.Map
        public int size() {
            Object2IntAVLTreeMap<K>.Submap.SubmapIterator i = new SubmapIterator();
            int n = 0;
            while (i.hasNext()) {
                n++;
                i.nextEntry();
            }
            return n;
        }

        @Override // it.unimi.dsi.fastutil.objects.AbstractObject2IntMap, java.util.Map
        public boolean isEmpty() {
            return !new SubmapIterator().hasNext();
        }

        @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
        public Comparator<? super K> comparator() {
            return Object2IntAVLTreeMap.this.actualComparator;
        }

        @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
        public Object2IntSortedMap<K> headMap(K to) {
            if (!this.top && Object2IntAVLTreeMap.this.compare(to, this.to) >= 0) {
                return this;
            }
            return new Submap(this.from, this.bottom, to, false);
        }

        @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
        public Object2IntSortedMap<K> tailMap(K from) {
            if (!this.bottom && Object2IntAVLTreeMap.this.compare(from, this.from) <= 0) {
                return this;
            }
            return new Submap(from, false, this.to, this.top);
        }

        @Override // it.unimi.dsi.fastutil.objects.Object2IntSortedMap, java.util.SortedMap
        public Object2IntSortedMap<K> subMap(K from, K to) {
            if (this.top && this.bottom) {
                return new Submap(from, false, to, false);
            }
            if (!this.top) {
                to = Object2IntAVLTreeMap.this.compare(to, this.to) < 0 ? to : this.to;
            }
            if (!this.bottom) {
                from = Object2IntAVLTreeMap.this.compare(from, this.from) > 0 ? from : this.from;
            }
            return (this.top || this.bottom || from != this.from || to != this.to) ? new Submap(from, false, to, false) : this;
        }

        public Entry<K> firstEntry() {
            Entry<K> e;
            if (Object2IntAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.bottom) {
                e = Object2IntAVLTreeMap.this.firstEntry;
            } else {
                e = Object2IntAVLTreeMap.this.locateKey(this.from);
                if (Object2IntAVLTreeMap.this.compare(e.key, this.from) < 0) {
                    e = e.next();
                }
            }
            if (e == null || (!this.top && Object2IntAVLTreeMap.this.compare(e.key, this.to) >= 0)) {
                return null;
            }
            return e;
        }

        public Entry<K> lastEntry() {
            Entry<K> e;
            if (Object2IntAVLTreeMap.this.tree == null) {
                return null;
            }
            if (this.top) {
                e = Object2IntAVLTreeMap.this.lastEntry;
            } else {
                e = Object2IntAVLTreeMap.this.locateKey(this.to);
                if (Object2IntAVLTreeMap.this.compare(e.key, this.to) >= 0) {
                    e = e.prev();
                }
            }
            if (e == null || (!this.bottom && Object2IntAVLTreeMap.this.compare(e.key, this.from) < 0)) {
                return null;
            }
            return e;
        }

        @Override // java.util.SortedMap
        public K firstKey() {
            Entry<K> e = firstEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        @Override // java.util.SortedMap
        public K lastKey() {
            Entry<K> e = lastEntry();
            if (e == null) {
                throw new NoSuchElementException();
            }
            return e.key;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public class SubmapIterator extends Object2IntAVLTreeMap<K>.TreeIterator {
            SubmapIterator() {
                super();
                this.next = Submap.this.firstEntry();
            }

            SubmapIterator(Submap submap, K k) {
                this();
                if (this.next != null) {
                    if (submap.bottom || Object2IntAVLTreeMap.this.compare(k, this.next.key) >= 0) {
                        if (!submap.top) {
                            Object2IntAVLTreeMap object2IntAVLTreeMap = Object2IntAVLTreeMap.this;
                            Entry<K> lastEntry = submap.lastEntry();
                            this.prev = lastEntry;
                            if (object2IntAVLTreeMap.compare(k, lastEntry.key) >= 0) {
                                this.next = null;
                                return;
                            }
                        }
                        this.next = Object2IntAVLTreeMap.this.locateKey(k);
                        if (Object2IntAVLTreeMap.this.compare(this.next.key, k) <= 0) {
                            this.prev = this.next;
                            this.next = this.next.next();
                            return;
                        } else {
                            this.prev = this.next.prev();
                            return;
                        }
                    }
                    this.prev = null;
                }
            }

            @Override // it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap.TreeIterator
            void updatePrevious() {
                this.prev = this.prev.prev();
                if (Submap.this.bottom || this.prev == null || Object2IntAVLTreeMap.this.compare(this.prev.key, Submap.this.from) >= 0) {
                    return;
                }
                this.prev = null;
            }

            @Override // it.unimi.dsi.fastutil.objects.Object2IntAVLTreeMap.TreeIterator
            void updateNext() {
                this.next = this.next.next();
                if (Submap.this.top || this.next == null || Object2IntAVLTreeMap.this.compare(this.next.key, Submap.this.to) < 0) {
                    return;
                }
                this.next = null;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public class SubmapEntryIterator extends Object2IntAVLTreeMap<K>.Submap.SubmapIterator implements ObjectListIterator<Object2IntMap.Entry<K>> {
            SubmapEntryIterator() {
                super();
            }

            SubmapEntryIterator(K k) {
                super(Submap.this, k);
            }

            @Override // java.util.Iterator, java.util.ListIterator
            public Object2IntMap.Entry<K> next() {
                return nextEntry();
            }

            @Override // it.unimi.dsi.fastutil.BidirectionalIterator, java.util.ListIterator
            public Object2IntMap.Entry<K> previous() {
                return previousEntry();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public final class SubmapKeyIterator extends Object2IntAVLTreeMap<K>.Submap.SubmapIterator implements ObjectListIterator<K> {
            public SubmapKeyIterator() {
                super();
            }

            public SubmapKeyIterator(K from) {
                super(Submap.this, from);
            }

            @Override // java.util.Iterator, java.util.ListIterator
            public K next() {
                return nextEntry().key;
            }

            @Override // it.unimi.dsi.fastutil.BidirectionalIterator, java.util.ListIterator
            public K previous() {
                return previousEntry().key;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public final class SubmapValueIterator extends Object2IntAVLTreeMap<K>.Submap.SubmapIterator implements IntListIterator {
            private SubmapValueIterator() {
                super();
            }

            /* synthetic */ SubmapValueIterator(Submap x0, AnonymousClass1 x1) {
                this();
            }

            @Override // it.unimi.dsi.fastutil.ints.IntIterator, java.util.PrimitiveIterator.OfInt
            public int nextInt() {
                return nextEntry().value;
            }

            @Override // it.unimi.dsi.fastutil.ints.IntBidirectionalIterator
            public int previousInt() {
                return previousEntry().value;
            }
        }
    }

    /* renamed from: clone, reason: merged with bridge method [inline-methods] */
    public Object2IntAVLTreeMap<K> m255clone() {
        try {
            Object2IntAVLTreeMap<K> c = (Object2IntAVLTreeMap) super.clone();
            c.keys = null;
            c.values = null;
            c.entries = null;
            c.allocatePaths();
            if (this.count != 0) {
                Entry<K> rp = new Entry<>();
                Entry<K> rq = new Entry<>();
                Entry<K> p = rp;
                rp.left(this.tree);
                Entry<K> q = rq;
                rq.pred((Entry) null);
                loop0: while (true) {
                    if (!p.pred()) {
                        Entry<K> e = p.left.m257clone();
                        e.pred(q.left);
                        e.succ(q);
                        q.left(e);
                        p = p.left;
                        q = q.left;
                    } else {
                        while (p.succ()) {
                            p = p.right;
                            if (p == null) {
                                break loop0;
                            }
                            q = q.right;
                        }
                        p = p.right;
                        q = q.right;
                    }
                    if (!p.succ()) {
                        Entry<K> e2 = p.right.m257clone();
                        e2.succ(q.right);
                        e2.pred(q);
                        q.right(e2);
                    }
                }
                q.right = null;
                c.tree = rq.left;
                c.firstEntry = c.tree;
                while (c.firstEntry.left != null) {
                    c.firstEntry = c.firstEntry.left;
                }
                c.lastEntry = c.tree;
                while (c.lastEntry.right != null) {
                    c.lastEntry = c.lastEntry.right;
                }
                return c;
            }
            return c;
        } catch (CloneNotSupportedException e3) {
            throw new InternalError();
        }
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
        int n = this.count;
        Object2IntAVLTreeMap<K>.EntryIterator i = new EntryIterator();
        s.defaultWriteObject();
        while (true) {
            int n2 = n - 1;
            if (n != 0) {
                Entry<K> e = i.nextEntry();
                s.writeObject(e.key);
                s.writeInt(e.value);
                n = n2;
            } else {
                return;
            }
        }
    }

    private Entry<K> readTree(ObjectInputStream objectInputStream, int i, Entry<K> entry, Entry<K> entry2) throws IOException, ClassNotFoundException {
        if (i == 1) {
            Entry<K> entry3 = new Entry<>(objectInputStream.readObject(), objectInputStream.readInt());
            entry3.pred(entry);
            entry3.succ(entry2);
            return entry3;
        }
        if (i == 2) {
            Entry<K> entry4 = new Entry<>(objectInputStream.readObject(), objectInputStream.readInt());
            entry4.right(new Entry<>(objectInputStream.readObject(), objectInputStream.readInt()));
            entry4.right.pred(entry4);
            entry4.balance(1);
            entry4.pred(entry);
            entry4.right.succ(entry2);
            return entry4;
        }
        int i2 = i / 2;
        Entry<K> entry5 = new Entry<>();
        entry5.left(readTree(objectInputStream, (i - i2) - 1, entry, entry5));
        entry5.key = (K) objectInputStream.readObject();
        entry5.value = objectInputStream.readInt();
        entry5.right(readTree(objectInputStream, i2, entry5, entry2));
        if (i == ((-i) & i)) {
            entry5.balance(1);
        }
        return entry5;
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        setActualComparator();
        allocatePaths();
        if (this.count != 0) {
            this.tree = readTree(s, this.count, null, null);
            Entry<K> e = this.tree;
            while (e.left() != null) {
                e = e.left();
            }
            this.firstEntry = e;
            Entry<K> e2 = this.tree;
            while (e2.right() != null) {
                e2 = e2.right();
            }
            this.lastEntry = e2;
        }
    }
}