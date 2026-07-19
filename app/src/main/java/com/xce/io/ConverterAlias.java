package com.xce.io;

/* JADX INFO: loaded from: classes.dex */
public class ConverterAlias {
    static String alias(String s) {
        if (s.toUpperCase().equals("EUC-KR")) {
            return "EUC_KR";
        }
        if (s.toUpperCase().equals("ISO-8859-8")) {
            return "ISO8859_8";
        }
        return s;
    }
}
