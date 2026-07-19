package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public final class Vibration {
    public static native int getLevelNum();

    public static native boolean isSupported();

    public static native void start(int i, int i2);

    public static native void stop();

    private Vibration() {
    }
}
