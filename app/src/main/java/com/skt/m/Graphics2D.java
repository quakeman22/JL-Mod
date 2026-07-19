package com.skt.m;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* JADX INFO: loaded from: classes.dex */
public class Graphics2D {
    public static final int DRAW_AND = 1;
    public static final int DRAW_COPY = 0;
    public static final int DRAW_OR = 2;
    public static final int DRAW_XOR = 3;
    private Graphics g;

    private Graphics2D(Graphics g1) {
        this.g = g1;
    }

    public static Graphics2D getGraphics2D(Graphics g1) {
        return null;
    }

    public void drawImage(int i, int j, Image image, int k, int l, int i1, int j1, int k1) {
    }

    public void invertRect(int i, int j, int k, int l) {
    }

    public int getPixel(int i, int j) {
        return 0;
    }

    public void setPixel(int i, int j, int k) {
    }

    public boolean getPixelMask(int i, int j) {
        return true;
    }

    public void setPixelMask(int i, int j, boolean flag) {
    }

    public static Image captureLCD(int i, int j, int k, int l) {
        return null;
    }

    public static Image createMaskableImage(int i, int j) {
        return null;
    }
}
