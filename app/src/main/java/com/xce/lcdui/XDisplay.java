package com.xce.lcdui;

import java.io.IOException;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* JADX INFO: loaded from: classes.dex */
public class XDisplay {
    public static final int IMG_AND = 1;
    public static final int IMG_COPY = 0;
    public static final int IMG_OR = 2;
    public static final int IMG_XOR = 3;
    public static int commandHeight = 256;
    public static int depth = 0;
    public static final int height = 256;
    public static final int height2 = 256;
    public static boolean is_color = false;
    public static boolean is_motioned = false;
    public static boolean is_pointed = false;
    public static boolean is_repeated = false;
    public static final int width = 256;

    public static void createImmutableImage(int i, int j, Image image, Image image1) {
    }

    public static void createMutableImage(int i, int j, Image image) {
    }

    public static void decodeImage(byte[] abyte0, int i, int j, Image image) throws IOException {
    }

    public static void drawArc(Graphics g, Image image, int i, int j, int k, int l, int i1, int j1, boolean flag) {
    }

    public static void drawChar(Graphics g, Image image, int i, int j, char c, int k) {
    }

    public static void drawChars(Graphics g, Image image, int i, int j, char[] ac, int k, int l, int i1) {
    }

    public static void drawImage(Graphics g, Image image, int i, int j, Image image1, int k) {
    }

    public static void drawLine(Graphics g, Image image, int i, int j, int k, int l) {
    }

    public static void drawRect(Graphics g, Image image, int i, int j, int k, int l, boolean flag) {
    }

    public static void drawRoundRect(Graphics g, Image image, int i, int j, int k, int l, int i1, int j1, boolean flag) {
    }

    public static void drawString(Graphics g, Image image, int i, int j, String s, int k, int l, int i1) {
    }

    public static int getFontHeight(int i, int j, int k) {
        return 256;
    }

    public static int stringWidth(int i, int j, int k, String s, int l, int i1) {
        return 256;
    }

    public static int charWidth(int i, int j, int k, char c) {
        return 256;
    }

    public static int charsWidth(int i, int j, int k, char[] ac, int l, int i1) {
        return 256;
    }

    public static void init(int[] ai, boolean[] aflag) {
    }

    public static void createMaskableImage(int i, int j, Image image) {
    }

    public static void clear(Graphics g, Image image, int i, int j) {
    }

    public static void setPixel(Graphics g, Image image, int i, int j, int k, int l) {
    }

    public static int getPixel(Graphics g, Image image, int i, int j) {
        return 256;
    }

    public static void setPixelMask(Graphics g, Image image, int i, int j, boolean flag) {
    }

    public static boolean getPixelMask(Graphics g, Image image, int i, int j) {
        return true;
    }

    public static void invertImage(Graphics g, Image image, int i, int j, int k, int l) {
    }

    public static void drawImageEx(Graphics g, Image image, int i, int j, Image image1, int k, int l, int i1, int j1, int k1) {
    }

    public static void copyLCD(Graphics g, Image image, int i, int j, int k, int l) {
    }

    public static int getGameAction(int i) {
        return 1;
    }

    public static int getKeyCode(int i) {
        return 0;
    }

    public static void setRefreshLock(boolean flag) {
    }

    public static void refresh(int i, int j, int k, int l) {
    }

    public static void refreshEnd(Image image) {
    }

    public static boolean resume() {
        return false;
    }

    static {
        int[] ai = new int[4];
        boolean[] aflag = new boolean[4];
        init(ai, aflag);
        depth = ai[3];
        is_color = aflag[0];
        is_pointed = aflag[1];
        is_motioned = aflag[2];
        is_repeated = aflag[3];
    }
}
