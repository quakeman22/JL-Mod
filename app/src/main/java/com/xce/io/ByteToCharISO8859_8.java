package com.xce.io;

/* JADX INFO: loaded from: classes.dex */
public class ByteToCharISO8859_8 extends ByteToCharConverter {
    private static int bBuf = 0;

    private static char convertIsoToUnicode(byte byte0) {
        int i = byte0 & 255;
        bBuf = i;
        if (i <= 190) {
            return (char) i;
        }
        if (i >= 224 && i <= 250) {
            return (char) (i + 1264);
        }
        if (i == 223) {
            return (char) 8215;
        }
        if (i == 253) {
            return (char) 8206;
        }
        return i != 254 ? (char) 0 : (char) 8207;
    }

    @Override // com.xce.io.ByteToCharConverter
    public int convert(byte[] abyte0, int i, int j, char[] ac, int k, int l) {
        int k1 = i + j;
        int l1 = k + l;
        if (k1 > abyte0.length) {
            k1 = abyte0.length;
        }
        if (l1 > ac.length) {
            l1 = ac.length;
        }
        int i1 = 0;
        int j1 = k;
        while (i1 < this.blen && j1 < l1) {
            ac[j1] = convertIsoToUnicode(this.buf[j1]);
            i1++;
            j1++;
        }
        if (i1 < this.blen) {
            System.arraycopy(this.buf, i1, this.buf, 0, this.blen - i1);
            this.blen -= i1;
            if (j > 0) {
                carry(abyte0, i, j);
            }
            return j1 - k;
        }
        this.blen = 0;
        int i12 = i;
        while (i12 < k1 && j1 < l1) {
            ac[j1] = convertIsoToUnicode(abyte0[i12]);
            i12++;
            j1++;
        }
        if (i12 < k1) {
            carry(abyte0, i12, k1 - i12);
        }
        return j1 - k;
    }

    @Override // com.xce.io.ByteToCharConverter
    public int getNumberOfChars(byte[] abyte0, int i, int j) {
        return j;
    }
}
