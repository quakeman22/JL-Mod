package com.xce.jam;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;

/* JADX INFO: loaded from: classes.dex */
public class ItemView {
    protected Canvas canvas = null;
    protected int leftCommand = -1;
    protected int rightCommand = -1;

    public void init(Canvas canvas1) {
        this.canvas = canvas1;
    }

    public void show() {
    }

    public void hide() {
    }

    public void keyPressed(int i) {
    }

    public void keyRepeated(int i) {
    }

    public void keyReleased(int i) {
    }

    public void pointerPressed(int i, int j) {
    }

    public void pointerReleased(int i, int j) {
    }

    public void pointerDragged(int i, int j) {
    }

    public void paint(Graphics g) {
    }

    public void setCommand(int i, int j) {
        this.leftCommand = i;
        this.rightCommand = j;
    }

    public int getLeftCommand() {
        return this.leftCommand;
    }

    public int getRightCommand() {
        return this.rightCommand;
    }
}
