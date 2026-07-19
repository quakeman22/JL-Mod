package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public class Wavelet {
    private static byte[] buffer;
    private static boolean clear;
    private static int x;
    private static int y;

    private static native boolean nativePlay(String str, byte[] bArr, int i, int i2, boolean z);

    public static void createBuffer(int i) {
        buffer = new byte[i];
    }

    public static void freeBuffer() {
        buffer = null;
    }

    public static void setLocation(int i, int j) {
        x = i;
        y = j;
    }

    public static void setClear(boolean flag) {
        clear = flag;
    }

    public static boolean play(String s) {
        if (s == null) {
            throw new NullPointerException("url is null");
        }
        if (buffer == null) {
            throw new IllegalStateException("buffer is not initialized");
        }
        if (!s.startsWith("file:/")) {
            throw new IllegalArgumentException("url format is wrong");
        }
        return nativePlay(s, buffer, x, y, clear);
    }
}
