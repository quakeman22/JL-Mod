package com.xce.util;

import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class DefaultTimeZone extends TimeZone {
    public DefaultTimeZone(int i, String s) {
        ((TimeZone) this).rawOffset = i;
        ((TimeZone) this).ID = s;
    }

    @Override // java.util.TimeZone
    public int getOffset(int i, int j, int k, int l, int i1, int j1) {
        return ((TimeZone) this).rawOffset;
    }

    @Override // java.util.TimeZone
    public int getRawOffset() {
        return ((TimeZone) this).rawOffset;
    }

    @Override // java.util.TimeZone
    public boolean useDaylightTime() {
        return false;
    }
}
