package com.xce.io;

import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class ITSUSB {
    public static final int CONNECTED = 1;
    public static final int DISCONNECTED = 0;
    static int handle = -1;
    private static ITSUSBListener listener;

    protected static native void init();

    protected static native int nativeAvailable(int i);

    protected static native int nativeClose(int i);

    protected static native int nativeCreate();

    protected static native int nativeGetState();

    protected static native int nativeRead(int i, byte[] bArr, int i2, int i3);

    protected static native int nativeWrite(int i, byte[] bArr, int i2, int i3);

    public ITSUSB() throws IOException {
        open();
    }

    public static void setITSUSBListener(ITSUSBListener itsusblistener) {
        listener = itsusblistener;
    }

    public static ITSUSBListener getITSUSBListener() {
        return listener;
    }

    public static void open() throws IOException {
        int iNativeCreate = nativeCreate();
        handle = iNativeCreate;
        if (iNativeCreate < 0) {
            throw new IOException("error on creating serial port");
        }
    }

    public static int read(byte[] abyte0, int i, int j) throws IOException {
        if (handle == -1) {
            throw new IOException("NOT OPENED");
        }
        if (abyte0.length < i + j) {
            throw new ArrayIndexOutOfBoundsException();
        }
        while (true) {
            int k = nativeRead(handle, abyte0, i, j);
            if (k > 0) {
                return k;
            }
            if (k < 0) {
                throw new IOException("error on reading from USB");
            }
            Thread.yield();
        }
    }

    public static int write(byte[] abyte0, int i, int j) throws IOException {
        if (handle == -1) {
            throw new IOException("NOT OPENED");
        }
        if (abyte0.length < i + j) {
            throw new ArrayIndexOutOfBoundsException();
        }
        while (true) {
            int k = nativeWrite(handle, abyte0, i, j);
            if (k <= 0) {
                if (k < 0) {
                    throw new IOException("error on writing to USB");
                }
                Thread.yield();
            } else {
                return k;
            }
        }
    }

    public static int available() throws IOException {
        int i = handle;
        if (i == -1) {
            throw new IOException("NOT OPENED");
        }
        return nativeAvailable(i);
    }

    public static void close() throws IOException {
        int i = handle;
        if (i == -1) {
            throw new IOException("NOT OPENED");
        }
        if (nativeClose(i) < 0) {
            throw new IOException("error on closing serial port");
        }
        handle = -1;
    }

    public static int getState() {
        return nativeGetState() != 1 ? 0 : 1;
    }

    static {
        init();
    }
}
