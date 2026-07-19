package com.xce.io;

import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class SerialPort {
    public static final int EVENPARITY = 2;
    public static final int MARKPARITY = 3;
    public static final int NOPARITY = 0;
    public static final int ODDPARITY = 1;
    public static final int ONE5STOPBITS = 1;
    public static final int ONESTOPBIT = 0;
    public static final int SPACEPARITY = 4;
    public static final int TWOSTOPBITS = 2;
    int handle;

    protected static native int nativeAvailable(int i);

    protected static native int nativeClose(int i);

    protected static native int nativeCreateSerialPort(int i, int i2, int i3, int i4, int i5);

    protected static native int nativeRead(int i, byte[] bArr, int i2, int i3);

    protected static native int nativeWrite(int i, byte[] bArr, int i2, int i3);

    public SerialPort(int i) throws IOException {
        int iNativeCreateSerialPort = nativeCreateSerialPort(i, 9600, 8, 0, 0);
        this.handle = iNativeCreateSerialPort;
        if (iNativeCreateSerialPort < 0) {
            throw new IOException("error on creating serial port");
        }
    }

    public SerialPort(int i, int j, int k, int l, int i1) throws IOException {
        int iNativeCreateSerialPort = nativeCreateSerialPort(i, j, k, l, i1);
        this.handle = iNativeCreateSerialPort;
        if (iNativeCreateSerialPort < 0) {
            throw new IOException("error on creating serial port");
        }
    }

    public int read(byte[] abyte0, int i, int j) throws IOException {
        int k;
        while (true) {
            k = nativeAvailable(this.handle);
            if (k != 0) {
                break;
            }
            Thread.yield();
        }
        if (k < 0) {
            throw new IOException("error on checking serial port's available");
        }
        int k2 = nativeRead(this.handle, abyte0, i, j);
        if (k2 < 0) {
            throw new IOException("error on reading from serial port");
        }
        return k2;
    }

    public int write(byte[] abyte0, int i, int j) throws IOException {
        while (true) {
            int k = nativeWrite(this.handle, abyte0, i, j);
            if (k <= 0) {
                if (k < 0) {
                    throw new IOException("error on writing to serial port");
                }
                Thread.yield();
            } else {
                return k;
            }
        }
    }

    public int available() {
        return nativeAvailable(this.handle);
    }

    public void close() throws IOException {
        if (nativeClose(this.handle) < 0) {
            throw new IOException("error on closing serial port");
        }
    }
}
