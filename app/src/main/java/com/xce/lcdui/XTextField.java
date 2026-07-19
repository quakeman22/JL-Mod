package com.xce.lcdui;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.TextField;

/* JADX INFO: loaded from: classes.dex */
public class XTextField {
    private static final TextComponentHandler imListener = TextComponentHandler.getTextComponentHandler();
    Canvas canvas;
    int caretPos;
    int constraints;
    int currInputMode;
    boolean focus;
    private boolean hebrewMode;
    int height;
    private final InputMethodImpl imi = new InputMethodImpl(this);
    boolean isCaretVisible = true;
    int lastInputMode;
    int maxSize;
    private int minNumOfChars;
    char[] password;
    int startPos;
    char[] text;
    int textCount;
    int visCount;
    int width;
    int x;
    int y;

    class InputMethodImpl implements TextComponent {
        XTextField textField;

        @Override // com.xce.lcdui.TextComponent
        public int getCaretPosition() {
            return XTextField.this.caretPos;
        }

        @Override // com.xce.lcdui.TextComponent
        public int getConstraints() {
            return XTextField.this.constraints;
        }

        @Override // com.xce.lcdui.TextComponent
        public int getMaxSize() {
            return this.textField.getMaxSize();
        }

        @Override // com.xce.lcdui.TextComponent
        public int size() {
            return XTextField.this.textCount;
        }

        @Override // com.xce.lcdui.TextComponent
        public void insert(char c) {
            XTextField.this.insert(c);
        }

        @Override // com.xce.lcdui.TextComponent
        public void delete() {
            XTextField.this.delete_char();
        }

        @Override // com.xce.lcdui.TextComponent
        public void clear() {
            XTextField.this.setText("");
        }

        @Override // com.xce.lcdui.TextComponent
        public void replace(char c) {
            this.textField.replace(c, XTextField.this.caretPos - 1);
        }

        @Override // com.xce.lcdui.TextComponent
        public void moveCursor(int i) {
            int j = XDisplay.getGameAction(i);
            if (XTextField.this.textCount > 0) {
                if (j != 2) {
                    if (j == 5) {
                        if (Toolkit.IS_HEBREW && XTextField.this.hebrewMode) {
                            XTextField.this.moveBackwardCaret();
                        } else {
                            XTextField.this.moveForewardCaret();
                        }
                    }
                } else if (!Toolkit.IS_HEBREW || !XTextField.this.hebrewMode) {
                    XTextField.this.moveBackwardCaret();
                } else {
                    XTextField.this.moveForewardCaret();
                }
            }
            XTextField.imListener.clear();
            repaint();
        }

        @Override // com.xce.lcdui.TextComponent
        public void setCaretPosition(int i) {
            if (i >= 0 && i <= XTextField.this.textCount) {
                XTextField.this.caretPos = i;
            }
        }

        @Override // com.xce.lcdui.TextComponent
        public void setCaretVisible(boolean flag) {
            if (XTextField.this.isCaretVisible != flag) {
                XTextField.this.isCaretVisible = flag;
                repaint();
            }
        }

        @Override // com.xce.lcdui.TextComponent
        public void repaint() {
            this.textField.repaint();
        }

        @Override // com.xce.lcdui.TextComponent
        public void repaintIM() {
            XTextField.this.canvas.repaintIM();
            if (Toolkit.IS_HEBREW) {
                XTextField xTextField = XTextField.this;
                xTextField.lastInputMode = xTextField.currInputMode;
                XTextField.this.currInputMode = XTextField.imListener.getInputMode();
                if (XTextField.this.lastInputMode != XTextField.this.currInputMode && XTextField.this.currInputMode == 32) {
                    XTextField xTextField2 = XTextField.this;
                    xTextField2.caretPos = xTextField2.textCount;
                    repaint();
                }
            }
        }

        InputMethodImpl(XTextField xtextfield1) {
            this.textField = xtextfield1;
        }
    }

    public XTextField(String s, int i, int j, Canvas canvas1) {
        s = s == null ? new String() : s;
        if (s.length() > i) {
            throw new IllegalArgumentException();
        }
        this.canvas = canvas1;
        this.maxSize = i;
        setConstraints(j);
        setText(s);
    }

    public void setText(String s) {
        if (this.text == null) {
            this.text = new char[this.maxSize];
        }
        if (s == null) {
            s = "";
        }
        char[] ac = s.toCharArray();
        int iMin = Math.min(ac.length, this.maxSize);
        this.textCount = iMin;
        this.caretPos = iMin;
        System.arraycopy(ac, 0, this.text, 0, iMin);
        if (hasFocus()) {
            imListener.clear();
        }
        if (Toolkit.IS_HEBREW) {
            checkHebrew();
        }
        updateText();
        repaint();
    }

    public String getText() {
        return new String(getData(), 0, this.textCount);
    }

    public char[] getData() {
        if (Toolkit.IS_HEBREW && this.hebrewMode) {
            return convertText(this.text);
        }
        return this.text;
    }

    void insert(char c) {
        int i = this.caretPos;
        int i2 = this.textCount;
        if (i <= i2 && i2 < this.maxSize) {
            if (Toolkit.IS_HEBREW) {
                if (this.hebrewMode) {
                    char[] cArr = this.text;
                    int i3 = this.caretPos;
                    System.arraycopy(cArr, i3, cArr, i3 + 1, this.textCount - i3);
                    this.text[this.caretPos] = c;
                    if (Toolkit.isHebrew(c) || c == ' ') {
                        this.caretPos++;
                    } else {
                        int i4 = this.caretPos;
                        int i5 = this.startPos;
                        if (i4 > i5) {
                            this.startPos = i5 + 1;
                        }
                    }
                } else {
                    if (Toolkit.isHebrew(c)) {
                        setHebrew();
                        insert(c);
                        return;
                    }
                    char[] cArr2 = this.text;
                    int i6 = this.caretPos;
                    System.arraycopy(cArr2, i6, cArr2, i6 + 1, this.textCount - i6);
                    char[] cArr3 = this.text;
                    int i7 = this.caretPos;
                    this.caretPos = i7 + 1;
                    cArr3[i7] = c;
                }
            } else {
                char[] cArr4 = this.text;
                int i8 = this.caretPos;
                System.arraycopy(cArr4, i8, cArr4, i8 + 1, this.textCount - i8);
                char[] cArr5 = this.text;
                int i9 = this.caretPos;
                this.caretPos = i9 + 1;
                cArr5[i9] = c;
            }
            this.textCount++;
            updateText();
        }
    }

    void delete_char() {
        int i = this.caretPos;
        if (i != 0) {
            char[] cArr = this.text;
            System.arraycopy(cArr, i, cArr, i - 1, this.textCount - i);
            this.textCount--;
            this.caretPos--;
            if (Toolkit.IS_HEBREW) {
                checkHebrew();
            }
            updateText();
        }
    }

    void replace(char c, int i) {
        if (Toolkit.IS_HEBREW && this.hebrewMode && !Toolkit.isHebrew(c)) {
            i++;
        }
        this.text[i] = c;
        updateText();
    }

    void updateText() {
        int l;
        int i = this.caretPos;
        int i2 = this.textCount;
        if (i == i2) {
            int i3 = 0;
            int k = i2 - 1;
            while (k >= 0) {
                i3 += Toolkit.DEFAULT_FONT.charWidth(this.text[k]);
                if (i3 > this.width - 4) {
                    break;
                } else {
                    k--;
                }
            }
            int i4 = k >= 0 ? k + 1 : 0;
            this.startPos = i4;
            this.visCount = this.textCount - i4;
            return;
        }
        int k2 = this.startPos;
        if (k2 > i) {
            int i5 = k2 - 1;
            this.startPos = i5;
            if (i5 < 0) {
                this.startPos = 0;
            }
        }
        Font font = Toolkit.DEFAULT_FONT;
        char[] cArr = this.text;
        int i6 = this.startPos;
        int j = font.charsWidth(cArr, i6, this.caretPos - i6);
        if (j > this.width - 4) {
            this.startPos++;
            l = this.startPos;
            j = 0;
        } else {
            l = this.caretPos;
        }
        while (l < this.textCount && (j = j + Toolkit.DEFAULT_FONT.charWidth(this.text[l])) <= this.width - 4) {
            l++;
        }
        this.visCount = l - this.startPos;
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
        int j;
        if (this.focus) {
            g.setColor(Toolkit.lt_gray);
            g.fillRect(this.x, this.y, this.width + 1, this.height + 1);
            g.setColor(0);
            g.drawRect(this.x, this.y, this.width, this.height);
        } else {
            g.setColor(16777215);
            g.fillRect(this.x, this.y, this.width + 1, this.height + 1);
            g.setColor(Toolkit.lt_gray);
            g.drawRect(this.x, this.y, this.width, this.height);
            g.setColor(0);
        }
        if (Toolkit.IS_HEBREW && this.hebrewMode) {
            int i = this.x + this.width;
            for (int k = 0; k < this.visCount; k++) {
                if ((this.constraints & TextField.PASSWORD) > 0) {
                    g.drawChar(this.password[0], i, this.y + 1, 24);
                } else {
                    g.drawChar(this.text[this.startPos + k], i, this.y + 1, 24);
                }
                i -= Toolkit.DEFAULT_FONT.charWidth(this.text[this.startPos + k]);
            }
        } else {
            int i2 = this.constraints;
            if ((i2 & TextField.PASSWORD) > 0) {
                g.drawChars(this.password, 0, this.visCount, this.x + 2, this.y + 1, 20);
            } else {
                g.drawChars(this.text, this.startPos, this.visCount, this.x + 2, this.y + 1, 20);
            }
        }
        if (this.isCaretVisible && this.focus) {
            if ((this.constraints & TextField.PASSWORD) > 0) {
                j = Toolkit.DEFAULT_FONT.charsWidth(this.password, 0, this.caretPos - this.startPos);
            } else {
                Font font = Toolkit.DEFAULT_FONT;
                char[] cArr = this.text;
                int i3 = this.startPos;
                j = font.charsWidth(cArr, i3, this.caretPos - i3);
            }
            if (Toolkit.IS_HEBREW && this.hebrewMode) {
                int i4 = this.x;
                int i5 = this.width;
                int i6 = this.y;
                g.drawLine(((i4 + i5) - j) - 1, i6, ((i4 + i5) - j) - 1, Toolkit.FONT_HEIGHT + i6);
                return;
            }
            int i7 = this.x;
            int i8 = this.y;
            g.drawLine(i7 + j + 1, i8, i7 + j + 1, Toolkit.FONT_HEIGHT + i8);
        }
    }

    public void repaint() {
        this.canvas.repaint(this.x, this.y, this.width, this.height);
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
            this.caretPos = Math.min(this.caretPos, this.textCount);
            if (hasFocus()) {
                imListener.clear();
            }
            updateText();
            repaint();
        }
    }

    public int getMaxSize() {
        return this.maxSize;
    }

    public void setBounds(int i, int j, int k, int l) {
        this.x = i;
        this.y = j;
        this.width = k;
        this.height = l;
        this.minNumOfChars = k / Toolkit.MAX_CHARWIDTH;
        updateText();
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
        insert(c);
        imListener.clear();
        repaint();
    }

    void setConstraints(int i) {
        int j = TextField.CONSTRAINT_MASK & i;
        if (j < 0 || j > 4) {
            throw new IllegalArgumentException("the value of the constraints parameter is invalid");
        }
        int i2 = this.constraints & TextField.CONSTRAINT_MASK;
        if (j == 1 || j == 3 || j == 4) {
            this.constraints = j;
        } else {
            this.constraints = i;
        }
        if (j == 2 || j == 3) {
            for (int l = 0; l < this.textCount; l++) {
                char[] cArr = this.text;
                if (cArr[l] < '0' || cArr[l] > '9') {
                    this.textCount = 0;
                    this.caretPos = 0;
                    break;
                }
            }
        }
        int l2 = this.constraints;
        if ((l2 & TextField.PASSWORD) > 0 && this.password == null) {
            this.password = new char[this.maxSize];
            for (int i1 = 0; i1 < this.maxSize; i1++) {
                this.password[i1] = '*';
            }
        }
        updateText();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveBackwardCaret() {
        int i = this.caretPos;
        if (i > 0) {
            this.caretPos = i - 1;
            updateText();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveForewardCaret() {
        int i = this.caretPos;
        if (i < this.textCount) {
            this.caretPos = i + 1;
            updateText();
        } else {
            insert(' ');
        }
    }

    private char[] convertText(char[] ac) {
        char[] ac1 = new char[this.maxSize];
        int i = 0;
        while (true) {
            if (i < this.textCount) {
                ac1[i] = ac[(r2 - i) - 1];
                i++;
            } else {
                return ac1;
            }
        }
    }

    private void setHebrew() {
        if (!this.hebrewMode) {
            this.hebrewMode = true;
            this.text = convertText(this.text);
        }
    }

    private void resetHebrew() {
        if (this.hebrewMode) {
            this.text = convertText(this.text);
            this.hebrewMode = false;
        }
    }

    private void checkHebrew() {
        if (Toolkit.isHebrew(this.text, this.textCount)) {
            if (!this.hebrewMode) {
                setHebrew();
                this.caretPos = this.textCount;
                return;
            }
            return;
        }
        if (this.hebrewMode) {
            resetHebrew();
            this.caretPos = this.textCount;
        }
    }
}
