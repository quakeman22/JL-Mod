package com.skt.m.video;

/* JADX INFO: loaded from: classes.dex */
public class VODContent {
    public static final int FORMAT_AAC = 2;
    public static final int FORMAT_ALL = 0;
    public static final int FORMAT_EVRC = 5;
    public static final int FORMAT_H263 = 6;
    public static final int FORMAT_H264 = 4;
    public static final int FORMAT_MPEG4 = 1;
    public static final int FORMAT_TCM = 3;
    public static final int PUI_IMAGE_PICTUREMATE_CALL = 1;
    public static final int PUI_IMAGE_PICTUREMATE_IDLE = 0;
    public static final int PUI_IMAGE_PICTUREMATE_NATE = 4;
    public static final int PUI_IMAGE_PICTUREMATE_NATE_AIR = 7;
    public static final int PUI_IMAGE_PICTUREMATE_NATE_NOT_REGISTER = 6;
    public static final int PUI_IMAGE_PICTUREMATE_NATE_OFF = 5;
    public static final int PUI_IMAGE_PICTUREMATE_POWER_OFF = 3;
    public static final int PUI_IMAGE_PICTUREMATE_POWER_ON = 2;
    public static final int SIDCID_LEN = 27;
    public static final int TYPE_ALL = 0;
    public static final int TYPE_LIVEBELL = 3;
    public static final int TYPE_LIVESCREEN = 2;
    public static final int TYPE_NORMAL = 1;
    public static final int TYPE_RECORD = 4;

    static native int native_DeleteData(String str, int i);

    static native int native_GetCount(int i, int i2);

    static native int native_GetData(String str, int i, byte[] bArr);

    static native int native_GetFormat(String str, int i);

    static native int native_GetFreeSpace(int i);

    static native int native_GetInformation(String str, int i, VideoInformation videoInformation);

    static native int native_GetList(int i, int i2, VODStorageInformation[] vODStorageInformationArr);

    static native int native_GetMaxCount(int i);

    static native int native_GetMaxNameLength(int i);

    static native int native_GetPictureMate(int i, VODStorageInformation vODStorageInformation);

    static native int native_GetSize(String str, int i);

    static native int native_RegPictureMate(String str, int i, int i2);

    static native int native_WriteData(String str, int i, int i2, int i3, byte[] bArr);

    public static VideoInformation getInformation(String s, int i) {
        VideoInformation videoinformation = new VideoInformation();
        if (native_GetInformation(s, i, videoinformation) < 0) {
            return null;
        }
        return videoinformation;
    }

    public static int getMaxCount(int i) {
        return native_GetMaxCount(i);
    }

    public static int getMaxNameLength(int i) {
        return native_GetMaxNameLength(i);
    }

    public static int getCount(int i, int j) {
        return native_GetCount(i, j);
    }

    public static VODStorageInformation[] getList(int i, int j) {
        int k = getCount(i, j);
        int l = getMaxNameLength(i);
        VODStorageInformation[] avodstorageinformation = new VODStorageInformation[k];
        for (int i1 = 0; i1 < k; i1++) {
            avodstorageinformation[i1] = new VODStorageInformation();
            avodstorageinformation[i1].allocArray(27, l);
        }
        int i12 = native_GetList(i, j, avodstorageinformation);
        if (i12 < 0) {
            return null;
        }
        return avodstorageinformation;
    }

    public static int getSize(String s, int i) {
        return native_GetSize(s, i);
    }

    public static int getFormat(String s, int i) {
        return native_GetFormat(s, i);
    }

    public static byte[] getData(String s, int i) {
        int j = getSize(s, i);
        byte[] abyte0 = new byte[j];
        if (native_GetData(s, i, abyte0) < 0) {
            return null;
        }
        return abyte0;
    }

    public static boolean writeData(String s, int i, int j, int k, byte[] abyte0) {
        return native_WriteData(s, i, j, k, abyte0) >= 0;
    }

    public static boolean deleteData(String s, int i) {
        return native_DeleteData(s, i) >= 0;
    }

    public static int getFreeSpace(int i) {
        return native_GetFreeSpace(i);
    }

    public static boolean regPictureMate(String s, int i, int j) {
        return native_RegPictureMate(s, i, j) >= 0;
    }

    public static VODStorageInformation getPictureMate(int i) {
        VODStorageInformation vodstorageinformation = new VODStorageInformation();
        int j = getMaxNameLength(1);
        vodstorageinformation.allocArray(27, j);
        if (native_GetPictureMate(i, vodstorageinformation) < 0) {
            return null;
        }
        return vodstorageinformation;
    }
}
