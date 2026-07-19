package com.xce.io;

/* JADX INFO: loaded from: classes.dex */
public class CharToByteISO8859_8 extends CharToByteConverter {
    private static byte convertUnicodeToIso(char c) {
        if (c <= 190) {
            return (byte) c;
        }
        if (c >= 1488 && c <= 1514) {
            return (byte) ((c - 1264) & 255);
        }
        if (c == 8215) {
            return (byte) -33;
        }
        if (c == 8206) {
            return (byte) -3;
        }
        return (byte) (c != 8207 ? 0 : -2);
    }

    @Override // com.xce.io.CharToByteConverter
    public int convert(char[] ac, int i, int j, byte[] abyte0, int k, int l) {
        int k1 = i + j;
        int l1 = k + l;
        if (k1 > ac.length) {
            k1 = ac.length - i;
        }
        if (l1 > abyte0.length) {
            l1 = abyte0.length - k;
        }
        int i1 = 0;
        int j1 = k;
        while (i1 < this.blen && j1 < l1) {
            abyte0[j1] = convertUnicodeToIso(this.buf[i1]);
            i1++;
            j1++;
        }
        if (i1 < this.blen) {
            System.arraycopy(this.buf, i1, this.buf, 0, this.blen - i1);
            this.blen -= i1;
            if (j > 0) {
                carry(ac, i, j);
            }
            return j1 - k;
        }
        int i12 = i;
        while (i12 < k1 && j1 < l1) {
            abyte0[j1] = convertUnicodeToIso(ac[i12]);
            i12++;
            j1++;
        }
        if (i12 < k1) {
            carry(ac, i12, k1 - i12);
        }
        return j1 - k;
    }

    @Override // com.xce.io.CharToByteConverter
    public int getNumberOfBytes(char[] ac, int i, int j) {
        return j;
    }
}
