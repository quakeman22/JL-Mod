package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public final class BackLight {
    private static int[] colors;
    private static int idx = 0;

    private BackLight() {
    }

    public static int getColor() {
        return idx;
    }

    public static int[] getColors() {
        return colors;
    }

    public static int getColorNum() {
        return 0;
    }

    public static void setColor(int i) {
        if (i >= colors.length) {
            throw new IllegalArgumentException();
        }
        idx = i;
    }

    public static void on(int i) {
        on(i, idx);
    }

    public static void off() {
    }

    private static void on(int i, int j) {
    }

    static void getColorArray(int[] ai) {
    }

    static {
        int i = getColorNum();
        int[] iArr = new int[i];
        colors = iArr;
        getColorArray(iArr);
    }
}
