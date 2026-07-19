package com.skt.m.camera;

/* JADX INFO: loaded from: classes.dex */
public class CameraInformation {
    int currentImageType;
    int currentResolution;
    boolean directLCDControl;
    boolean embedded;
    int[] imageType;
    int maxImageType;
    int maxResolutionIndex;
    short[] resolution;

    CameraInformation() {
    }

    public boolean isEmbedded() {
        return this.embedded;
    }

    public boolean isDirectLCDControl() {
        return this.directLCDControl;
    }

    public int getMaxResolutionIndex() {
        return this.maxResolutionIndex;
    }

    public int getCurrentResolution() {
        return this.currentResolution;
    }

    public CameraResolution[] getResolution() {
        int i = this.resolution.length / 2;
        CameraResolution[] acameraresolution = new CameraResolution[i];
        for (int j = 0; j < i; j++) {
            acameraresolution[j] = new CameraResolution();
            acameraresolution[j].x = this.resolution[j * 2];
            acameraresolution[j].y = this.resolution[(j * 2) + 1];
        }
        return acameraresolution;
    }

    public int getMaxImageType() {
        return this.maxImageType;
    }

    public int getCurrentImageType() {
        return this.currentImageType;
    }

    public int[] getImageType() {
        return this.imageType;
    }
}
