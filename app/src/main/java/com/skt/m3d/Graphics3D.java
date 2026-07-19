package com.skt.m3d;

import javax.microedition.lcdui.Graphics;

/* JADX INFO: loaded from: classes.dex */
public class Graphics3D {
    private static boolean isBackfaceCull = true;
    private static boolean isUsedZBuffer = false;
    private static boolean isCreatedZBuffer = false;

    private static native void _clearZBuffer();

    private static native void _createZBuffer();

    private static native void _destroyZBuffer();

    private static native void _draw(Graphics graphics, Object3D object3D, boolean z, boolean z2);

    private static native void _fill(Graphics graphics, Object3D object3D, boolean z, boolean z2);

    private static native void init();

    public static void clearZBuffer() {
        if (isCreatedZBuffer) {
            _clearZBuffer();
        }
    }

    public static void destroyZBuffer() {
        if (isCreatedZBuffer) {
            isCreatedZBuffer = false;
            _destroyZBuffer();
        }
    }

    public static void setZBufferEnabled(boolean flag) {
        if (flag && !isCreatedZBuffer) {
            isCreatedZBuffer = true;
            _createZBuffer();
        }
        isUsedZBuffer = flag;
    }

    public static boolean isZBufferEnabled() {
        return isUsedZBuffer;
    }

    public static void setBackfaceCulled(boolean flag) {
        isBackfaceCull = flag;
    }

    public static boolean isBackfaceCulled() {
        return isBackfaceCull;
    }

    public static void render(Graphics g, Object3D object3d) throws IllegalStateException {
        boolean z = isUsedZBuffer;
        if (z && !isCreatedZBuffer) {
            throw new IllegalStateException();
        }
        _fill(g, object3d, z, isBackfaceCull);
    }

    public static void drawWireframe(Graphics g, Object3D object3d) throws IllegalStateException {
        boolean z = isUsedZBuffer;
        if (z && !isCreatedZBuffer) {
            throw new IllegalStateException();
        }
        _draw(g, object3d, z, isBackfaceCull);
    }

    static {
        init();
    }
}
