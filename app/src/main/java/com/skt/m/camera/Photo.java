package com.skt.m.camera;

/* JADX INFO: loaded from: classes.dex */
public class Photo {
    public static final int FAIL = -1;
    public static final int IMAGE_BMP = 1;
    public static final int IMAGE_GIF = 2;
    public static final int IMAGE_JPG = 3;
    public static final int IMAGE_OEM = 7;
    public static final int IMAGE_PICTUREMATE_CALL = 1;
    public static final int IMAGE_PICTUREMATE_IDLE = 0;
    public static final int IMAGE_PICTUREMATE_NATE = 4;
    public static final int IMAGE_PICTUREMATE_NATE_NOT_REGISTER = 6;
    public static final int IMAGE_PICTUREMATE_NATE_OFF = 5;
    public static final int IMAGE_PICTUREMATE_POWER_OFF = 3;
    public static final int IMAGE_PICTUREMATE_POWER_ON = 2;
    public static final int IMAGE_PNG = 5;
    public static final int IMAGE_SIS = 4;
    public static final int IMAGE_VDI = 6;
    public static final int IMAGE_WBMP = 0;
    public static final int IMAGE_WTA = 9;
    public static final int IMAGE_WTB = 8;
    public static final int IMAGE_YUV = 10;
    public static final int LOCKED = -2;
    public static final int SET_FALSE = 0;
    public static final int SET_TRUE = 1;
    public static final int SUCCESS = 0;

    static native int nativePhoto_DeletePhoto(int i);

    static native int nativePhoto_GetCount(int[] iArr);

    static native int nativePhoto_GetData(int i, byte[] bArr);

    static native int nativePhoto_GetFormat(int i, int[] iArr);

    static native int nativePhoto_GetLockState(int i, int[] iArr);

    static native int nativePhoto_GetMaxCount(int[] iArr);

    static native int nativePhoto_GetMaxNameLength();

    static native int nativePhoto_GetName(byte[] bArr, int[] iArr);

    static native int nativePhoto_GetSize(int i, int[] iArr);

    static native int nativePhoto_RegPictureMate(int i, int i2);

    static native int nativePhoto_SetLockState(int i, int i2);

    public static int getMaxCount() {
        int[] ai = new int[1];
        if (nativePhoto_GetMaxCount(ai) == 0) {
            return ai[0];
        }
        return -1;
    }

    public static int getCount() {
        int[] ai = new int[1];
        if (nativePhoto_GetCount(ai) == 0) {
            return ai[0];
        }
        return -1;
    }

    public static String[] getNameList() {
        int i = nativePhoto_GetMaxNameLength();
        int j = getCount();
        byte[] abyte0 = new byte[j * i];
        int[] ai = new int[1];
        if (nativePhoto_GetName(abyte0, ai) != 0) {
            return null;
        }
        int k = 0;
        int l = 0;
        int i1 = ai[0];
        int j1 = 0;
        String[] as = new String[j];
        while (l < i1) {
            while (abyte0[l] != 0 && l < i1) {
                l++;
            }
            if (l >= i1 && k == l) {
                break;
            }
            as[j1] = new String(abyte0, k, l - k);
            l++;
            k = l;
            j1++;
        }
        return as;
    }

    public static int getSize(int i) throws IllegalArgumentException {
        if (i >= getCount() || i < 0) {
            throw new IllegalArgumentException("invalid index value");
        }
        int[] ai = new int[1];
        if (nativePhoto_GetSize(i, ai) == 0) {
            return ai[0];
        }
        return -1;
    }

    public static int getFormat(int i) throws IllegalArgumentException {
        if (i >= getMaxCount() || i < 0) {
            throw new IllegalArgumentException("invalid index value");
        }
        int[] ai = new int[1];
        if (nativePhoto_GetFormat(i, ai) == 0) {
            return ai[0];
        }
        return -1;
    }

    public static byte[] getData(int i) throws IllegalArgumentException {
        if (i >= getMaxCount() || i < 0) {
            throw new IllegalArgumentException("invalid index value");
        }
        int j = getSize(i);
        if (j < 0) {
            return null;
        }
        byte[] abyte0 = new byte[j];
        if (nativePhoto_GetData(i, abyte0) != 0) {
            return null;
        }
        return abyte0;
    }

    public static int regPictureMate(int i, int j) throws IllegalArgumentException {
        if (i >= getMaxCount() || i < 0) {
            throw new IllegalArgumentException("invalid index value");
        }
        return nativePhoto_RegPictureMate(i, j);
    }

    public static int deletePhoto(int i) throws IllegalArgumentException {
        if (i >= getMaxCount() || i < 0) {
            throw new IllegalArgumentException("invalid index value");
        }
        return nativePhoto_DeletePhoto(i);
    }

    public static int setLockState(int i, int j) throws IllegalArgumentException {
        if (i >= getMaxCount() || i < 0) {
            throw new IllegalArgumentException("invalid index value");
        }
        return nativePhoto_SetLockState(i, j);
    }

    public static int getLockState(int i) throws IllegalArgumentException {
        if (i >= getMaxCount() || i < 0) {
            throw new IllegalArgumentException("invalid index value");
        }
        int[] ai = new int[1];
        if (nativePhoto_GetLockState(i, ai) == 0) {
            return ai[0];
        }
        return -1;
    }
}
