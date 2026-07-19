package com.skt.m;

import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public interface AudioClip {
    void close() throws IOException;

    void loop() throws UserStopException, IOException;

    void open(byte[] bArr, int i, int i2) throws UnsupportedFormatException, ResourceAllocException;

    void pause() throws IOException;

    void play() throws UserStopException, IOException;

    void resume() throws IOException;

    void stop() throws IOException;
}
