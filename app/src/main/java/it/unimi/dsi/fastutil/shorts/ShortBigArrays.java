package it.unimi.dsi.fastutil.shorts;

import io.netty.handler.codec.http2.Http2CodecUtil;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;
import okhttp3.internal.ws.RealWebSocket;

/* loaded from: classes4.dex */
public final class ShortBigArrays {
    private static final int DIGITS_PER_ELEMENT = 2;
    private static final int DIGIT_BITS = 8;
    private static final int DIGIT_MASK = 255;
    private static final int MEDIUM = 40;
    private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
    private static final int QUICKSORT_NO_REC = 7;
    private static final int RADIXSORT_NO_REC = 1024;
    public static final short[][] EMPTY_BIG_ARRAY = new short[0];
    public static final short[][] DEFAULT_EMPTY_BIG_ARRAY = new short[0];
    public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();

    private ShortBigArrays() {
    }

    @Deprecated
    public static short get(short[][] array, long index) {
        return array[BigArrays.segment(index)][BigArrays.displacement(index)];
    }

    @Deprecated
    public static void set(short[][] array, long index, short value) {
        array[BigArrays.segment(index)][BigArrays.displacement(index)] = value;
    }

    @Deprecated
    public static void swap(short[][] array, long first, long second) {
        short t = array[BigArrays.segment(first)][BigArrays.displacement(first)];
        array[BigArrays.segment(first)][BigArrays.displacement(first)] = array[BigArrays.segment(second)][BigArrays.displacement(second)];
        array[BigArrays.segment(second)][BigArrays.displacement(second)] = t;
    }

    @Deprecated
    public static void add(short[][] array, long index, short incr) {
        short[] sArr = array[BigArrays.segment(index)];
        int displacement = BigArrays.displacement(index);
        sArr[displacement] = (short) (sArr[displacement] + incr);
    }

    @Deprecated
    public static void mul(short[][] array, long index, short factor) {
        short[] sArr = array[BigArrays.segment(index)];
        int displacement = BigArrays.displacement(index);
        sArr[displacement] = (short) (sArr[displacement] * factor);
    }

    @Deprecated
    public static void incr(short[][] array, long index) {
        short[] sArr = array[BigArrays.segment(index)];
        int displacement = BigArrays.displacement(index);
        sArr[displacement] = (short) (sArr[displacement] + 1);
    }

    @Deprecated
    public static void decr(short[][] array, long index) {
        array[BigArrays.segment(index)][BigArrays.displacement(index)] = (short) (r0[r1] - 1);
    }

    @Deprecated
    public static long length(short[][] array) {
        int length = array.length;
        if (length == 0) {
            return 0L;
        }
        return BigArrays.start(length - 1) + array[length - 1].length;
    }

    @Deprecated
    public static void copy(short[][] srcArray, long srcPos, short[][] destArray, long destPos, long length) {
        BigArrays.copy(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static void copyFromBig(short[][] srcArray, long srcPos, short[] destArray, int destPos, int length) {
        BigArrays.copyFromBig(srcArray, srcPos, destArray, destPos, length);
    }

    @Deprecated
    public static void copyToBig(short[] srcArray, int srcPos, short[][] destArray, long destPos, long length) {
        BigArrays.copyToBig(srcArray, srcPos, destArray, destPos, length);
    }

    public static short[][] newBigArray(long length) {
        if (length == 0) {
            return EMPTY_BIG_ARRAY;
        }
        BigArrays.ensureLength(length);
        int baseLength = (int) ((length + 134217727) >>> 27);
        short[][] base = new short[baseLength];
        int residual = (int) (134217727 & length);
        if (residual != 0) {
            for (int i = 0; i < baseLength - 1; i++) {
                base[i] = new short[BigArrays.SEGMENT_SIZE];
            }
            base[baseLength - 1] = new short[residual];
        } else {
            for (int i2 = 0; i2 < baseLength; i2++) {
                base[i2] = new short[BigArrays.SEGMENT_SIZE];
            }
        }
        return base;
    }

    @Deprecated
    public static short[][] wrap(short[] array) {
        return BigArrays.wrap(array);
    }

    @Deprecated
    public static short[][] ensureCapacity(short[][] array, long length) {
        return ensureCapacity(array, length, length(array));
    }

    @Deprecated
    public static short[][] forceCapacity(short[][] array, long length, long preserve) {
        return BigArrays.forceCapacity(array, length, preserve);
    }

    @Deprecated
    public static short[][] ensureCapacity(short[][] array, long length, long preserve) {
        return length > length(array) ? forceCapacity(array, length, preserve) : array;
    }

    @Deprecated
    public static short[][] grow(short[][] array, long length) {
        long oldLength = length(array);
        return length > oldLength ? grow(array, length, oldLength) : array;
    }

    @Deprecated
    public static short[][] grow(short[][] array, long length, long preserve) {
        long oldLength = length(array);
        return length > oldLength ? ensureCapacity(array, Math.max((oldLength >> 1) + oldLength, length), preserve) : array;
    }

    @Deprecated
    public static short[][] trim(short[][] array, long length) {
        BigArrays.ensureLength(length);
        long oldLength = length(array);
        if (length >= oldLength) {
            return array;
        }
        int baseLength = (int) ((length + 134217727) >>> 27);
        short[][] base = (short[][]) Arrays.copyOf(array, baseLength);
        int residual = (int) (134217727 & length);
        if (residual != 0) {
            base[baseLength - 1] = ShortArrays.trim(base[baseLength - 1], residual);
        }
        return base;
    }

    @Deprecated
    public static short[][] setLength(short[][] array, long length) {
        return BigArrays.setLength(array, length);
    }

    @Deprecated
    public static short[][] copy(short[][] array, long offset, long length) {
        return BigArrays.copy(array, offset, length);
    }

    @Deprecated
    public static short[][] copy(short[][] array) {
        return BigArrays.copy(array);
    }

    @Deprecated
    public static void fill(short[][] array, short value) {
        int i = array.length;
        while (true) {
            int i2 = i - 1;
            if (i == 0) {
                return;
            }
            Arrays.fill(array[i2], value);
            i = i2;
        }
    }

    @Deprecated
    public static void fill(short[][] array, long from, long to, short value) {
        BigArrays.fill(array, from, to, value);
    }

    @Deprecated
    public static boolean equals(short[][] a1, short[][] a2) {
        return BigArrays.equals(a1, a2);
    }

    @Deprecated
    public static String toString(short[][] a) {
        return BigArrays.toString(a);
    }

    @Deprecated
    public static void ensureFromTo(short[][] a, long from, long to) {
        BigArrays.ensureFromTo(length(a), from, to);
    }

    @Deprecated
    public static void ensureOffsetLength(short[][] a, long offset, long length) {
        BigArrays.ensureOffsetLength(length(a), offset, length);
    }

    @Deprecated
    public static void ensureSameLength(short[][] a, short[][] b) {
        if (length(a) != length(b)) {
            throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
        }
    }

    /* loaded from: classes4.dex */
    private static final class BigArrayHashStrategy implements Hash.Strategy<short[][]>, Serializable {
        private static final long serialVersionUID = -7046029254386353129L;

        private BigArrayHashStrategy() {
        }

        @Override // it.unimi.dsi.fastutil.Hash.Strategy
        public int hashCode(short[][] o) {
            return Arrays.deepHashCode(o);
        }

        @Override // it.unimi.dsi.fastutil.Hash.Strategy
        public boolean equals(short[][] a, short[][] b) {
            return ShortBigArrays.equals(a, b);
        }
    }

    private static ForkJoinPool getPool() {
        ForkJoinPool current = ForkJoinTask.getPool();
        return current == null ? ForkJoinPool.commonPool() : current;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void swap(short[][] x, long a, long b, long n) {
        int i = 0;
        while (i < n) {
            BigArrays.swap(x, a, b);
            i++;
            a++;
            b++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0030, code lost:            if (r1 > 0) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0029, code lost:            if (r1 < 0) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0035, code lost:            return r6;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:?, code lost:            return r10;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static long med3(short[][] r5, long r6, long r8, long r10, it.unimi.dsi.fastutil.shorts.ShortComparator r12) {
        /*
            short r0 = it.unimi.dsi.fastutil.BigArrays.get(r5, r6)
            short r1 = it.unimi.dsi.fastutil.BigArrays.get(r5, r8)
            int r0 = r12.compare(r0, r1)
            short r1 = it.unimi.dsi.fastutil.BigArrays.get(r5, r6)
            short r2 = it.unimi.dsi.fastutil.BigArrays.get(r5, r10)
            int r1 = r12.compare(r1, r2)
            short r2 = it.unimi.dsi.fastutil.BigArrays.get(r5, r8)
            short r3 = it.unimi.dsi.fastutil.BigArrays.get(r5, r10)
            int r2 = r12.compare(r2, r3)
            if (r0 >= 0) goto L2c
            if (r2 >= 0) goto L29
            goto L2e
        L29:
            if (r1 >= 0) goto L34
            goto L32
        L2c:
            if (r2 <= 0) goto L30
        L2e:
            r3 = r8
            goto L35
        L30:
            if (r1 <= 0) goto L34
        L32:
            r3 = r10
            goto L35
        L34:
            r3 = r6
        L35:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: it.unimi.dsi.fastutil.shorts.ShortBigArrays.med3(short[][], long, long, long, it.unimi.dsi.fastutil.shorts.ShortComparator):long");
    }

    private static void selectionSort(short[][] a, long from, long to, ShortComparator comp) {
        for (long i = from; i < to - 1; i++) {
            long m = i;
            for (long j = i + 1; j < to; j++) {
                if (comp.compare(BigArrays.get(a, j), BigArrays.get(a, m)) < 0) {
                    m = j;
                }
            }
            if (m != i) {
                BigArrays.swap(a, i, m);
            }
        }
    }

    public static void quickSort(short[][] x, long from, long to, ShortComparator comp) {
        long d;
        long c;
        int comparison;
        int comparison2;
        ShortComparator shortComparator = comp;
        long len = to - from;
        if (len < 7) {
            selectionSort(x, from, to, comp);
            return;
        }
        long m = from + (len / 2);
        if (len > 7) {
            long l = from;
            long n = to - 1;
            if (len > 40) {
                long s = len / 8;
                l = med3(x, l, l + s, l + (s * 2), comp);
                m = med3(x, m - s, m, m + s, comp);
                n = med3(x, n - (2 * s), n - s, n, comp);
            }
            m = med3(x, l, m, n, comp);
        }
        short v = BigArrays.get(x, m);
        long c2 = to - 1;
        long c3 = c2;
        long a = from;
        long b = c2;
        long b2 = from;
        while (true) {
            if (b2 <= b && (comparison2 = shortComparator.compare(BigArrays.get(x, b2), v)) <= 0) {
                if (comparison2 == 0) {
                    BigArrays.swap(x, a, b2);
                    a++;
                }
                b2++;
            } else {
                d = c3;
                c = b;
                while (c >= b2 && (comparison = shortComparator.compare(BigArrays.get(x, c), v)) >= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, c, d);
                        d--;
                    }
                    c--;
                }
                if (b2 > c) {
                    break;
                }
                long c4 = c;
                long b3 = b2;
                long c5 = c4 - 1;
                BigArrays.swap(x, b3, c4);
                shortComparator = comp;
                b2 = b3 + 1;
                b = c5;
                v = v;
                c3 = d;
                len = len;
            }
        }
        long d2 = d;
        long c6 = c;
        long s2 = Math.min(a - from, b2 - a);
        long c7 = b2;
        swap(x, from, b2 - s2, s2);
        long s3 = Math.min(d2 - c6, (to - d2) - 1);
        swap(x, c7, to - s3, s3);
        long s4 = c7 - a;
        if (s4 > 1) {
            quickSort(x, from, from + s4, comp);
        }
        long s5 = d2 - c6;
        if (s5 > 1) {
            quickSort(x, to - s5, to, comp);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0030, code lost:            if (r1 > 0) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:4:0x0029, code lost:            if (r1 < 0) goto L11;     */
    /* JADX WARN: Code restructure failed: missing block: B:6:0x0035, code lost:            return r6;     */
    /* JADX WARN: Code restructure failed: missing block: B:9:?, code lost:            return r10;     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static long med3(short[][] r5, long r6, long r8, long r10) {
        /*
            short r0 = it.unimi.dsi.fastutil.BigArrays.get(r5, r6)
            short r1 = it.unimi.dsi.fastutil.BigArrays.get(r5, r8)
            int r0 = java.lang.Short.compare(r0, r1)
            short r1 = it.unimi.dsi.fastutil.BigArrays.get(r5, r6)
            short r2 = it.unimi.dsi.fastutil.BigArrays.get(r5, r10)
            int r1 = java.lang.Short.compare(r1, r2)
            short r2 = it.unimi.dsi.fastutil.BigArrays.get(r5, r8)
            short r3 = it.unimi.dsi.fastutil.BigArrays.get(r5, r10)
            int r2 = java.lang.Short.compare(r2, r3)
            if (r0 >= 0) goto L2c
            if (r2 >= 0) goto L29
            goto L2e
        L29:
            if (r1 >= 0) goto L34
            goto L32
        L2c:
            if (r2 <= 0) goto L30
        L2e:
            r3 = r8
            goto L35
        L30:
            if (r1 <= 0) goto L34
        L32:
            r3 = r10
            goto L35
        L34:
            r3 = r6
        L35:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: it.unimi.dsi.fastutil.shorts.ShortBigArrays.med3(short[][], long, long, long):long");
    }

    private static void selectionSort(short[][] a, long from, long to) {
        for (long i = from; i < to - 1; i++) {
            long m = i;
            for (long j = i + 1; j < to; j++) {
                if (BigArrays.get(a, j) < BigArrays.get(a, m)) {
                    m = j;
                }
            }
            if (m != i) {
                BigArrays.swap(a, i, m);
            }
        }
    }

    public static void quickSort(short[][] x, ShortComparator comp) {
        quickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static void quickSort(short[][] x, long from, long to) {
        long c;
        long d;
        int comparison;
        int comparison2;
        long d2 = to - from;
        if (d2 < 7) {
            selectionSort(x, from, to);
            return;
        }
        long m = from + (d2 / 2);
        if (d2 > 7) {
            long l = from;
            long n = to - 1;
            if (d2 > 40) {
                long s = d2 / 8;
                l = med3(x, l, l + s, l + (s * 2));
                m = med3(x, m - s, m, m + s);
                n = med3(x, n - (2 * s), n - s, n);
            }
            m = med3(x, l, m, n);
        }
        short v = BigArrays.get(x, m);
        long c2 = to - 1;
        long len = c2;
        long b = c2;
        long a = from;
        long b2 = from;
        while (true) {
            if (b2 <= b && (comparison2 = Short.compare(BigArrays.get(x, b2), v)) <= 0) {
                if (comparison2 == 0) {
                    BigArrays.swap(x, a, b2);
                    a++;
                }
                b2++;
            } else {
                c = b;
                long len2 = d2;
                d = len;
                while (c >= b2 && (comparison = Short.compare(BigArrays.get(x, c), v)) >= 0) {
                    if (comparison == 0) {
                        BigArrays.swap(x, c, d);
                        d--;
                    }
                    c--;
                }
                if (b2 > c) {
                    break;
                }
                long m2 = m;
                long m3 = c;
                long b3 = b2;
                long c3 = m3 - 1;
                BigArrays.swap(x, b3, m3);
                m = m2;
                a = a;
                b2 = b3 + 1;
                b = c3;
                long j = d;
                d2 = len2;
                len = j;
            }
        }
        long c4 = c;
        long s2 = Math.min(a - from, b2 - a);
        long a2 = b2 - s2;
        long b4 = b2;
        swap(x, from, a2, s2);
        long s3 = Math.min(d - c4, (to - d) - 1);
        swap(x, b4, to - s3, s3);
        long s4 = b4 - a;
        if (s4 > 1) {
            quickSort(x, from, from + s4);
        }
        long s5 = d - c4;
        if (s5 > 1) {
            quickSort(x, to - s5, to);
        }
    }

    public static void quickSort(short[][] x) {
        quickSort(x, 0L, BigArrays.length(x));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes4.dex */
    public static class ForkJoinQuickSort extends RecursiveAction {
        private static final long serialVersionUID = 1;
        private final long from;
        private final long to;
        private final short[][] x;

        public ForkJoinQuickSort(short[][] x, long from, long to) {
            this.from = from;
            this.to = to;
            this.x = x;
        }

        @Override // java.util.concurrent.RecursiveAction
        protected void compute() {
            long c;
            long d;
            int comparison;
            int comparison2;
            short[][] x = this.x;
            long len = this.to - this.from;
            if (len < Http2CodecUtil.DEFAULT_HEADER_LIST_SIZE) {
                ShortBigArrays.quickSort(x, this.from, this.to);
                return;
            }
            long m = this.from + (len / 2);
            long l = this.from;
            long n = this.to - serialVersionUID;
            long s = len / 8;
            long l2 = ShortBigArrays.med3(x, l, l + s, l + (s * 2));
            long m2 = ShortBigArrays.med3(x, m - s, m, m + s);
            long d2 = ShortBigArrays.med3(x, n - (2 * s), n - s, n);
            long m3 = ShortBigArrays.med3(x, l2, m2, d2);
            short v = BigArrays.get(x, m3);
            long a = this.from;
            long c2 = this.to - serialVersionUID;
            long n2 = c2;
            long a2 = a;
            long len2 = a;
            while (true) {
                if (len2 <= c2 && (comparison2 = Short.compare(BigArrays.get(x, len2), v)) <= 0) {
                    if (comparison2 == 0) {
                        long a3 = a2 + serialVersionUID;
                        BigArrays.swap(x, a2, len2);
                        a2 = a3;
                    }
                    len2 += serialVersionUID;
                } else {
                    c = c2;
                    long n3 = d2;
                    d = n2;
                    while (c >= len2 && (comparison = Short.compare(BigArrays.get(x, c), v)) >= 0) {
                        if (comparison == 0) {
                            long d3 = d - serialVersionUID;
                            BigArrays.swap(x, c, d);
                            d = d3;
                        }
                        c -= serialVersionUID;
                    }
                    if (len2 > c) {
                        break;
                    }
                    long m4 = m3;
                    long m5 = c;
                    long b = len2 + serialVersionUID;
                    c2 = m5 - serialVersionUID;
                    BigArrays.swap(x, len2, m5);
                    len2 = b;
                    a2 = a2;
                    m3 = m4;
                    long j = d;
                    d2 = n3;
                    n2 = j;
                }
            }
            long c3 = c;
            long s2 = Math.min(a2 - this.from, len2 - a2);
            ShortBigArrays.swap(x, this.from, len2 - s2, s2);
            long s3 = Math.min(d - c3, (this.to - d) - serialVersionUID);
            ShortBigArrays.swap(x, len2, this.to - s3, s3);
            long s4 = len2 - a2;
            long t = d - c3;
            if (s4 > serialVersionUID && t > serialVersionUID) {
                invokeAll(new ForkJoinQuickSort(x, this.from, this.from + s4), new ForkJoinQuickSort(x, this.to - t, this.to));
            } else if (s4 <= serialVersionUID) {
                invokeAll(new ForkJoinTask[]{new ForkJoinQuickSort(x, this.to - t, this.to)});
            } else {
                invokeAll(new ForkJoinTask[]{new ForkJoinQuickSort(x, this.from, this.from + s4)});
            }
        }
    }

    public static void parallelQuickSort(short[][] x, long from, long to) {
        ForkJoinPool pool = getPool();
        if (to - from < Http2CodecUtil.DEFAULT_HEADER_LIST_SIZE || pool.getParallelism() == 1) {
            quickSort(x, from, to);
        } else {
            pool.invoke(new ForkJoinQuickSort(x, from, to));
        }
    }

    public static void parallelQuickSort(short[][] x) {
        parallelQuickSort(x, 0L, BigArrays.length(x));
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes4.dex */
    public static class ForkJoinQuickSortComp extends RecursiveAction {
        private static final long serialVersionUID = 1;
        private final ShortComparator comp;
        private final long from;
        private final long to;
        private final short[][] x;

        public ForkJoinQuickSortComp(short[][] x, long from, long to, ShortComparator comp) {
            this.from = from;
            this.to = to;
            this.x = x;
            this.comp = comp;
        }

        @Override // java.util.concurrent.RecursiveAction
        protected void compute() {
            long c;
            long c2;
            long d;
            long n;
            short[][] x = this.x;
            long len = this.to - this.from;
            if (len < Http2CodecUtil.DEFAULT_HEADER_LIST_SIZE) {
                ShortBigArrays.quickSort(x, this.from, this.to, this.comp);
                return;
            }
            long m = this.from + (len / 2);
            long l = this.from;
            long n2 = this.to - serialVersionUID;
            long s = len / 8;
            long l2 = ShortBigArrays.med3(x, l, l + s, l + (s * 2), this.comp);
            long m2 = ShortBigArrays.med3(x, m - s, m, m + s, this.comp);
            long n3 = ShortBigArrays.med3(x, n2 - (2 * s), n2 - s, n2, this.comp);
            short v = BigArrays.get(x, ShortBigArrays.med3(x, l2, m2, n3, this.comp));
            long a = this.from;
            long c3 = this.to - serialVersionUID;
            long len2 = c3;
            long a2 = c3;
            long a3 = a;
            long b = a;
            while (true) {
                if (b <= a2) {
                    c = a2;
                    int comparison = this.comp.compare(BigArrays.get(x, b), v);
                    if (comparison <= 0) {
                        if (comparison == 0) {
                            long a4 = a3 + serialVersionUID;
                            BigArrays.swap(x, a3, b);
                            a3 = a4;
                        }
                        b += serialVersionUID;
                        a2 = c;
                    }
                } else {
                    c = a2;
                }
                c2 = c;
                long len3 = len;
                d = len2;
                while (true) {
                    if (c2 >= b) {
                        n = n3;
                        int comparison2 = this.comp.compare(BigArrays.get(x, c2), v);
                        if (comparison2 < 0) {
                            break;
                        }
                        if (comparison2 == 0) {
                            long d2 = d - serialVersionUID;
                            BigArrays.swap(x, c2, d);
                            d = d2;
                        }
                        c2 -= serialVersionUID;
                        n3 = n;
                    } else {
                        n = n3;
                        break;
                    }
                }
                if (b > c2) {
                    break;
                }
                long c4 = c2;
                long d3 = d;
                long d4 = b;
                b = d4 + serialVersionUID;
                a2 = c4 - serialVersionUID;
                BigArrays.swap(x, d4, c4);
                len = len3;
                n3 = n;
                a3 = a3;
                len2 = d3;
            }
            long c5 = c2;
            long s2 = Math.min(a3 - this.from, b - a3);
            long c6 = b;
            ShortBigArrays.swap(x, this.from, b - s2, s2);
            long s3 = Math.min(d - c5, (this.to - d) - serialVersionUID);
            ShortBigArrays.swap(x, c6, this.to - s3, s3);
            long s4 = c6 - a3;
            long t = d - c5;
            if (s4 > serialVersionUID && t > serialVersionUID) {
                invokeAll(new ForkJoinQuickSortComp(x, this.from, this.from + s4, this.comp), new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp));
            } else if (s4 <= serialVersionUID) {
                invokeAll(new ForkJoinTask[]{new ForkJoinQuickSortComp(x, this.to - t, this.to, this.comp)});
            } else {
                invokeAll(new ForkJoinTask[]{new ForkJoinQuickSortComp(x, this.from, this.from + s4, this.comp)});
            }
        }
    }

    public static void parallelQuickSort(short[][] x, long from, long to, ShortComparator comp) {
        ForkJoinPool pool = getPool();
        if (to - from < Http2CodecUtil.DEFAULT_HEADER_LIST_SIZE || pool.getParallelism() == 1) {
            quickSort(x, from, to, comp);
        } else {
            pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
        }
    }

    public static void parallelQuickSort(short[][] x, ShortComparator comp) {
        parallelQuickSort(x, 0L, BigArrays.length(x), comp);
    }

    public static long binarySearch(short[][] a, long from, long to, short key) {
        long to2 = to - 1;
        while (from <= to2) {
            long mid = (from + to2) >>> 1;
            short midVal = BigArrays.get(a, mid);
            if (midVal < key) {
                from = mid + 1;
            } else if (midVal > key) {
                to2 = mid - 1;
            } else {
                return mid;
            }
        }
        return -(1 + from);
    }

    public static long binarySearch(short[][] a, short key) {
        return binarySearch(a, 0L, BigArrays.length(a), key);
    }

    public static long binarySearch(short[][] a, long from, long to, short key, ShortComparator c) {
        long to2 = to - 1;
        while (from <= to2) {
            long mid = (from + to2) >>> 1;
            short midVal = BigArrays.get(a, mid);
            int cmp = c.compare(midVal, key);
            if (cmp < 0) {
                from = mid + 1;
            } else if (cmp > 0) {
                to2 = mid - 1;
            } else {
                return mid;
            }
        }
        return -(1 + from);
    }

    public static long binarySearch(short[][] a, short key, ShortComparator c) {
        return binarySearch(a, 0L, BigArrays.length(a), key, c);
    }

    public static void radixSort(short[][] a) {
        radixSort(a, 0L, BigArrays.length(a));
    }

    public static void radixSort(short[][] a, long from, long to) {
        int levelPos;
        int maxLevel = 1;
        int stackSize = 256;
        long[] offsetStack = new long[256];
        long[] lengthStack = new long[256];
        int[] levelStack = new int[256];
        int lengthPos = 0 + 1;
        offsetStack[0] = from;
        int lengthPos2 = 0 + 1;
        lengthStack[0] = to - from;
        int levelPos2 = 0 + 1;
        levelStack[0] = 0;
        long[] count = new long[256];
        long[] pos = new long[256];
        byte[][] digit = ByteBigArrays.newBigArray(to - from);
        while (lengthPos > 0) {
            lengthPos--;
            long first = offsetStack[lengthPos];
            lengthPos2--;
            long length = lengthStack[lengthPos2];
            levelPos2--;
            int level = levelStack[levelPos2];
            int signMask = level % 2 == 0 ? 128 : 0;
            if (length < 40) {
                selectionSort(a, first, first + length);
                offsetStack = offsetStack;
            } else {
                long[] offsetStack2 = offsetStack;
                int shift = (1 - (level % 2)) * 8;
                long i = length;
                while (true) {
                    levelPos = lengthPos2;
                    long i2 = i - 1;
                    if (i == 0) {
                        break;
                    }
                    BigArrays.set(digit, i2, (byte) (((BigArrays.get(a, first + i2) >>> shift) & 255) ^ signMask));
                    maxLevel = maxLevel;
                    stackSize = stackSize;
                    i = i2;
                    lengthPos2 = levelPos;
                }
                int maxLevel2 = maxLevel;
                int stackSize2 = stackSize;
                long i3 = length;
                while (true) {
                    long i4 = i3 - 1;
                    if (i3 == 0) {
                        break;
                    }
                    int i5 = BigArrays.get(digit, i4) & 255;
                    count[i5] = count[i5] + 1;
                    i3 = i4;
                }
                int lastUsed = -1;
                long p = 0;
                int i6 = 0;
                while (true) {
                    int shift2 = shift;
                    if (i6 >= 256) {
                        break;
                    }
                    if (count[i6] != 0) {
                        lastUsed = i6;
                        if (level < 1 && count[i6] > 1) {
                            int offsetPos = lengthPos + 1;
                            offsetStack2[lengthPos] = p + first;
                            int lengthPos3 = levelPos + 1;
                            lengthStack[levelPos] = count[i6];
                            levelStack[levelPos2] = level + 1;
                            levelPos2++;
                            levelPos = lengthPos3;
                            lengthPos = offsetPos;
                        }
                    }
                    long j = p + count[i6];
                    p = j;
                    pos[i6] = j;
                    i6++;
                    shift = shift2;
                }
                long end = length - count[lastUsed];
                count[lastUsed] = 0;
                long p2 = 0;
                while (p2 < end) {
                    int lastUsed2 = lastUsed;
                    long end2 = end;
                    short t = BigArrays.get(a, p2 + first);
                    int c = BigArrays.get(digit, p2) & 255;
                    while (true) {
                        long d = pos[c] - 1;
                        pos[c] = d;
                        if (d > p2) {
                            short z = t;
                            int zz = c;
                            int offsetPos2 = lengthPos;
                            t = BigArrays.get(a, d + first);
                            int c2 = BigArrays.get(digit, d) & 255;
                            c = c2;
                            BigArrays.set(a, d + first, z);
                            BigArrays.set(digit, d, (byte) zz);
                            level = level;
                            lengthPos = offsetPos2;
                            lengthStack = lengthStack;
                            levelPos2 = levelPos2;
                        }
                    }
                    BigArrays.set(a, p2 + first, t);
                    p2 += count[c];
                    count[c] = 0;
                    lastUsed = lastUsed2;
                    end = end2;
                    lengthStack = lengthStack;
                    levelPos2 = levelPos2;
                }
                offsetStack = offsetStack2;
                maxLevel = maxLevel2;
                stackSize = stackSize2;
                lengthPos2 = levelPos;
            }
        }
    }

    private static void selectionSort(short[][] a, short[][] b, long from, long to) {
        for (long i = from; i < to - 1; i++) {
            long m = i;
            for (long j = i + 1; j < to; j++) {
                if (BigArrays.get(a, j) < BigArrays.get(a, m) || (BigArrays.get(a, j) == BigArrays.get(a, m) && BigArrays.get(b, j) < BigArrays.get(b, m))) {
                    m = j;
                }
            }
            if (m != i) {
                short t = BigArrays.get(a, i);
                BigArrays.set(a, i, BigArrays.get(a, m));
                BigArrays.set(a, m, t);
                short t2 = BigArrays.get(b, i);
                BigArrays.set(b, i, BigArrays.get(b, m));
                BigArrays.set(b, m, t2);
            }
        }
    }

    public static void radixSort(short[][] a, short[][] b) {
        radixSort(a, b, 0L, BigArrays.length(a));
    }

    public static void radixSort(short[][] a, short[][] b, long from, long to) {
        int layers;
        int maxLevel;
        int layers2 = 2;
        if (BigArrays.length(a) != BigArrays.length(b)) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        int maxLevel2 = 3;
        int zz = 766;
        long[] offsetStack = new long[766];
        long[] lengthStack = new long[766];
        int[] levelStack = new int[766];
        int offsetPos = 0 + 1;
        offsetStack[0] = from;
        int lengthPos = 0 + 1;
        lengthStack[0] = to - from;
        int levelPos = 0 + 1;
        int i = 0;
        levelStack[0] = 0;
        long[] count = new long[256];
        long[] pos = new long[256];
        byte[][] digit = ByteBigArrays.newBigArray(to - from);
        while (offsetPos > 0) {
            int lengthPos2 = offsetPos - 1;
            long first = offsetStack[lengthPos2];
            int levelPos2 = lengthPos - 1;
            long length = lengthStack[levelPos2];
            int levelPos3 = levelPos - 1;
            int levelPos4 = levelStack[levelPos3];
            int signMask = levelPos4 % 2 == 0 ? 128 : i;
            if (length < 40) {
                selectionSort(a, b, first, first + length);
                digit = digit;
                offsetPos = lengthPos2;
                lengthPos = levelPos2;
                levelPos = levelPos3;
                count = count;
                pos = pos;
                i = 0;
            } else {
                byte[][] digit2 = digit;
                long[] count2 = count;
                long[] pos2 = pos;
                short[][] k = levelPos4 < 2 ? a : b;
                int shift = (1 - (levelPos4 % 2)) * 8;
                long i2 = length;
                while (true) {
                    layers = layers2;
                    maxLevel = maxLevel2;
                    long i3 = i2 - 1;
                    if (i2 == 0) {
                        break;
                    }
                    BigArrays.set(digit2, i3, (byte) (((BigArrays.get(k, first + i3) >>> shift) & 255) ^ signMask));
                    i2 = i3;
                    layers2 = layers;
                    maxLevel2 = maxLevel;
                }
                long i4 = length;
                while (true) {
                    long i5 = i4 - 1;
                    if (i4 == 0) {
                        break;
                    }
                    int i6 = BigArrays.get(digit2, i5) & 255;
                    count2[i6] = count2[i6] + 1;
                    i4 = i5;
                }
                int lastUsed = -1;
                long p = 0;
                for (int i7 = 0; i7 < 256; i7++) {
                    if (count2[i7] != 0) {
                        lastUsed = i7;
                        if (levelPos4 < 3 && count2[i7] > 1) {
                            int offsetPos2 = lengthPos2 + 1;
                            offsetStack[lengthPos2] = p + first;
                            int lengthPos3 = levelPos2 + 1;
                            lengthStack[levelPos2] = count2[i7];
                            levelStack[levelPos3] = levelPos4 + 1;
                            levelPos3++;
                            levelPos2 = lengthPos3;
                            lengthPos2 = offsetPos2;
                        }
                    }
                    long j = p + count2[i7];
                    p = j;
                    pos2[i7] = j;
                }
                long end = length - count2[lastUsed];
                count2[lastUsed] = 0;
                long i8 = 0;
                while (i8 < end) {
                    short[][] k2 = k;
                    int shift2 = shift;
                    short t = BigArrays.get(a, i8 + first);
                    short u = BigArrays.get(b, i8 + first);
                    int c = BigArrays.get(digit2, i8) & 255;
                    short u2 = u;
                    short u3 = t;
                    while (true) {
                        long d = pos2[c] - 1;
                        pos2[c] = d;
                        if (d > i8) {
                            short z = u3;
                            int zz2 = c;
                            int stackSize = zz;
                            short t2 = BigArrays.get(a, d + first);
                            BigArrays.set(a, d + first, z);
                            short z2 = u2;
                            u2 = BigArrays.get(b, d + first);
                            BigArrays.set(b, d + first, z2);
                            c = BigArrays.get(digit2, d) & 255;
                            BigArrays.set(digit2, d, (byte) zz2);
                            zz = stackSize;
                            offsetStack = offsetStack;
                            p = p;
                            u3 = t2;
                        }
                    }
                    int stackSize2 = zz;
                    BigArrays.set(a, i8 + first, u3);
                    BigArrays.set(b, i8 + first, u2);
                    i8 += count2[c];
                    count2[c] = 0;
                    k = k2;
                    shift = shift2;
                    zz = stackSize2;
                    offsetStack = offsetStack;
                    p = p;
                }
                digit = digit2;
                offsetPos = lengthPos2;
                lengthPos = levelPos2;
                levelPos = levelPos3;
                layers2 = layers;
                maxLevel2 = maxLevel;
                count = count2;
                pos = pos2;
                i = 0;
            }
        }
    }

    private static void insertionSortIndirect(long[][] perm, short[][] a, short[][] b, long from, long to) {
        long i = from;
        while (true) {
            long j = i + 1;
            i = j;
            if (j < to) {
                long t = BigArrays.get(perm, i);
                long j2 = i;
                long u = BigArrays.get(perm, j2 - 1);
                while (true) {
                    if (BigArrays.get(a, t) < BigArrays.get(a, u) || (BigArrays.get(a, t) == BigArrays.get(a, u) && BigArrays.get(b, t) < BigArrays.get(b, u))) {
                        BigArrays.set(perm, j2, u);
                        if (from != j2 - 1) {
                            long j3 = j2 - 1;
                            j2 = j3;
                            u = BigArrays.get(perm, j3 - 1);
                        } else {
                            j2--;
                            break;
                        }
                    }
                }
                BigArrays.set(perm, j2, t);
            } else {
                return;
            }
        }
    }

    public static void radixSortIndirect(long[][] perm, short[][] a, short[][] b, boolean stable) {
        ensureSameLength(a, b);
        radixSortIndirect(perm, a, b, 0L, BigArrays.length(a), stable);
    }

    public static void radixSortIndirect(long[][] perm, short[][] a, short[][] b, long from, long to, boolean stable) {
        long[][] jArr;
        long[] pos;
        int[] levelStack;
        int layers;
        int maxLevel;
        long[][] support;
        long[] count;
        int stackSize;
        long j;
        int stackSize2;
        int c;
        boolean z;
        long i;
        int maxLevel2;
        long[] count2;
        if (to - from < RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE) {
            insertionSortIndirect(perm, a, b, from, to);
            return;
        }
        int layers2 = 2;
        int maxLevel3 = 3;
        int stackSize3 = 766;
        long[] offsetStack = new long[766];
        long[] lengthStack = new long[766];
        int[] levelStack2 = new int[766];
        offsetStack[0] = from;
        lengthStack[0] = to - from;
        int stackPos = 0 + 1;
        levelStack2[0] = 0;
        int stackSize4 = 256;
        long[] count3 = new long[256];
        long[] pos2 = new long[256];
        if (stable) {
            jArr = LongBigArrays.newBigArray(BigArrays.length(perm));
        } else {
            jArr = null;
        }
        long[][] support2 = jArr;
        while (stackPos > 0) {
            int stackPos2 = stackPos - 1;
            long first = offsetStack[stackPos2];
            long length = lengthStack[stackPos2];
            int level = levelStack2[stackPos2];
            int signMask = level % 2 == 0 ? 128 : 0;
            short[][] k = level < 2 ? a : b;
            int shift = (1 - (level % 2)) * 8;
            long i2 = first + length;
            while (true) {
                long i3 = i2 - 1;
                if (i2 == first) {
                    break;
                }
                int i4 = ((BigArrays.get(k, BigArrays.get(perm, i3)) >>> shift) & 255) ^ signMask;
                count3[i4] = count3[i4] + 1;
                level = level;
                i2 = i3;
            }
            int level2 = level;
            long p = stable ? 0L : first;
            int lastUsed = -1;
            for (int i5 = 0; i5 < stackSize4; i5++) {
                if (count3[i5] != 0) {
                    lastUsed = i5;
                }
                long j2 = p + count3[i5];
                p = j2;
                pos2[i5] = j2;
            }
            if (stable) {
                long i6 = first + length;
                while (true) {
                    long i7 = i6 - 1;
                    if (i6 == first) {
                        break;
                    }
                    int i8 = ((BigArrays.get(k, BigArrays.get(perm, i7)) >>> shift) & 255) ^ signMask;
                    long j3 = pos2[i8] - 1;
                    pos2[i8] = j3;
                    BigArrays.set(support2, j3, BigArrays.get(perm, i7));
                    k = k;
                    count3 = count3;
                    pos2 = pos2;
                    i6 = i7;
                }
                support = support2;
                long[] count4 = count3;
                pos = pos2;
                levelStack = levelStack2;
                layers = layers2;
                BigArrays.copy(support2, 0L, perm, first, length);
                long p2 = first;
                int i9 = 0;
                for (int layers3 = stackSize4; i9 < layers3; layers3 = 256) {
                    if (level2 < 3) {
                        long[] count5 = count4;
                        if (count5[i9] <= 1) {
                            count2 = count5;
                            maxLevel2 = maxLevel3;
                        } else if (count5[i9] < RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE) {
                            count2 = count5;
                            maxLevel2 = maxLevel3;
                            insertionSortIndirect(perm, a, b, p2, p2 + count5[i9]);
                        } else {
                            count2 = count5;
                            maxLevel2 = maxLevel3;
                            offsetStack[stackPos2] = p2;
                            lengthStack[stackPos2] = count2[i9];
                            levelStack[stackPos2] = level2 + 1;
                            stackPos2++;
                        }
                    } else {
                        maxLevel2 = maxLevel3;
                        count2 = count4;
                    }
                    p2 += count2[i9];
                    i9++;
                    count4 = count2;
                    maxLevel3 = maxLevel2;
                }
                maxLevel = maxLevel3;
                count = count4;
                Arrays.fill(count, 0L);
                stackSize = stackSize3;
                stackPos = stackPos2;
                j = RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE;
            } else {
                pos = pos2;
                levelStack = levelStack2;
                layers = layers2;
                maxLevel = maxLevel3;
                support = support2;
                count = count3;
                long end = (first + length) - count[lastUsed];
                long i10 = first;
                while (i10 <= end) {
                    long t = BigArrays.get(perm, i10);
                    int c2 = ((BigArrays.get(k, t) >>> shift) & 255) ^ signMask;
                    if (i10 >= end) {
                        stackSize2 = stackSize3;
                        c = c2;
                    } else {
                        while (true) {
                            long d = pos[c2] - 1;
                            pos[c2] = d;
                            if (d <= i10) {
                                break;
                            }
                            long z2 = t;
                            t = BigArrays.get(perm, d);
                            BigArrays.set(perm, d, z2);
                            c2 = ((BigArrays.get(k, t) >>> shift) & 255) ^ signMask;
                            stackSize3 = stackSize3;
                        }
                        stackSize2 = stackSize3;
                        c = c2;
                        BigArrays.set(perm, i10, t);
                    }
                    if (level2 >= 3 || count[c] <= 1) {
                        z = 3;
                        i = i10;
                    } else if (count[c] < RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE) {
                        z = 3;
                        i = i10;
                        insertionSortIndirect(perm, a, b, i10, i10 + count[c]);
                    } else {
                        z = 3;
                        i = i10;
                        offsetStack[stackPos2] = i;
                        lengthStack[stackPos2] = count[c];
                        levelStack[stackPos2] = level2 + 1;
                        stackPos2++;
                    }
                    i10 = i + count[c];
                    count[c] = 0;
                    stackSize3 = stackSize2;
                }
                stackSize = stackSize3;
                j = RealWebSocket.DEFAULT_MINIMUM_DEFLATE_SIZE;
                stackPos = stackPos2;
            }
            stackSize3 = stackSize;
            count3 = count;
            support2 = support;
            pos2 = pos;
            levelStack2 = levelStack;
            layers2 = layers;
            maxLevel3 = maxLevel;
            stackSize4 = 256;
        }
    }

    public static short[][] shuffle(short[][] a, long from, long to, Random random) {
        return BigArrays.shuffle(a, from, to, random);
    }

    public static short[][] shuffle(short[][] a, Random random) {
        return BigArrays.shuffle(a, random);
    }
}