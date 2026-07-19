package com.xce.jam;

import com.skt.m.Graphics2D;
import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import java.lang.reflect.Array;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* JADX INFO: loaded from: classes.dex */
public class IconView extends ItemView implements Runnable {
    private static final int icon_bottom;
    private static final int icon_column;
    private static final int icon_count;
    private static final int icon_indent;
    private static final int icon_left = 3;
    private static final int icon_right;
    private static final int icon_row;
    private static final int icon_select_indent;
    private static final int icon_top;
    private static final int[][] icon_xy;
    private static final int line_width = XDisplay.width - 10;
    private static final int[][] select_xy;
    private Image aniIcon;
    private int aniStep;
    private String contentsName;
    private Image emptyAniIcon;
    private Image emptyIcon;
    private Graphics2D g2d;
    private Image[] icons;
    private boolean isAni;
    private int lastIndex;
    private int scroll_height;
    private int scrollbar_height;
    private int selectedIndex;
    private int startIndex;
    private int step;
    private int ticker_end;
    private int ticker_init;
    private boolean ticker_sleep;
    private int ticker_start;

    public IconView() {
        paintInit();
        this.icons = new Image[getSize()];
    }

    @Override // com.xce.jam.ItemView
    public void init(Canvas canvas) {
        this.canvas = canvas;
        this.startIndex = 0;
        this.selectedIndex = 0;
        this.lastIndex = 0;
        this.aniStep = 0;
        initIcons();
    }

    @Override // com.xce.jam.ItemView
    public void show() {
        repaintAll();
        tickerSet();
    }

    @Override // com.xce.jam.ItemView
    public void hide() {
        tickerStop();
    }

    private void initIcons() {
        for (int i = 0; i < getSize(); i++) {
            this.icons[i] = null;
        }
        this.emptyIcon = JAMGraphic.getDefaultIcon(0, false);
        this.emptyAniIcon = JAMGraphic.getDefaultIcon(0, true);
        loadIcons();
    }

    private void loadIcons() {
        for (int i = this.startIndex; i < this.startIndex + icon_count && i < getSize(); i++) {
            Image[] imageArr = this.icons;
            if (imageArr[i] == null) {
                imageArr[i] = JAMGraphic.getContentsIcon(i, false);
            }
        }
        int i2 = this.selectedIndex;
        if (i2 < getSize()) {
            this.aniIcon = JAMGraphic.getContentsIcon(this.selectedIndex, true);
            this.contentsName = getItemName(this.selectedIndex);
            this.isAni = true;
            this.aniStep = 0;
            return;
        }
        this.contentsName = XBrowser.JAM_RES.CONTENTS_EMPTY;
        this.isAni = false;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    @Override // com.xce.jam.ItemView
    public void keyPressed(int i) {
        if (i == 141) {
            XBrowser.playSound(5);
            keyUp();
            return;
        }
        if (i == 142) {
            XBrowser.playSound(5);
            keyLeft();
        } else if (i == 145) {
            XBrowser.playSound(5);
            keyRight();
        } else if (i == 146) {
            XBrowser.playSound(5);
            keyDown();
        }
    }

    @Override // com.xce.jam.ItemView
    public void keyRepeated(int i) {
        keyPressed(i);
    }

    private void keyUp() {
        int i = this.selectedIndex;
        int i2 = icon_column;
        int i3 = i - i2;
        int i4 = this.startIndex;
        if (i3 >= i4) {
            tickerReset();
            int i5 = this.selectedIndex;
            this.lastIndex = i5;
            this.selectedIndex = i5 - i2;
            loadIcons();
            tickerSet();
            repaintMove();
            return;
        }
        if (i4 > 0) {
            tickerReset();
            int i6 = this.selectedIndex;
            this.lastIndex = i6;
            this.selectedIndex = i6 - i2;
            this.startIndex -= i2;
            loadIcons();
            tickerSet();
            repaintAll();
        }
    }

    private void keyDown() {
        int i = this.selectedIndex;
        int i2 = icon_column;
        int i3 = i + i2;
        int i4 = this.startIndex;
        int i5 = icon_count;
        if (i3 <= (i4 + i5) - 1 && i + i2 < getSize()) {
            tickerReset();
            int i6 = this.selectedIndex;
            this.lastIndex = i6;
            this.selectedIndex = i6 + i2;
            loadIcons();
            tickerSet();
            repaintMove();
            return;
        }
        if (this.startIndex + i5 < getSize()) {
            tickerReset();
            int i7 = this.selectedIndex;
            this.lastIndex = i7;
            int i8 = i7 + i2;
            this.selectedIndex = i8;
            if (i8 >= getSize()) {
                this.selectedIndex = getSize() - 1;
            }
            this.startIndex += i2;
            loadIcons();
            tickerSet();
            repaintAll();
        }
    }

    private void keyLeft() {
        int i = this.selectedIndex;
        int i2 = this.startIndex;
        if (i > i2) {
            tickerReset();
            int i3 = this.selectedIndex;
            this.lastIndex = i3;
            this.selectedIndex = i3 - 1;
            loadIcons();
            tickerSet();
            repaintMove();
            return;
        }
        if (i2 > 0) {
            tickerReset();
            int i4 = this.selectedIndex;
            this.lastIndex = i4;
            this.selectedIndex = i4 - 1;
            this.startIndex -= icon_column;
            loadIcons();
            tickerSet();
            repaintAll();
        }
    }

    private void keyRight() {
        int i = this.selectedIndex;
        int i2 = this.startIndex;
        int i3 = icon_count;
        if (i < (i2 + i3) - 1 && i < getSize() - 1) {
            tickerReset();
            int i4 = this.selectedIndex;
            this.lastIndex = i4;
            this.selectedIndex = i4 + 1;
            loadIcons();
            tickerSet();
            repaintMove();
            return;
        }
        if (this.startIndex + i3 < getSize()) {
            tickerReset();
            int i5 = this.selectedIndex;
            this.lastIndex = i5;
            this.selectedIndex = i5 + 1;
            this.startIndex += icon_column;
            loadIcons();
            tickerSet();
            repaintAll();
        }
    }

    @Override // com.xce.jam.ItemView
    public void paint(Graphics g) {
        this.g2d = Graphics2D.getGraphics2D(g);
        int i = g.getClipHeight();
        if (i <= JAMGraphic.icon_select_size) {
            paintAniIcon(g);
        } else if (i < XDisplay.height2) {
            paintScrollbar(g);
            paintLastIcon(g);
            paintAniIcon(g);
            paintTitle(g);
        } else {
            paintBackground(g);
            paintScrollbar(g);
            paintAllIcons(g);
            paintTitle(g);
        }
        g.setColor(0);
    }

    private void paintBackground(Graphics g) {
        JAMGraphic.paintBack(g);
        g.drawImage(JAMGraphic.getImg_text_listicon(), XDisplay.width / 2, JAMGraphic.frame_title_height - 2, 33);
        g.setColor(JAMGraphic.color_border_dark);
        g.drawRect(4, JAMGraphic.frame_title_height + 1, XDisplay.width - 9, JAMGraphic.frame_subtitle_height - 4);
        g.drawLine(3, (JAMGraphic.frame_title_height + JAMGraphic.frame_subtitle_height) - 1, XDisplay.width - 3, (JAMGraphic.frame_title_height + JAMGraphic.frame_subtitle_height) - 1);
        g.setColor(JAMGraphic.color_border_light);
        g.drawRect(3, JAMGraphic.frame_title_height, XDisplay.width - 7, JAMGraphic.frame_subtitle_height - 2);
        g.drawImage(JAMGraphic.getImg_button_menu(), JAMGraphic.frame_left, JAMGraphic.frame_bottom + 1, 20);
        g.drawImage(JAMGraphic.getImg_button_run(), JAMGraphic.frame_right + 1, JAMGraphic.frame_bottom + 1, 24);
    }

    private void paintAllIcons(Graphics g) {
        for (int i = this.startIndex; i < this.startIndex + icon_count; i++) {
            paintIcon(g, i);
        }
        paintAniIcon(g);
    }

    private void paintIcon(Graphics g, int i) {
        int i2 = this.startIndex;
        if (i < i2 || i >= i2 + icon_count) {
            return;
        }
        Image img_icon_back = JAMGraphic.getImg_icon_back();
        int[][] iArr = icon_xy;
        int i3 = this.startIndex;
        g.drawImage(img_icon_back, iArr[i - i3][0], iArr[i - i3][1], 20);
        if (i < getSize()) {
            Image image = this.icons[i];
            int i4 = this.startIndex;
            int i5 = iArr[i - i4][0];
            int i6 = icon_indent;
            g.drawImage(image, i5 + i6, iArr[i - i4][1] + i6, 20);
        }
    }

    private void paintLastIcon(Graphics g) {
        int i;
        int i2;
        int i3 = this.lastIndex;
        if (i3 < 0) {
            return;
        }
        Graphics2D graphics2D = this.g2d;
        int[][] iArr = select_xy;
        int i4 = this.startIndex;
        graphics2D.drawImage(iArr[i3 - i4][0], iArr[i3 - i4][1], JAMGraphic.getImg_frame_back(), iArr[r6 - r7][0] - 3, iArr[this.lastIndex - this.startIndex][1] - icon_top, JAMGraphic.icon_select_size, JAMGraphic.icon_select_size, 0);
        int i5 = (this.lastIndex - icon_column) - 1;
        while (true) {
            i = this.lastIndex;
            if (i5 > (i - icon_column) + 1) {
                break;
            }
            paintIcon(g, i5);
            i5++;
        }
        int j = i - 1;
        while (true) {
            i2 = this.lastIndex;
            if (j > i2 + 1) {
                break;
            }
            paintIcon(g, j);
            j++;
        }
        int j2 = icon_column;
        for (int k = (i2 + j2) - 1; k <= this.lastIndex + icon_column + 1; k++) {
            paintIcon(g, k);
        }
    }

    private void paintAniIcon(Graphics g) {
        if (getSize() <= 0) {
            return;
        }
        Graphics2D graphics2d = Graphics2D.getGraphics2D(g);
        int[][] iArr = select_xy;
        int i = this.selectedIndex;
        int i2 = this.startIndex;
        int i3 = iArr[i - i2][0];
        int j = iArr[i - i2][1];
        g.drawImage(JAMGraphic.getImg_icon_select(), i3, j, 20);
        if (this.selectedIndex < getSize()) {
            int i4 = icon_select_indent;
            graphics2d.drawImage(i3 + i4, j + i4, this.aniIcon, this.aniStep * 36, 0, 36, 36, 0);
        }
    }

    private void paintTitle(Graphics g) {
        this.g2d.drawImage(5, JAMGraphic.frame_title_height + 2, JAMGraphic.getImg_frame_subtitle(), 0, 0, XDisplay.width - 10, JAMGraphic.frame_subtitle_height - 5, 0);
        g.setColor(0);
        int i = XDisplay.width / 2;
        int j = (JAMGraphic.frame_title_height + (JAMGraphic.frame_subtitle_height / 2)) - (Toolkit.FONT_HEIGHT / 2);
        if (getSize() > 0) {
            if (this.ticker_init > 0) {
                String str = this.contentsName;
                int i2 = this.ticker_start;
                g.drawSubstring(str, i2, (this.ticker_end - i2) + 1, i, j, 17);
                return;
            }
            g.drawString(JAMUtil.getLineString(this.contentsName, line_width), i, j, 17);
            return;
        }
        g.drawString(XBrowser.JAM_RES.CONTENTS_EMPTY, i, j, 17);
    }

    private void paintScrollbar(Graphics g) {
        int i = icon_xy[0][1];
        int j = (JAMGraphic.frame_right - JAMGraphic.scroll_width) + 1;
        g.setColor(JAMGraphic.color_border_dark);
        g.drawLine(j, i, JAMGraphic.scroll_width + j, i);
        g.drawLine(j, i, j, (this.scrollbar_height + i) - 1);
        g.drawLine(j, (this.scrollbar_height + i) - 1, JAMGraphic.scroll_width + j, (this.scrollbar_height + i) - 1);
        g.drawRect(j + 2, i + 2, JAMGraphic.scroll_width - 2, (this.scrollbar_height - 1) - 4);
        g.setColor(JAMGraphic.color_border_light);
        g.drawLine(j + 1, i + 1, JAMGraphic.scroll_width + j, i + 1);
        g.drawLine(j + 1, i + 1, j + 1, ((this.scrollbar_height + i) - 1) - 1);
        g.drawLine(j + 1, ((this.scrollbar_height + i) - 1) - 1, JAMGraphic.scroll_width + j, ((this.scrollbar_height + i) - 1) - 1);
        this.g2d.drawImage(j + 3, i + 3, JAMGraphic.getImg_frame_scroll(), 0, 0, JAMGraphic.scroll_width - 3, (this.scrollbar_height - 1) - 5, 0);
        int i2 = icon_count;
        if (i2 < getSize()) {
            int i3 = this.startIndex;
            int k = i + 3 + ((i3 / icon_column) * this.step);
            if (i3 + i2 >= getSize()) {
                k = ((((this.scrollbar_height + i) - 1) - 3) - this.scroll_height) + 1;
            }
            g.setColor(JAMGraphic.color_scroll);
            g.fillRect(j + 3, k, JAMGraphic.scroll_width - 4, this.scroll_height);
        }
    }

    private void repaintAll() {
        this.canvas.repaint(0, 0, XDisplay.width, XDisplay.height2);
        this.canvas.serviceRepaints();
    }

    private void repaintMove() {
        int[][] iArr = select_xy;
        int i = iArr[this.selectedIndex - this.startIndex][1] + JAMGraphic.icon_select_size;
        int j = iArr[this.lastIndex - this.startIndex][1] + JAMGraphic.icon_select_size;
        int k = i <= j ? j : i;
        this.canvas.repaint(3, JAMGraphic.frame_title_height, XDisplay.width - 6, k - JAMGraphic.frame_title_height);
        this.canvas.serviceRepaints();
    }

    private void repaintTicker() {
        if (this.ticker_init > 0) {
            this.canvas.repaint(3, JAMGraphic.frame_title_height, XDisplay.width - 6, (select_xy[this.selectedIndex - this.startIndex][1] + JAMGraphic.icon_select_size) - JAMGraphic.frame_title_height);
        } else {
            Canvas canvas = this.canvas;
            int[][] iArr = select_xy;
            int i = this.selectedIndex;
            int i2 = this.startIndex;
            canvas.repaint(iArr[i - i2][0], iArr[i - i2][1], JAMGraphic.icon_select_size, JAMGraphic.icon_select_size);
        }
        this.canvas.serviceRepaints();
    }

    private void paintInit() {
        int i = icon_right - 3;
        int i2 = icon_column;
        int i3 = (i - (JAMGraphic.icon_back_size * i2)) / (i2 + 1);
        int i4 = icon_bottom;
        int i5 = icon_top;
        int i6 = icon_row;
        int j = ((i4 - i5) - (JAMGraphic.icon_back_size * i6)) / (i6 + 1);
        int l = i5 + j;
        int i1 = 0;
        while (true) {
            if (i1 >= icon_row) {
                break;
            }
            int k = i3 + 3;
            int j1 = 0;
            while (true) {
                int i7 = icon_column;
                if (j1 >= i7) {
                    break;
                }
                int l1 = (i1 * i7) + j1;
                int[][] iArr = icon_xy;
                iArr[l1][0] = k;
                iArr[l1][1] = l;
                if (j1 == 0) {
                    select_xy[l1][0] = k;
                } else if (j1 == i7 - 1) {
                    select_xy[l1][0] = (JAMGraphic.icon_back_size + k) - JAMGraphic.icon_select_size;
                } else {
                    select_xy[l1][0] = ((JAMGraphic.icon_back_size / 2) + k) - (JAMGraphic.icon_select_size / 2);
                }
                int i8 = icon_row;
                if (i8 == 1) {
                    select_xy[l1][1] = ((JAMGraphic.icon_back_size / 2) + l) - (JAMGraphic.icon_select_size / 2);
                } else if (i1 == 0) {
                    select_xy[l1][1] = l;
                } else if (i1 == i8 - 1) {
                    select_xy[l1][1] = (JAMGraphic.icon_back_size + l) - JAMGraphic.icon_select_size;
                } else {
                    select_xy[l1][1] = ((JAMGraphic.icon_back_size / 2) + l) - (JAMGraphic.icon_select_size / 2);
                }
                k += JAMGraphic.icon_back_size + i3;
                j1++;
            }
            l += JAMGraphic.icon_back_size + j;
            i1++;
        }
        int[][] iArr2 = icon_xy;
        this.scrollbar_height = (iArr2[icon_count - 1][1] - iArr2[0][1]) + JAMGraphic.icon_back_size;
        if (getSize() > 0) {
            int size = getSize();
            int i9 = icon_column;
            int k1 = ((size + i9) - 1) / i9;
            int i10 = (this.scrollbar_height - 6) / k1;
            this.step = i10;
            this.scroll_height = (((r5 * i10) + r2) - 6) - (i10 * k1);
            return;
        }
        this.step = 0;
        this.scroll_height = 0;
    }

    private void tickerSet() {
        this.ticker_init = 0;
        int i = Toolkit.DEFAULT_FONT.stringWidth(this.contentsName);
        if (this.selectedIndex < getSize()) {
            this.isAni = true;
        } else {
            this.isAni = false;
        }
        int i2 = line_width;
        if (i > i2) {
            this.ticker_start = 0;
            int lineIndex = JAMUtil.getLineIndex(this.contentsName, i2);
            this.ticker_end = lineIndex;
            this.ticker_init = lineIndex;
        } else {
            this.ticker_end = 0;
            this.ticker_init = 0;
            this.ticker_start = 0;
        }
        if (this.ticker_init > 0 || this.isAni) {
            tickerStart();
        } else {
            tickerStop();
        }
    }

    private void tickerReset() {
        this.isAni = false;
        this.ticker_end = 0;
        this.ticker_init = 0;
        this.ticker_start = 0;
    }

    private void tickerStart() {
        XBrowser.setThread(this);
    }

    private void tickerStop() {
        tickerReset();
        XBrowser.endThread();
    }

    private void tickerMove() {
        int i = this.ticker_init;
        if (i <= 0) {
            return;
        }
        int i2 = this.ticker_end;
        if (i2 == i) {
            if (!this.ticker_sleep) {
                this.ticker_sleep = true;
                return;
            }
            this.ticker_sleep = false;
        }
        int i3 = i2 + 1;
        this.ticker_end = i3;
        if (i3 >= this.contentsName.length()) {
            this.ticker_end = this.ticker_init;
            this.ticker_start = 0;
            return;
        }
        this.ticker_start++;
        int i4 = Toolkit.DEFAULT_FONT.stringWidth(this.contentsName.substring(this.ticker_start, this.ticker_end + 1));
        if (i4 > line_width) {
            this.ticker_start++;
        }
    }

    private void iconAni() {
        if (this.isAni) {
            this.aniStep = (this.aniStep + 1) % 2;
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        while (true) {
            if (this.ticker_init > 0 || this.isAni) {
                iconAni();
                tickerMove();
                repaintTicker();
                try {
                    Thread.sleep(700L);
                } catch (Exception e) {
                }
            } else {
                return;
            }
        }
    }

    private int getSize() {
        return XBrowser.appdb.size();
    }

    private String getItemName(int i) {
        return XBrowser.appdb.elementAt(i).name.trim();
    }

    static {
        int i = JAMGraphic.frame_title_height + JAMGraphic.frame_subtitle_height;
        icon_top = i;
        int i2 = (XDisplay.height2 - 3) - JAMGraphic.button_height;
        icon_bottom = i2;
        int i3 = (XDisplay.width - 3) - JAMGraphic.scroll_width;
        icon_right = i3;
        int i4 = (i3 - 3) / (JAMGraphic.icon_back_size + JAMGraphic.icon_space);
        icon_column = i4;
        int i5 = (i2 - i) / (JAMGraphic.icon_back_size + JAMGraphic.icon_space);
        icon_row = i5;
        int i6 = i4 * i5;
        icon_count = i6;
        icon_indent = (JAMGraphic.icon_back_size - 23) / 2;
        icon_select_indent = (JAMGraphic.icon_select_size - 36) / 2;
        icon_xy = (int[][]) Array.newInstance((Class<?>) int.class, i6, 2);
        select_xy = (int[][]) Array.newInstance((Class<?>) int.class, i6, 2);
    }
}
