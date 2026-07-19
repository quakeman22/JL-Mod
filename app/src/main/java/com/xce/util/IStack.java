package com.xce.util;

import java.util.EmptyStackException;

/* JADX INFO: loaded from: classes.dex */
public class IStack {
    int[] buf = new int[10];
    int top = -1;

    public synchronized int peek() {
        if (size() == 0) {
            throw new EmptyStackException();
        }
        return this.buf[this.top];
    }

    public synchronized int pop() {
        int[] iArr;
        int i;
        if (size() == 0) {
            throw new EmptyStackException();
        }
        iArr = this.buf;
        i = this.top;
        this.top = i - 1;
        return iArr[i];
    }

    public synchronized void push(int i) {
        int i2 = this.top + 1;
        this.top = i2;
        int[] iArr = this.buf;
        if (i2 >= iArr.length) {
            int[] ai = new int[iArr.length + 6];
            System.arraycopy(iArr, 0, ai, 0, iArr.length);
            this.buf = ai;
        }
        this.buf[this.top] = i;
    }

    public int size() {
        return this.top + 1;
    }

    public boolean empty() {
        return this.top == -1;
    }
}
