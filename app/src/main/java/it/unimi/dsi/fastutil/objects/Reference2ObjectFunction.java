package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Function;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction;
import it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction;
import it.unimi.dsi.fastutil.chars.Char2ObjectFunction;
import it.unimi.dsi.fastutil.chars.Char2ReferenceFunction;
import it.unimi.dsi.fastutil.doubles.Double2ObjectFunction;
import it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction;
import it.unimi.dsi.fastutil.floats.Float2ObjectFunction;
import it.unimi.dsi.fastutil.floats.Float2ReferenceFunction;
import it.unimi.dsi.fastutil.ints.Int2ObjectFunction;
import it.unimi.dsi.fastutil.ints.Int2ReferenceFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ReferenceFunction;
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction;
import it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction;

@FunctionalInterface
/* loaded from: classes4.dex */
public interface Reference2ObjectFunction<K, V> extends Function<K, V> {
    @Override // it.unimi.dsi.fastutil.Function
    V get(Object obj);

    @Override // it.unimi.dsi.fastutil.Function
    default V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override // it.unimi.dsi.fastutil.Function
    default V getOrDefault(Object key, V defaultValue) {
        V v = get(key);
        return (v != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
    }

    @Override // it.unimi.dsi.fastutil.Function
    default V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    default void defaultReturnValue(V rv) {
        throw new UnsupportedOperationException();
    }

    default V defaultReturnValue() {
        return null;
    }

    default Reference2ByteFunction<K> andThenByte(final Object2ByteFunction<V> after) {
        return new Reference2ByteFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda3
            @Override // it.unimi.dsi.fastutil.objects.Reference2ByteFunction
            public final byte getByte(Object obj) {
                byte b;
                b = after.getByte(Reference2ObjectFunction.this.get(obj));
                return b;
            }
        };
    }

    default Byte2ObjectFunction<V> composeByte(final Byte2ReferenceFunction<K> before) {
        return new Byte2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda10
            @Override // it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction
            public final Object get(byte b) {
                Object obj;
                obj = Reference2ObjectFunction.this.get(before.get(b));
                return obj;
            }
        };
    }

    default Reference2ShortFunction<K> andThenShort(final Object2ShortFunction<V> after) {
        return new Reference2ShortFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda8
            @Override // it.unimi.dsi.fastutil.objects.Reference2ShortFunction
            public final short getShort(Object obj) {
                short s;
                s = after.getShort(Reference2ObjectFunction.this.get(obj));
                return s;
            }
        };
    }

    default Short2ObjectFunction<V> composeShort(final Short2ReferenceFunction<K> before) {
        return new Short2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda4
            @Override // it.unimi.dsi.fastutil.shorts.Short2ObjectFunction
            public final Object get(short s) {
                Object obj;
                obj = Reference2ObjectFunction.this.get(before.get(s));
                return obj;
            }
        };
    }

    default Reference2IntFunction<K> andThenInt(final Object2IntFunction<V> after) {
        return new Reference2IntFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda9
            @Override // it.unimi.dsi.fastutil.objects.Reference2IntFunction
            public final int getInt(Object obj) {
                int i;
                i = after.getInt(Reference2ObjectFunction.this.get(obj));
                return i;
            }
        };
    }

    default Int2ObjectFunction<V> composeInt(final Int2ReferenceFunction<K> before) {
        return new Int2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda7
            @Override // it.unimi.dsi.fastutil.ints.Int2ObjectFunction
            public final Object get(int i) {
                Object obj;
                obj = Reference2ObjectFunction.this.get(before.get(i));
                return obj;
            }
        };
    }

    default Reference2LongFunction<K> andThenLong(final Object2LongFunction<V> after) {
        return new Reference2LongFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda12
            @Override // it.unimi.dsi.fastutil.objects.Reference2LongFunction
            public final long getLong(Object obj) {
                long j;
                j = after.getLong(Reference2ObjectFunction.this.get(obj));
                return j;
            }
        };
    }

    default Long2ObjectFunction<V> composeLong(final Long2ReferenceFunction<K> before) {
        return new Long2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda2
            @Override // it.unimi.dsi.fastutil.longs.Long2ObjectFunction
            public final Object get(long j) {
                Object obj;
                obj = Reference2ObjectFunction.this.get(before.get(j));
                return obj;
            }
        };
    }

    default Reference2CharFunction<K> andThenChar(final Object2CharFunction<V> after) {
        return new Reference2CharFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda11
            @Override // it.unimi.dsi.fastutil.objects.Reference2CharFunction
            public final char getChar(Object obj) {
                char c;
                c = after.getChar(Reference2ObjectFunction.this.get(obj));
                return c;
            }
        };
    }

    default Char2ObjectFunction<V> composeChar(final Char2ReferenceFunction<K> before) {
        return new Char2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda14
            @Override // it.unimi.dsi.fastutil.chars.Char2ObjectFunction
            public final Object get(char c) {
                Object obj;
                obj = Reference2ObjectFunction.this.get(before.get(c));
                return obj;
            }
        };
    }

    default Reference2FloatFunction<K> andThenFloat(final Object2FloatFunction<V> after) {
        return new Reference2FloatFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda15
            @Override // it.unimi.dsi.fastutil.objects.Reference2FloatFunction
            public final float getFloat(Object obj) {
                float f;
                f = after.getFloat(Reference2ObjectFunction.this.get(obj));
                return f;
            }
        };
    }

    default Float2ObjectFunction<V> composeFloat(final Float2ReferenceFunction<K> before) {
        return new Float2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda6
            @Override // it.unimi.dsi.fastutil.floats.Float2ObjectFunction
            public final Object get(float f) {
                Object obj;
                obj = Reference2ObjectFunction.this.get(before.get(f));
                return obj;
            }
        };
    }

    default Reference2DoubleFunction<K> andThenDouble(final Object2DoubleFunction<V> after) {
        return new Reference2DoubleFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda1
            @Override // it.unimi.dsi.fastutil.objects.Reference2DoubleFunction
            public final double getDouble(Object obj) {
                double d;
                d = after.getDouble(Reference2ObjectFunction.this.get(obj));
                return d;
            }
        };
    }

    default Double2ObjectFunction<V> composeDouble(final Double2ReferenceFunction<K> before) {
        return new Double2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda13
            @Override // it.unimi.dsi.fastutil.doubles.Double2ObjectFunction
            public final Object get(double d) {
                Object obj;
                obj = Reference2ObjectFunction.this.get(before.get(d));
                return obj;
            }
        };
    }

    default <T> Reference2ObjectFunction<K, T> andThenObject(final Object2ObjectFunction<? super V, ? extends T> after) {
        return new Reference2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda5
            @Override // it.unimi.dsi.fastutil.objects.Reference2ObjectFunction, it.unimi.dsi.fastutil.Function
            public final Object get(Object obj) {
                Object obj2;
                obj2 = after.get(Reference2ObjectFunction.this.get(obj));
                return obj2;
            }
        };
    }

    default <T> Object2ObjectFunction<T, V> composeObject(final Object2ReferenceFunction<? super T, ? extends K> before) {
        return new Object2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda0
            @Override // it.unimi.dsi.fastutil.objects.Object2ObjectFunction, it.unimi.dsi.fastutil.Function
            public final Object get(Object obj) {
                Object obj2;
                obj2 = Reference2ObjectFunction.this.get(before.get(obj));
                return obj2;
            }
        };
    }

    default <T> Reference2ReferenceFunction<K, T> andThenReference(final Object2ReferenceFunction<? super V, ? extends T> after) {
        return new Reference2ReferenceFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda16
            @Override // it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction, it.unimi.dsi.fastutil.Function
            public final Object get(Object obj) {
                Object obj2;
                obj2 = after.get(Reference2ObjectFunction.this.get(obj));
                return obj2;
            }
        };
    }

    default <T> Reference2ObjectFunction<T, V> composeReference(final Reference2ReferenceFunction<? super T, ? extends K> before) {
        return new Reference2ObjectFunction() { // from class: it.unimi.dsi.fastutil.objects.Reference2ObjectFunction$$ExternalSyntheticLambda17
            @Override // it.unimi.dsi.fastutil.objects.Reference2ObjectFunction, it.unimi.dsi.fastutil.Function
            public final Object get(Object obj) {
                Object obj2;
                obj2 = Reference2ObjectFunction.this.get(before.get(obj));
                return obj2;
            }
        };
    }
}