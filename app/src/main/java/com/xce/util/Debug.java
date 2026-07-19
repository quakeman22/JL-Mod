package com.xce.util;

/* JADX INFO: loaded from: classes.dex */
public class Debug {
    public static final boolean COMPILE = true;
    public static final boolean IS_EMUL = true;
    public static final boolean IS_POINT_DEV = false;

    public static native void debugOut(String str);
}
