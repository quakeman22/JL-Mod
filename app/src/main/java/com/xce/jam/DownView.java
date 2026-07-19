package com.xce.jam;

import com.skt.m.Graphics2D;
import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* JADX INFO: loaded from: classes.dex */
public class DownView extends ItemView {
    private static int check_x;
    private static int down_space;
    private static Image img_down_check;
    private static Image img_down_checkbox;
    private static Image img_text_downloading;
    private static int progress_height;
    private static int progress_width;
    private static int progress_x;
    private static int progress_y;
    private String appName;
    private int currValue;
    private int downStep;
    private Graphics2D g2d;
    private int maxValue;
    private int title_height;

    @Override // com.xce.jam.ItemView
    public void init(Canvas canvas) {
        this.canvas = canvas;
        paintInit();
    }

    @Override // com.xce.jam.ItemView
    public void show() {
        repaintAll();
    }

    public void setMaxValue(int i) {
        if (i >= 0) {
            this.maxValue = i;
        }
    }

    public void setValue(int i) {
        int i2 = this.maxValue;
        if (i < i2) {
            this.currValue = i;
        } else {
            this.currValue = i2;
        }
        repaintProgress();
    }

    public void setDownStep(int i) {
        this.downStep = i;
        repaintCheck();
    }

    public void setAppName(String s) {
        this.appName = s;
        repaintName();
    }

    @Override // com.xce.jam.ItemView
    public void paint(Graphics g) {
        this.g2d = Graphics2D.getGraphics2D(g);
        int i = g.getClipHeight();
        if (i == progress_height) {
            paintProgress(g);
        } else if (i == JAMGraphic.frame_bottom - JAMGraphic.frame_top) {
            paintCheck(g);
        } else if (i < XDisplay.height2) {
            paintName(g);
        } else {
            paintBackground(g);
            paintName(g);
            paintCheck(g);
            paintProgress(g);
        }
        g.setColor(0);
    }

    private void paintBackground(Graphics g) {
        JAMGraphic.paintBack(g);
        g.drawImage(JAMGraphic.getImg_text_down(), XDisplay.width / 2, JAMGraphic.frame_title_height - 2, 33);
        this.g2d.drawImage(5, JAMGraphic.frame_title_height + 2, JAMGraphic.getImg_frame_subtitle(), 0, 0, XDisplay.width - 10, JAMGraphic.frame_subtitle_height - 5, 0);
        g.setColor(JAMGraphic.color_border_dark);
        g.drawRect(4, JAMGraphic.frame_title_height + 1, XDisplay.width - 9, JAMGraphic.frame_subtitle_height - 4);
        g.drawLine(3, (JAMGraphic.frame_title_height + JAMGraphic.frame_subtitle_height) - 1, XDisplay.width - 3, (JAMGraphic.frame_title_height + JAMGraphic.frame_subtitle_height) - 1);
        g.setColor(JAMGraphic.color_border_light);
        g.drawRect(3, JAMGraphic.frame_title_height, XDisplay.width - 7, JAMGraphic.frame_subtitle_height - 2);
        g.drawImage(JAMGraphic.getImg_button_cancel(), JAMGraphic.frame_left, JAMGraphic.frame_bottom + 1, 20);
        int i = JAMGraphic.frame_top + JAMGraphic.frame_subtitle_height + (down_space * 3);
        if (this.title_height > 0) {
            g.drawImage(img_text_downloading, XDisplay.width / 2, i, 17);
            i += 21;
        }
        g.setColor(0);
        int i2 = i + (down_space * 2);
        g.drawString(XBrowser.JAM_RES.DOWNLOAD_MSD, check_x + 13 + 5, i2, 20);
        int i3 = i2 + Toolkit.FONT_HEIGHT + down_space;
        g.drawString(XBrowser.JAM_RES.DOWNLOAD_ICON, check_x + 13 + 5, i3, 20);
        g.drawString(XBrowser.JAM_RES.DOWNLOAD_JAR, check_x + 13 + 5, i3 + Toolkit.FONT_HEIGHT + down_space, 20);
    }

    private void paintName(Graphics g) {
        if (this.appName == null) {
            return;
        }
        g.setColor(0);
        int i = XDisplay.width / 2;
        int j = (JAMGraphic.frame_title_height + (JAMGraphic.frame_subtitle_height / 2)) - (Toolkit.FONT_HEIGHT / 2);
        g.drawString(JAMUtil.getLineString(this.appName, JAMGraphic.line_width), i, j, 17);
    }

    private void paintCheck(Graphics g) {
        int i = JAMGraphic.frame_top + JAMGraphic.frame_subtitle_height;
        int i2 = down_space;
        int i3 = i + (i2 * 3) + this.title_height + (i2 * 2) + (Toolkit.FONT_HEIGHT / 2);
        if (this.downStep >= 1) {
            g.drawImage(img_down_check, check_x, i3, 6);
        } else {
            g.drawImage(img_down_checkbox, check_x, i3, 6);
        }
        int i4 = i3 + Toolkit.FONT_HEIGHT + down_space;
        if (this.downStep >= 2) {
            g.drawImage(img_down_check, check_x, i4, 6);
        } else {
            g.drawImage(img_down_checkbox, check_x, i4, 6);
        }
        int i5 = i4 + Toolkit.FONT_HEIGHT + down_space;
        if (this.downStep >= 4) {
            g.drawImage(img_down_check, check_x, i5, 6);
        } else {
            g.drawImage(img_down_checkbox, check_x, i5, 6);
        }
    }

    private void paintProgress(Graphics g) {
        int i;
        int i2 = 0;
        int i3 = this.maxValue;
        if (i3 > 0 && (i = this.currValue) > 0) {
            i2 = (i * progress_width) / i3;
        }
        g.setColor(JAMGraphic.color_scroll);
        this.g2d.drawImage(progress_x, progress_y, JAMGraphic.getImg_frame_subtitle(), 0, 3, progress_width, progress_height, 0);
        g.fillRect(progress_x, progress_y, i2, progress_height);
    }

    private void paintInit() {
        if ((JAMGraphic.frame_bottom - JAMGraphic.frame_top) - JAMGraphic.frame_subtitle_height > (Toolkit.FONT_HEIGHT * 3) + 21) {
            this.title_height = 21;
        } else {
            this.title_height = 0;
        }
        if (XDisplay.width >= 176) {
            progress_x = 55;
            progress_width = (XDisplay.width - progress_x) - 3;
            progress_height = 11;
            progress_y = (XDisplay.height2 - 3) - progress_height;
            down_space = ((((JAMGraphic.frame_bottom - JAMGraphic.frame_top) - JAMGraphic.frame_subtitle_height) - this.title_height) - (Toolkit.FONT_HEIGHT * 3)) / 10;
        } else {
            progress_x = 44;
            progress_width = (XDisplay.width - progress_x) - 3;
            progress_height = 9;
            progress_y = (XDisplay.height2 - 3) - progress_height;
            down_space = ((((JAMGraphic.frame_bottom - JAMGraphic.frame_top) - JAMGraphic.frame_subtitle_height) - this.title_height) - (Toolkit.FONT_HEIGHT * 3)) / 10;
        }
        int i = Toolkit.DEFAULT_FONT.stringWidth(XBrowser.JAM_RES.DOWNLOAD_MSD);
        int j = Toolkit.DEFAULT_FONT.stringWidth(XBrowser.JAM_RES.DOWNLOAD_ICON);
        int i2 = i <= j ? j : i;
        int j2 = Toolkit.DEFAULT_FONT.stringWidth(XBrowser.JAM_RES.DOWNLOAD_JAR);
        check_x = (XDisplay.width / 2) - (((i2 <= j2 ? j2 : i2) + 18) / 2);
        try {
            img_text_downloading = Toolkit.createExImage("/iconsx", "/text_downloading");
            img_down_check = Toolkit.createExImage("/iconsx", "/down_check");
            img_down_checkbox = Toolkit.createExImage("/iconsx", "/down_checkbox");
        } catch (Exception e) {
        }
    }

    private void repaintAll() {
        this.canvas.repaint(0, 0, XDisplay.width, XDisplay.height2);
        this.canvas.serviceRepaints();
    }

    private void repaintProgress() {
        this.canvas.repaint(progress_x, progress_y, progress_width, progress_height);
        this.canvas.serviceRepaints();
    }

    private void repaintName() {
        this.canvas.repaint(0, JAMGraphic.frame_title_height, XDisplay.width, JAMGraphic.frame_subtitle_height);
        this.canvas.serviceRepaints();
    }

    private void repaintCheck() {
        this.canvas.repaint(check_x, JAMGraphic.frame_top, 13, JAMGraphic.frame_bottom - JAMGraphic.frame_top);
        this.canvas.serviceRepaints();
    }
}
