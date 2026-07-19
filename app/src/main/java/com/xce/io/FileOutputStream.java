package com.xce.io;

import java.io.IOException;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes.dex */
public class FileOutputStream extends OutputStream {
    private byte[] _abuf;
    protected XFile file;

    public FileOutputStream(int i) {
        this._abuf = new byte[1];
        if (i < 1 || i > 2) {
            throw new IllegalArgumentException("invalid descriptor");
        }
        this.file = new XFile(i);
    }

    public FileOutputStream(String s) throws IOException {
        this(new XFile(s, 2));
    }

    public FileOutputStream(String s, boolean flag) throws IOException {
        this(new XFile(s, flag ? 2 : 3));
    }

    public FileOutputStream(XFile xfile) throws IOException {
        this._abuf = new byte[1];
        if ((xfile.mode & 2) == 2 && xfile.type == 1) {
            this.file = xfile;
            return;
        }
        throw new IllegalArgumentException("fail to write");
    }

    @Override // java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.file.close();
    }

    @Override // java.io.OutputStream, java.io.Flushable
    public void flush() throws IOException {
        this.file.flush();
    }

    @Override // java.io.OutputStream
    public void write(byte[] abyte0, int i, int j) throws Exception {
        int k = 0;
        while (k < j) {
            int l = this.file.write(abyte0, i + k, j - k);
            k += l;
            Thread.yield();
        }
    }

    @Override // java.io.OutputStream
    public void write(int i) throws Exception {
        byte[] bArr = this._abuf;
        bArr[0] = (byte) (i & 255);
        write(bArr, 0, 1);
    }
}
