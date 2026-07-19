package com.xce.io;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class FileInputStream extends InputStream {
    private byte[] _abuf;
    private XFile file;
    private int marked;

    public FileInputStream(int i) {
        this._abuf = new byte[1];
        if (i != 0) {
            throw new IllegalArgumentException("illegal stdin");
        }
        this.file = new XFile(i);
    }

    public FileInputStream(String s) throws IOException {
        this(new XFile(s, 1));
    }

    public FileInputStream(XFile xfile) throws IOException {
        this._abuf = new byte[1];
        if ((xfile.mode & 1) == 1 && (xfile.type == 1 || xfile.type == 3)) {
            this.file = xfile;
            this.marked = -1;
            return;
        }
        throw new IllegalArgumentException("illegal file mode");
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        return this.file.available();
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.file.close();
    }

    @Override // java.io.InputStream
    public synchronized void mark(int i) {
        this.marked = this.file.offset;
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (read(this._abuf, 0, 1) == -1) {
            return -1;
        }
        return this._abuf[0] & 255;
    }

    @Override // java.io.InputStream
    public int read(byte[] abyte0) throws IOException {
        return read(abyte0, 0, abyte0.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] abyte0, int i, int j) throws IOException {
        return this.file.read(abyte0, i, j);
    }

    @Override // java.io.InputStream
    public synchronized void reset() throws IOException {
        this.file.seek(this.marked, 0);
    }

    @Override // java.io.InputStream
    public long skip(long l) throws IOException {
        this.file.seek((int) l, 1);
        return l;
    }
}
