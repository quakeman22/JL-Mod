package com.xce.jam;

import com.xce.io.FileOutputStream;
import com.xce.util.XProperties;
import javax.microedition.lcdui.Image;
import net.lingala.zip4j.util.InternalZipConstants;

/* JADX INFO: loaded from: classes.dex */
public class AppInfo {
    public static final int GVM = 2;
    public static final int JAR = 1;
    public static final int ROM = 0;
    public static final int WAP = 3;
    static int strIndex;
    public String className;
    public long downTime;
    public String fileName;
    public Image icon;
    public int id;
    public String jarUrl;
    public String name;
    public XProperties prop;
    public int size;
    public int type;
    public String updateUrl;
    public int useCount;
    public long useTime;
    public int userSet;
    public String vendor;
    public long xdate;

    public String getProperty(String s) {
        XProperties xProperties = this.prop;
        if (xProperties != null) {
            return xProperties.getProperty(s);
        }
        return null;
    }

    public void loadFull() {
        if (this.type != 1 || this.prop != null) {
            return;
        }
        XProperties xproperties = new XProperties();
        String s = InternalZipConstants.ZIP_FILE_SEPARATOR + this.id + '/' + this.id + ".msd";
        xproperties.loadFile(s);
        this.prop = xproperties;
        this.updateUrl = xproperties.getProperty("MIDlet-X-Update-URL");
        try {
            StringBuffer stringBuffer = new StringBuffer(3);
            stringBuffer.append('/');
            stringBuffer.append(this.id);
            this.size = JAMUtil.dirsize(stringBuffer.toString());
        } catch (Exception e) {
            this.size = 0;
        }
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:11:0x003b -> B:25:0x004d). Please report as a decompilation issue!!! */
    public void store() {
        if (this.type != 1 || this.prop == null) {
            return;
        }
        FileOutputStream fileoutputstream = null;
        try {
            try {
                String s = InternalZipConstants.ZIP_FILE_SEPARATOR + this.id + '/' + this.id + ".msd";
                fileoutputstream = new FileOutputStream(s);
                this.prop.storeOriginal(fileoutputstream);
                fileoutputstream.close();
            } catch (Exception e) {
            }
        } catch (Exception e2) {
            if (fileoutputstream != null) {
                fileoutputstream.close();
            }
        } catch (Throwable th) {
            if (fileoutputstream != null) {
                try {
                    fileoutputstream.close();
                } catch (Exception e3) {
                }
            }
            throw th;
        }
    }

    public static boolean isUpdated(String s, String s1) {
        int i = getVersion(s, 0);
        int j = strIndex;
        int k = getVersion(s1, 0);
        int l = strIndex;
        if (i < k) {
            return true;
        }
        if (i > k) {
            return false;
        }
        int i2 = getVersion(s, j);
        int j2 = strIndex;
        int k2 = getVersion(s1, l);
        int l2 = strIndex;
        if (i2 < k2) {
            return true;
        }
        if (i2 > k2 || getVersion(s, j2) >= getVersion(s1, l2)) {
            return false;
        }
        return true;
    }

    static int getVersion(String s, int i) {
        if (s.length() == i) {
            return 0;
        }
        int j = s.indexOf(46, i);
        if (j == -1) {
            strIndex = s.length();
            return Integer.parseInt(s.substring(i));
        }
        strIndex = j + 1;
        return Integer.parseInt(s.substring(i, j));
    }
}
