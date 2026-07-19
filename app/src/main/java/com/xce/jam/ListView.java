package com.xce.jam;

import com.skt.m.Graphics2D;
import com.xce.lcdui.Toolkit;
import com.xce.lcdui.XDisplay;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/* JADX INFO: loaded from: classes.dex */
public class ListView extends ItemView implements Runnable {
    public static final int TYPE_DELETE = 3;
    public static final int TYPE_LIST = 1;
    public static final int TYPE_MENU = 2;
    public static final int TYPE_SORT = 4;
    private static int frame_top;
    private static int line_count;
    private static int top_space;
    private Graphics2D g2d;
    private int lastIndex;
    private String[] list;
    private int scroll_height;
    private int scrollbar_height;
    private int selectedIndex;
    private int startIndex;
    private int step;
    private int ticker_end;
    private int ticker_init;
    private int ticker_start;
    private String title;
    private int type;
    private static int text_space = 15;
    private static int line_width = JAMGraphic.line_width - text_space;
    private static int select_width = (((JAMGraphic.frame_right - JAMGraphic.frame_left) - JAMGraphic.scroll_width) - 23) - 12;
    private Image leftButton = null;
    private Image rightButton = null;

    @Override // com.xce.jam.ItemView
    public void init(Canvas canvas) {
        this.canvas = canvas;
        this.startIndex = 0;
        this.selectedIndex = 0;
        this.lastIndex = 0;
    }

    @Override // com.xce.jam.ItemView
    public void show() {
        if (this.selectedIndex >= XBrowser.appdb.appList.size()) {
            this.selectedIndex--;
        }
        repaintAll();
        tickerSet();
    }

    @Override // com.xce.jam.ItemView
    public void hide() {
        tickerStop();
    }

    public void setData(String[] as) {
        this.list = as;
        paintInit();
    }

    public void setData(String s, String[] as) {
        this.title = s;
        setData(as);
    }

    public void setIndex(int i) {
        if (i < this.list.length) {
            this.selectedIndex = i;
            int i2 = line_count;
            if (i >= i2) {
                this.startIndex = (i - i2) + 1;
            } else {
                this.startIndex = 0;
            }
        }
    }

    public void setTitle(String s) {
        this.title = s;
        paintInit();
    }

    public void setType(int i) {
        this.type = i;
        if (i == 1) {
            this.leftButton = JAMGraphic.getImg_button_menu();
            this.rightButton = JAMGraphic.getImg_button_run();
            return;
        }
        if (i == 2) {
            this.leftButton = JAMGraphic.getImg_button_cancel();
            this.rightButton = JAMGraphic.getImg_button_ok();
        } else if (i == 3) {
            this.leftButton = JAMGraphic.getImg_button_cancel();
            this.rightButton = JAMGraphic.getImg_button_delete();
        } else if (i == 4) {
            this.leftButton = JAMGraphic.getImg_button_cancel();
            this.rightButton = JAMGraphic.getImg_button_ok();
        }
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
        if (this.selectedIndex > 0) {
            tickerReset();
            int i = this.selectedIndex;
            this.lastIndex = i;
            int i2 = i - 1;
            this.selectedIndex = i2;
            int i3 = this.startIndex;
            if (i2 < i3) {
                this.startIndex = i3 - 1;
                tickerSet();
                repaintFrame();
            } else {
                tickerSet();
                repaintMove();
            }
        }
    }

    private void keyDown() {
        if (this.list.length > 0 && this.selectedIndex < r0.length - 1) {
            tickerReset();
            int i = this.selectedIndex;
            this.lastIndex = i;
            int i2 = i + 1;
            this.selectedIndex = i2;
            int i3 = this.startIndex;
            if (i2 >= line_count + i3) {
                this.startIndex = i3 + 1;
                tickerSet();
                repaintFrame();
            } else {
                tickerSet();
                repaintMove();
            }
        }
    }

    private void paintInit() {
        if (this.title != null) {
            frame_top = JAMGraphic.frame_top + Toolkit.FONT_HEIGHT + 3;
        } else {
            frame_top = JAMGraphic.frame_top;
        }
        line_count = ((((JAMGraphic.frame_bottom - frame_top) - JAMGraphic.list_select_height) - (JAMGraphic.list_space * 2)) / JAMGraphic.line_height) + 1;
        top_space = (((JAMGraphic.frame_bottom - frame_top) - (JAMGraphic.line_height * (line_count - 1))) - JAMGraphic.list_select_height) / 2;
        if (this.list.length > 0) {
            int i = JAMGraphic.list_select_height + ((line_count - 1) * JAMGraphic.line_height);
            this.scrollbar_height = i;
            String[] strArr = this.list;
            int length = (i - 6) / strArr.length;
            this.step = length;
            this.scroll_height = (((line_count * length) + i) - 6) - (strArr.length * length);
            return;
        }
        this.step = 0;
        this.scroll_height = 0;
    }

    @Override // com.xce.jam.ItemView
    public void paint(Graphics g) {
        this.g2d = Graphics2D.getGraphics2D(g);
        if (this.list.length <= 0) {
            paintBackground(g);
            paintEmpty(g);
            return;
        }
        int i = g.getClipHeight();
        if (i <= JAMGraphic.list_select_height) {
            paintSelectedLine(g);
        } else if (i <= JAMGraphic.list_select_height + JAMGraphic.line_height) {
            paintLastLine(g);
            paintSelectedLine(g);
        } else if (i <= JAMGraphic.frame_bottom - frame_top) {
            paintFrame(g);
            paintSubtitle(g);
            paintScrollbar(g);
            paintAllList(g);
        } else {
            paintBackground(g);
            paintSubtitle(g);
            paintScrollbar(g);
            paintAllList(g);
        }
        g.setColor(0);
    }

    private void paintEmpty(Graphics g) {
        int i = frame_top + top_space;
        int j = XDisplay.width / 2;
        g.setColor(0);
        g.drawString(XBrowser.JAM_RES.CONTENTS_EMPTY, j, i, 17);
    }

    private void paintBackground(Graphics g) {
        JAMGraphic.paintBack(g);
        int i = this.type;
        if (i == 1) {
            g.drawImage(JAMGraphic.getImg_text_listicon(), XDisplay.width / 2, JAMGraphic.frame_title_height - 2, 33);
        } else if (i == 4) {
            g.drawImage(JAMGraphic.getImg_text_sort(), XDisplay.width / 2, JAMGraphic.frame_title_height - 2, 33);
        } else if (i == 3) {
            g.drawImage(JAMGraphic.getImg_text_delete(), XDisplay.width / 2, JAMGraphic.frame_title_height - 2, 33);
        }
        g.drawImage(this.leftButton, JAMGraphic.frame_left, JAMGraphic.frame_bottom + 1, 20);
        g.drawImage(this.rightButton, JAMGraphic.frame_right + 1, JAMGraphic.frame_bottom + 1, 24);
    }

    private void paintFrame(Graphics g) {
        this.g2d.drawImage(JAMGraphic.frame_left, frame_top, JAMGraphic.getImg_frame_back(), 0, 0, (JAMGraphic.frame_right - JAMGraphic.frame_left) + 1, (JAMGraphic.frame_bottom - frame_top) + 1, 0);
    }

    private void paintSubtitle(Graphics g) {
        if (this.title == null) {
            return;
        }
        int i = JAMGraphic.frame_top;
        this.g2d.drawImage(3, i, JAMGraphic.getImg_frame_subtitle(), 0, 0, XDisplay.width - 6, Toolkit.FONT_HEIGHT, 0);
        g.setColor(0);
        g.drawString(this.title, XDisplay.width / 2, i, 17);
        int i2 = i + Toolkit.FONT_HEIGHT;
        g.setColor(JAMGraphic.color_border_dark);
        g.drawLine(2, i2, XDisplay.width - 3, i2);
        g.drawLine(2, i2 + 2, XDisplay.width - 3, i2 + 2);
        g.setColor(JAMGraphic.color_border_light);
        g.drawLine(2, i2 + 1, XDisplay.width - 3, i2 + 1);
    }

    private void paintLine(Graphics g, int i, int j, int k) {
        int i2 = this.startIndex;
        if (k + i2 < this.list.length) {
            if (i2 + k < 9) {
                this.g2d.drawImage(i + 3, ((Toolkit.FONT_HEIGHT / 2) + j) - 4, JAMGraphic.getImg_text_number(), (this.startIndex + k) * 7, 0, 7, 9, 0);
            } else {
                g.drawImage(JAMGraphic.getImg_list_icon(), i + 1, (Toolkit.FONT_HEIGHT / 2) + j, 6);
            }
            g.setColor(0);
            g.drawString(JAMUtil.getLineString(this.list[this.startIndex + k], line_width), text_space + i, j, 20);
        }
        int j2 = j + Toolkit.FONT_HEIGHT;
        g.setColor(JAMGraphic.color_border_light);
        g.drawLine(i, j2, JAMGraphic.line_width + i, j2);
        int j3 = j2 + 1;
        g.setColor(JAMGraphic.color_border_dark);
        g.drawLine(i, j3, JAMGraphic.line_width + i, j3);
    }

    private void paintAllList(Graphics g) {
        int i = frame_top + top_space;
        int j = JAMGraphic.frame_left + JAMGraphic.line_space;
        if (this.selectedIndex - this.startIndex != 0) {
            i += 2;
        }
        for (int k = 0; k < line_count; k++) {
            if (this.startIndex + k != this.selectedIndex) {
                paintLine(g, j, i, k);
                i += JAMGraphic.line_height;
            } else {
                i += JAMGraphic.list_select_height;
                if (k != 0) {
                    i -= 2;
                }
            }
        }
        paintSelectedLine(g);
    }

    private void paintSelectedLine(Graphics g) {
        int i = JAMGraphic.frame_left - 1;
        int j = frame_top + top_space + ((this.selectedIndex - this.startIndex) * JAMGraphic.line_height);
        g.drawImage(JAMGraphic.getImg_list_select(), i, j, 20);
        int i2 = i + 5;
        g.drawImage(JAMGraphic.getContentsIcon(this.selectedIndex, false), i2, (JAMGraphic.list_select_height / 2) + j, 6);
        int i3 = i2 + 28;
        int j2 = j + ((JAMGraphic.list_select_height / 2) - (Toolkit.FONT_HEIGHT / 2));
        g.setColor(0);
        if (this.ticker_init > 0) {
            String str = this.list[this.selectedIndex];
            int i4 = this.ticker_start;
            g.drawSubstring(str, i4, (this.ticker_end - i4) + 1, i3, j2, 20);
            return;
        }
        g.drawString(JAMUtil.getLineString(this.list[this.selectedIndex], select_width), i3, j2, 20);
    }

    private void paintLastLine(Graphics g) {
        int j;
        int i = JAMGraphic.frame_left;
        int j2 = frame_top + top_space + ((this.selectedIndex - this.startIndex) * JAMGraphic.line_height);
        if (this.lastIndex < this.selectedIndex) {
            j = j2 - JAMGraphic.line_height;
        } else {
            j = j2 + JAMGraphic.list_select_height;
        }
        this.g2d.drawImage(i, j, JAMGraphic.getImg_frame_back(), 0, j - frame_top, ((JAMGraphic.frame_right - JAMGraphic.frame_left) - JAMGraphic.scroll_width) + 1, JAMGraphic.line_height, 0);
        int i2 = i + JAMGraphic.line_space;
        int i3 = this.lastIndex;
        if (i3 < this.selectedIndex) {
            j += 2;
        }
        paintLine(g, i2, j, i3 - this.startIndex);
        if (this.lastIndex - this.startIndex > 0) {
            g.setColor(JAMGraphic.color_border_dark);
            g.drawLine(i2, j - 1, JAMGraphic.line_width + i2, j - 1);
            g.setColor(JAMGraphic.color_border_light);
            g.drawLine(i2, j - 2, JAMGraphic.line_width + i2, j - 2);
        }
        g.setColor(JAMGraphic.color_border_dark);
        g.drawLine(JAMGraphic.frame_left - 1, j - 1, JAMGraphic.frame_left - 1, JAMGraphic.line_height + j);
        g.drawLine((JAMGraphic.frame_right - JAMGraphic.scroll_width) + 1, j - 1, (JAMGraphic.frame_right - JAMGraphic.scroll_width) + 1, JAMGraphic.line_height + j);
    }

    private void paintScrollbar(Graphics g) {
        int i = frame_top + top_space;
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
        String[] strArr = this.list;
        int length = strArr.length;
        int i2 = line_count;
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
        this.canvas.repaint(JAMGraphic.frame_left, frame_top, JAMGraphic.frame_right - JAMGraphic.frame_left, JAMGraphic.frame_bottom - frame_top);
        this.canvas.serviceRepaints();
    }

    private void repaintMove() {
        int i = this.selectedIndex;
        int i2 = this.lastIndex;
        if (i >= i2) {
            i = i2;
        }
        int j = frame_top + top_space + ((i - this.startIndex) * JAMGraphic.line_height);
        int k = JAMGraphic.list_select_height + JAMGraphic.line_height;
        this.canvas.repaint(JAMGraphic.frame_left - 1, j, (JAMGraphic.frame_right - JAMGraphic.frame_left) + 1, k);
        this.canvas.serviceRepaints();
    }

    private void repaintTicker() {
        int i = frame_top + top_space + ((this.selectedIndex - this.startIndex) * JAMGraphic.line_height);
        this.canvas.repaint(JAMGraphic.frame_left - 1, i, (JAMGraphic.frame_right - JAMGraphic.frame_left) - JAMGraphic.scroll_width, JAMGraphic.list_select_height);
        this.canvas.serviceRepaints();
    }

    private void tickerSet() {
        if (this.list.length <= 0 || this.type != 1) {
            return;
        }
        this.ticker_init = 0;
        int i = Toolkit.DEFAULT_FONT.stringWidth(this.list[this.selectedIndex]);
        int i2 = select_width;
        if (i > i2) {
            this.ticker_start = 0;
            int lineIndex = JAMUtil.getLineIndex(this.list[this.selectedIndex], i2);
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
        if (i >= this.list[this.selectedIndex].length()) {
            this.ticker_end = this.ticker_init;
            this.ticker_start = 0;
            return;
        }
        this.ticker_start++;
        int i2 = Toolkit.DEFAULT_FONT.stringWidth(this.list[this.selectedIndex].substring(this.ticker_start, this.ticker_end + 1));
        if (i2 > select_width) {
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
}
