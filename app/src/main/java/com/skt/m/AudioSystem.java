package com.skt.m;

import javax.microedition.util.ContextHolder;

/* JADX INFO: loaded from: classes.dex */
public class AudioSystem {
    private AudioSystem() {
    }

    public static String[] getClipFormats() {
        return null;
    }

    public static AudioClip getAudioClip(String s) throws UnsupportedFormatException {
        return null;
    }

    public static int getMaxVolume(String s) throws UnsupportedFormatException {
        return 10;
    }

    public static int getVolume(String s) throws UnsupportedFormatException {
        return 10;
    }

    public static void setVolume(String s, int i) throws UnsupportedFormatException {
        if (s.equals("효과음")) {
            int i2 = i + 40;
            System.out.println("효과음" + i2);
            ContextHolder.getResourceAsStream(ContextHolder.tempClass, "/snd/" + i2, 1);
            return;
        }
        System.out.println("사운드볼륨" + i);
        ContextHolder.soundVolume = i;
        ContextHolder.getResourceAsStream(ContextHolder.tempClass, ContextHolder.tempString);
    }
}
