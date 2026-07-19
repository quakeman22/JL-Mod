package com.skt.m.video;

/* JADX INFO: loaded from: classes.dex */
public class Video {
    public static final int QAULITY_ECONOMIC = 1;
    public static final int QAULITY_HIQUALITY = 3;
    public static final int QAULITY_STANDARD = 2;
    public static final int VOD_MEDIA_AAC_BUFFER = 8;
    public static final int VOD_MEDIA_AAC_FILE = 9;
    public static final int VOD_MEDIA_AAC_RESOURCE = 7;
    public static final int VOD_MEDIA_AAC_URL = 6;
    public static final int VOD_MEDIA_EVRC_BUFFER = 20;
    public static final int VOD_MEDIA_EVRC_FILE = 21;
    public static final int VOD_MEDIA_EVRC_RESOURCE = 19;
    public static final int VOD_MEDIA_EVRC_URL = 18;
    public static final int VOD_MEDIA_H263_BUFFER = 24;
    public static final int VOD_MEDIA_H263_FILE = 25;
    public static final int VOD_MEDIA_H263_RESOURCE = 23;
    public static final int VOD_MEDIA_H263_URL = 22;
    public static final int VOD_MEDIA_H264_BUFFER = 16;
    public static final int VOD_MEDIA_H264_FILE = 17;
    public static final int VOD_MEDIA_H264_RESOURCE = 15;
    public static final int VOD_MEDIA_H264_URL = 14;
    public static final int VOD_MEDIA_MPEG4_BUFFER = 4;
    public static final int VOD_MEDIA_MPEG4_FILE = 5;
    public static final int VOD_MEDIA_MPEG4_RESOURCE = 3;
    public static final int VOD_MEDIA_MPEG4_URL = 2;
    public static final int VOD_MEDIA_NULL = 0;
    public static final int VOD_MEDIA_TCM_BUFFER = 12;
    public static final int VOD_MEDIA_TCM_FILE = 13;
    public static final int VOD_MEDIA_TCM_RESOURCE = 11;
    public static final int VOD_MEDIA_TCM_URL = 10;
    public static final int VOD_MEDIA_WMLS_URL = 1;
    public static final int VOD_METHOD_AAC = 2;
    public static final int VOD_METHOD_EVRC = 5;
    public static final int VOD_METHOD_H263 = 6;
    public static final int VOD_METHOD_H264 = 4;
    public static final int VOD_METHOD_MPEG4 = 1;
    public static final int VOD_METHOD_NULL = 0;
    public static final int VOD_METHOD_TCM = 3;

    private static native int native_GetInformation(VideoInformation videoInformation);

    private static native int native_GetPosition(VideoPosition videoPosition);

    private static native int native_Pause();

    private static native int native_Record(int i, int i2, int i3, int i4, byte[] bArr, int i5);

    private static native int native_Resume();

    private static native int native_SetMedia(int i, int i2, byte[] bArr);

    private static native int native_SetPosition(int i, int i2, int i3, int i4);

    private static native int native_Start(boolean z, boolean z2);

    private static native int native_Stop();

    public static int setMedia(int i, int j, byte[] abyte0) {
        return native_SetMedia(i, j, abyte0);
    }

    public static int start(boolean flag, boolean flag1) {
        return native_Start(flag, flag1);
    }

    public static int stop() {
        return native_Stop();
    }

    public static int pause() {
        return native_Pause();
    }

    public static int resume() {
        return native_Resume();
    }

    public static int setPosition(int i, int j, int k, int l) {
        return native_SetPosition(i, j, k, l);
    }

    public static VideoPosition getPosition() {
        VideoPosition videoposition = new VideoPosition();
        if (native_GetPosition(videoposition) < 0) {
            return null;
        }
        return videoposition;
    }

    public static VideoInformation getInformation() {
        VideoInformation videoinformation = new VideoInformation();
        if (native_GetInformation(videoinformation) < 0) {
            return null;
        }
        return videoinformation;
    }

    public static int record(int i, int j, int k, int l, byte[] abyte0, int i1) {
        return native_Record(i, j, k, l, abyte0, i1);
    }
}
