package com.xce.jam;

import com.xce.io.XFile;
import com.xce.util.Debug;
import java.io.IOException;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import net.lingala.zip4j.util.InternalZipConstants;

/* JADX INFO: loaded from: classes.dex */
public class XBrowser extends MIDlet implements Runnable {
    public static boolean IS_GVM_CHAT = false;
    public static boolean JAM_DISP_EFS = false;
    public static final int JAM_SND_CURSOR = 5;
    public static final int JAM_SND_DOWN_END = 4;
    public static final int JAM_SND_DOWN_START = 3;
    public static final int JAM_SND_END = 2;
    public static final int JAM_SND_MENU = 10;
    public static final int JAM_SND_MODE = 7;
    public static final int JAM_SND_NOTICE = 9;
    public static final int JAM_SND_PAGE = 6;
    public static final int JAM_SND_START = 1;
    public static final int JAM_SND_WARN = 8;
    public static final int MODE_95A = 1;
    public static final int MODE_95C = 2;
    public static AppDB appdb;
    static XBrowser browser;
    private static Runnable currThread;
    static Display display;
    public static boolean download;
    public static boolean isRunning;
    private static Runnable nextThread;
    public static String retUrl;
    public static String updateUrl;
    private static XThread xthread;
    public static JAMRes JAM_RES = JAMUtil.getRes();
    public static String JAM_SND_FMT = System.getProperty("m.JAM_SND_FMT");

    static native int getMaxOpeningID();

    static native void paintOpening(int i);

    public static native void playSound(int i);

    private static native void removeDir(byte[] bArr) throws IOException;

    public static native boolean removeMenuLocation(String str);

    public static native boolean setMenuLocation(String str, String str2);

    public static native void setNetworkMode(int i);

    class XThread extends Thread {
        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            while (XBrowser.isRunning) {
                synchronized (this) {
                    while (XBrowser.currThread == null) {
                        try {
                            wait();
                        } catch (Exception e) {
                        }
                    }
                }
                XBrowser.currThread.run();
                if (XBrowser.nextThread != null) {
                    XBrowser.currThread = XBrowser.nextThread;
                    XBrowser.nextThread = null;
                }
            }
        }

        public synchronized void wakeup() {
            notifyAll();
        }

        public XThread() {
            XBrowser.currThread = null;
            XBrowser.nextThread = null;
        }
    }

    public XBrowser() {
        browser = this;
        if (updateUrl != null) {
            AboutForm.getForm();
        }
        JAMGraphic.init();
        appdb = AppDB.getDB();
    }

    @Override // javax.microedition.midlet.MIDlet
    public void startApp() {
        throw new Error("Unresolved compilation problems: \n\tSocket cannot be resolved\n\tXEventHandler cannot be resolved\n");
    }

    @Override // javax.microedition.midlet.MIDlet
    public void destroyApp(boolean z) {
        throw new Error("Unresolved compilation problems: \n\tMIDletMan cannot be resolved\n\tSocket cannot be resolved\n\tSocket cannot be resolved\n");
    }

    @Override // javax.microedition.midlet.MIDlet
    public void pauseApp() {
    }

    public static void download(String s) {
        if (s != null) {
            updateUrl = s;
            retUrl = null;
            AboutForm.getForm();
            download = true;
            AboutForm.getForm().setUpgrade(true);
            AboutForm.getForm().init(updateUrl);
        }
    }

    static void setCurrent(Displayable displayable) {
        display.setCurrent(displayable);
    }

    static void deleteDir(String s) {
        if (!s.endsWith(InternalZipConstants.ZIP_FILE_SEPARATOR)) {
            s = String.valueOf(s) + InternalZipConstants.ZIP_FILE_SEPARATOR;
        }
        try {
            if (XFile.exists(s)) {
                removeDir(s.getBytes());
            }
        } catch (IOException e) {
            Debug.debugOut("fail removeDir");
        }
    }

    void initOpening() {
        playSound(1);
        try {
            setThread(this);
        } catch (Exception e) {
            finalizeOpening();
        }
    }

    void finalizeOpening() {
        throw new Error("Unresolved compilation problem: \n\tXEventHandler cannot be resolved\n");
    }

    @Override // java.lang.Runnable
    public void run() {
        try {
            int i = getMaxOpeningID();
            for (int j = 0; j <= i; j++) {
                paintOpening(j);
            }
        } catch (Exception e) {
        } catch (Throwable th) {
            finalizeOpening();
            endThread();
            throw th;
        }
        finalizeOpening();
        endThread();
    }

    public static void setThread(Runnable runnable) {
        Runnable runnable2 = currThread;
        if (runnable2 != null) {
            if (runnable2 != runnable) {
                nextThread = runnable;
            }
        } else {
            currThread = runnable;
            xthread.wakeup();
        }
    }

    public static void endThread() {
        currThread = null;
    }

    public static void setRun(boolean flag) {
        isRunning = flag;
        endThread();
    }

    static {
        JAM_DISP_EFS = false;
        IS_GVM_CHAT = false;
        String s = System.getProperty("m.JAM_DISP_EFS");
        if (s != null && s.compareTo("1") == 0) {
            JAM_DISP_EFS = true;
        } else {
            JAM_DISP_EFS = false;
        }
        String s1 = System.getProperty("m.GVM_VER");
        if (s1 != null && s1.charAt(0) == '1') {
            IS_GVM_CHAT = true;
        }
    }
}
