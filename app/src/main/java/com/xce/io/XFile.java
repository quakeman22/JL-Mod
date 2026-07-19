package com.xce.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.impl.RecordStoreImpl;

/* JADX INFO: loaded from: classes.dex */
public class XFile {
    public static final int DIRECTORY = 2;
    public static final int FILE_JAR = 3;
    public static final int NORMAL = 1;
    public static final int READ = 1;
    public static final int READ_DIRECTORY = 4;
    public static final int READ_RESOURCE = 8;
    public static final int READ_WRITE = 3;
    public static final int SEEK_CUR = 1;
    public static final int SEEK_END = 2;
    public static final int SEEK_SET = 0;
    public static final int STDERR = 2;
    public static final int STDIN = 0;
    public static final int STDOUT = 1;
    public static final int STDSTREAM = 0;
    public static final int WRITE = 2;
    public ByteArrayInputStream bis;
    public boolean bool;
    public ByteArrayOutputStream bos;
    protected byte[] buf;
    protected int fd;
    protected int mode;
    protected int offset;
    public RecordStoreImpl recordStore;
    public String str;
    protected int type;

    public XFile(String paramString, int paramInt) throws Exception {
        this.bool = true;
        this.bis = null;
        this.bos = null;
        System.out.println("XFile paramInt " + paramInt);
        System.out.println("XFile paramString " + paramString);
        String paramString2 = paramString.replace('/', '_');
        this.str = paramString2;
        try {
            System.out.println("XFile " + paramString2);
            RecordStoreImpl recordStoreImpl = (RecordStoreImpl) RecordStore.openRecordStore(paramString2, paramInt != 1);
            this.recordStore = recordStoreImpl;
            if (recordStoreImpl == null) {
                throw new IOException();
            }
        } catch (Exception exception) {
            this.bool = false;
            if (paramInt == 1) {
                throw exception;
            }
        }
    }

    public XFile(int i) {
        this.bool = true;
        this.bis = null;
        this.bos = null;
        if (i != 0 && i != 1 && i != 2) {
            throw new IllegalArgumentException("illegal standard IO");
        }
        this.fd = i;
        this.type = 0;
        if (i == 0) {
            this.mode = 1;
        }
        if (i == 1 || i == 2) {
            this.mode = 2;
        }
    }

    public XFile(String s, String s1) throws IOException {
        this.bool = true;
        this.bis = null;
        this.bos = null;
        if (s == null || s1 == null) {
            throw new IllegalArgumentException("jarfile or name is null");
        }
        this.mode = 1;
        this.type = 3;
        this.fd = 0;
        this.offset = 0;
        byte[] bArrNativeJarOpen = nativeJarOpen(s.getBytes(), s1.getBytes());
        this.buf = bArrNativeJarOpen;
        if (bArrNativeJarOpen == null) {
            throw new IOException("File not found (in jar): " + s1);
        }
    }

    public int available() throws RecordStoreException, IOException {
        System.out.println("available " + this.str);
        System.out.println("available recordStore " + this.recordStore.getNextRecordID());
        if (this.bool) {
            int j = this.recordStore.getNextRecordID() - 1;
            int i = this.recordStore.getRecordSize(j);
            return i;
        }
        System.out.println("available false");
        throw new IOException("invalid xfile type");
    }

    public void close() {
        if (this.bos != null) {
            try {
                if (this.recordStore.getNumRecords() > 0) {
                    this.recordStore.closeRecordStore();
                    RecordStore.deleteRecordStore(this.str);
                    this.recordStore = (RecordStoreImpl) RecordStore.openRecordStore(this.str, true);
                }
                byte[] arrayOfByte = this.bos.toByteArray();
                System.out.println("close : " + arrayOfByte.length);
                this.recordStore.addRecord(arrayOfByte, 0, arrayOfByte.length);
            } catch (Exception e) {
            }
        }
        try {
            this.recordStore.closeRecordStore();
        } catch (Exception e2) {
        }
    }

    public int write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws Exception {
        System.out.println("write " + this.str);
        System.out.println("write paramInt1 " + paramInt1);
        System.out.println("write paramInt2 " + paramInt2);
        if (this.bos == null) {
            this.bos = new ByteArrayOutputStream();
        }
        this.bos.write(paramArrayOfbyte, paramInt1, paramInt2);
        return 0;
    }

    public int read(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws Exception {
        System.out.println("read " + this.str);
        System.out.println("read paramInt1 " + paramInt1);
        System.out.println("read paramInt2 " + paramInt2);
        if (this.bis == null) {
            this.bis = new ByteArrayInputStream(this.recordStore.getRecord(r1.getNextRecordID() - 1));
        }
        this.bis.read(paramArrayOfbyte, paramInt1, paramInt2);
        return 0;
    }

    public static void mkdir(String s) throws IOException {
    }

    public static void rmrdir(String s) throws IOException {
    }

    public static void rmdir(String s) throws IOException {
    }

    public String readdir() throws IOException {
        return "";
    }

    public void flush() throws IOException {
    }

    public int seek(int i, int j) throws IOException {
        return 1;
    }

    public static boolean exists(String paramString) throws IOException {
        String paramString2 = paramString.replace('/', '_');
        System.out.println("exists " + paramString2);
        try {
            RecordStore.openRecordStore(paramString2, false).closeRecordStore();
            return true;
        } catch (Exception e) {
            System.out.println("exists false");
            return false;
        }
    }

    public static int filesize(String s) throws IOException {
        return 1;
    }

    public static final int unlink(String paramString) {
        String paramString2 = paramString.replace('/', '_');
        System.out.println("unlink " + paramString2);
        try {
            RecordStore.deleteRecordStore(paramString2);
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static int fsused() {
        return -1;
    }

    public static int fsavail() {
        return -1;
    }

    private int nativeResourceOpen(byte[] abyte0) {
        return 0;
    }

    private static byte[] nativeJarOpen(byte[] abyte0, byte[] abyte1) {
        return null;
    }

    private static int nativeFileOpen(byte[] abyte0, int i) {
        return 0;
    }

    private static int nativeDirOpen(byte[] abyte0) {
        return 0;
    }

    private static int nativeDirMake(byte[] abyte0) {
        return 0;
    }

    private static int nativeDirRemove(byte[] abyte0) {
        return 0;
    }

    private static int nativeFileAvailable(int i) {
        return 0;
    }

    private static int nativeFileClose(int i) {
        return 0;
    }

    private static int nativeDirClose(int i) {
        return 0;
    }

    private static int nativeStdinRead(byte[] abyte0, int i, int j) {
        return 0;
    }

    private static int nativeFileRead(int i, byte[] abyte0, int j, int k) {
        return 0;
    }

    private static byte[] nativeDirRead(int i) {
        return null;
    }

    private static int nativeStdoutWrite(byte[] abyte0, int i, int j) {
        return 0;
    }

    private static int nativeStderrWrite(byte[] abyte0, int i, int j) {
        return 0;
    }

    private static int nativeFileWrite(int i, byte[] abyte0, int j, int k) {
        return 1;
    }

    private static int nativeFileFlush(int i) {
        return 1;
    }

    private static int nativeFileSeek(int i, int j, int k) {
        return 1;
    }

    private static int nativeFileExists(byte[] abyte0) {
        return 1;
    }

    private static int nativeFileSize(byte[] abyte0) {
        return 1;
    }

    private static int nativeFileUnlink(byte[] abyte0) {
        return 1;
    }
}
