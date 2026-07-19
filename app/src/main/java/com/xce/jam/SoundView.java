package com.xce.jam;

import com.skt.m.AudioSystem;
import com.skt.m.Graphics2D;
import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* JADX INFO: loaded from: classes.dex */
public class SoundView extends ItemView {
    private static int bar_base;
    private static int bar_height;
    private static int bar_indent;
    private static int bar_space;
    private static int bar_width;
    private static Image img_sound_icon1;
    private static Image img_sound_icon2;
    private static Image img_sound_off;
    private static Image img_sound_on;
    private static Image img_volume_down;
    private static Image img_volume_up;
    private int currVolume;
    private Graphics2D g2d;
    private int lastVolume;
    private int maxVolume = getMaxVolume();

    @Override // com.xce.jam.ItemView
    public void init(Canvas canvas) {
        this.canvas = canvas;
        int volume = getVolume();
        this.currVolume = volume;
        this.lastVolume = volume;
        paintInit();
    }

    @Override // com.xce.jam.ItemView
    public void show() {
        repaintAll();
    }

    public void reset() {
        int i = this.lastVolume;
        this.currVolume = i;
        setVolume(i);
    }

    public int getSound() {
        return this.currVolume;
    }

    @Override // com.xce.jam.ItemView
    public void keyPressed(int i) {
        if (i != 141) {
            if (i != 142) {
                if (i != 145) {
                    if (i != 146) {
                        return;
                    }
                }
            }
            volumeDown();
            return;
        }
        volumeUp();
    }

    private void volumeUp() {
        int i = this.currVolume;
        if (i < this.maxVolume) {
            int i2 = i + 1;
            this.currVolume = i2;
            setVolume(i2);
            repaintMove();
        }
    }

    private void volumeDown() {
        int i = this.currVolume;
        if (i > 0) {
            int i2 = i - 1;
            this.currVolume = i2;
            setVolume(i2);
            repaintMove();
        }
    }

    @Override // com.xce.jam.ItemView
    public void paint(Graphics g) {
        this.g2d = Graphics2D.getGraphics2D(g);
        int i = g.getClipHeight();
        if (i < XDisplay.height2) {
            paintBar(g);
        } else {
            paintBackground(g);
            paintBar(g);
        }
        g.setColor(0);
    }

    private void paintBackground(Graphics g) {
        JAMGraphic.paintBack(g);
        g.drawImage(JAMGraphic.getImg_text_control(), XDisplay.width / 2, JAMGraphic.frame_title_height - 2, 33);
        this.g2d.drawImage(3, JAMGraphic.frame_top, JAMGraphic.getImg_frame_subtitle(), 0, 0, XDisplay.width - 6, Toolkit.FONT_HEIGHT, 0);
        g.setColor(0);
        g.drawString(XBrowser.JAM_RES.MENU_SET_VOLUME, XDisplay.width / 2, JAMGraphic.frame_top, 17);
        g.setColor(JAMGraphic.color_border_dark);
        g.drawLine(2, JAMGraphic.frame_top + Toolkit.FONT_HEIGHT, XDisplay.width - 3, JAMGraphic.frame_top + Toolkit.FONT_HEIGHT);
        g.drawLine(2, JAMGraphic.frame_top + Toolkit.FONT_HEIGHT + 2, XDisplay.width - 3, JAMGraphic.frame_top + Toolkit.FONT_HEIGHT + 2);
        g.setColor(JAMGraphic.color_border_light);
        g.drawLine(2, JAMGraphic.frame_top + Toolkit.FONT_HEIGHT + 1, XDisplay.width - 3, JAMGraphic.frame_top + Toolkit.FONT_HEIGHT + 1);
        g.drawImage(JAMGraphic.getImg_button_cancel(), JAMGraphic.frame_left, JAMGraphic.frame_bottom + 1, 20);
        g.drawImage(JAMGraphic.getImg_button_ok(), JAMGraphic.frame_right + 1, JAMGraphic.frame_bottom + 1, 24);
        g.drawImage(img_volume_down, bar_indent + 3, bar_base + 5, 20);
        g.drawImage(img_volume_up, (XDisplay.width - 3) - bar_indent, bar_base + 5, 24);
        g.drawImage(JAMGraphic.getImg_arrow_left(), bar_indent + 1, bar_base + 7, 24);
        g.drawImage(JAMGraphic.getImg_arrow_right(), (XDisplay.width - 1) - bar_indent, bar_base + 7, 20);
        if ((((bar_base - JAMGraphic.frame_top) - Toolkit.FONT_HEIGHT) - 3) - bar_height > 28) {
            g.drawImage(img_sound_icon2, (XDisplay.width - 3) - bar_indent, (bar_base - bar_height) - 10, 40);
            g.drawImage(img_sound_icon1, ((XDisplay.width - 3) - bar_indent) - 15, (bar_base - bar_height) - 7, 40);
            g.drawImage(img_sound_icon1, ((XDisplay.width - 3) - bar_indent) - 25, (bar_base - bar_height) - 14, 40);
        }
    }

    private void paintBar(Graphics g) {
        int i = bar_indent + 3;
        int j = bar_base;
        for (int k = 0; k < this.maxVolume; k++) {
            if (k < this.currVolume) {
                g.drawImage(img_sound_on, i, j, 36);
            } else {
                g.drawImage(img_sound_off, i, j, 36);
            }
            i += bar_space + bar_width;
        }
    }

    private void paintInit() {
        if (XDisplay.width >= 176) {
            bar_width = 13;
            bar_height = 20;
        } else {
            bar_width = 11;
            bar_height = 19;
        }
        int i = XDisplay.width - 6;
        bar_space = ((i - (bar_width * r2)) - 16) / (this.maxVolume + 1);
        int i2 = XDisplay.width - 6;
        int i3 = this.maxVolume;
        bar_indent = ((i2 - (bar_width * i3)) - ((i3 - 1) * bar_space)) / 2;
        bar_base = JAMGraphic.frame_top + Toolkit.FONT_HEIGHT + 3 + ((((JAMGraphic.frame_bottom - JAMGraphic.frame_top) - Toolkit.FONT_HEIGHT) - 3) / 2) + (bar_height / 2);
        try {
            img_sound_icon1 = Toolkit.createExImage("/iconsx", "/sound_icon1");
            img_sound_icon2 = Toolkit.createExImage("/iconsx", "/sound_icon2");
            img_sound_on = Toolkit.createExImage("/iconsx", "/sound_on");
            img_sound_off = Toolkit.createExImage("/iconsx", "/sound_off");
            img_volume_up = Toolkit.createExImage("/iconsx", "/text_volup");
            img_volume_down = Toolkit.createExImage("/iconsx", "/text_voldown");
        } catch (Exception e) {
        }
    }

    private void repaintAll() {
        this.canvas.repaint(0, 0, XDisplay.width, XDisplay.height2);
        this.canvas.serviceRepaints();
    }

    private void repaintMove() {
        Canvas canvas = this.canvas;
        int i = bar_indent;
        int i2 = bar_base;
        int i3 = bar_height;
        canvas.repaint(i, i2 - i3, (bar_width + bar_space) * this.maxVolume, i3);
        this.canvas.serviceRepaints();
    }

    private int getMaxVolume() {
        try {
            int i = AudioSystem.getMaxVolume(XBrowser.JAM_SND_FMT);
            return i;
        } catch (Exception e) {
            return 0;
        }
    }

    private int getVolume() {
        try {
            int i = AudioSystem.getVolume(XBrowser.JAM_SND_FMT);
            return i;
        } catch (Exception e) {
            return 0;
        }
    }

    private void setVolume(int i) {
        try {
            AudioSystem.setVolume(XBrowser.JAM_SND_FMT, i);
            XBrowser.playSound(9);
        } catch (Exception e) {
        }
    }
}
