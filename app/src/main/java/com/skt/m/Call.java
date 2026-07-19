package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public final class Call {
    public static native boolean disconnect();

    public static native boolean isSupported();

    private static native boolean nativeConnect(String str);

    private Call() {
    }

    public static synchronized boolean connect(String s) {
        return true;
    }
}
