package com.xce.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;

/* JADX INFO: loaded from: classes.dex */
public class XTextFieldT {
    private static final int freeSpace = 3;
    private static final TextComponentHandler imListener = TextComponentHandler.getTextComponentHandler();
    private int backColor;
    private int borderColor;
    Canvas canvas;
    private int caretLine;
    private int caretPos;
    private int constraints;
    private int curX;
    private int curY;
    char[] echo;
    private boolean focus;
    private int focusBackColor;
    private int focusBorderColor;
    private int lineSize;
    private int maxLine;
    private int maxSize;
    private int minCharsPerLine;
    private int scrollBackColor;
    private int scrollBarColor;
    private int startLine;
    char[] text;
    private int textColor;
    private int textCount;
    private int viewPortHeight;
    private int viewPortWidth;
    private int visHeight;
    private final InputMethodImpl imi = new InputMethodImpl(this);
    private int[] lineIndex = new int[4];
    private int lineCount = 1;
    private int x = 0;
    private int y = 0;
    private boolean isCaretVisible = true;
    private boolean enableUpDown = false;
    private int scrollX = 0;
    private int scrollY = 0;

    class InputMethodImpl implements TextComponent {
        XTextFieldT xtft;

        @Override // com.xce.lcdui.TextComponent
        public int getCaretPosition() {
            return this.xtft.getCaretPosition();
        }

        @Override // com.xce.lcdui.TextComponent
        public int getConstraints() {
            return this.xtft.getConstraints();
        }

        @Override // com.xce.lcdui.TextComponent
        public int getMaxSize() {
            return this.xtft.getMaxSize();
        }

        @Override // com.xce.lcdui.TextComponent
        public int size() {
            return this.xtft.size();
        }

        @Override // com.xce.lcdui.TextComponent
        public void insert(char c) {
            this.xtft.insertChar(c);
        }

        @Override // com.xce.lcdui.TextComponent
        public void delete() {
            this.xtft.deleteChar();
        }

        @Override // com.xce.lcdui.TextComponent
        public void clear() {
            this.xtft.setString("");
        }

        @Override // com.xce.lcdui.TextComponent
        public void replace(char c) {
            this.xtft.replaceChar(c, XTextFieldT.this.caretPos - 1);
        }

        @Override // com.xce.lcdui.TextComponent
        public void moveCursor(int i) {
            int j = XDisplay.getGameAction(i);
            if (XTextFieldT.this.textCount > 0) {
                if (j != 1) {
                    if (j == 2) {
                        XTextFieldT.this.moveLeftCaret();
                    } else if (j == 5) {
                        XTextFieldT.this.moveRightCaret();
                    } else if (j == 6 && XTextFieldT.this.enableUpDown) {
                        XTextFieldT.this.moveDownCaret();
                    }
                } else if (XTextFieldT.this.enableUpDown) {
                    XTextFieldT.this.moveUpCaret();
                }
            }
            this.xtft.repaint();
            XTextFieldT.imListener.clear();
        }

        @Override // com.xce.lcdui.TextComponent
        public void setCaretPosition(int i) {
            if (i < 0 || i > XTextFieldT.this.textCount) {
                return;
            }
            XTextFieldT.this.caretPos = i;
            int j = 0;
            while (true) {
                if (j >= XTextFieldT.this.lineCount) {
                    break;
                }
                if (XTextFieldT.this.caretPos > XTextFieldT.this.lineIndex[j]) {
                    j++;
                } else {
                    XTextFieldT.this.caretLine = j;
                    break;
                }
            }
            XTextFieldT.this.updateCaretLine();
        }

        @Override // com.xce.lcdui.TextComponent
        public void setCaretVisible(boolean flag) {
            if (flag == XTextFieldT.this.isCaretVisible) {
                return;
            }
            XTextFieldT.this.isCaretVisible = flag;
            this.xtft.repaintCaret();
        }

        @Override // com.xce.lcdui.TextComponent
        public void repaint() {
            this.xtft.repaint();
        }

        @Override // com.xce.lcdui.TextComponent
        public void repaintIM() {
            XTextFieldT.this.canvas.repaintIM();
        }

        InputMethodImpl(XTextFieldT xtextfieldt1) {
            this.xtft = xtextfieldt1;
        }
    }

    public XTextFieldT(String s, int i, int j, Canvas canvas1) {
        s = s == null ? new String() : s;
        if (i <= 0 || s.length() > i) {
            throw new IllegalArgumentException();
        }
        this.canvas = canvas1;
        char[] ac = new char[i];
        char[] charArray = s.toCharArray();
        this.text = charArray;
        int length = charArray.length;
        this.textCount = length;
        this.caretPos = length;
        System.arraycopy(charArray, 0, ac, 0, length);
        this.text = ac;
        setMaxSize(i);
        setConstraints(j);
        setBounds(0, 0, canvas1.getWidth(), canvas1.getHeight());
        setBoxColor(16777215, 0, Toolkit.lt_gray, 0);
        setScrollColor(Toolkit.dk_gray, 16777215);
        setTextColor(0);
        repaint();
    }

    public void keyPressed(int i) {
        imListener.keyPressed(i);
    }

    public void keyReleased(int i) {
        imListener.keyReleased(i);
    }

    public void keyRepeated(int i) {
        imListener.keyRepeated(i);
    }

    public void paint(Graphics g) {
        if (g.getClipWidth() == 1) {
            paintCaret(g);
            return;
        }
        if (this.scrollX > 0) {
            g.setClip(this.x, this.y, this.viewPortWidth + 4, this.viewPortHeight);
        } else {
            g.setClip(this.x, this.y, this.viewPortWidth, this.viewPortHeight);
        }
        g.setColor(16777215);
        g.fillRect(this.x, this.y, this.viewPortWidth, this.viewPortHeight);
        g.setColor(0);
        paintBorder(g);
        paintChars(g);
        updateCaret();
        paintCaret(g);
        if (this.scrollX > 0) {
            paintScrollBar(g);
        }
    }

    private void paintCaret(Graphics g) {
        if (this.isCaretVisible) {
            g.setColor(0);
        } else {
            g.setColor(16777215);
        }
        int i = this.curX;
        int i2 = this.curY;
        g.drawLine(i, i2, i, Toolkit.FONT_HEIGHT + i2);
        g.setColor(0);
    }

    private void paintBorder(Graphics g) {
        if (this.focus) {
            g.setColor(this.focusBackColor);
            g.fillRect(this.x, this.y, this.viewPortWidth - 1, this.viewPortHeight - 1);
            g.setColor(this.focusBorderColor);
            g.drawRect(this.x, this.y, this.viewPortWidth - 1, this.viewPortHeight - 1);
        } else {
            g.setColor(this.backColor);
            g.fillRect(this.x, this.y, this.viewPortWidth - 1, this.viewPortHeight - 1);
            g.setColor(this.borderColor);
            g.drawRect(this.x, this.y, this.viewPortWidth - 1, this.viewPortHeight - 1);
        }
        g.setColor(0);
    }

    /* JADX WARN: Incorrect condition in loop: B:11:0x0026 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void paintChars(javax.microedition.lcdui.Graphics r13) {
        /*
            r12 = this;
            int r0 = r12.textCount
            if (r0 != 0) goto L5
            return
        L5:
            int r0 = r12.x
            int r0 = r0 + 3
            int r1 = r12.y
            int r1 = r1 + 3
            r2 = 0
            r3 = 0
            char[] r4 = r12.echo
            if (r4 == 0) goto L17
            char[] r3 = r12.echo
            r8 = r3
            goto L1a
        L17:
            char[] r3 = r12.text
            r8 = r3
        L1a:
            int r3 = r12.textColor
            r13.setColor(r3)
            int r3 = r12.startLine
            r9 = r1
            r10 = r2
            r11 = r3
        L24:
            int r1 = r12.lineCount
            if (r11 >= r1) goto L5d
            int r1 = r12.maxLine
            if (r10 < r1) goto L2d
            goto L5d
        L2d:
            if (r11 != 0) goto L3e
            r3 = 0
            int[] r1 = r12.lineIndex
            r4 = r1[r11]
            r7 = 20
            r1 = r13
            r2 = r8
            r5 = r0
            r6 = r9
            r1.drawChars(r2, r3, r4, r5, r6, r7)
            goto L55
        L3e:
            int[] r1 = r12.lineIndex
            int r2 = r11 + (-1)
            r3 = r1[r2]
            r2 = r1[r11]
            int r4 = r11 + (-1)
            r1 = r1[r4]
            int r4 = r2 - r1
            r7 = 20
            r1 = r13
            r2 = r8
            r5 = r0
            r6 = r9
            r1.drawChars(r2, r3, r4, r5, r6, r7)
        L55:
            int r1 = com.xce.lcdui.Toolkit.FONT_HEIGHT
            int r9 = r9 + r1
            int r10 = r10 + 1
            int r11 = r11 + 1
            goto L24
        L5d:
            r1 = 0
            r13.setColor(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xce.lcdui.XTextFieldT.paintChars(javax.microedition.lcdui.Graphics):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void repaintCaret() {
        updateCaret();
        this.canvas.repaint(this.curX, this.curY, 1, Toolkit.FONT_HEIGHT + 1);
    }

    public void repaint() {
        if (this.scrollX > 0) {
            this.canvas.repaint(this.x, this.y, this.viewPortWidth + 4, this.viewPortHeight);
        } else {
            this.canvas.repaint(this.x, this.y, this.viewPortWidth, this.viewPortHeight);
        }
    }

    private void paintScrollBar(Graphics g) {
        g.setColor(this.scrollBackColor);
        g.fillRect(this.scrollX, this.y, 4, this.viewPortHeight);
        g.setColor(16777215);
        g.fillRect(this.scrollX + 1, this.y + this.scrollY + 1, 2, this.visHeight);
        if (this.focus) {
            g.setColor(this.focusBorderColor);
        } else {
            g.setColor(this.borderColor);
        }
        g.drawRect(this.scrollX, this.y, 3, this.viewPortHeight - 1);
        g.setColor(0);
    }

    public void insert(char[] ac, int i, int j, int k) {
        if (ac == null) {
            throw new NullPointerException();
        }
        if (i < 0 || j < 0 || i + j > ac.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        int i2 = this.textCount;
        if (i2 + j > this.maxSize) {
            throw new IllegalArgumentException();
        }
        char[] cArr = this.text;
        System.arraycopy(cArr, k, cArr, k + j, i2 - k);
        System.arraycopy(ac, 0, this.text, k, j);
        this.textCount += j;
        int i3 = this.caretPos;
        if (i3 >= k) {
            this.caretPos = i3 + j;
        }
        imListener.clear();
        doLayout(0);
        repaint();
    }

    public void insert(String s, int i) {
        if (s == null) {
            throw new NullPointerException();
        }
        insert(s.toCharArray(), 0, s.length(), i);
    }

    public void setChars(char[] ac, int i, int j) {
        if (ac == null) {
            this.caretPos = 0;
            this.textCount = 0;
        } else {
            if (i < 0 || j < 0 || i + j > ac.length) {
                throw new ArrayIndexOutOfBoundsException();
            }
            if (j <= this.maxSize) {
                System.arraycopy(ac, i, this.text, 0, j);
                this.textCount = j;
                this.caretPos = j;
            } else {
                throw new IllegalArgumentException();
            }
        }
        imListener.clear();
        doLayout(0);
        repaint();
    }

    public void setString(String s) {
        if (s == null || s.length() > this.maxSize) {
            throw new IllegalArgumentException();
        }
        setChars(s.toCharArray(), 0, s.length());
    }

    public void delete(int i, int j) {
        if (i < 0 || j < 0) {
            throw new StringIndexOutOfBoundsException();
        }
        int i2 = i + j;
        int i3 = this.textCount;
        if (i2 > i3) {
            j = i3 - i;
        }
        char[] cArr = this.text;
        System.arraycopy(cArr, i + j, cArr, i, (i3 - i) - j);
        this.textCount -= j;
        this.caretPos -= j;
        imListener.clear();
        doLayout(0);
        repaint();
    }

    public String getString() {
        return String.valueOf(this.text, 0, this.textCount);
    }

    public int getChars(char[] ac) {
        if (ac == null) {
            throw new NullPointerException();
        }
        int length = ac.length;
        int i = this.textCount;
        if (length < i) {
            throw new ArrayIndexOutOfBoundsException();
        }
        System.arraycopy(this.text, 0, ac, 0, i);
        return this.textCount;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void insertChar(char c) {
        int i = this.caretPos;
        int i2 = this.textCount;
        if (i <= i2 && i2 < this.maxSize) {
            char[] cArr = this.text;
            System.arraycopy(cArr, i, cArr, i + 1, i2 - i);
            char[] cArr2 = this.text;
            int i3 = this.caretPos;
            this.caretPos = i3 + 1;
            cArr2[i3] = c;
            this.textCount++;
            doLayout(this.caretLine);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteChar() {
        int i = this.caretPos;
        if (i != 0) {
            char[] cArr = this.text;
            System.arraycopy(cArr, i, cArr, i - 1, this.textCount - i);
            this.textCount--;
            this.caretPos--;
            int i2 = this.caretLine;
            doLayout(i2 != 0 ? i2 - 1 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replaceChar(char c, int i) {
        this.text[i] = c;
        doLayout(this.caretLine);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCaretLine() {
        int i = this.caretLine;
        int i2 = this.lineCount;
        if (i >= i2) {
            this.caretLine = i2 - 1;
        }
        int i3 = this.startLine;
        int i4 = this.startLine;
        int i5 = this.maxLine;
        int i6 = i4 + i5;
        int i7 = this.caretLine;
        if (i6 <= i7) {
            this.startLine = i7 - (i5 - 1);
        } else if (i4 > i7) {
            this.startLine = i7;
        }
        if (i3 != this.startLine) {
            repaint();
        }
        if (this.scrollX > 0) {
            updateScrollBar();
        }
    }

    private void updateCaret() {
        int j;
        int i = this.caretLine;
        int i2 = i != 0 ? this.lineIndex[i - 1] : 0;
        if ((this.constraints & TextField.PASSWORD) > 0) {
            j = Toolkit.DEFAULT_FONT.charsWidth(this.echo, i2, this.caretPos - i2);
        } else {
            j = Toolkit.DEFAULT_FONT.charsWidth(this.text, i2, this.caretPos - i2);
        }
        this.curY = this.y + 3 + (Toolkit.FONT_HEIGHT * (this.caretLine - this.startLine));
        this.curX = ((this.x + 3) + j) - 1;
    }

    public void setConstraints(int i) {
        int j = TextField.CONSTRAINT_MASK & i;
        if (j < 0 || j > 4) {
            throw new IllegalArgumentException();
        }
        this.constraints = i;
        if ((TextField.PASSWORD & i) > 0) {
            if (this.echo == null) {
                this.echo = new char[this.maxSize];
                for (int k = 0; k < this.maxSize; k++) {
                    this.echo[k] = '*';
                }
            } else {
                doLayout(0);
            }
        } else if (this.echo != null) {
            doLayout(0);
            this.echo = null;
        }
        if (this.focus) {
            TextComponentHandler textComponentHandler = imListener;
            textComponentHandler.clear();
            textComponentHandler.setTextComponent(this.imi);
            doLayout(0);
            repaint();
        }
    }

    public int getConstraints() {
        return this.constraints;
    }

    public int getCaretPosition() {
        return this.caretPos;
    }

    private void doLayout(int i) {
        char[] ac;
        if (this.viewPortWidth == 0) {
            return;
        }
        int l = i != 0 ? getLineStart(i - 1) : 0;
        int i1 = i;
        this.lineCount = i + 1;
        boolean flag2 = false;
        if (this.caretPos > l) {
            flag2 = true;
        }
        if (this.echo != null) {
            ac = this.echo;
        } else {
            ac = this.text;
        }
        while (true) {
            int i2 = this.textCount;
            if (l < i2) {
                int i3 = this.minCharsPerLine;
                if (l + i3 > i2) {
                    i3 = i2 - l;
                }
                int k = i3;
                int j = Toolkit.DEFAULT_FONT.charsWidth(ac, l, k);
                int j1 = l + k;
                while (true) {
                    if (j1 < this.textCount) {
                        j += Toolkit.DEFAULT_FONT.charWidth(ac[j1]);
                        k++;
                        if (j <= this.lineSize) {
                            j1++;
                        } else {
                            k = j1 - l;
                            break;
                        }
                    } else {
                        break;
                    }
                }
                l += k;
                if (flag2 && l >= this.caretPos) {
                    this.caretLine = i1;
                    flag2 = false;
                }
                int i12 = i1 + 1;
                setLineStart(i1, l);
                int i4 = this.lineCount;
                if (i12 > i4) {
                    this.lineCount = i4 + 1;
                }
                i1 = i12;
            } else {
                updateCaretLine();
                return;
            }
        }
    }

    private void setLineStart(int i, int j) {
        int[] iArr = this.lineIndex;
        if (i >= iArr.length) {
            int[] ai = new int[iArr.length + 4];
            System.arraycopy(iArr, 0, ai, 0, iArr.length);
            this.lineIndex = ai;
        }
        this.lineIndex[i] = j;
    }

    private int getLineStart(int i) {
        return this.lineIndex[i];
    }

    public void setBounds(int i, int j, int k, int l) {
        this.x = i;
        this.y = j;
        this.viewPortWidth = k;
        this.viewPortHeight = l;
        int i2 = k - 6;
        this.lineSize = i2;
        this.minCharsPerLine = i2 / Toolkit.MAX_CHARWIDTH;
        this.maxLine = (l - 6) / Toolkit.FONT_HEIGHT;
        doLayout(0);
    }

    public void setMaxSize(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException();
        }
        int j = this.maxSize;
        this.maxSize = i;
        if (i != j) {
            char[] ac = new char[i];
            int iMin = Math.min(this.textCount, i);
            this.textCount = iMin;
            System.arraycopy(this.text, 0, ac, 0, iMin);
            this.text = ac;
            if ((this.constraints & TextField.PASSWORD) > 0) {
                this.echo = new char[i];
                for (int k = 0; k < i; k++) {
                    this.echo[k] = '*';
                }
            }
            int k2 = this.caretPos;
            this.caretPos = Math.min(k2, this.textCount);
            imListener.clear();
            doLayout(0);
            repaint();
        }
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public int size() {
        if (this.text != null) {
            return this.textCount;
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveLeftCaret() {
        int i = this.caretPos;
        if (i > 0) {
            int i2 = i - 1;
            this.caretPos = i2;
            int i3 = this.caretLine;
            if (i3 != 0 && this.lineIndex[i3 - 1] > i2) {
                this.caretLine = i3 - 1;
                updateCaretLine();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveRightCaret() {
        int i = this.caretPos;
        int i2 = this.textCount;
        if (i < i2) {
            int i3 = i + 1;
            this.caretPos = i3;
            int[] iArr = this.lineIndex;
            int i4 = this.caretLine;
            if (iArr[i4] < i3) {
                this.caretLine = i4 + 1;
                updateCaretLine();
                return;
            }
            return;
        }
        if (i2 < this.maxSize) {
            insertChar(' ');
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveUpCaret() {
        if (this.caretLine <= 0) {
            return;
        }
        Font font = Toolkit.DEFAULT_FONT;
        char[] cArr = this.text;
        int[] iArr = this.lineIndex;
        int i = this.caretLine;
        int i2 = font.charsWidth(cArr, iArr[i - 1], this.caretPos - iArr[i - 1]);
        int i3 = this.caretLine - 1;
        this.caretLine = i3;
        int j = i3 != 0 ? this.lineIndex[i3 - 1] : 0;
        int k = 0;
        int l = j;
        while (k < i2 && l < this.lineIndex[this.caretLine]) {
            k += Toolkit.DEFAULT_FONT.charWidth(this.text[l]);
            l++;
        }
        this.caretPos = l != this.lineIndex[this.caretLine] ? l : l - 1;
        updateCaretLine();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveDownCaret() {
        int i = this.caretLine;
        if (i >= this.lineCount - 1) {
            return;
        }
        int i2 = i != 0 ? this.lineIndex[i - 1] : 0;
        int j = Toolkit.DEFAULT_FONT.charsWidth(this.text, i2, this.caretPos - i2);
        int i3 = this.caretLine + 1;
        this.caretLine = i3;
        int i4 = this.lineIndex[i3 - 1];
        int k = 0;
        int l = i4;
        while (k < j && l < this.lineIndex[this.caretLine]) {
            k += Toolkit.DEFAULT_FONT.charWidth(this.text[l]);
            l++;
        }
        this.caretPos = l <= this.lineIndex[this.caretLine] ? l : l - 1;
        updateCaretLine();
    }

    public String getText() {
        return getString();
    }

    public void setText(String s) {
        setString(s);
    }

    private void updateScrollBar() {
        int i = this.viewPortHeight - 2;
        int j = i / Toolkit.FONT_HEIGHT;
        int i2 = this.lineCount;
        if (i2 > j) {
            int i3 = this.startLine;
            int i4 = (i * i3) / i2;
            this.scrollY = i4;
            if (i3 + j >= i2) {
                this.visHeight = i - i4;
                return;
            } else {
                this.visHeight = (i * j) / i2;
                return;
            }
        }
        this.scrollY = 0;
        this.visHeight = i;
    }

    public void setScrollBar(boolean flag) {
        if (flag) {
            this.scrollX = (this.x + this.viewPortWidth) - 1;
        } else {
            this.scrollX = 0;
        }
        updateScrollBar();
    }

    public void setBoxColor(int i, int j, int k, int l) {
        this.backColor = i;
        this.borderColor = j;
        this.focusBackColor = k;
        repaint();
    }

    public void setScrollColor(int i, int j) {
        this.scrollBackColor = i;
        this.scrollBarColor = j;
    }

    public void setTextColor(int i) {
        this.textColor = i;
    }

    public void setUpDown(boolean flag) {
        this.enableUpDown = flag;
    }

    public boolean hasFocus() {
        return this.focus;
    }

    public void setFocus(boolean flag) {
        this.focus = flag;
        if (flag) {
            imListener.setTextComponent(this.imi);
        } else {
            imListener.setTextComponent(null);
        }
    }

    public void inputChar(char c) {
        if (c == '\b' || c == 127) {
            deleteChar();
        } else {
            insertChar(c);
        }
        imListener.clear();
        repaint();
    }
}
