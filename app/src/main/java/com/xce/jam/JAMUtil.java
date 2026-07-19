package com.xce.jam;

import com.skt.m.Device;
import com.xce.io.XFile;
import com.xce.lcdui.Toolkit;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.lingala.zip4j.util.InternalZipConstants;

/* JADX INFO: loaded from: classes.dex */
public class JAMUtil {
    public static final int SIZE_ANIICON = 2976;
    public static final int SIZE_ICON = 622;
    private static HttpConnection con;
    private static DataInputStream dis;
    private static InputStream is;

    public static JAMRes getRes() {
        try {
            String s = System.getProperty("microedition.locale");
            int i = s.indexOf(45);
            if (i != -1) {
                s = s.substring(0, i);
            }
            JAMRes jamres = (JAMRes) Class.forName("com.xce.jam.JAMRes_" + s).newInstance();
            return jamres;
        } catch (Exception e) {
            try {
                JAMRes jamres2 = (JAMRes) Class.forName("com.xce.jam.JAMRes_en").newInstance();
                return jamres2;
            } catch (Exception e2) {
                return null;
            }
        }
    }

    public static void invokeDefaultWap() {
        if (Toolkit.IS_KOREAN) {
            Device.invokeWapBrowser("http://wap.nate.com/jsp/wizard.jsp");
        }
    }

    public static String getUAF() {
        StringBuffer stringbuffer = new StringBuffer();
        stringbuffer.append(System.getProperty("m.CARRIER"));
        stringbuffer.append(System.getProperty("m.TYPE"));
        stringbuffer.append(System.getProperty("m.VENDER"));
        stringbuffer.append(System.getProperty("m.MODEL"));
        stringbuffer.append(System.getProperty("m.CONF_PROFILE"));
        stringbuffer.append(System.getProperty("m.SK_VM"));
        stringbuffer.append(System.getProperty("m.EXT_SW"));
        stringbuffer.append(System.getProperty("m.LCD_PIXEL"));
        stringbuffer.append(System.getProperty("m.LCD_FONT"));
        stringbuffer.append(System.getProperty("m.SOUND_POLY"));
        stringbuffer.append(System.getProperty("m.COLOR"));
        stringbuffer.append(System.getProperty("m.MIN"));
        return stringbuffer.toString();
    }

    public static void setMenu(String s, AppInfo appinfo) {
        XBrowser.setMenuLocation(s, "");
    }

    public static long getFree(int i, int j, int k) {
        int l = 0;
        long l1 = XFile.fsavail();
        if (k > 0) {
            String s = InternalZipConstants.ZIP_FILE_SEPARATOR + k + InternalZipConstants.ZIP_FILE_SEPARATOR;
            try {
                if (XFile.exists(String.valueOf(s) + k + ".msd")) {
                    l = 0 + XFile.filesize(String.valueOf(s) + k + ".msd");
                }
                if (XFile.exists(String.valueOf(s) + k + ".jar")) {
                    l += XFile.filesize(String.valueOf(s) + k + ".jar");
                }
                if (XFile.exists(String.valueOf(s) + k + ".lbm")) {
                    l += XFile.filesize(String.valueOf(s) + k + ".lbm");
                }
                if (XFile.exists(String.valueOf(s) + "ani" + k + ".lbm")) {
                    l += XFile.filesize(String.valueOf(s) + "ani" + k + ".lbm");
                }
            } catch (Exception e) {
            }
        } else {
            l = 0;
        }
        return ((long) (((i + j) + SIZE_ICON) + SIZE_ANIICON)) - (((long) l) + l1);
    }

    public static long getFree(int i, String s, int j) {
        int k = 0;
        long l = XFile.fsavail();
        if (j > 0) {
            String s2 = InternalZipConstants.ZIP_FILE_SEPARATOR + j + InternalZipConstants.ZIP_FILE_SEPARATOR + s;
            try {
                if (XFile.exists(s2)) {
                    k = 0 + XFile.filesize(s2);
                }
            } catch (Exception e) {
            }
        } else {
            k = 0;
        }
        return ((long) i) - (((long) k) + l);
    }

    public static int dirsize(String str) {
        throw new Error("Unresolved compilation problem: \n\tSyntax error, insert \"VariableDeclarators\" to complete LocalVariableDeclaration\n");
    }

    public static String getLineString(String s, int i) {
        String s1 = s.substring(0, getLineIndex(s, i) + 1);
        return s1;
    }

    public static int getLineIndex(String s, int i) {
        int j = Toolkit.DEFAULT_FONT.stringWidth(s);
        int k = s.length() - 1;
        while (j > i) {
            j -= Toolkit.DEFAULT_FONT.charWidth(s.charAt(k));
            k--;
        }
        return k;
    }

    public static String[] getLines(String s, int i) {
        int[] ai = new int[s.length()];
        int j = 0;
        int k = 0;
        for (int l = 0; l < s.length(); l++) {
            char c = s.charAt(l);
            if (c == '\n') {
                if (k != 0 && l != s.length() - 1) {
                    j++;
                }
                k = 0;
            } else if (k == 0) {
                ai[j] = l;
                k = Toolkit.DEFAULT_FONT.charWidth(c);
            } else {
                k += Toolkit.DEFAULT_FONT.charWidth(c);
                if (k > i) {
                    j++;
                    ai[j] = l;
                    k = Toolkit.DEFAULT_FONT.charWidth(c);
                }
            }
        }
        int l2 = j + 1;
        String[] as = new String[l2];
        for (int i1 = 0; i1 <= j; i1++) {
            if (i1 == j) {
                as[i1] = s.substring(ai[i1]);
            } else {
                as[i1] = s.substring(ai[i1], ai[i1 + 1]);
            }
        }
        return as;
    }

    public static void setConnection(String s) throws IOException {
        try {
            HttpConnection httpConnection = (HttpConnection) Connector.open(s);
            con = httpConnection;
            setConn(httpConnection);
        } catch (IOException e) {
            throw new IOException(XBrowser.JAM_RES.MSG_FAIL_CONN);
        }
    }

    public static void setConn(HttpConnection httpconnection) throws IOException {
        httpconnection.setRequestProperty("User-Agent", getUAF());
    }

    public static void setRequestProperty(String s, String s1) throws IOException {
        try {
            con.setRequestProperty(s, s1);
        } catch (IOException e) {
            throw new IOException(XBrowser.JAM_RES.MSG_FAIL_CONN);
        }
    }

    public static int getResponseCode() {
        try {
            return con.getResponseCode();
        } catch (IOException e) {
            return -1;
        }
    }

    public static int getConLength() {
        return (int) con.getLength();
    }

    public static void setInputStream() throws IOException {
        try {
            is = con.openInputStream();
        } catch (IOException e) {
            throw new IOException(XBrowser.JAM_RES.MSG_FAIL_CONN);
        }
    }

    public static void setDataInputStream() throws IOException {
        try {
            dis = con.openDataInputStream();
        } catch (IOException e) {
            throw new IOException(XBrowser.JAM_RES.MSG_FAIL_CONN);
        }
    }

    public static void inputStreamRead(byte[] abyte0) throws IOException {
        int i = 0;
        do {
            try {
                int j = is.read(abyte0, i, abyte0.length - i);
                if (j > 0) {
                    i += j;
                } else {
                    throw new IOException(XBrowser.JAM_RES.MSG_NOT_DATA);
                }
            } catch (IOException e) {
                throw new IOException(XBrowser.JAM_RES.MSG_NOT_DATA);
            }
        } while (i != abyte0.length);
    }

    public static int DataInputStreamRead(byte[] abyte0, int i, int j) throws IOException {
        try {
            int k = dis.read(abyte0, i, j);
            return k;
        } catch (IOException e) {
            throw new IOException(XBrowser.JAM_RES.MSG_NOT_DATA);
        }
    }

    public static InputStream getInputStream() {
        return is;
    }

    public static void setBilling(boolean flag) {
        try {
            if (flag) {
                con.sendACK("RELEASE");
            } else {
                con.sendACK("UPDATE");
            }
        } catch (IOException e) {
        }
    }

    public static void endDown() throws IOException {
        throw new Error("Unresolved compilation problem: \n\tSocket cannot be resolved\n");
    }

    public static void netFinalize() {
        try {
            InputStream inputStream = is;
            if (inputStream != null) {
                inputStream.close();
            }
            HttpConnection httpConnection = con;
            if (httpConnection != null) {
                httpConnection.close();
            }
            DataInputStream dataInputStream = dis;
            if (dataInputStream != null) {
                dataInputStream.close();
            }
        } catch (IOException e) {
        } catch (Throwable th) {
            is = null;
            con = null;
            dis = null;
            throw th;
        }
        is = null;
        con = null;
        dis = null;
    }
}
