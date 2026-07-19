package com.xce.io;

/* JADX INFO: loaded from: classes.dex */
public class CharToByteEUC_KR extends CharToByteConverter {
    static int[] retConvert = new int[2];

    private static native void nativeConvert(int[] iArr, char[] cArr, int i, char[] cArr2, int i2, int i3, byte[] bArr, int i4, int i5);

    private static native int nativeNumberOfBytes(char[] cArr, int i, int i2);

    public static native int unicodeToEUCKR(int i);

    @Override // com.xce.io.CharToByteConverter
    public int convert(char[] ac, int i, int j, byte[] abyte0, int k, int l) throws Throwable {
        int i1;
        int j1;
        synchronized (retConvert) {
            try {
                nativeConvert(retConvert, this.buf, this.blen, ac, i, j, abyte0, k, l);
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
                carry(ac, i, j);
            }
            return i1;
        }
        this.blen = 0;
        int j12 = j1 + (i - this.blen);
        int k1 = (i + j) - j12;
        if (k1 > 0) {
            carry(ac, j12, k1);
        }
        return i1;
    }

    @Override // com.xce.io.CharToByteConverter
    public int getNumberOfBytes(char[] ac, int i, int j) {
        return nativeNumberOfBytes(ac, i, j);
    }
}
