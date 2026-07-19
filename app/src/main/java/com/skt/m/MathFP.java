package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public final class MathFP {
    public static final long E = 4613303445314885481L;
    public static final long MAX_VALUE = 9218868437227405311L;
    public static final long MIN_VALUE = 4503599627370496L;
    public static final long PI = 4614256656552045848L;

    public static native long abs(long j);

    public static native long acos(long j);

    public static native long add(long j, long j2);

    public static native long asin(long j);

    public static native long atan(long j);

    public static native long cos(long j);

    public static native long divide(long j, long j2);

    public static native long exp(long j);

    public static native long log(long j);

    public static native long max(long j, long j2);

    public static native long min(long j, long j2);

    public static native long multiply(long j, long j2);

    public static native long parseFP(long j);

    public static native long parseFPString(String str);

    public static native long pow(long j, long j2);

    public static native long round(long j);

    public static native long sin(long j);

    public static native long sqrt(long j);

    public static native long sub(long j, long j2);

    public static native long tan(long j);

    private static native byte[] toByteArray(long j, int i, int i2);

    public static native long toLong(long j);

    private MathFP() {
    }

    public static String toStringLF(long l, int i) {
        return new String(toByteArray(l, i, 1));
    }

    public static String toStringE(long l) {
        return new String(toByteArray(l, 0, 0));
    }
}
