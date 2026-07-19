package com.skt.m.video;

/* JADX INFO: loaded from: classes.dex */
public class VODStorageInformation {
    byte[] CIDSID;
    byte[] title;

    public byte[] getCIDSID() {
        return this.CIDSID;
    }

    public byte[] getTitle() {
        return this.title;
    }

    void allocArray(int i, int j) {
        this.CIDSID = new byte[i];
        this.title = new byte[j];
    }
}
