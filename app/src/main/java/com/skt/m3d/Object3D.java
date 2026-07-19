package com.skt.m3d;

/* JADX INFO: loaded from: classes.dex */
public class Object3D {
    private static final int increment = 64;
    private int[] col;
    private int[] dx;
    private int[] dy;
    private int[] dz;
    private int[] i0;
    private int[] i1;
    private int[] i2;
    private int[] m0;
    private int[] m1;
    private int[] m2;
    String name;
    private int tcount;
    private boolean txUpdated;
    private int vcount;
    private int[] vscale;
    private int[] vtrans;
    private int[] vx;
    private int[] vy;
    private int[] vz;

    public native void rotate(int i, int i2, int i3);

    public Object3D(String s) {
        this.m0 = new int[]{1024, 0, 0};
        this.m1 = new int[]{0, 1024, 0};
        this.m2 = new int[]{0, 0, 1024};
        this.vscale = new int[]{1024, 1024, 1024};
        this.txUpdated = true;
        this.vtrans = new int[3];
        this.vx = new int[64];
        this.vy = new int[64];
        this.vz = new int[64];
        this.dx = new int[64];
        this.dy = new int[64];
        this.dz = new int[64];
        this.i0 = new int[64];
        this.i1 = new int[64];
        this.i2 = new int[64];
        this.col = new int[64];
        this.name = s;
    }

    public void setName(String s) {
        this.name = s;
    }

    public String getName() {
        return this.name;
    }

    public Object3D(String s, int[] ai, int[] ai1, int[] ai2, int[] ai3, int[] ai4, int[] ai5, int[] ai6) {
        this.m0 = new int[]{1024, 0, 0};
        this.m1 = new int[]{0, 1024, 0};
        this.m2 = new int[]{0, 0, 1024};
        this.vscale = new int[]{1024, 1024, 1024};
        this.txUpdated = true;
        this.vtrans = new int[3];
        this.vx = new int[64];
        this.vy = new int[64];
        this.vz = new int[64];
        this.dx = new int[64];
        this.dy = new int[64];
        this.dz = new int[64];
        this.i0 = new int[64];
        this.i1 = new int[64];
        this.i2 = new int[64];
        this.col = new int[64];
        this.name = s;
        setVertices(ai, ai1, ai2);
        setTriangles(ai3, ai4, ai5, ai6);
    }

    public void addVertex(int i, int j, int k) {
        int i2 = this.vcount;
        int[] iArr = this.vx;
        if (i2 >= iArr.length) {
            int l = iArr.length + 64;
            int[] ai = new int[l];
            int[] ai1 = new int[l];
            int[] ai2 = new int[l];
            System.arraycopy(iArr, 0, ai, 0, iArr.length);
            System.arraycopy(this.vy, 0, ai1, 0, this.vx.length);
            System.arraycopy(this.vz, 0, ai2, 0, this.vx.length);
            this.vx = ai;
            this.vy = ai1;
            this.vz = ai2;
            this.dx = new int[l];
            this.dy = new int[l];
            this.dz = new int[l];
        }
        int[] iArr2 = this.vx;
        int i3 = this.vcount;
        iArr2[i3] = i;
        this.vy[i3] = j;
        this.vz[i3] = k;
        this.vcount = i3 + 1;
        this.txUpdated = true;
    }

    public void setVertices(int[] ai, int[] ai1, int[] ai2) {
        if (ai.length != ai1.length || ai.length != ai2.length) {
            throw new IllegalArgumentException();
        }
        this.vcount = 0;
        for (int i = 0; i < ai.length; i++) {
            addVertex(ai[i], ai1[i], ai2[i]);
        }
    }

    public void addTriangle(int i, int j, int k, int l) {
        int i2 = this.tcount;
        int[] iArr = this.i0;
        if (i2 >= iArr.length) {
            int j1 = iArr.length + 64;
            int[] ai = new int[j1];
            int[] ai1 = new int[j1];
            int[] ai2 = new int[j1];
            int[] ai3 = new int[j1];
            System.arraycopy(iArr, 0, ai, 0, iArr.length);
            System.arraycopy(this.i1, 0, ai1, 0, this.i0.length);
            System.arraycopy(this.i2, 0, ai2, 0, this.i0.length);
            int[] iArr2 = this.col;
            System.arraycopy(iArr2, 0, ai3, 0, iArr2.length);
            this.i0 = ai;
            this.i1 = ai1;
            this.i2 = ai2;
            this.col = ai3;
        }
        int[] iArr3 = this.i0;
        int i3 = this.tcount;
        iArr3[i3] = i;
        this.i1[i3] = j;
        this.i2[i3] = k;
        this.col[i3] = l;
        this.tcount = i3 + 1;
        this.txUpdated = true;
    }

    public void setTriangles(int[] ai, int[] ai1, int[] ai2, int[] ai3) {
        if (ai.length != ai1.length || ai.length != ai2.length || ai.length != ai3.length) {
            throw new IllegalArgumentException();
        }
        this.tcount = 0;
        for (int i = 0; i < ai.length; i++) {
            addTriangle(ai[i], ai1[i], ai2[i], ai3[i]);
        }
    }

    public void translate(int i, int j, int k) {
        int[] iArr = this.vtrans;
        iArr[0] = i;
        iArr[1] = j;
        iArr[2] = k;
        this.txUpdated = true;
    }

    public void scale(int i, int j, int k) {
        int[] iArr = this.vscale;
        iArr[0] = i;
        iArr[1] = j;
        iArr[2] = k;
        this.txUpdated = true;
    }

    public int[] getMatrixRow0() {
        return this.m0;
    }

    public int[] getMatrixRow1() {
        return this.m1;
    }

    public int[] getMatrixRow2() {
        return this.m2;
    }
}
