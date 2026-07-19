package com.skt.m.camera;

/* JADX INFO: loaded from: classes.dex */
public class Camera {
    public static final int ACCESS_DENIED = 10;
    public static final int ALREADY_EXIST = 8;
    public static final int BAD_FILENAME = 6;
    public static final int CAM_IMAGE_BMP = 1;
    public static final int CAM_IMAGE_GIF = 2;
    public static final int CAM_IMAGE_JPG = 3;
    public static final int CAM_IMAGE_MAX = 11;
    public static final int CAM_IMAGE_OEM = 7;
    public static final int CAM_IMAGE_PNG = 5;
    public static final int CAM_IMAGE_SIS = 4;
    public static final int CAM_IMAGE_VDI = 6;
    public static final int CAM_IMAGE_WBMP = 0;
    public static final int CAM_IMAGE_WTA = 9;
    public static final int CAM_IMAGE_WTB = 8;
    public static final int CAM_IMAGE_YUV = 10;
    public static final int DIR_NOT_FOUND = 7;
    public static final int DISK_FULL = 9;
    public static final int FAIL = 1;
    public static final int NOT_EQUIPPED = 4;
    public static final int NOT_SUFFICIENT_MEMORY = 2;
    public static final int NOT_SUPPORTED = 3;
    public static final int NOT_SUPPORTED_OPT = 5;
    public static final int OPT_CURRENT_BRIGHT = 2;
    public static final int OPT_CURRENT_IMAGE_FORMAT = 8;
    public static final int OPT_CURRENT_MAG_POWER = 12;
    public static final int OPT_CURRENT_PREVIEW_POS = 11;
    public static final int OPT_CURRENT_PREVIEW_SIZE = 10;
    public static final int OPT_CURRENT_RESOLUTION = 6;
    public static final int OPT_CURRENT_YUV_RES = 16;
    public static final int OPT_DIRECT_LCD_CONTROL = 5;
    public static final int OPT_EMBEDDED = 4;
    public static final int OPT_FLASH = 3;
    public static final int OPT_FLIP = 0;
    public static final int OPT_MAX_BRIGHT = 1;
    public static final int OPT_MAX_IMAGE = 9;
    public static final int OPT_MAX_MAG_POWER = 13;
    public static final int OPT_MAX_RESOLUTION = 7;
    public static final int OPT_YUV_RES_HEIGHT = 15;
    public static final int OPT_YUV_RES_MAX = 17;
    public static final int OPT_YUV_RES_WIDTH = 14;
    public static final int STATUS_POWER_OFF = 0;
    public static final int STATUS_POWER_ON = 1;
    public static final int STATUS_UNDEFINED = 100;
    public static final int SUCCESS = 0;
    static int lastError = 0;

    static native int nativeCamera_GetCamOpt(int i, CameraOption cameraOption);

    static native int nativeCamera_GetCamStatus();

    static native int nativeCamera_GetInformation(CameraInformation cameraInformation);

    static native int nativeCamera_PowerOff();

    static native int nativeCamera_PowerOn();

    static native int nativeCamera_SetCamOpt(int i, int i2, int i3);

    static native int nativeCamera_SnapShot(CameraSnapShot cameraSnapShot);

    static native void nativeCamera_Start();

    static native int nativeCamera_Stop();

    static native int nativeCamera_Write(String str, int i, int i2, byte[] bArr);

    public static int getLastError() {
        return lastError;
    }

    public static CameraInformation getInformation() {
        CameraInformation camerainformation = new CameraInformation();
        int iNativeCamera_GetInformation = nativeCamera_GetInformation(camerainformation);
        lastError = iNativeCamera_GetInformation;
        if (iNativeCamera_GetInformation == 0) {
            return camerainformation;
        }
        return null;
    }

    public static int getStatus() {
        int iNativeCamera_GetCamStatus = nativeCamera_GetCamStatus();
        lastError = iNativeCamera_GetCamStatus;
        return iNativeCamera_GetCamStatus;
    }

    public static int powerOn() {
        int iNativeCamera_PowerOn = nativeCamera_PowerOn();
        lastError = iNativeCamera_PowerOn;
        return iNativeCamera_PowerOn;
    }

    public static int powerOff() {
        int iNativeCamera_PowerOff = nativeCamera_PowerOff();
        lastError = iNativeCamera_PowerOff;
        return iNativeCamera_PowerOff;
    }

    public static CameraSnapShot snapShot() {
        CameraSnapShot camerasnapshot = new CameraSnapShot();
        int iNativeCamera_SnapShot = nativeCamera_SnapShot(camerasnapshot);
        lastError = iNativeCamera_SnapShot;
        if (iNativeCamera_SnapShot == 0) {
            return camerasnapshot;
        }
        return null;
    }

    public static CameraOption getOption(int i) {
        CameraOption cameraoption = new CameraOption();
        int iNativeCamera_GetCamOpt = nativeCamera_GetCamOpt(i, cameraoption);
        lastError = iNativeCamera_GetCamOpt;
        if (iNativeCamera_GetCamOpt == 0) {
            return cameraoption;
        }
        return null;
    }

    public static int setOption(int i, int j, int k) {
        int iNativeCamera_SetCamOpt = nativeCamera_SetCamOpt(i, j, k);
        lastError = iNativeCamera_SetCamOpt;
        return iNativeCamera_SetCamOpt;
    }

    public static int writeImage(String s, int i, int j, byte[] abyte0) {
        return nativeCamera_Write(s, i, j, abyte0);
    }

    public static void start() {
        nativeCamera_Start();
    }

    public static void stop() {
        nativeCamera_Stop();
    }
}
