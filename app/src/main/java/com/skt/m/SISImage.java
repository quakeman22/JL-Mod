package com.skt.m;

import java.io.IOException;
import javax.microedition.io.HttpConnection;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* JADX INFO: loaded from: classes.dex */
public class SISImage {
    public static final int IMG_LEVEL_256C = 8;
    public static final int IMG_LEVEL_4G = 2;
    public static final int IMG_LEVEL_BW = 1;
    private static byte[] codeBuffer;
    private static SISImage currImg;
    private static byte[] frameBuffer;
    private static byte[] objectBuffer;
    private static int[] palBuffer;
    private int bestID;
    private byte[] data;
    private int dataLength;
    private int dataOffset;
    private int height;
    private int imgLevel;
    private int maxFrameID;
    private int maxObjID;
    private int palType;
    private int width;

    private native int getObjectSize(int i, int[] iArr);

    private native int init(byte[] bArr, byte[] bArr2, byte[] bArr3, int[] iArr);

    private static native int nativeBufferSize(byte[] bArr, int i, int i2);

    private native int nativePaintFrame(byte[] bArr, int[] iArr, Graphics graphics, Image image, int i, int i2, int i3);

    private native int nativePaintObject(int[] iArr, Graphics graphics, Image image, int i, int i2, int i3, boolean z);

    private SISImage(byte[] abyte0, int i, int j) throws IOException {
        byte[] bArr = objectBuffer;
        if (bArr == null) {
            throw new IllegalStateException("Buffer isn't intialized");
        }
        this.data = abyte0;
        this.dataOffset = i;
        this.dataLength = j;
        if (init(bArr, codeBuffer, frameBuffer, palBuffer) < 0) {
            throw new IOException("SIS decode error");
        }
        currImg = this;
    }

    public static void createBuffer(int i, int j) {
        if (i <= 0 || j <= 0) {
            throw new IllegalArgumentException("illegal argument");
        }
        objectBuffer = new byte[i];
        frameBuffer = new byte[j];
        codeBuffer = new byte[j];
        palBuffer = new int[256];
    }

    public static void freeBuffer() {
        objectBuffer = null;
        frameBuffer = null;
        codeBuffer = null;
        palBuffer = null;
    }

    public static SISImage createSISImage(String str) throws IOException {
        throw new Error("Unresolved compilation problem: \n\tSyntax error, insert \"VariableDeclarators\" to complete LocalVariableDeclaration\n");
    }

    public static SISImage createSISImage(byte[] abyte0, int i, int j) throws IOException, ArrayIndexOutOfBoundsException {
        if (abyte0 == null) {
            throw new NullPointerException("imgData is null");
        }
        if (i < 0 || j <= 0 || i + j > abyte0.length) {
            throw new ArrayIndexOutOfBoundsException("imgOffset and imgLength specify an invalid range");
        }
        return new SISImage(abyte0, i, j);
    }

    public static int getRequiredBufferSize(byte[] abyte0, int i, int j) {
        if (abyte0 == null) {
            throw new NullPointerException("imgData is null");
        }
        if (i >= 0 && j > 0 && i + j > abyte0.length) {
            throw new ArrayIndexOutOfBoundsException("imgOffset and imgLength specify an invalid range");
        }
        return nativeBufferSize(abyte0, i, j);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getBestID() {
        return this.bestID;
    }

    public int getMaxObjectID() {
        return this.maxObjID;
    }

    public int getImageLevel() {
        return this.imgLevel;
    }

    public int getMaxFrameID() {
        return this.maxFrameID;
    }

    public int getDelay(int i) {
        return HttpConnection.HTTP_INTERNAL_ERROR;
    }

    public Image getObject(int i, boolean flag) throws IllegalArgumentException {
        byte[] bArr = objectBuffer;
        if (bArr == null) {
            throw new IllegalStateException("Buffer isn't intialized");
        }
        if (i < 0 || i > this.maxObjID) {
            throw new IllegalArgumentException("invalid objID");
        }
        if (this.imgLevel != 8) {
            return null;
        }
        if (this != currImg) {
            if (init(bArr, codeBuffer, frameBuffer, palBuffer) < 0) {
                return null;
            }
            currImg = this;
        }
        int[] ai = new int[2];
        if (getObjectSize(i, ai) < 0) {
            return null;
        }
        Image image = Graphics2D.createMaskableImage(ai[0], ai[1]);
        nativePaintObject(palBuffer, image.getGraphics(), image, i, 0, 0, flag);
        return image;
    }

    public Image getFrame(int i) throws IllegalArgumentException {
        byte[] bArr = objectBuffer;
        if (bArr == null) {
            throw new IllegalStateException("Buffer isn't intialized");
        }
        if (i < 0 || i > this.maxFrameID) {
            throw new IllegalArgumentException("invalid frameID");
        }
        if (this != currImg) {
            if (init(bArr, codeBuffer, frameBuffer, palBuffer) < 0) {
                return null;
            }
            currImg = this;
        }
        Image image = Image.createImage(this.width, this.height);
        nativePaintFrame(frameBuffer, palBuffer, image.getGraphics(), image, i, 0, 0);
        return image;
    }

    public void paintFrame(Graphics g, int i, int j, int k) throws IllegalArgumentException, NullPointerException {
        byte[] bArr = objectBuffer;
        if (bArr == null) {
            throw new IllegalStateException("Buffer isn't intialized");
        }
        if (g == null) {
            throw new NullPointerException("Graphics is null");
        }
        if (i < 0 || i > this.maxFrameID) {
            throw new IllegalArgumentException("invalid frameID");
        }
        if (this != currImg) {
            if (init(bArr, codeBuffer, frameBuffer, palBuffer) < 0) {
                return;
            } else {
                currImg = this;
            }
        }
        nativePaintFrame(frameBuffer, palBuffer, g, g.getImage(), i, j, k);
    }

    public void paintObject(Graphics g, int i, int j, int k, boolean flag) throws IllegalArgumentException, NullPointerException {
        byte[] bArr = objectBuffer;
        if (bArr == null) {
            throw new IllegalStateException("Buffer isn't intialized");
        }
        if (g == null) {
            throw new NullPointerException("Graphics is null");
        }
        if (i < 0 || i > this.maxObjID) {
            throw new IllegalArgumentException("invalid objID");
        }
        if (this.imgLevel != 8) {
            return;
        }
        if (this != currImg) {
            if (init(bArr, codeBuffer, frameBuffer, palBuffer) < 0) {
                return;
            } else {
                currImg = this;
            }
        }
        nativePaintObject(palBuffer, g, g.getImage(), i, j, k, flag);
    }
}
