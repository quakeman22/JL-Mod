package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public class VOD {
    private static byte[] buffer;
    private static boolean clear;
    private static int h;
    private static int w;
    private static int x;
    private static int y;

    private static native boolean nativePlay(String str, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, boolean z);

    private static native boolean nativeStop();

    public static void setBounds(int i, int j, int k, int l) {
        x = i;
        y = j;
        w = k;
        h = l;
    }

    public static void setClear(boolean flag) {
        clear = flag;
    }

    public static boolean play(String s) {
        if (s == null) {
            throw new NullPointerException("url is null");
        }
        if (!s.startsWith("file:/")) {
            throw new IllegalArgumentException("url format is wrong");
        }
        return nativePlay(s, null, 0, 0, x, y, w, h, clear);
    }

    public static boolean play(byte[] abyte0, int i, int j) {
        if (abyte0 == null) {
            throw new NullPointerException("data is null");
        }
        if (i < 0 || j <= 0 || i + j > abyte0.length) {
            throw new ArrayIndexOutOfBoundsException("dataOffset and dataLength specify an invalid range");
        }
        return nativePlay(null, abyte0, i, j, x, y, w, h, clear);
    }

    public static boolean stop() {
        return nativeStop();
    }
}
