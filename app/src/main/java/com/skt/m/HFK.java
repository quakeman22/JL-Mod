package com.skt.m;

import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class HFK {
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 2;
    public static final int MODE_95A = 1;
    public static final int MODE_95C = 2;
    public static final int UNKNOWN = 0;
    private static HFKListener listener;

    public static native boolean callConnect(String str);

    public static native int getState();

    private static native void init();

    private static native int nativeRead(byte[] bArr, int i, int i2);

    private static native int nativeWrite(byte[] bArr, int i, int i2);

    public static native void setNetworkMode(int i);

    public static void setHFKListener(HFKListener hfklistener) {
        listener = hfklistener;
    }

    public static HFKListener getHFKListener() {
        return listener;
    }

    public static int read(byte[] abyte0) throws IOException {
        return read(abyte0, 0, abyte0.length);
    }

    public static int read(byte[] abyte0, int i, int j) throws IOException {
        if (i + j > abyte0.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        while (true) {
            int k = nativeRead(abyte0, i, j);
            if (k > 0) {
                return k;
            }
            if (k < 0) {
                throw new IOException();
            }
            Thread.yield();
        }
    }

    public static int write(byte[] abyte0) throws IOException {
        return write(abyte0, 0, abyte0.length);
    }

    public static int write(byte[] abyte0, int i, int j) throws IOException {
        if (i + j > abyte0.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        while (true) {
            int k = nativeWrite(abyte0, i, j);
            if (k > 0) {
                return k;
            }
            if (k < 0) {
                throw new IOException();
            }
            Thread.yield();
        }
    }

    static {
        init();
    }
}
