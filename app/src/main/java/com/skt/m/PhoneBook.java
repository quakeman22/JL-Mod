package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public class PhoneBook {
    public static final int ALL = 0;
    public static final int EMAIL = 6;
    public static final int GROUP = 2;
    public static final int HANDPHONE = 3;
    public static final int HOME = 4;
    private static final int MAX_FIELD = 7;
    public static final int MEMO = 7;
    public static final int NAME = 1;
    public static final int OFFICE = 5;

    public static native void findRecord(int i, String str);

    public static native void first();

    public static native String getField(int i, int i2);

    private static native String getGroupName(int i);

    private static native int getMaxGroupID();

    public static native int getMaxRecordID();

    public static native boolean isUsed(int i);

    public static native int next();

    private PhoneBook() {
    }

    public static String[] getRecord(int i) {
        return null;
    }

    public static String[] getGroupNames() {
        return null;
    }

    static {
        first();
    }
}
