package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public class Device {
    public static final int COLOR_MODE_256C = 1;
    public static final int COLOR_MODE_4G = 0;
    public static final int COLOR_MODE_64KC = 2;
    public static final int MELODY_MUSICBELL = 1;
    public static final int MELODY_MYBELL = 0;
    public static final int SIS_CALL = 1;
    public static final int SIS_NORMAL = 0;
    public static final int SIS_PWR_OFF = 4;
    public static final int SIS_PWR_ON = 3;
    public static final int SIS_WAP = 2;

    private Device() {
    }

    public static void setBacklightEnabled(boolean flag) {
    }

    public static boolean isBacklightEnabled() {
        return false;
    }

    public static void setKeyToneEnabled(boolean flag) {
    }

    public static boolean isKeyToneEnabled() {
        return false;
    }

    public static void beep(int i, int j) {
    }

    public static void invokeWapBrowser(String s) {
    }

    public static void enableRestoreLCD(boolean flag) {
    }

    public static void setKeyRepeatTime(int i, int j) {
    }

    public static void setColorMode(int i) {
    }

    public static boolean setSISImage(int i, String s, byte[] abyte0) {
        return false;
    }

    public static boolean setMelody(int i, String s, byte[] abyte0) {
        return false;
    }

    public static void setNAI(int i) {
    }
}
