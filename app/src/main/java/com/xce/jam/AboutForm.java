package com.xce.jam;

import android.support.v4.media.session.PlaybackStateCompat;
import com.skt.m.Graphics2D;
import com.xce.io.XFile;
import com.xce.lcdui.XDisplay;
import com.xce.util.XProperties;
import java.io.IOException;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import net.lingala.zip4j.util.InternalZipConstants;

/* JADX INFO: loaded from: classes.dex */
public class AboutForm extends Canvas implements Runnable {
    private static final int CMD_BROWSER = 8;
    private static final int CMD_CANCEL = 1;
    private static final int CMD_DELETE = 5;
    private static final int CMD_DELETE_CANCEL = 7;
    private static final int CMD_DELETE_OK = 6;
    private static final int CMD_END = 3;
    private static final int CMD_RETRY = 2;
    private static final int CMD_RUN = 9;
    private static final int CMD_STOP = 4;
    public static final int MODE_ANIICON = 3;
    public static final int MODE_ICON = 2;
    public static final int MODE_JAR = 4;
    public static final int MODE_MSD = 1;
    public static final int MODE_START = 0;
    private static final int NEW = 0;
    private static final int NOBILLING = 5;
    private static final int STOP_DOWN = 2;
    private static final int STOP_END = 5;
    private static final int STOP_ERROR = 4;
    private static final int STOP_RUN = 3;
    private static final int STOP_SPACE = 1;
    private static final int UNEXPIRED = 4;
    private static final int UPDATE = 2;
    private static final int UPGRADE = 1;
    private static final int bufferSize = 92160;
    public static byte[] downBuffer;
    static AboutForm form;
    private AlertView alertView;
    private int currValue;
    private ItemView currView;
    private ListView deleteView;
    private String dirName;
    private int downLen;
    private int downMode;
    private DownView downView;
    private boolean downloadStop;
    private Graphics2D g2d;
    private AppInfo info;
    private boolean isError;
    private boolean isSuccess;
    private int jar_tlen;
    private int leftCommand;
    private int maxValue;
    private String message;
    private int mode;
    private int msdSize;
    private long needSize;
    private int nread;
    private XProperties prop;
    private int rightCommand;
    private AppInfo sameInfo;
    private int saveLen;
    private int stopMode;
    private String iconURL = null;
    private String aniIconURL = null;
    private boolean upgradeFlag = false;

    public AboutForm() {
        form = this;
        if (downBuffer == null) {
            downBuffer = new byte[bufferSize];
        }
    }

    public static AboutForm getForm() {
        if (form == null) {
            new AboutForm();
        }
        return form;
    }

    public void init(String s) {
        AppInfo appinfo = new AppInfo();
        appinfo.type = 1;
        appinfo.updateUrl = s;
        init(appinfo);
    }

    public void init(AppInfo appinfo) {
        XBrowser.setNetworkMode(2);
        this.info = appinfo;
        setDownMode(0);
        XBrowser.setCurrent(this);
        XBrowser.playSound(3);
        XBrowser.setThread(this);
    }

    public void init() {
        setDownMode(this.downMode);
        XBrowser.setCurrent(this);
        XBrowser.playSound(3);
        XBrowser.setThread(this);
    }

    public void setUpgrade(boolean flag) {
        this.upgradeFlag = flag;
    }

    public void resetForm() {
        downBuffer = null;
        form = null;
    }

    private void setCurrent(ItemView itemview) {
        ItemView itemView = this.currView;
        if (itemView != null && itemView != itemview) {
            itemView.hide();
        }
        this.currView = itemview;
        itemview.show();
    }

    private void setDownView() {
        if (this.downView == null) {
            DownView downView = new DownView();
            this.downView = downView;
            downView.init(this);
            this.downView.setCommand(4, -1);
        }
        setCurrent(this.downView);
    }

    private void setAlertView(String s, int i, int j) {
        if (this.alertView == null) {
            AlertView alertView = new AlertView();
            this.alertView = alertView;
            alertView.init(this);
        }
        this.alertView.setData(4, -1, (String) null, s);
        this.alertView.setCommand(i, j);
        XBrowser.playSound(9);
        setCurrent(this.alertView);
    }

    private void setDeleteView(boolean flag) {
        if (this.deleteView == null) {
            JAMGraphic.popupView(XBrowser.JAM_RES.MSG_SEARCHING);
            ListView listView = new ListView();
            this.deleteView = listView;
            listView.init(this);
            this.deleteView.setCommand(3, 5);
            this.deleteView.setType(3);
            XBrowser.playSound(9);
        }
        if (flag) {
            this.deleteView.setData(getAppList());
            this.deleteView.setTitle(String.valueOf(XBrowser.JAM_RES.DELETE_LACK) + ":" + (this.needSize / PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID) + "KB");
        }
        setCurrent(this.deleteView);
    }

    private void setDownMode(int i) {
        this.downMode = i;
        if (i != 0) {
            if (i == 1 || i == 2 || i == 3 || i == 4) {
                if (this.currView != this.downView) {
                    setDownView();
                }
                this.downView.setDownStep(i);
            }
        } else {
            setDownView();
        }
        if (i == 2) {
            this.downView.setAppName(this.info.name);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        repaintAll();
        download();
        XBrowser.endThread();
    }

    private void download() {
        throw new Error("Unresolved compilation problem: \n\tMIDletMan cannot be resolved\n");
    }

    public void resetDownload() {
        this.downloadStop = true;
        this.stopMode = 5;
        JAMUtil.netFinalize();
        if (!this.isSuccess) {
            deleteData();
        }
    }

    private void saveInfo() {
        AppDB appdb = AppDB.getDB();
        AppInfo appinfo = appdb.getJarElement(this.info.id);
        if (appinfo != null) {
            AppInfo appinfo1 = this.info;
            this.info = appinfo;
            appinfo.fileName = appinfo1.fileName;
            this.info.prop = appinfo1.prop;
            this.info.jarUrl = appinfo1.jarUrl;
            this.info.updateUrl = appinfo1.updateUrl;
            this.info.icon = appinfo1.icon;
            if (this.mode == 5) {
                this.info.xdate = appinfo1.xdate;
            }
            this.info.downTime = System.currentTimeMillis();
            this.info.useTime = appinfo1.useTime;
            this.info.useCount = appinfo1.useCount;
            this.info.userSet = appinfo1.userSet;
        } else {
            this.info.downTime = System.currentTimeMillis();
            this.info.useTime = System.currentTimeMillis();
            this.info.useCount = 1;
            this.info.userSet = 0;
        }
        appdb.addJarElement(this.info);
        XBrowser.updateUrl = null;
        XBrowser.retUrl = null;
    }

    private boolean loadJar() throws IOException {
        throw new Error("Unresolved compilation problems: \n\tSyntax error, insert \"VariableDeclarators\" to complete LocalVariableDeclaration\n\tlocal cannot be resolved\n\tJVM cannot be resolved to a type\n\tSyntax error on token \"ret\", = expected\n");
    }

    private void saveJar() throws IOException {
        if (this.downLen <= 0) {
            return;
        }
        XFile xfile = null;
        try {
            try {
                if (this.saveLen > 0) {
                    xfile = new XFile(this.info.fileName, this.saveLen != 0 ? 3 : 2);
                    xfile.seek(this.saveLen, 0);
                } else {
                    String str = this.info.fileName;
                    if (this.saveLen != 0) {
                    }
                    xfile = new XFile(str, 2);
                }
                xfile.write(downBuffer, 0, this.downLen);
                this.saveLen += this.downLen;
                try {
                    xfile.close();
                } catch (IOException e) {
                }
            } catch (IOException e2) {
                throw new IOException(XBrowser.JAM_RES.MSG_NOT_WRITE);
            }
        } catch (Throwable th) {
            if (xfile != null) {
                try {
                    xfile.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    private void loadIcon(String s, boolean flag) throws IOException {
        String s1;
        XFile xfile = null;
        if (s == null) {
            return;
        }
        JAMUtil.setConnection(s);
        JAMUtil.setInputStream();
        if (JAMUtil.getResponseCode() != 200) {
            JAMUtil.netFinalize();
            return;
        }
        try {
            int i = JAMUtil.getConLength();
            if (i > 0) {
                byte[] abyte0 = new byte[i];
                JAMUtil.inputStreamRead(abyte0);
                if (flag) {
                    s1 = InternalZipConstants.ZIP_FILE_SEPARATOR + this.info.id + "/ani" + this.info.id + ".lbm";
                } else {
                    s1 = InternalZipConstants.ZIP_FILE_SEPARATOR + this.info.id + InternalZipConstants.ZIP_FILE_SEPARATOR + this.info.id + ".lbm";
                }
                xfile = new XFile(s1, 2);
                xfile.write(abyte0, 0, abyte0.length);
            }
            if (xfile != null) {
                try {
                    xfile.close();
                } catch (IOException e) {
                }
            }
        } catch (Exception e2) {
            if (xfile != null) {
                try {
                    xfile.close();
                } catch (IOException e3) {
                }
            }
        } catch (Throwable th) {
            if (xfile != null) {
                try {
                    xfile.close();
                } catch (IOException e4) {
                }
            }
            JAMUtil.netFinalize();
            throw th;
        }
        JAMUtil.netFinalize();
    }

    public void loadMsd() throws IOException {
        JAMUtil.setConnection(this.info.updateUrl);
        JAMUtil.setInputStream();
        if (JAMUtil.getResponseCode() != 200) {
            throw new IOException("Msd File" + XBrowser.JAM_RES.MSG_NOT_READ);
        }
        AppInfo appInfo = this.info;
        XProperties xProperties = new XProperties();
        appInfo.prop = xProperties;
        this.prop = xProperties;
        try {
            try {
                this.msdSize = JAMUtil.getConLength();
                this.prop.load(JAMUtil.getInputStream(), this.msdSize, true);
            } catch (Exception e) {
                this.downloadStop = true;
                this.stopMode = 4;
                throw new IOException("MSD " + XBrowser.JAM_RES.MSG_NOT_DATA);
            }
        } finally {
            JAMUtil.netFinalize();
        }
    }

    private boolean checkSpace() throws IOException {
        int i = this.mode;
        if (i == 0) {
            long free = JAMUtil.getFree(this.info.size, this.msdSize, -1);
            this.needSize = free;
            if (free > 0) {
                this.downloadStop = true;
                this.stopMode = 1;
                return false;
            }
        } else if (i == 1) {
            this.info.id = this.sameInfo.id;
            this.info.fileName = this.sameInfo.fileName;
            this.message = XBrowser.JAM_RES.ABOUT_UPGRADE;
            long free2 = JAMUtil.getFree(this.info.size, this.msdSize, this.sameInfo.id);
            this.needSize = free2;
            if (free2 > 0) {
                this.downloadStop = true;
                this.stopMode = 1;
                return false;
            }
        } else if (i == 2) {
            this.info.id = this.sameInfo.id;
            this.info.fileName = this.sameInfo.fileName;
            this.message = XBrowser.JAM_RES.ABOUT_UPDATE;
            long free3 = JAMUtil.getFree(this.info.size, this.msdSize, this.sameInfo.id);
            this.needSize = free3;
            if (free3 > 0) {
                this.downloadStop = true;
                this.stopMode = 1;
                return false;
            }
        } else {
            if (i == 4) {
                this.downloadStop = true;
                this.stopMode = 3;
                return false;
            }
            if (i == 5) {
                this.info.id = this.sameInfo.id;
                this.info.fileName = this.sameInfo.fileName;
                this.message = XBrowser.JAM_RES.ABOUT_UPDATE;
                long free4 = JAMUtil.getFree(this.info.size, this.msdSize, this.sameInfo.id);
                this.needSize = free4;
                if (free4 > 0) {
                    this.downloadStop = true;
                    this.stopMode = 1;
                    return false;
                }
            }
        }
        return true;
    }

    private void checkUpgrade() {
        int i = this.mode;
        if (i == 1) {
            this.message = XBrowser.JAM_RES.ABOUT_UPGRADE;
            this.downloadStop = true;
            this.stopMode = 2;
        } else if (i == 2) {
            this.message = XBrowser.JAM_RES.ABOUT_UPDATE;
            this.downloadStop = true;
            this.stopMode = 2;
        }
    }

    private void checkInfo() throws IOException {
        throw new Error("Unresolved compilation problems: \n\tMIDletMan cannot be resolved\n\tMIDletMan cannot be resolved\n\tMIDletInfo cannot be resolved to a type\n\tMIDletInfo cannot be resolved\n");
    }

    @Override // javax.microedition.lcdui.Canvas
    public void paint(Graphics g) {
        ItemView itemView = this.currView;
        if (itemView != null) {
            itemView.paint(g);
        }
    }

    private void repaintAll() {
        repaint(0, 0, XDisplay.width, XDisplay.height2);
        serviceRepaints();
    }

    @Override // javax.microedition.lcdui.Canvas
    public void keyPressed(int i) {
        if (i == 129) {
            processCommand(this.currView.getLeftCommand());
            return;
        }
        if (i != 131) {
            if (i == 141 || i == 146) {
                this.currView.keyPressed(i);
                return;
            } else if (i != 148) {
                return;
            }
        }
        processCommand(this.currView.getRightCommand());
    }

    private void processCommand(int i) {
        if (i == -1) {
        }
        switch (i) {
            case 2:
                if (this.isError) {
                    deleteData();
                    returnWAP();
                } else {
                    init();
                }
                break;
            case 3:
                deleteData();
                returnWAP();
                break;
            case 4:
                if (this.currValue < 130) {
                    this.message = XBrowser.JAM_RES.DOWNLOAD_RETRY;
                    this.downloadStop = true;
                    this.stopMode = 2;
                }
                break;
            case 5:
                setAlertView(XBrowser.JAM_RES.DELETE_DELETE, 7, 6);
                break;
            case 6:
                deleteApp();
                break;
            case 7:
                setDeleteView(false);
                break;
            case 8:
                setBrowserView();
                break;
            case 9:
                runApp();
                break;
        }
    }

    private void setBrowserView() {
        XBrowser.appdb.init();
        resetForm();
        BrowserForm.getForm().init(true);
    }

    private void notEnoughEFS() throws IOException {
        JAMGraphic.popupView(XBrowser.JAM_RES.MSG_SEARCHING);
        this.downloadStop = true;
        if (XBrowser.appdb.size() <= 0 || (!XBrowser.JAM_DISP_EFS && XBrowser.appdb.sizeOfJarElement() <= 0)) {
            throw new IOException(XBrowser.JAM_RES.ABOUT_JAR_SIZE);
        }
        setDeleteView(true);
    }

    private void makeDirectory() throws IOException {
        AppDB appdb = AppDB.getDB();
        if (this.info.id == 0) {
            this.info.id = appdb.getNextJarID();
            if (this.info.id <= 0) {
                throw new IOException("exceed application limit");
            }
            String str = InternalZipConstants.ZIP_FILE_SEPARATOR + this.info.id;
            this.dirName = str;
            try {
                XFile.mkdir(str);
                return;
            } catch (IOException e) {
                return;
            }
        }
        this.dirName = InternalZipConstants.ZIP_FILE_SEPARATOR + this.info.id;
    }

    static void returnWAP() {
        throw new Error("Unresolved compilation problem: \n\tMIDletMan cannot be resolved\n");
    }

    private void runApp() {
        throw new Error("Unresolved compilation problem: \n\tMIDletMan cannot be resolved\n");
    }

    private void deleteData() {
        XBrowser.deleteDir(InternalZipConstants.ZIP_FILE_SEPARATOR + this.info.id);
    }

    private String[] getAppList() {
        if (!XBrowser.JAM_DISP_EFS) {
            XBrowser.appdb.init(false);
        }
        int i = XBrowser.appdb.size();
        String[] as = new String[i];
        for (int j = 0; j < i; j++) {
            AppInfo appinfo = XBrowser.appdb.elementAt(j);
            int k = 0;
            if (appinfo.type == 1) {
                k = JAMUtil.dirsize(InternalZipConstants.ZIP_FILE_SEPARATOR + appinfo.id);
            } else if (appinfo.type == 2) {
                k = Gvm.getContentSize(appinfo.id);
            }
            as[j] = "[" + ((k + 1023) / 1024) + "KB]" + appinfo.name.trim();
        }
        return as;
    }

    private void deleteApp() {
        XBrowser.appdb.removeElementAt(this.deleteView.getSelectedIndex());
        try {
            if (checkSpace()) {
                init();
            } else {
                notEnoughEFS();
            }
        } catch (IOException ioexception) {
            this.isError = true;
            deleteData();
            String message = ioexception.getMessage();
            this.message = message;
            setAlertView(message, -1, 3);
        }
    }

    void setValue(int i) {
        int i2 = this.maxValue;
        if (i > i2) {
            this.currValue = i2;
        } else {
            this.currValue = i;
        }
        this.downView.setValue(this.currValue);
    }

    void setMaxValue(int i) {
        this.maxValue = i;
        this.currValue = 0;
        this.downView.setMaxValue(i);
    }
}
