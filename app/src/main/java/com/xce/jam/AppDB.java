package com.xce.jam;

import com.skt.m.AudioSystem;
import com.xce.io.XFile;
import com.xce.lcdui.Toolkit;
import com.xce.util.Debug;
import com.xce.util.XProperties;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Vector;
import net.lingala.zip4j.util.InternalZipConstants;

/* JADX INFO: loaded from: classes.dex */
public class AppDB {
    public static final String DB_NAME = "/rs/appdb.lst";
    public static final int GVMDATA_SIZE = 24;
    public static final String HEADER = "XBROWSER11";
    public static final int HEADER_SIZE = 10;
    public static final int JARDATA_SIZE = 189;
    public static final int JAR_POS = 17;
    public static final int MAX_JARAPP_SIZE = 30;
    public static final int MAX_NAME_LEN = 50;
    public static final int SORT_COUNT = 2;
    public static final int SORT_DOWN = 0;
    public static final int SORT_TIME = 1;
    public static final int SORT_USER = 3;
    public static final int VIEW_ICON = 0;
    public static final int VIEW_LIST = 1;
    public static AppDB db;
    public static byte setSort;
    public static byte setSound;
    public static byte setView;
    AppInfo[] backList;
    int romAppCount;
    public static final int GVM_POS = 5687;
    public static final int DB_SIZE = ((Gvm.getMaxContents() + 1) * 24) + GVM_POS;
    AppInfo[] jarAppHash = new AppInfo[30];
    AppInfo[] gvmAppHash = new AppInfo[Gvm.getMaxContents() + 1];
    Vector appList = new Vector(40);

    private static native String readString(byte[] bArr, int i, int i2);

    private static native void writeString(String str, byte[] bArr, int i);

    private AppDB() {
        db = this;
        init();
    }

    public void init() {
        db.init(true);
    }

    public void init(boolean flag) {
        this.appList.removeAllElements();
        addRomElements();
        load(flag);
    }

    public static synchronized AppDB getDB() {
        if (db == null) {
            new AppDB();
        }
        return db;
    }

    public static void loadSoundVolume() {
        throw new Error("Unresolved compilation problem: \n\tSyntax error, insert \"VariableDeclarators\" to complete LocalVariableDeclaration\n");
    }

    public void load() {
        load(true);
    }

    public void load(boolean z) {
        throw new Error("Unresolved compilation problem: \n\tSyntax error, insert \"VariableDeclarators\" to complete LocalVariableDeclaration\n");
    }

    private void storeAll() {
        XFile xfile = null;
        try {
            try {
                try {
                    byte[] abyte0 = new byte[DB_SIZE];
                    byte[] abyte1 = HEADER.getBytes();
                    System.arraycopy(abyte1, 0, abyte0, 0, abyte1.length);
                    int i = 0 + abyte1.length;
                    int i2 = i + 1;
                    abyte0[i] = setView;
                    int i3 = i2 + 1;
                    abyte0[i2] = setSort;
                    int i4 = i3 + 1;
                    abyte0[i3] = setSound;
                    writeInt(abyte0, i4, sizeOfJarElement());
                    int i5 = i4 + 4;
                    for (int j = 0; j < 30; j++) {
                        AppInfo appinfo = this.jarAppHash[j];
                        if (appinfo == null) {
                            i5 += 189;
                        } else {
                            writeInt(abyte0, i5, appinfo.id);
                            int i6 = i5 + 4;
                            int l = appinfo.name.length();
                            int i7 = i6 + 1;
                            abyte0[i6] = (byte) (l <= 25 ? l : 25);
                            writeString(appinfo.name, abyte0, i7);
                            int i8 = i7 + 50;
                            int l2 = appinfo.vendor.length();
                            int l3 = i8 + 1;
                            abyte0[i8] = (byte) (l2 <= 25 ? l2 : 25);
                            writeString(appinfo.vendor, abyte0, l3);
                            int i9 = l3 + 50;
                            byte[] abyte2 = appinfo.className.getBytes();
                            int l4 = abyte2.length;
                            int i10 = i9 + 1;
                            abyte0[i9] = (byte) (l4 <= 50 ? l4 : 50);
                            System.arraycopy(abyte2, 0, abyte0, i10, l4);
                            int i11 = i10 + 50;
                            writeLong(abyte0, i11, appinfo.xdate);
                            int i12 = i11 + 8;
                            writeLong(abyte0, i12, appinfo.downTime);
                            int i13 = i12 + 8;
                            writeLong(abyte0, i13, appinfo.useTime);
                            int i14 = i13 + 8;
                            writeInt(abyte0, i14, appinfo.useCount);
                            int i15 = i14 + 4;
                            writeInt(abyte0, i15, appinfo.userSet);
                            i5 = i15 + 4;
                        }
                    }
                    int k = 0;
                    while (true) {
                        AppInfo[] appInfoArr = this.gvmAppHash;
                        if (k < appInfoArr.length) {
                            AppInfo appinfo1 = appInfoArr[k];
                            if (appinfo1 == null) {
                                i5 += 24;
                            } else {
                                writeLong(abyte0, i5, appinfo1.downTime);
                                int i16 = i5 + 8;
                                writeLong(abyte0, i16, appinfo1.useTime);
                                int i17 = i16 + 8;
                                writeInt(abyte0, i17, appinfo1.useCount);
                                int i18 = i17 + 4;
                                writeInt(abyte0, i18, appinfo1.userSet);
                                i5 = i18 + 4;
                            }
                            k++;
                        } else {
                            xfile = new XFile(DB_NAME, 3);
                            xfile.write(abyte0, 0, abyte0.length);
                            xfile.close();
                            return;
                        }
                    }
                } catch (Exception e) {
                }
            } catch (Exception exception) {
                Debug.debugOut("Critical Error : " + exception.getClass().getName());
                if (xfile != null) {
                    xfile.close();
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    xfile.close();
                } catch (Exception e2) {
                }
            }
            throw th;
        }
    }

    public void recover() {
        throw new Error("Unresolved compilation problems: \n\tMIDletInfo cannot be resolved to a type\n\tMIDletInfo cannot be resolved\n");
    }

    public void storeAppInfo(int i, int i2, boolean z) {
        throw new Error("Unresolved compilation problems: \n\tSyntax error, insert \"VariableDeclarators\" to complete LocalVariableDeclaration\n\tSyntax error on token(s), misplaced construct(s)\n\tINSTR cannot be resolved to a type\n\tSyntax error, insert \";\" to complete LocalVariableDeclarationStatement\n\tSyntax error on token \"AppInfo\", AssignmentOperator expected after this token\n\tSyntax error on token \"goto\", delete this token\n\tSyntax error on token \":\", ; expected\n\tSyntax error on token \"goto\", { expected\n\tSyntax error on token \"ioexception\", delete this token\n\tSyntax error on tokens, delete these tokens\n\tSyntax error on token \"ret\", = expected\n\tSyntax error, insert \"VariableDeclarators\" to complete LocalVariableDeclaration\n\tSyntax error, insert \";\" to complete LocalVariableDeclarationStatement\n\tSyntax error, insert \"}\" to complete Block\n");
    }

    public int getNextJarID() {
        for (int i = 0; i < 30; i++) {
            if (this.jarAppHash[i] == null) {
                return i + 1;
            }
        }
        return -1;
    }

    private void addRomElements() {
        XProperties xproperties = new XProperties();
        InputStream inputstream = null;
        try {
            try {
                inputstream = getClass().getResourceAsStream("__xvmrom__.lst");
                if (inputstream != null && inputstream.available() != 0) {
                    xproperties.load(inputstream, -1, false);
                }
                if (inputstream != null) {
                    inputstream.close();
                }
            } catch (IOException e) {
                if (inputstream != null) {
                    inputstream.close();
                }
            } catch (Throwable th) {
                if (inputstream != null) {
                    try {
                        inputstream.close();
                    } catch (Exception e2) {
                    }
                }
                throw th;
            }
        } catch (Exception e3) {
        }
        if (xproperties.size() <= 0) {
            return;
        }
        Enumeration enumeration = xproperties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String s = (String) enumeration.nextElement();
            addRomElement(s, xproperties.getProperty(s));
        }
    }

    private void addRomElement(String s, String s1) {
        AppInfo appinfo = new AppInfo();
        appinfo.type = 0;
        appinfo.name = s;
        appinfo.fileName = "";
        appinfo.className = s1;
        appinfo.icon = Toolkit.appImg();
        this.appList.addElement(appinfo);
        this.romAppCount++;
    }

    public int getRomNum() {
        return this.romAppCount;
    }

    public boolean addJarElement(AppInfo appInfo) {
        throw new Error("Unresolved compilation problems: \n\tMIDletInfo cannot be resolved to a type\n\tMIDletInfo cannot be resolved\n");
    }

    public Enumeration elements() {
        return this.appList.elements();
    }

    public AppInfo elementAt(int i) {
        if (i >= this.appList.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return (AppInfo) this.appList.elementAt(i);
    }

    public String getAppName(int i) {
        return elementAt(i).name.trim();
    }

    public int size() {
        return this.appList.size();
    }

    public void removeElementAt(int i) {
        if (i >= this.appList.size()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        AppInfo appinfo = (AppInfo) this.appList.elementAt(i);
        this.appList.removeElementAt(i);
        storeAppInfo(appinfo.type, appinfo.id, true);
        int i2 = appinfo.type;
        if (i2 != 1) {
            if (i2 == 2) {
                Gvm.removeContent(appinfo.id);
            }
        } else {
            XBrowser.deleteDir(InternalZipConstants.ZIP_FILE_SEPARATOR + appinfo.id);
        }
    }

    public AppInfo getJarElement(int i) {
        return this.jarAppHash[i - 1];
    }

    public AppInfo hasElement(String s, String s1) {
        int i = this.appList.size();
        int j = s.length();
        if (j > 25) {
            s = s.substring(0, 25);
        }
        int j2 = s1.length();
        if (j2 > 25) {
            s1 = s1.substring(0, 25);
        }
        for (int k = 0; k < i; k++) {
            AppInfo appinfo = (AppInfo) this.appList.elementAt(k);
            if (s.equals(appinfo.name) && s1.equals(appinfo.vendor)) {
                return appinfo;
            }
        }
        return null;
    }

    public int sizeOfJarElement() {
        int i = this.jarAppHash.length;
        int j = 0;
        for (int k = 0; k < i; k++) {
            if (this.jarAppHash[k] != null) {
                j++;
            }
        }
        return j;
    }

    public void setRunInfo(int i) {
        AppInfo appinfo = (AppInfo) this.appList.elementAt(i);
        appinfo.useTime = System.currentTimeMillis();
        appinfo.useCount++;
        storeAppInfo(appinfo.type, appinfo.id, false);
    }

    private void sortList(AppInfo appinfo) {
        int i = 0;
        int j = this.appList.size();
        while (true) {
            if (i < j) {
                AppInfo appinfo1 = (AppInfo) this.appList.elementAt(i);
                byte b = setSort;
                if (b == 0) {
                    if (appinfo.downTime < appinfo1.downTime) {
                        i++;
                    } else {
                        this.appList.insertElementAt(appinfo, i);
                        break;
                    }
                } else if (b == 1) {
                    if (appinfo.useTime < appinfo1.useTime) {
                        i++;
                    } else {
                        this.appList.insertElementAt(appinfo, i);
                        break;
                    }
                } else if (b != 2) {
                    if (b != 3) {
                        continue;
                    } else if (appinfo.userSet < appinfo1.userSet) {
                        this.appList.insertElementAt(appinfo, i);
                        break;
                    } else if (appinfo.userSet == appinfo1.userSet && appinfo.downTime >= appinfo1.downTime) {
                        this.appList.insertElementAt(appinfo, i);
                        break;
                    }
                    i++;
                } else if (appinfo.useCount < appinfo1.useCount) {
                    i++;
                } else {
                    this.appList.insertElementAt(appinfo, i);
                    break;
                }
            } else {
                break;
            }
        }
        if (i == j) {
            this.appList.addElement(appinfo);
        }
    }

    private void resortList() {
        Vector vector = new Vector(this.appList.size());
        int i = this.appList.size();
        for (int j = 0; j < i; j++) {
            AppInfo appinfo = (AppInfo) this.appList.elementAt(j);
            int k = vector.size();
            int l = 0;
            while (l < k) {
                AppInfo appinfo1 = (AppInfo) vector.elementAt(l);
                if ((setSort != 0 || appinfo.downTime < appinfo1.downTime) && ((setSort != 1 || appinfo.useTime < appinfo1.useTime) && ((setSort != 2 || appinfo.useCount < appinfo1.useCount) && ((setSort != 3 || appinfo.userSet >= appinfo1.userSet) && (setSort != 3 || appinfo.userSet != appinfo1.userSet || appinfo.downTime < appinfo1.downTime))))) {
                    l++;
                } else {
                    vector.insertElementAt(appinfo, l);
                    break;
                }
            }
            if (l == k) {
                vector.addElement(appinfo);
            }
        }
        this.appList = vector;
    }

    public void deleteAll() {
        if (this.appList.size() == 0) {
            return;
        }
        try {
            XFile.unlink(DB_NAME);
        } catch (Exception e) {
        }
        int i = this.jarAppHash.length;
        for (int j = 0; j < i; j++) {
            AppInfo appinfo = this.jarAppHash[j];
            if (appinfo != null) {
                XBrowser.deleteDir(InternalZipConstants.ZIP_FILE_SEPARATOR + appinfo.id);
                this.jarAppHash[j] = null;
            }
        }
        int i2 = this.gvmAppHash.length;
        for (int k = 0; k < i2; k++) {
            AppInfo appinfo1 = this.gvmAppHash[k];
            if (appinfo1 != null) {
                try {
                    Gvm.removeContent(appinfo1.id);
                    this.gvmAppHash[appinfo1.id] = null;
                } catch (Exception e2) {
                }
            }
        }
        this.appList.removeAllElements();
        storeAll();
    }

    public void deleteExp() {
        throw new Error("Unresolved compilation problem: \n\tMIDletMan cannot be resolved\n");
    }

    public void setView(int i) {
        if (setView == i) {
            return;
        }
        byte b = (byte) i;
        setView = b;
        try {
            setInfo(0, b);
        } catch (IOException e) {
            recover();
        }
    }

    public void setSort(int i) {
        if (setSort == i) {
            return;
        }
        byte b = (byte) i;
        setSort = b;
        try {
            setInfo(1, b);
        } catch (IOException e) {
            recover();
        }
        resortList();
    }

    public void setSound(int i) {
        if (setSound == i) {
            return;
        }
        byte b = (byte) i;
        setSound = b;
        try {
            setInfo(2, b);
            AudioSystem.setVolume(XBrowser.JAM_SND_FMT, setSound);
        } catch (IOException e) {
            recover();
        } catch (Exception e2) {
        }
    }

    public void setUserSort(int i, int j) {
        Vector vector = this.appList;
        vector.insertElementAt(vector.elementAt(j), i);
        this.appList.removeElementAt(j + 1);
    }

    public void backupList() {
        AppInfo[] appInfoArr = new AppInfo[this.appList.size()];
        this.backList = appInfoArr;
        this.appList.copyInto(appInfoArr);
    }

    public void saveUserSort(boolean flag) {
        if (flag) {
            Vector vector = new Vector();
            for (int j = 0; j < size(); j++) {
                AppInfo appinfo = (AppInfo) this.appList.elementAt(j);
                appinfo.userSet = j;
                vector.addElement(appinfo);
            }
            this.appList = vector;
            setSort = (byte) 3;
            storeAll();
        } else {
            this.appList.removeAllElements();
            int i = 0;
            while (true) {
                AppInfo[] appInfoArr = this.backList;
                if (i >= appInfoArr.length) {
                    break;
                }
                this.appList.addElement(appInfoArr[i]);
                i++;
            }
        }
        this.backList = null;
    }

    public void setInfo(int i, byte byte0) throws IOException {
        XFile xfile = null;
        try {
            try {
                xfile = new XFile(DB_NAME, 3);
                xfile.seek(i + 10, 0);
                byte[] abyte0 = {byte0};
                xfile.write(abyte0, 0, 1);
                xfile.close();
            } catch (IOException ioexception) {
                throw ioexception;
            }
        } catch (Throwable th) {
            if (xfile != null) {
                xfile.close();
            }
            throw th;
        }
    }

    static long readLong(byte[] abyte0, int i) {
        long l = 0;
        for (int j = 0; j < 8; j++) {
            l = (l << 8) | ((long) (abyte0[i + j] & 255));
        }
        return l;
    }

    static int readInt(byte[] abyte0, int i) {
        int j = 0;
        for (int k = 0; k < 4; k++) {
            j = (j << 8) | (abyte0[i + k] & 255);
        }
        return j;
    }

    static void writeLong(byte[] abyte0, int i, long l) {
        for (int j = 7; j >= 0; j--) {
            abyte0[i + j] = (byte) (255 & l);
            l >>= 8;
        }
    }

    static void writeInt(byte[] abyte0, int i, int j) {
        for (int k = 3; k >= 0; k--) {
            abyte0[i + k] = (byte) (j & 255);
            j >>= 8;
        }
    }

    static {
        try {
            setSound = (byte) (AudioSystem.getMaxVolume(XBrowser.JAM_SND_FMT) / 2);
        } catch (Exception e) {
        }
    }
}
