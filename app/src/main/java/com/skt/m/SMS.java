package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public final class SMS {
    private static SMSListener listener;

    public static native boolean get(int i, SMSMessage sMSMessage);

    private static native boolean nativeSend(String str, SMSMessage sMSMessage);

    private SMS() {
    }

    public static synchronized void setSMSListener(SMSListener smslistener) {
    }

    public static synchronized SMSListener getSMSListener() {
        return listener;
    }

    public static synchronized SMSMessage get(int i) throws IllegalArgumentException {
        return null;
    }

    public static synchronized boolean send(String s, SMSMessage smsmessage) {
        return false;
    }
}
