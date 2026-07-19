package com.xce.io;

/* JADX INFO: loaded from: classes.dex */
public class ByteToCharEUC_KR extends ByteToCharConverter {
    static int[] retConvert = new int[2];

    public static native int euckrToUnicode(int i);

    private static native void nativeConvert(int[] iArr, byte[] bArr, int i, byte[] bArr2, int i2, int i3, char[] cArr, int i4, int i5);

    private static native int nativeNumberOfChars(byte[] bArr, int i, int i2);

    @Override // com.xce.io.ByteToCharConverter
    public int convert(byte[] abyte0, int i, int j, char[] ac, int k, int l) throws Throwable {
        int i1;
        int j1;
        synchronized (retConvert) {
            try {
                nativeConvert(retConvert, this.buf, this.blen, abyte0, i, j, ac, k, l);
                int[] iArr = retConvert;
                i1 = iArr[0];
                j1 = iArr[1];
            } catch (Throwable th) {
                th = th;
                while (true) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            }
        }
        if (j1 < this.blen) {
            System.arraycopy(this.buf, j1, this.buf, 0, this.blen - j1);
            this.blen -= j1;
            if (j > 0) {
                carry(abyte0, i, j);
            }
            return i1;
        }
        int j12 = j1 + (i - this.blen);
        int k1 = (i + j) - j12;
        this.blen = 0;
        if (k1 > 0) {
            carry(abyte0, j12, k1);
        }
        return i1;
    }

    @Override // com.xce.io.ByteToCharConverter
    public int getNumberOfChars(byte[] abyte0, int i, int j) {
        return nativeNumberOfChars(abyte0, i, j);
    }
}
