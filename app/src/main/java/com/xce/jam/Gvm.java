package com.xce.jam;

/* JADX INFO: loaded from: classes.dex */
public class Gvm {
    static native int getMaxChatStrings();

    static native int getMaxContents();

    static native void init();

    static native String nativeGetChatString(int i);

    static native String nativeGetContentName(int i);

    static native int nativeGetContentSize(int i);

    static native String nativeGetDirName(int i);

    static native boolean nativeRemoveContent(int i);

    static native void nativeSetChatString(int i, String str);

    static String getContentName(int i) {
        if (i < 0 || i > getMaxContents()) {
            throw new IllegalArgumentException();
        }
        return nativeGetContentName(i);
    }

    static String getDirName(int i) {
        if (i < 0 || i > getMaxContents()) {
            throw new IllegalArgumentException();
        }
        return nativeGetDirName(i);
    }

    static String getChatString(int i) {
        if (i < 0 || i > getMaxChatStrings()) {
            throw new IllegalArgumentException();
        }
        return nativeGetChatString(i);
    }

    static void setChatString(int i, String s) {
        if (i < 0 || i > getMaxChatStrings()) {
            throw new IllegalArgumentException();
        }
        nativeSetChatString(i, s);
    }

    static boolean removeContent(int i) {
        if (i < 0 || i > getMaxContents()) {
            throw new IllegalArgumentException();
        }
        return nativeRemoveContent(i);
    }

    static int getContentSize(int i) {
        if (i < 0 || i > getMaxContents()) {
            throw new IllegalArgumentException();
        }
        return nativeGetContentSize(i);
    }

    static {
        init();
    }
}
