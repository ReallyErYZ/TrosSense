package kotlin.collections;

import kotlin.Metadata;
import kotlin.UByteArray;
import kotlin.UShort;
import kotlin.UShortArray;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: UArraySorting.kt */
@Metadata(d1 = {"\u00000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0010\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0006\u0010\u0007\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\t\u0010\n\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\f\u0010\r\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u000f\u0010\u0010\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0013\u0010\u0014\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0015\u0010\u0016\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0017\u0010\u0018\u001a*\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0003ø\u0001\u0000¢\u0006\u0004\b\u0019\u0010\u001a\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b\u001e\u0010\u0014\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\b2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b\u001f\u0010\u0016\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000b2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b \u0010\u0018\u001a*\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0002\u001a\u00020\u000e2\u0006\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\u0001H\u0001ø\u0001\u0000¢\u0006\u0004\b!\u0010\u001a\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006\""}, d2 = {"partition", "", "array", "Lkotlin/UByteArray;", "left", "right", "partition-4UcCI2c", "([BII)I", "Lkotlin/UIntArray;", "partition-oBK06Vg", "([III)I", "Lkotlin/ULongArray;", "partition--nroSd4", "([JII)I", "Lkotlin/UShortArray;", "partition-Aa5vz7o", "([SII)I", "quickSort", "", "quickSort-4UcCI2c", "([BII)V", "quickSort-oBK06Vg", "([III)V", "quickSort--nroSd4", "([JII)V", "quickSort-Aa5vz7o", "([SII)V", "sortArray", "fromIndex", "toIndex", "sortArray-4UcCI2c", "sortArray-oBK06Vg", "sortArray--nroSd4", "sortArray-Aa5vz7o", "kotlin-stdlib"}, k = 2, mv = {1, 8, 0}, xi = 48)
/* loaded from: classes4.dex */
public final class UArraySortingKt {
    /* renamed from: partition-4UcCI2c, reason: not valid java name */
    private static final int m736partition4UcCI2c(byte[] array, int left, int right) {
        int i = left;
        int j = right;
        byte pivot = UByteArray.m356getw2LRezQ(array, (left + right) / 2);
        while (i <= j) {
            while (Intrinsics.compare(UByteArray.m356getw2LRezQ(array, i) & 255, pivot & 255) < 0) {
                i++;
            }
            while (Intrinsics.compare(UByteArray.m356getw2LRezQ(array, j) & 255, pivot & 255) > 0) {
                j--;
            }
            if (i <= j) {
                byte tmp = UByteArray.m356getw2LRezQ(array, i);
                UByteArray.m361setVurrAj0(array, i, UByteArray.m356getw2LRezQ(array, j));
                UByteArray.m361setVurrAj0(array, j, tmp);
                i++;
                j--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-4UcCI2c, reason: not valid java name */
    private static final void m740quickSort4UcCI2c(byte[] array, int left, int right) {
        int index = m736partition4UcCI2c(array, left, right);
        if (left < index - 1) {
            m740quickSort4UcCI2c(array, left, index - 1);
        }
        if (index < right) {
            m740quickSort4UcCI2c(array, index, right);
        }
    }

    /* renamed from: partition-Aa5vz7o, reason: not valid java name */
    private static final int m737partitionAa5vz7o(short[] array, int left, int right) {
        int i = left;
        int j = right;
        short pivot = UShortArray.m619getMh2AYeg(array, (left + right) / 2);
        while (i <= j) {
            while (Intrinsics.compare(UShortArray.m619getMh2AYeg(array, i) & UShort.MAX_VALUE, pivot & UShort.MAX_VALUE) < 0) {
                i++;
            }
            while (Intrinsics.compare(UShortArray.m619getMh2AYeg(array, j) & UShort.MAX_VALUE, pivot & UShort.MAX_VALUE) > 0) {
                j--;
            }
            if (i <= j) {
                short tmp = UShortArray.m619getMh2AYeg(array, i);
                UShortArray.m624set01HTLdE(array, i, UShortArray.m619getMh2AYeg(array, j));
                UShortArray.m624set01HTLdE(array, j, tmp);
                i++;
                j--;
            }
        }
        return i;
    }

    /* renamed from: quickSort-Aa5vz7o, reason: not valid java name */
    private static final void m741quickSortAa5vz7o(short[] array, int left, int right) {
        int index = m737partitionAa5vz7o(array, left, right);
        if (left < index - 1) {
            m741quickSortAa5vz7o(array, left, index - 1);
        }
        if (index < right) {
            m741quickSortAa5vz7o(array, index, right);
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:4:0x0014 */
    /* JADX WARN: Incorrect condition in loop: B:8:0x0021 */
    /* renamed from: partition-oBK06Vg, reason: not valid java name */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static final int m738partitionoBK06Vg(int[] r5, int r6, int r7) {
        /*
            r0 = r6
            r1 = r7
            int r2 = r6 + r7
            int r2 = r2 / 2
            int r2 = kotlin.UIntArray.m435getpVg5ArA(r5, r2)
        La:
            if (r0 > r1) goto L3c
        Lc:
            int r3 = kotlin.UIntArray.m435getpVg5ArA(r5, r0)
            int r3 = kotlin.UByte$$ExternalSyntheticBackport2.m(r3, r2)
            if (r3 >= 0) goto L19
            int r0 = r0 + 1
            goto Lc
        L19:
            int r3 = kotlin.UIntArray.m435getpVg5ArA(r5, r1)
            int r3 = kotlin.UByte$$ExternalSyntheticBackport2.m(r3, r2)
            if (r3 <= 0) goto L26
            int r1 = r1 + (-1)
            goto L19
        L26:
            if (r0 > r1) goto La
            int r3 = kotlin.UIntArray.m435getpVg5ArA(r5, r0)
            int r4 = kotlin.UIntArray.m435getpVg5ArA(r5, r1)
            kotlin.UIntArray.m440setVXSXFK8(r5, r0, r4)
            kotlin.UIntArray.m440setVXSXFK8(r5, r1, r3)
            int r0 = r0 + 1
            int r1 = r1 + (-1)
            goto La
        L3c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.UArraySortingKt.m738partitionoBK06Vg(int[], int, int):int");
    }

    /* renamed from: quickSort-oBK06Vg, reason: not valid java name */
    private static final void m742quickSortoBK06Vg(int[] array, int left, int right) {
        int index = m738partitionoBK06Vg(array, left, right);
        if (left < index - 1) {
            m742quickSortoBK06Vg(array, left, index - 1);
        }
        if (index < right) {
            m742quickSortoBK06Vg(array, index, right);
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:4:0x0014 */
    /* JADX WARN: Incorrect condition in loop: B:8:0x0021 */
    /* renamed from: partition--nroSd4, reason: not valid java name */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static final int m735partitionnroSd4(long[] r8, int r9, int r10) {
        /*
            r0 = r9
            r1 = r10
            int r2 = r9 + r10
            int r2 = r2 / 2
            long r2 = kotlin.ULongArray.m514getsVKNKU(r8, r2)
        La:
            if (r0 > r1) goto L3c
        Lc:
            long r4 = kotlin.ULongArray.m514getsVKNKU(r8, r0)
            int r4 = kotlin.UByte$$ExternalSyntheticBackport4.m(r4, r2)
            if (r4 >= 0) goto L19
            int r0 = r0 + 1
            goto Lc
        L19:
            long r4 = kotlin.ULongArray.m514getsVKNKU(r8, r1)
            int r4 = kotlin.UByte$$ExternalSyntheticBackport4.m(r4, r2)
            if (r4 <= 0) goto L26
            int r1 = r1 + (-1)
            goto L19
        L26:
            if (r0 > r1) goto La
            long r4 = kotlin.ULongArray.m514getsVKNKU(r8, r0)
            long r6 = kotlin.ULongArray.m514getsVKNKU(r8, r1)
            kotlin.ULongArray.m519setk8EXiF4(r8, r0, r6)
            kotlin.ULongArray.m519setk8EXiF4(r8, r1, r4)
            int r0 = r0 + 1
            int r1 = r1 + (-1)
            goto La
        L3c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.UArraySortingKt.m735partitionnroSd4(long[], int, int):int");
    }

    /* renamed from: quickSort--nroSd4, reason: not valid java name */
    private static final void m739quickSortnroSd4(long[] array, int left, int right) {
        int index = m735partitionnroSd4(array, left, right);
        if (left < index - 1) {
            m739quickSortnroSd4(array, left, index - 1);
        }
        if (index < right) {
            m739quickSortnroSd4(array, index, right);
        }
    }

    /* renamed from: sortArray-4UcCI2c, reason: not valid java name */
    public static final void m744sortArray4UcCI2c(byte[] array, int fromIndex, int toIndex) {
        Intrinsics.checkNotNullParameter(array, "array");
        m740quickSort4UcCI2c(array, fromIndex, toIndex - 1);
    }

    /* renamed from: sortArray-Aa5vz7o, reason: not valid java name */
    public static final void m745sortArrayAa5vz7o(short[] array, int fromIndex, int toIndex) {
        Intrinsics.checkNotNullParameter(array, "array");
        m741quickSortAa5vz7o(array, fromIndex, toIndex - 1);
    }

    /* renamed from: sortArray-oBK06Vg, reason: not valid java name */
    public static final void m746sortArrayoBK06Vg(int[] array, int fromIndex, int toIndex) {
        Intrinsics.checkNotNullParameter(array, "array");
        m742quickSortoBK06Vg(array, fromIndex, toIndex - 1);
    }

    /* renamed from: sortArray--nroSd4, reason: not valid java name */
    public static final void m743sortArraynroSd4(long[] array, int fromIndex, int toIndex) {
        Intrinsics.checkNotNullParameter(array, "array");
        m739quickSortnroSd4(array, fromIndex, toIndex - 1);
    }
}
