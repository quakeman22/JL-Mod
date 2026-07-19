package com.skt.m.camera;

/* JADX INFO: loaded from: classes.dex */
public class CameraSnapShot {
    int bitsPixel;
    byte[] data;
    int height;
    int imageDataSize;
    int imageType;
    int paletteNum;
    int[] paletteTable;
    int width;

    CameraSnapShot() {
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getBitsPixel() {
        return this.bitsPixel;
    }

    public int getPaletteNum() {
        return this.paletteNum;
    }

    public int[] getPaletteTable() {
        return this.paletteTable;
    }

    public int getImageType() {
        return this.imageType;
    }

    public int getImageDataSize() {
        return this.imageDataSize;
    }

    public byte[] getData() {
        return this.data;
    }
}
