package com.xce.jam;

import com.xce.io.XFile;
import com.xce.lcdui.XDisplay;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/* JADX INFO: loaded from: classes.dex */
public class MenuForm extends Canvas {
    private static final int CMD_BACK = 1;
    private static final int CMD_BROWSER = 0;
    private static final int CMD_DELALL = 4;
    private static final int CMD_DELAPP = 3;
    private static final int CMD_DELEXP = 5;
    private static final int CMD_RESET_VOLUME = 6;
    private static final int CMD_SELECT = 2;
    private static final int CMD_SET_VOLUME = 7;
    private static final int CMD_SORT_CANCEL = 8;
    private static final int CMD_SORT_OK = 9;
    private static final int CMD_SORT_TOP = 10;
    private static final int MENU_APP = 0;
    private static final int MENU_APP_DELETE = 9;
    private static final int MENU_APP_INFO = 7;
    private static final int MENU_APP_SORT = 8;
    private static final int MENU_APP_UPGRADE = 10;
    private static final int MENU_DELETE = 5;
    private static final int MENU_DELETE_ALL = 17;
    private static final int MENU_DELETE_EXP = 18;
    private static final int MENU_GVM_CHAT = 21;
    private static final int MENU_OPTION = 1;
    private static final int MENU_SELECT_VIEW = 2;
    private static final int MENU_SET_VOLUME = 3;
    private static final int MENU_SORT_ALL = 4;
    private static final int MENU_SORT_COUNT = 15;
    private static final int MENU_SORT_DOWN = 13;
    private static final int MENU_SORT_TIME = 14;
    private static final int MENU_SORT_TOP = 19;
    private static final int MENU_SORT_UPDOWN = 20;
    private static final int MENU_SORT_USER = 16;
    private static final int MENU_SYSTEM_INFO = 6;
    private static final int MENU_VIEW_ICON = 12;
    private static final int MENU_VIEW_LIST = 11;
    static MenuForm form;
    private AlertView alertView;
    private int appIndex;
    private int backMenu;
    private int currMenu;
    private ItemView currView;
    private int menuCount;
    private int[] menuList = new int[5];
    private AlertView menuView;
    private ListView sortView;
    private SoundView soundView;

    public static MenuForm getForm() {
        if (form == null) {
            form = new MenuForm();
        }
        return form;
    }

    public static void resetForm() {
        form = null;
    }

    public void init() {
        init(-1);
    }

    public void init(int i) {
        this.appIndex = i;
        this.backMenu = -1;
        if (i >= 0) {
            setMenuView(0);
        } else {
            setMenuView(1);
        }
        XBrowser.setCurrent(this);
    }

    @Override // javax.microedition.lcdui.Canvas
    public void paint(Graphics g) {
        this.currView.paint(g);
    }

    public void repaintAll() {
        repaint(0, 0, XDisplay.width, XDisplay.height2);
        serviceRepaints();
    }

    @Override // javax.microedition.lcdui.Canvas
    public void keyPressed(int i) {
        int j;
        if (i == 129) {
            processCommand(this.currView.getLeftCommand());
        }
        if (i == 131 || i == 148) {
            processCommand(this.currView.getRightCommand());
            return;
        }
        if (i == 141) {
            if (this.currView == this.sortView) {
                sortUp();
            }
            this.currView.keyPressed(i);
            return;
        }
        if (i == 142 || i == 145) {
            this.currView.keyPressed(i);
            return;
        }
        if (i == 146) {
            if (this.currView == this.sortView) {
                sortDown();
            }
            this.currView.keyPressed(i);
        } else {
            switch (i) {
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57:
                    if (this.currView == this.menuView && (i - 48) - 1 < this.menuCount) {
                        menuRun(this.menuList[j]);
                    }
                    break;
            }
        }
    }

    @Override // javax.microedition.lcdui.Canvas
    public void keyRepeated(int i) {
    }

    @Override // javax.microedition.lcdui.Canvas
    public void keyReleased(int i) {
    }

    private synchronized void processCommand(int i) {
        if (i == -1) {
            return;
        }
        switch (i) {
            case 0:
                setBrowserView(false);
                break;
            case 1:
                int i2 = this.backMenu;
                if (i2 >= 0) {
                    menuRun(i2);
                }
                break;
            case 2:
                menuRun(this.menuList[this.menuView.getSelectedIndex()]);
                break;
            case 3:
                JAMGraphic.popupView(XBrowser.JAM_RES.MSG_DELING);
                deleteApp(this.appIndex);
                setBrowserView(true);
                break;
            case 4:
                JAMGraphic.popupView(XBrowser.JAM_RES.MSG_DELING);
                deleteAll();
                setBrowserView(true);
                break;
            case 5:
                JAMGraphic.popupView(XBrowser.JAM_RES.MSG_DELING);
                deleteExpAll();
                setBrowserView(true);
                break;
            case 6:
                this.soundView.reset();
                setBrowserView(false);
                break;
            case 7:
                setSound();
                setBrowserView(false);
                break;
            case 8:
                JAMGraphic.popupView(XBrowser.JAM_RES.MSG_SAVING);
                sortSave(false);
                setBrowserView(true);
                break;
            case 9:
                JAMGraphic.popupView(XBrowser.JAM_RES.MSG_SAVING);
                sortSave(true);
                setBrowserView(true);
                break;
            case 10:
                sortTop();
                setBrowserView(true);
                break;
            default:
        }
    }

    private synchronized void menuRun(int i) {
        this.currMenu = i;
        this.backMenu = -1;
        if (i != 3) {
            if (i != 6) {
                if (i == 7) {
                    setAlertView(getMenuName(i), getAppInfo(this.appIndex), -1, 0);
                } else {
                    switch (i) {
                        case 9:
                            if (isROMApp(this.appIndex)) {
                                setAlertView(getMenuName(this.currMenu), XBrowser.JAM_RES.MAIN_NOT_DEL, -1, 0);
                            } else {
                                setAlertView(getMenuName(this.currMenu), XBrowser.JAM_RES.DELETE_DELETE, 0, 3);
                            }
                            break;
                        case 10:
                            String s = getUpURL(this.appIndex);
                            if (s == null) {
                                setAlertView(getMenuName(this.currMenu), XBrowser.JAM_RES.MSG_CONN, -1, 0);
                            } else {
                                setDownView(s);
                            }
                            break;
                        case 11:
                            setDefaultView(1);
                            setBrowserView(true);
                            break;
                        case 12:
                            setDefaultView(0);
                            setBrowserView(true);
                            break;
                        case 13:
                            setDefaultSort(0);
                            setBrowserView(true);
                            break;
                        case 14:
                            setDefaultSort(1);
                            setBrowserView(true);
                            break;
                        case 15:
                            setDefaultSort(2);
                            setBrowserView(true);
                            break;
                        case 16:
                            setDefaultSort(3);
                            setAlertView(getMenuName(this.currMenu), XBrowser.JAM_RES.SORT_USER, -1, 0);
                            break;
                        case 17:
                            if (getAppCount() <= 0) {
                                setAlertView(getMenuName(this.currMenu), XBrowser.JAM_RES.MAIN_NOT_ALLDEL, -1, 0);
                            } else {
                                setAlertView(getMenuName(this.currMenu), XBrowser.JAM_RES.DELETE_DELETE, 0, 4);
                            }
                            break;
                        case 18:
                            int j = getExpiredCount();
                            if (j <= 0) {
                                setAlertView(getMenuName(this.currMenu), XBrowser.JAM_RES.MAIN_NOT_ALLDEL, -1, 0);
                            } else {
                                setAlertView(getMenuName(this.currMenu), String.valueOf(j) + XBrowser.JAM_RES.DELETE_EXPIRED, 0, 5);
                            }
                            break;
                        case 19:
                            setAlertView(getMenuName(i), XBrowser.JAM_RES.SORT_TOP, 0, 10);
                            break;
                        case 20:
                            setSortView();
                            break;
                        case 21:
                            GVMChatForm.getForm().init();
                            break;
                        default:
                            setMenuView(i);
                            repaintAll();
                            break;
                    }
                }
            } else {
                setAlertView(getMenuName(i), getSysInfo(), -1, 0);
            }
        } else {
            setSoundView();
        }
    }

    private void setCurrent(ItemView itemview) {
        ItemView itemView = this.currView;
        if (itemView != null) {
            itemView.hide();
        }
        this.currView = itemview;
        itemview.show();
    }

    private void setMenuView(int i) {
        if (this.menuView == null) {
            this.menuView = new AlertView();
        }
        this.menuView.init(this);
        setMenu(i);
        int i2 = this.appIndex;
        if (i2 >= 0) {
            int i3 = this.currMenu;
            if (i3 == 0) {
                this.menuView.setData(2, i2, (String) null, getMenuNameList());
            } else {
                this.menuView.setData(2, i2, getMenuName(i3), getMenuNameList());
            }
        } else {
            int i4 = this.currMenu;
            if (i4 == 1) {
                this.menuView.setData(3, -1, (String) null, getMenuNameList());
            } else {
                this.menuView.setData(3, -1, getMenuName(i4), getMenuNameList());
            }
        }
        XBrowser.playSound(10);
        setCurrent(this.menuView);
    }

    private void setAlertView(String s, String s1, int i, int j) {
        if (this.alertView == null) {
            AlertView alertView = new AlertView();
            this.alertView = alertView;
            alertView.init(this);
        }
        int i2 = this.appIndex;
        if (i2 >= 0) {
            this.alertView.setData(2, i2, s, s1);
        } else {
            this.alertView.setData(3, i2, s, s1);
        }
        this.alertView.setCommand(i, j);
        XBrowser.playSound(10);
        setCurrent(this.alertView);
    }

    private void setSoundView() {
        if (this.soundView == null) {
            SoundView soundView = new SoundView();
            this.soundView = soundView;
            soundView.init(this);
        }
        this.soundView.setCommand(6, 7);
        XBrowser.playSound(10);
        setCurrent(this.soundView);
    }

    private void setSortView() {
        ListView listView = this.sortView;
        if (listView == null) {
            ListView listView2 = new ListView();
            this.sortView = listView2;
            listView2.init(this);
            this.sortView.setCommand(8, 9);
            this.sortView.setType(4);
            this.sortView.setData(getAppList());
            this.sortView.setIndex(this.appIndex);
            sortBackup();
            XBrowser.playSound(10);
        } else {
            listView.setData(getAppList());
        }
        setCurrent(this.sortView);
    }

    private void setBrowserView(boolean flag) {
        ItemView itemView = this.currView;
        if (itemView != null) {
            itemView.hide();
        }
        resetForm();
        XBrowser.playSound(10);
        BrowserForm.getForm().init(flag);
    }

    private void setDownView(String s) {
        ItemView itemView = this.currView;
        if (itemView != null) {
            itemView.hide();
        }
        resetForm();
        XBrowser.download(s);
    }

    private void setMenu(int i) {
        this.currMenu = i;
        if (i == 0) {
            this.menuCount = 3;
            int[] iArr = this.menuList;
            iArr[0] = 7;
            iArr[1] = 9;
            iArr[2] = 8;
            if (isJARApp(this.appIndex)) {
                this.menuList[3] = 10;
                this.menuCount++;
            }
            this.backMenu = -1;
            this.menuView.setCommand(0, 2);
            return;
        }
        if (i == 1) {
            this.menuCount = 5;
            int[] iArr2 = this.menuList;
            iArr2[0] = 2;
            iArr2[1] = 3;
            iArr2[2] = 4;
            iArr2[3] = 5;
            iArr2[4] = 6;
            if (XBrowser.IS_GVM_CHAT) {
                this.menuList[5] = 21;
                this.menuCount++;
            }
            this.backMenu = -1;
            this.menuView.setCommand(0, 2);
            return;
        }
        if (i == 2) {
            this.menuCount = 2;
            int[] iArr3 = this.menuList;
            iArr3[0] = 11;
            iArr3[1] = 12;
            this.backMenu = 1;
            this.menuView.setCommand(1, 2);
            return;
        }
        if (i == 4) {
            this.menuCount = 4;
            int[] iArr4 = this.menuList;
            iArr4[0] = 13;
            iArr4[1] = 14;
            iArr4[2] = 15;
            iArr4[3] = 16;
            this.backMenu = 1;
            this.menuView.setCommand(1, 2);
            return;
        }
        if (i == 5) {
            this.menuCount = 2;
            int[] iArr5 = this.menuList;
            iArr5[0] = 17;
            iArr5[1] = 18;
            this.backMenu = 1;
            this.menuView.setCommand(1, 2);
            return;
        }
        if (i == 8) {
            this.menuCount = 2;
            int[] iArr6 = this.menuList;
            iArr6[0] = 19;
            iArr6[1] = 20;
            this.backMenu = 0;
            this.menuView.setCommand(1, 2);
        }
    }

    private String[] getMenuNameList() {
        int i = this.menuCount;
        if (i <= 0) {
            return null;
        }
        String[] as = new String[i];
        for (int i2 = 0; i2 < this.menuCount; i2++) {
            as[i2] = getMenuName(this.menuList[i2]);
        }
        return as;
    }

    private String getMenuName(int i) {
        switch (i) {
            case 0:
                return XBrowser.JAM_RES.MENU_APP;
            case 1:
                return XBrowser.JAM_RES.MENU_OPTION;
            case 2:
                return XBrowser.JAM_RES.MENU_SELECT_VIEW;
            case 3:
                return XBrowser.JAM_RES.MENU_SET_VOLUME;
            case 4:
                return XBrowser.JAM_RES.MENU_SORT_ALL;
            case 5:
            case 9:
                return XBrowser.JAM_RES.MENU_DELETE;
            case 6:
                return XBrowser.JAM_RES.MENU_SYSTEM_INFO;
            case 7:
                return XBrowser.JAM_RES.MENU_APP_INFO;
            case 8:
                return XBrowser.JAM_RES.MENU_APP_SORT;
            case 10:
                return XBrowser.JAM_RES.MENU_APP_UPGRADE;
            case 11:
                return XBrowser.JAM_RES.MENU_VIEW_LIST;
            case 12:
                return XBrowser.JAM_RES.MENU_VIEW_ICON;
            case 13:
                return XBrowser.JAM_RES.MENU_SORT_DOWN;
            case 14:
                return XBrowser.JAM_RES.MENU_SORT_TIME;
            case 15:
                return XBrowser.JAM_RES.MENU_SORT_COUNT;
            case 16:
                return XBrowser.JAM_RES.MENU_SORT_USER;
            case 17:
                return XBrowser.JAM_RES.MENU_DELETE_ALL;
            case 18:
                return XBrowser.JAM_RES.MENU_DELETE_EXP;
            case 19:
                return XBrowser.JAM_RES.MENU_SORT_TOP;
            case 20:
                return XBrowser.JAM_RES.MENU_SORT_UPDOWN;
            case 21:
                return XBrowser.JAM_RES.MENU_GVM_CHAT;
            default:
                return null;
        }
    }

    private void sortUp() {
        int i = this.sortView.getSelectedIndex();
        if (i > 0) {
            XBrowser.appdb.setUserSort(i - 1, i);
            this.sortView.setData(getAppList());
        }
    }

    private void sortDown() {
        int i = this.sortView.getSelectedIndex();
        if (i < getAppCount() - 1) {
            XBrowser.appdb.setUserSort(i, i + 1);
            this.sortView.setData(getAppList());
        }
    }

    private void sortSave(boolean flag) {
        XBrowser.appdb.saveUserSort(flag);
    }

    private void sortBackup() {
        XBrowser.appdb.backupList();
    }

    private void sortTop() {
        XBrowser.appdb.setUserSort(0, this.appIndex);
        sortSave(true);
    }

    private void setSound() {
        XBrowser.appdb.setSound(this.soundView.getSound());
    }

    private void deleteApp(int i) {
        XBrowser.appdb.removeElementAt(i);
    }

    private void deleteAll() {
        XBrowser.appdb.deleteAll();
    }

    private void deleteExpAll() {
        XBrowser.appdb.deleteExp();
    }

    private boolean isExpired(int i) {
        throw new Error("Unresolved compilation problem: \n\tMIDletMan cannot be resolved\n");
    }

    private boolean isJARApp(int i) {
        AppInfo appinfo = XBrowser.appdb.elementAt(i);
        return appinfo.type == 1;
    }

    private boolean isROMApp(int i) {
        AppInfo appinfo = XBrowser.appdb.elementAt(i);
        return appinfo.type == 0;
    }

    private void setDefaultView(int i) {
        XBrowser.appdb.setView(i);
        BrowserForm.getForm().setViewMode(i);
    }

    private void setDefaultSort(int i) {
        XBrowser.appdb.setSort(i);
    }

    private String getAppName(int i) {
        return XBrowser.appdb.elementAt(i).name.trim();
    }

    private int getAppCount() {
        return XBrowser.appdb.size();
    }

    private String getUpURL(int i) {
        AppInfo appinfo = XBrowser.appdb.elementAt(i);
        appinfo.loadFull();
        return appinfo.updateUrl;
    }

    private int getExpiredCount() {
        if (getAppCount() <= 0) {
            return -1;
        }
        int i = 0;
        for (int j = 0; j < getAppCount(); j++) {
            if (isExpired(j)) {
                i++;
            }
        }
        return i;
    }

    public String getAppInfo(int i) {
        AppInfo appinfo = XBrowser.appdb.elementAt(i);
        StringBuffer stringbuffer = new StringBuffer();
        if (appinfo != null) {
            if (appinfo.type == 1) {
                if (appinfo.size == 0) {
                    StringBuffer stringBuffer = new StringBuffer(3);
                    stringBuffer.append('/');
                    stringBuffer.append(appinfo.id);
                    appinfo.size = JAMUtil.dirsize(stringBuffer.toString());
                }
                stringbuffer.append(XBrowser.JAM_RES.INFO_VENDOR);
                stringbuffer.append(String.valueOf(appinfo.vendor) + "\n");
                stringbuffer.append(XBrowser.JAM_RES.INFO_SIZE);
                stringbuffer.append(String.valueOf((appinfo.size + 1023) / 1024) + "KB\n");
                stringbuffer.append(XBrowser.JAM_RES.INFO_EXPIRED);
                if (appinfo.xdate == 0) {
                    stringbuffer.append(XBrowser.JAM_RES.INFO_UNLIMITED);
                } else {
                    String s = String.valueOf(appinfo.xdate);
                    stringbuffer.append(String.valueOf(s.substring(0, 4)) + XBrowser.JAM_RES.INFO_YEAR + "\n  " + s.substring(4, 6) + XBrowser.JAM_RES.INFO_MONTH + s.substring(6, 8) + XBrowser.JAM_RES.INFO_DAY + s.substring(8, 10) + ":" + s.substring(10, 12));
                }
            } else if (appinfo.type == 2) {
                stringbuffer.append(XBrowser.JAM_RES.INFO_CATEGORY);
                String s1 = Gvm.getDirName(appinfo.id);
                if (s1 == null) {
                    s1 = XBrowser.JAM_RES.INFO_NONE;
                }
                stringbuffer.append(s1);
            } else {
                stringbuffer.append(XBrowser.JAM_RES.MAIN_NOT_PROP);
            }
        }
        return stringbuffer.toString();
    }

    private String getSysInfo() {
        String s;
        String s2;
        String s3;
        if (XBrowser.JAM_DISP_EFS) {
            int i = XFile.fsavail() / 1024;
            s = String.valueOf(XBrowser.JAM_RES.SYS_MEMORY) + "\n  " + i + "KB\n" + XBrowser.JAM_RES.SYS_COUNT + getAppCount() + "\n";
        } else {
            s = String.valueOf(XBrowser.JAM_RES.SYS_COUNT) + getAppCount() + "\n";
        }
        if (AppDB.setView == 0) {
            s2 = String.valueOf(s) + XBrowser.JAM_RES.SYS_ICON + "\n";
        } else {
            s2 = String.valueOf(s) + XBrowser.JAM_RES.SYS_LIST + "\n";
        }
        if (AppDB.setSort == 0) {
            s3 = String.valueOf(s2) + XBrowser.JAM_RES.SYS_SORT_DOWN + "\n";
        } else if (AppDB.setSort == 1) {
            s3 = String.valueOf(s2) + XBrowser.JAM_RES.SYS_SORT_TIME + "\n";
        } else if (AppDB.setSort == 2) {
            s3 = String.valueOf(s2) + XBrowser.JAM_RES.SYS_SORT_COUNT + "\n";
        } else {
            s3 = String.valueOf(s2) + XBrowser.JAM_RES.SYS_SORT_USER + "\n";
        }
        if (AppDB.setSound == 0) {
            return String.valueOf(s3) + XBrowser.JAM_RES.SYS_SOUND + "OFF";
        }
        return String.valueOf(s3) + XBrowser.JAM_RES.SYS_SOUND + "ON";
    }

    private String[] getAppList() {
        int i = XBrowser.appdb.size();
        String[] as = new String[i];
        for (int j = 0; j < i; j++) {
            as[j] = XBrowser.appdb.elementAt(j).name.trim();
        }
        return as;
    }
}
