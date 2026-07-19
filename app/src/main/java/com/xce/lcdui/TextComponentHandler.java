package com.xce.lcdui;

/* JADX INFO: loaded from: classes.dex */
public abstract class TextComponentHandler {
    public static final int ENG_CAPITAL = 1;
    public static final int ENG_SMALL = 2;
    public static final int HANGUL = 16;
    public static final int HEBREW = 32;
    public static final int NUMBER = 4;
    public static final int SYMBOL = 8;
    private static TextComponentHandler tcHandler;

    public abstract void clear();

    public abstract int getInputMode();

    public abstract TextComponent getTextComponent();

    public abstract boolean keyPressed(int i);

    public abstract boolean keyReleased(int i);

    public abstract boolean keyRepeated(int i);

    public abstract void setTextComponent(TextComponent textComponent);

    public static synchronized boolean isLoaded() {
        return tcHandler != null;
    }

    public static synchronized TextComponentHandler getTextComponentHandler() {
        if (tcHandler == null) {
            try {
                String s = System.getProperty("lcdui.tchandler");
                tcHandler = (TextComponentHandler) Class.forName(s).newInstance();
            } catch (Exception e) {
            }
        }
        return tcHandler;
    }
}
