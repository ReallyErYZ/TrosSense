package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import java.io.Serializable;
import java.util.Objects;

/* loaded from: classes4.dex */
public class LongFloatMutablePair implements LongFloatPair, Serializable {
    private static final long serialVersionUID = 0;
    protected long left;
    protected float right;

    public LongFloatMutablePair(long left, float right) {
        this.left = left;
        this.right = right;
    }

    public static LongFloatMutablePair of(long left, float right) {
        return new LongFloatMutablePair(left, right);
    }

    @Override // it.unimi.dsi.fastutil.longs.LongFloatPair
    public long leftLong() {
        return this.left;
    }

    @Override // it.unimi.dsi.fastutil.longs.LongFloatPair
    public LongFloatMutablePair left(long l) {
        this.left = l;
        return this;
    }

    @Override // it.unimi.dsi.fastutil.longs.LongFloatPair
    public float rightFloat() {
        return this.right;
    }

    @Override // it.unimi.dsi.fastutil.longs.LongFloatPair
    public LongFloatMutablePair right(float r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        return other instanceof LongFloatPair ? this.left == ((LongFloatPair) other).leftLong() && this.right == ((LongFloatPair) other).rightFloat() : (other instanceof Pair) && Objects.equals(Long.valueOf(this.left), ((Pair) other).left()) && Objects.equals(Float.valueOf(this.right), ((Pair) other).right());
    }

    public int hashCode() {
        return (HashCommon.long2int(this.left) * 19) + HashCommon.float2int(this.right);
    }

    public String toString() {
        return "<" + leftLong() + "," + rightFloat() + ">";
    }
}