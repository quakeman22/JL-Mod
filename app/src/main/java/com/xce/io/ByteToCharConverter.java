package com.xce.io;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/* JADX INFO: loaded from: classes.dex */
public abstract class ByteToCharConverter {
    private static Hashtable cache;
    private static Object noConverter = new Object();
    protected int blen = 0;
    protected byte[] buf;

    public abstract int convert(byte[] bArr, int i, int i2, char[] cArr, int i3, int i4);

    public abstract int getNumberOfChars(byte[] bArr, int i, int i2);

    public void carry(byte[] abyte0, int i, int j) {
        int i1 = this.blen + j;
        byte[] bArr = this.buf;
        if (bArr == null) {
            int k = j >= 128 ? j : 128;
            this.buf = new byte[k];
        } else if (i1 > bArr.length) {
            int l = bArr.length * 2;
            while (l < i1) {
                l *= 2;
            }
            byte[] abyte1 = new byte[l];
            System.arraycopy(this.buf, 0, abyte1, 0, this.blen);
            this.buf = abyte1;
        }
        System.arraycopy(abyte0, i, this.buf, this.blen, j);
        this.blen = i1;
    }

    public int flush(char[] ac, int i, int j) {
        if (this.blen == 0) {
            return 0;
        }
        int k = this.blen;
        this.blen = 0;
        return convert(this.buf, 0, k, ac, i, j);
    }

    public int bflush(byte[] abyte0, int i, int j) {
        byte[] bArr = this.buf;
        if (bArr == null) {
            return 0;
        }
        int i2 = this.blen;
        if (i2 > j) {
            System.arraycopy(bArr, 0, abyte0, i, j);
            byte[] bArr2 = this.buf;
            System.arraycopy(bArr2, j, bArr2, 0, this.blen - j);
            this.blen -= j;
            return j;
        }
        System.arraycopy(bArr, 0, abyte0, i, i2);
        int k = this.blen;
        this.blen = 0;
        return k;
    }

    private static ByteToCharConverter getConverterInternal(String s) {
        Object obj = cache.get(s);
        if (obj == noConverter) {
            return null;
        }
        if (obj != null) {
            return (ByteToCharConverter) obj;
        }
        String s1 = "com.xce.io.ByteToChar" + ConverterAlias.alias(s);
        try {
            Object obj1 = Class.forName(s1).newInstance();
            cache.put(s, obj1);
            return (ByteToCharConverter) obj1;
        } catch (ClassCastException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            cache.put(s, noConverter);
            return null;
        }
    }

    public static ByteToCharConverter getConverter(String s) throws UnsupportedEncodingException {
        ByteToCharConverter bytetocharconverter = getConverterInternal(s);
        if (bytetocharconverter != null) {
            return bytetocharconverter;
        }
        throw new UnsupportedEncodingException(s);
    }

    public static ByteToCharConverter getDefault() {
        try {
            String s = System.getProperty("file.encoding");
            if (s != null) {
                return getConverter(s);
            }
        } catch (UnsupportedEncodingException e) {
        }
        return new ByteToCharDefault();
    }

    static {
        Hashtable hashtable = new Hashtable();
        cache = hashtable;
        hashtable.put("EUC-KR", new ByteToCharEUC_KR());
        cache.put("ISO-8859-8", new ByteToCharISO8859_8());
    }
}
