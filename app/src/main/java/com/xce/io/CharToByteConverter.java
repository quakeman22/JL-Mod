package com.xce.io;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/* JADX INFO: loaded from: classes.dex */
public abstract class CharToByteConverter {
    private static Hashtable cache;
    private static Object noConverter = new Object();
    protected int blen;
    protected char[] buf;

    public abstract int convert(char[] cArr, int i, int i2, byte[] bArr, int i3, int i4);

    public abstract int getNumberOfBytes(char[] cArr, int i, int i2);

    public void carry(char[] ac, int i, int j) {
        int i1 = this.blen + j;
        char[] cArr = this.buf;
        if (ac == cArr && i == 0) {
            return;
        }
        if (cArr == null) {
            int k = j >= 128 ? j : 128;
            this.buf = new char[k];
        } else if (i1 > cArr.length) {
            int l = cArr.length * 2;
            while (l < i1) {
                l *= 2;
            }
            char[] ac1 = new char[l];
            System.arraycopy(this.buf, 0, ac1, 0, this.blen);
            this.buf = ac1;
        }
        System.arraycopy(ac, i, this.buf, this.blen, j);
        this.blen = i1;
    }

    public int flush(byte[] abyte0, int i, int j) {
        if (this.blen == 0) {
            return 0;
        }
        int k = this.blen;
        this.blen = 0;
        return convert(this.buf, 0, k, abyte0, i, j);
    }

    private static CharToByteConverter getConverterInternal(String s) {
        Object obj = cache.get(s);
        if (obj == noConverter) {
            return null;
        }
        if (obj != null) {
            return (CharToByteConverter) obj;
        }
        String s1 = "com.xce.io.CharToByte" + ConverterAlias.alias(s);
        try {
            Object obj1 = Class.forName(s1).newInstance();
            cache.put(s, obj1);
            return (CharToByteConverter) obj1;
        } catch (ClassCastException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            cache.put(s, noConverter);
            return null;
        }
    }

    public static CharToByteConverter getConverter(String s) throws UnsupportedEncodingException {
        CharToByteConverter chartobyteconverter = getConverterInternal(s);
        if (chartobyteconverter == null) {
            throw new UnsupportedEncodingException(s);
        }
        return chartobyteconverter;
    }

    public static CharToByteConverter getDefault() {
        try {
            String s = System.getProperty("file.encoding");
            if (s != null) {
                return getConverter(s);
            }
        } catch (UnsupportedEncodingException e) {
        }
        return new CharToByteDefault();
    }

    static {
        Hashtable hashtable = new Hashtable();
        cache = hashtable;
        hashtable.put("EUC-KR", new CharToByteEUC_KR());
        cache.put("ISO-8859-8", new CharToByteISO8859_8());
    }
}
