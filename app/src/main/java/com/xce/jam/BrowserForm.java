package com.xce.jam;

import com.xce.lcdui.XDisplay;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/* JADX INFO: loaded from: classes.dex */
public class BrowserForm extends Canvas {
    private static final int CMD_BROWSER = 2;
    private static final int CMD_DOWN = 3;
    private static final int CMD_MENU = 1;
    private static final int CMD_RUN = 0;
    static BrowserForm form;
    private AlertView alertView;
    private ItemView currView;
    private IconView iconView;
    private ItemView lastView;
    private ListView listView;
    private int viewMode = getDefaultView();

    public static BrowserForm getForm() {
        if (form == null) {
            form = new BrowserForm();
        }
        return form;
    }

    public void init() {
        init(true);
    }

    public void init(boolean flag) {
        setBrowserView(flag);
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
        if (i == 35) {
            ItemView itemView = this.currView;
            if (itemView == this.listView || itemView == this.iconView) {
                changeMode();
            }
            return;
        }
        if (i == 42) {
            ItemView itemView2 = this.currView;
            if (itemView2 == this.listView || itemView2 == this.iconView) {
                setControlView();
                return;
            }
            return;
        }
        if (i == 129) {
            processCommand(this.currView.getLeftCommand());
            return;
        }
        if (i == 131 || i == 148) {
            processCommand(this.currView.getRightCommand());
            return;
        }
        if (i == 141 || i == 142 || i == 145 || i == 146) {
            this.currView.keyPressed(i);
            return;
        }
        switch (i) {
            case 48:
                ItemView itemView3 = this.currView;
                if (itemView3 == this.listView || itemView3 == this.iconView) {
                    URLSelectForm.getForm().init();
                }
                break;
            case 49:
            case 50:
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
                if (this.currView == this.listView) {
                    appRun((i - 48) - 1);
                }
                break;
        }
    }

    @Override // javax.microedition.lcdui.Canvas
    public void keyRepeated(int i) {
        if (i == 141 || i == 142 || i == 145 || i == 146) {
            this.currView.keyRepeated(i);
        }
    }

    @Override // javax.microedition.lcdui.Canvas
    public void keyReleased(int i) {
    }

    private synchronized void processCommand(int i) {
        if (i == -1) {
            return;
        }
        if (i == 0) {
            appRun(getAppIndex());
        } else if (i == 1) {
            setMenuView();
        } else {
            if (i != 2) {
                if (i == 3) {
                    appUpdate(getAppIndex());
                }
            }
            setBrowserView(false);
        }
    }

    private void appRun(int i) {
        throw new Error("Unresolved compilation problems: \n\tMIDletMan cannot be resolved\n\tMIDletMan cannot be resolved\n");
    }

    private void appUpdate(int i) {
        AppInfo appinfo = getAppInfo(i);
        appinfo.loadFull();
        String s = appinfo.updateUrl;
        if (s != null) {
            setDownView(s);
        } else {
            setAlertView(XBrowser.JAM_RES.MSG_EXPIRED, i, XBrowser.JAM_RES.MSG_CONN, -1, 2);
        }
    }

    private void changeMode() {
        ItemView itemView = this.currView;
        if (itemView == this.listView) {
            XBrowser.playSound(7);
            setIconView(true);
            this.listView = null;
        } else if (itemView == this.iconView) {
            XBrowser.playSound(7);
            setListView(true);
            this.iconView = null;
        }
    }

    private int getAppIndex() {
        ItemView itemView = this.currView;
        ListView listView = this.listView;
        if (itemView == listView) {
            int i = listView.getSelectedIndex();
            return i;
        }
        IconView iconView = this.iconView;
        if (itemView != iconView) {
            return 0;
        }
        int i2 = iconView.getSelectedIndex();
        return i2;
    }

    private void setCurrent(ItemView itemview) {
        if (itemview == this.alertView) {
            this.lastView = this.currView;
        } else {
            this.lastView = null;
        }
        ItemView itemView = this.currView;
        if (itemView != null && itemView != itemview) {
            itemView.hide();
        }
        this.currView = itemview;
        itemview.show();
    }

    private void setListView(boolean flag) {
        if (this.listView == null) {
            this.listView = new ListView();
            flag = true;
        }
        if (flag) {
            this.listView.init(this);
            this.listView.setData(getAppList());
            this.listView.setCommand(1, 0);
            this.listView.setType(1);
        }
        this.viewMode = 1;
        setCurrent(this.listView);
    }

    private void setIconView(boolean flag) {
        if (this.iconView == null) {
            this.iconView = new IconView();
            flag = true;
        }
        if (flag) {
            this.iconView.init(this);
            this.iconView.setCommand(1, 0);
        }
        this.viewMode = 0;
        setCurrent(this.iconView);
    }

    private void setAlertView(String s, int i, String s1, int j, int k) {
        if (this.alertView == null) {
            this.alertView = new AlertView();
        }
        this.alertView.init(this);
        this.alertView.setData(1, i, s, s1);
        this.alertView.setCommand(j, k);
        setCurrent(this.alertView);
    }

    private void setBrowserView(boolean flag) {
        if (this.viewMode == 1) {
            setListView(flag);
        } else {
            setIconView(flag);
        }
    }

    private void setMenuView() {
        if (getAppIndex() >= getAppCount()) {
            return;
        }
        ItemView itemView = this.currView;
        if (itemView != null) {
            itemView.hide();
        }
        MenuForm.getForm().init(getAppIndex());
    }

    private void setControlView() {
        ItemView itemView = this.currView;
        if (itemView != null) {
            itemView.hide();
        }
        MenuForm.getForm().init();
    }

    private void setDownView(String s) {
        ItemView itemView = this.currView;
        if (itemView != null) {
            itemView.hide();
        }
        XBrowser.download(s);
    }

    public void setViewMode(int i) {
        this.viewMode = i;
    }

    private String[] getAppList() {
        int i = XBrowser.appdb.size();
        String[] as = new String[i];
        for (int j = 0; j < i; j++) {
            as[j] = XBrowser.appdb.elementAt(j).name.trim();
        }
        return as;
    }

    private int getAppCount() {
        return XBrowser.appdb.size();
    }

    private AppInfo getAppInfo(int i) {
        return XBrowser.appdb.elementAt(i);
    }

    private void setAppInfo(int i) {
        XBrowser.appdb.setRunInfo(i);
    }

    private int getDefaultView() {
        return AppDB.setView;
    }
}
