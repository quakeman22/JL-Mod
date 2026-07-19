package com.skt.m;

import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import javax.microedition.lcdui.Image;

/* JADX INFO: loaded from: classes.dex */
public final class ProgressBar {
    private Image backImg;
    private int imgHeight;
    private int imgWidth;
    private int imgX;
    private int imgY;
    private Image progImg;
    private String title;
    private int value;
    private int maxValue = 100;
    private Graphics2D g2 = Graphics2D.getGraphics2D(Toolkit.graphics);

    public ProgressBar(String s) {
        try {
            this.backImg = Toolkit.createImage("/app_open");
            if (Toolkit.IS_HEBREW) {
                this.progImg = Toolkit.createExImage("/iconsx", "/down_progress");
            } else {
                this.progImg = Toolkit.createImage("/app_prog");
            }
        } catch (Exception e) {
        }
        this.imgWidth = this.progImg.getWidth();
        this.imgHeight = this.progImg.getHeight() / 2;
        if (Toolkit.IS_HEBREW) {
            this.imgX = (XDisplay.width / 2) - (this.imgWidth / 2);
            this.imgY = (XDisplay.height2 / 2) - (this.imgHeight / 2);
        } else if (XDisplay.width == 176) {
            this.imgX = (XDisplay.width / 2) - (this.imgWidth / 2);
            this.imgY = (XDisplay.height2 / 2) + 35;
        } else if (XDisplay.width >= 90 && XDisplay.height2 >= 90) {
            this.imgX = (XDisplay.width / 2) - (this.imgWidth / 2);
            this.imgY = (XDisplay.height2 / 2) + 22;
        }
        repaint();
    }

    public void setValue(int i) {
        int i2 = this.maxValue;
        if (i > i2) {
            this.value = i2;
        } else {
            this.value = i;
        }
        repaintBar();
    }

    public int getValue() {
        return this.value;
    }

    public void setMaxValue(int i) {
        this.maxValue = i;
        if (this.value > i) {
            this.value = i;
        }
        if (this.value != 0) {
            repaintBar();
        }
    }

    public int getMaxValue() {
        return this.maxValue;
    }

    private void repaintBar() {
    }

    private void repaint() {
    }

    private void paintBar() {
    }
}
