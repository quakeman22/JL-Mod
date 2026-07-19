package com.skt.m;

/* JADX INFO: loaded from: classes.dex */
public class SMSMessage {
    public static final int APPLICATION_DATA = 3;
    public static final byte DEFAULT_OPTION = 1;
    public static final int DOWNLOAD_NOTIFICATION = 2;
    public static final byte NOT_CONFIRM_OPTION = 2;
    public static final int SHORT_MESSAGE = 1;
    public static final int UNKNOWN = 0;
    private String comment;
    private byte[] data;
    private String name;
    private byte option;
    private String sender;
    private int type;
    private String url;

    public SMSMessage() {
        this.sender = null;
        this.url = null;
        this.name = null;
        this.comment = null;
        this.data = null;
    }

    public SMSMessage(byte[] abyte0, String s) {
        this.type = 1;
        this.sender = s;
        this.data = abyte0;
    }

    public SMSMessage(String s, String s1, String s2) {
        this.type = 2;
        this.url = s;
        this.name = s1;
        this.comment = s2;
    }

    public SMSMessage(String s, byte[] abyte0) {
        this((byte) 1, s, abyte0);
    }

    public SMSMessage(byte byte0, String s, byte[] abyte0) {
        this.type = 3;
        if (byte0 != 1 && byte0 != 2) {
            this.option = (byte) 1;
        } else {
            this.option = byte0;
        }
        this.name = s;
        this.data = abyte0;
    }

    public int getType() {
        return this.type;
    }

    public byte[] getShortMessage() {
        if (this.type != 1) {
            return null;
        }
        return this.data;
    }

    public String getSender() {
        if (this.type != 1) {
            return null;
        }
        return this.sender;
    }

    public String getURL() {
        if (this.type != 2) {
            return null;
        }
        return this.url;
    }

    public String getName() {
        if (this.type != 2) {
            return null;
        }
        return this.name;
    }

    public String getComment() {
        if (this.type != 2) {
            return null;
        }
        return this.comment;
    }

    public String getCName() {
        if (this.type != 3) {
            return null;
        }
        return this.name;
    }

    public byte[] getAppData() {
        if (this.type != 3) {
            return null;
        }
        return this.data;
    }

    public byte getServiceOption() {
        return this.option;
    }
}
