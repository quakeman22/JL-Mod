package com.xce.jam;

import com.skt.m.Graphics2D;
import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* JADX INFO: loaded from: classes.dex */
public class AlertView extends ItemView implements Runnable {
    public static final int TYPE_DOWNLOAD = 4;
    public static final int TYPE_INFO = 1;
    public static final int TYPE_MENU = 2;
    public static final int TYPE_OPTION = 3;
    private Image appIcon;
    private int appIndex = -1;
    private String appName;
    private int frame_top;
    private Graphics2D g2d;
    private boolean isList;
    private int lastIndex;
    private int line_count;
    private int line_width;
    private String[] list;
    private int name_height;
    private int name_width;
    private int scroll_height;
    private int scrollbar_height;
    private int selectedIndex;
    private int startIndex;
    private int step;
    private int ticker_end;
    private int ticker_init;
    private int ticker_start;
    private String title;
    private int title_height;
    private int top_space;
    private int type;

    @Override // com.xce.jam.ItemView
    public void init(Canvas canvas) {
        this.canvas = canvas;
        this.startIndex = 0;
        this.selectedIndex = 0;
        this.lastIndex = 0;
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

    public void setData(int i, int j, String s, String s1) {
        this.isList = false;
        this.type = i;
        this.appIndex = j;
        this.title = s;
        int i2 = JAMGraphic.line_width;
        this.line_width = i2;
        this.list = JAMUtil.getLines(s1, i2);
        paintInit();
        this.lastIndex = 0;
        this.startIndex = 0;
        this.selectedIndex = 0;
    }

    public void setData(int i, int j, String s, String[] as) {
        this.isList = true;
        this.type = i;
        this.appIndex = j;
        this.title = s;
        this.list = as;
        this.line_width = JAMGraphic.line_width - 15;
        paintInit();
        this.lastIndex = 0;
        this.startIndex = 0;
        this.selectedIndex = 0;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    @Override // com.xce.jam.ItemView
    public void keyPressed(int i) {
        if (i == 141) {
            XBrowser.playSound(5);
            keyUp();
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
        if (this.isList) {
            int i = this.selectedIndex;
            if (i > 0) {
                this.lastIndex = i;
                int i2 = i - 1;
                this.selectedIndex = i2;
                int i3 = this.startIndex;
                if (i2 < i3) {
                    this.startIndex = i3 - 1;
                    repaintFrame();
                    return;
                } else {
                    repaintMove();
                    return;
                }
            }
            return;
        }
        int i4 = this.startIndex;
        if (i4 > 0) {
            this.startIndex = i4 - 1;
            repaintFrame();
        }
    }

    private void keyDown() {
        int i;
        if (this.isList) {
            if (this.list.length > 0 && (i = this.selectedIndex) < r0.length - 1) {
                this.lastIndex = i;
                int i2 = i + 1;
                this.selectedIndex = i2;
                int i3 = this.startIndex;
                if (i2 >= this.line_count + i3) {
                    this.startIndex = i3 + 1;
                    repaintFrame();
                    return;
                } else {
                    repaintMove();
                    return;
                }
            }
            return;
        }
        int i4 = this.startIndex;
        if (this.line_count + i4 < this.list.length) {
            this.startIndex = i4 + 1;
            repaintFrame();
        }
    }

    private void paintInit() {
        if (this.appIndex >= 0) {
            this.name_height = JAMGraphic.frame_name_height;
            this.appName = getItemName(this.appIndex);
            this.appIcon = JAMGraphic.getContentsIcon(this.appIndex, false);
        } else {
            this.name_height = 0;
        }
        if (this.title != null) {
            this.title_height = Toolkit.FONT_HEIGHT + 3;
        } else {
            this.title_height = 0;
        }
        this.frame_top = JAMGraphic.frame_top + this.name_height + this.title_height;
        this.line_count = ((JAMGraphic.frame_bottom - this.frame_top) - (JAMGraphic.list_space * 2)) / JAMGraphic.line_height;
        this.top_space = ((JAMGraphic.frame_bottom - this.frame_top) - (JAMGraphic.line_height * this.line_count)) / 2;
        this.name_width = ((XDisplay.width - 10) - 23) - 6;
        int i = this.line_count * JAMGraphic.line_height;
        this.scrollbar_height = i;
        String[] strArr = this.list;
        int length = (i - 6) / strArr.length;
        this.step = length;
        this.scroll_height = (((this.line_count * length) + i) - 6) - (strArr.length * length);
    }

    @Override // com.xce.jam.ItemView
    public void paint(Graphics g) {
        this.g2d = Graphics2D.getGraphics2D(g);
        int i = g.getClipHeight();
        if (this.appIndex >= 0 && i <= this.name_height && g.getClipY() < JAMGraphic.frame_title_height + this.name_height) {
            paintName(g);
        } else if (this.isList && this.line_count > 2 && i <= JAMGraphic.line_height * 2) {
            paintLastLine(g);
            paintSelectedLine(g);
        } else if (i <= JAMGraphic.frame_bottom - this.frame_top) {
            paintFrame(g);
            paintScrollbar(g);
            paintAllList(g);
        } else {
            paintBackground(g);
            paintScrollbar(g);
            paintAllList(g);
            if (this.appIndex >= 0) {
                paintName(g);
            }
            if (this.title != null) {
                paintSubtitle(g);
            }
        }
        g.setColor(0);
    }

    private void paintBackground(Graphics g) {
        JAMGraphic.paintBack(g);
        int i = this.type;
        if (i == 2) {
            g.drawImage(JAMGraphic.getImg_text_menu(), XDisplay.width / 2, JAMGraphic.frame_title_height - 2, 33);
        } else if (i == 3) {
            g.drawImage(JAMGraphic.getImg_text_control(), XDisplay.width / 2, JAMGraphic.frame_title_height - 2, 33);
        } else if (i == 4) {
            g.drawImage(JAMGraphic.getImg_text_down(), XDisplay.width / 2, JAMGraphic.frame_title_height - 2, 33);
        }
        if (this.leftCommand >= 0) {
            g.drawImage(JAMGraphic.getImg_button_cancel(), JAMGraphic.frame_left, JAMGraphic.frame_bottom + 1, 20);
        }
        if (this.rightCommand >= 0) {
            g.drawImage(JAMGraphic.getImg_button_ok(), JAMGraphic.frame_right + 1, JAMGraphic.frame_bottom + 1, 24);
        }
    }

    private void paintName(Graphics g) {
        int j = JAMGraphic.frame_title_height;
        g.drawImage(JAMGraphic.getImg_menu_title(), 3, j, 20);
        int i = 3 + 5;
        g.drawImage(this.appIcon, i, (JAMGraphic.frame_name_height / 2) + j, 6);
        int i2 = i + 28;
        int j2 = j + ((JAMGraphic.frame_name_height / 2) - (Toolkit.FONT_HEIGHT / 2));
        g.setColor(0);
        if (this.ticker_init > 0) {
            String str = this.appName;
            int i3 = this.ticker_start;
            g.drawSubstring(str, i3, (this.ticker_end - i3) + 1, i2, j2, 20);
            return;
        }
        g.drawString(JAMUtil.getLineString(this.appName, this.name_width), i2, j2, 20);
    }

    private void paintSubtitle(Graphics g) {
        int i = JAMGraphic.frame_top + this.name_height;
        this.g2d.drawImage(3, i, JAMGraphic.getImg_frame_subtitle(), 0, 0, XDisplay.width - 6, Toolkit.FONT_HEIGHT, 0);
        g.setColor(0);
        g.drawString(this.title, XDisplay.width / 2, i, 17);
        int i2 = i + (this.title_height - 3);
        g.setColor(JAMGraphic.color_border_dark);
        g.drawLine(2, i2, XDisplay.width - 3, i2);
        g.drawLine(2, i2 + 2, XDisplay.width - 3, i2 + 2);
        g.setColor(JAMGraphic.color_border_light);
        g.drawLine(2, i2 + 1, XDisplay.width - 3, i2 + 1);
    }

    private void paintFrame(Graphics g) {
        this.g2d.drawImage(JAMGraphic.frame_left, this.frame_top, JAMGraphic.getImg_frame_back(), 0, this.frame_top - JAMGraphic.frame_top, (JAMGraphic.frame_right - JAMGraphic.frame_left) + 1, (JAMGraphic.frame_bottom - this.frame_top) + 1, 0);
    }

    private void paintLine(Graphics g, int i, int j, int k) {
        int i2 = i;
        g.setColor(JAMGraphic.color_border_light);
        g.drawLine(i2, Toolkit.FONT_HEIGHT + j, JAMGraphic.line_width + i2, Toolkit.FONT_HEIGHT + j);
        g.setColor(JAMGraphic.color_border_dark);
        g.drawLine(i2, Toolkit.FONT_HEIGHT + j + 1, JAMGraphic.line_width + i2, Toolkit.FONT_HEIGHT + j + 1);
        int i3 = this.startIndex;
        if (k + i3 < this.list.length) {
            if (this.isList) {
                if (k + i3 == this.selectedIndex) {
                    g.drawImage(JAMGraphic.getImg_arrow_right(), i2 + 1, (Toolkit.FONT_HEIGHT / 2) + j, 6);
                    i2 += 7;
                }
                if (k + this.startIndex < 9) {
                    this.g2d.drawImage(i2 + 3, ((Toolkit.FONT_HEIGHT / 2) + j) - 4, JAMGraphic.getImg_text_number(), (k + this.startIndex) * 7, 0, 7, 9, 0);
                } else {
                    g.drawImage(JAMGraphic.getImg_list_icon(), i2 + 1, (Toolkit.FONT_HEIGHT / 2) + j, 6);
                }
                g.setColor(0);
                g.drawString(JAMUtil.getLineString(this.list[k + this.startIndex], this.line_width), i2 + 6 + 7, j, 20);
                return;
            }
            g.setColor(0);
            g.drawString(this.list[k + this.startIndex], i2, j, 20);
        }
    }

    private void paintAllList(Graphics g) {
        int i = this.frame_top + this.top_space;
        int j = JAMGraphic.frame_left + JAMGraphic.line_space;
        for (int k = 0; k < this.line_count; k++) {
            paintLine(g, j, i, k);
            i += JAMGraphic.line_height;
        }
    }

    private void paintSelectedLine(Graphics g) {
        int i = this.selectedIndex - this.startIndex;
        int j = this.frame_top + this.top_space + (JAMGraphic.line_height * i);
        int k = JAMGraphic.frame_left + JAMGraphic.line_space;
        this.g2d.drawImage(JAMGraphic.frame_left, j, JAMGraphic.getImg_frame_back(), 0, j - JAMGraphic.frame_top, (JAMGraphic.frame_right - JAMGraphic.frame_left) - JAMGraphic.scroll_width, JAMGraphic.line_height, 0);
        paintLine(g, k, j, i);
    }

    private void paintLastLine(Graphics g) {
        int i = this.lastIndex - this.startIndex;
        int j = this.frame_top + this.top_space + (JAMGraphic.line_height * i);
        int k = JAMGraphic.frame_left + JAMGraphic.line_space;
        this.g2d.drawImage(JAMGraphic.frame_left, j, JAMGraphic.getImg_frame_back(), 0, j - JAMGraphic.frame_top, (JAMGraphic.frame_right - JAMGraphic.frame_left) - JAMGraphic.scroll_width, JAMGraphic.line_height, 0);
        paintLine(g, k, j, i);
    }

    private void paintScrollbar(Graphics g) {
        int i = this.frame_top + this.top_space;
        int j = (JAMGraphic.frame_right - JAMGraphic.scroll_width) + 1;
        g.setColor(JAMGraphic.color_border_dark);
        g.drawLine(j, i, JAMGraphic.scroll_width + j, i);
        g.drawLine(j, i, j, (this.scrollbar_height + i) - 1);
        g.drawLine(j, (this.scrollbar_height + i) - 1, JAMGraphic.scroll_width + j, (this.scrollbar_height + i) - 1);
        g.drawRect(j + 2, i + 2, JAMGraphic.scroll_width - 2, (this.scrollbar_height - 4) - 1);
        g.setColor(JAMGraphic.color_border_light);
        g.drawLine(j + 1, i + 1, JAMGraphic.scroll_width + j, i + 1);
        g.drawLine(j + 1, i + 1, j + 1, ((this.scrollbar_height + i) - 1) - 1);
        g.drawLine(j + 1, ((this.scrollbar_height + i) - 1) - 1, JAMGraphic.scroll_width + j, ((this.scrollbar_height + i) - 1) - 1);
        this.g2d.drawImage(j + 3, i + 3, JAMGraphic.getImg_frame_scroll(), 0, 0, JAMGraphic.scroll_width - 3, (this.scrollbar_height - 1) - 5, 0);
        String[] strArr = this.list;
        int length = strArr.length;
        int i2 = this.line_count;
        if (length > i2) {
            int i3 = this.startIndex;
            int k = i + 3 + (this.step * i3);
            if (i3 + i2 == strArr.length) {
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

    private void repaintFrame() {
        this.canvas.repaint(JAMGraphic.frame_left, this.frame_top, JAMGraphic.frame_right - JAMGraphic.frame_left, JAMGraphic.frame_bottom - this.frame_top);
        this.canvas.serviceRepaints();
    }

    private void repaintMove() {
        int i = this.selectedIndex;
        int i2 = this.lastIndex;
        if (i >= i2) {
            i = i2;
        }
        int j = this.frame_top + this.top_space + ((i - this.startIndex) * JAMGraphic.line_height);
        int k = JAMGraphic.line_height * 2;
        this.canvas.repaint(JAMGraphic.frame_left, j, (JAMGraphic.frame_right - JAMGraphic.frame_left) + 1, k);
        this.canvas.serviceRepaints();
    }

    private void repaintTicker() {
        int i = JAMGraphic.frame_title_height;
        this.canvas.repaint(0, i, XDisplay.width, this.name_height);
        this.canvas.serviceRepaints();
    }

    private void tickerSet() {
        if (this.appIndex < 0) {
            return;
        }
        this.ticker_init = 0;
        int i = Toolkit.DEFAULT_FONT.stringWidth(this.appName);
        int i2 = this.name_width;
        if (i > i2) {
            this.ticker_start = 0;
            int lineIndex = JAMUtil.getLineIndex(this.appName, i2);
            this.ticker_end = lineIndex;
            this.ticker_init = lineIndex;
            tickerStart();
            return;
        }
        tickerStop();
    }

    private void tickerReset() {
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
        if (this.ticker_end == this.ticker_init) {
            try {
                Thread.sleep(1500L);
            } catch (Exception e) {
            }
        }
        int i = this.ticker_end + 1;
        this.ticker_end = i;
        if (i >= this.appName.length()) {
            this.ticker_end = this.ticker_init;
            this.ticker_start = 0;
            return;
        }
        this.ticker_start++;
        int i2 = Toolkit.DEFAULT_FONT.stringWidth(this.appName.substring(this.ticker_start, this.ticker_end + 1));
        if (i2 > this.name_width) {
            this.ticker_start++;
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        while (this.ticker_init > 0) {
            tickerMove();
            repaintTicker();
            try {
                Thread.sleep(700L);
            } catch (Exception e) {
            }
        }
    }

    private String getItemName(int i) {
        return XBrowser.appdb.elementAt(i).name.trim();
    }
}
