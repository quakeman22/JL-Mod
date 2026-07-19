package com.xce.lcdui;

/* JADX INFO: loaded from: classes.dex */
public interface TextComponent {
    void clear();

    void delete();

    int getCaretPosition();

    int getConstraints();

    int getMaxSize();

    void insert(char c);

    void moveCursor(int i);

    void repaint();

    void repaintIM();

    void replace(char c);

    void setCaretPosition(int i);

    void setCaretVisible(boolean z);

    int size();
}
