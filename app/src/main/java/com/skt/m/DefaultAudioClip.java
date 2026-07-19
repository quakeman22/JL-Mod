package com.skt.m;

import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class DefaultAudioClip implements AudioClip {
    private byte[] data;
    private int length;
    private int offset;
    private boolean playing;
    private int type;

    private DefaultAudioClip(int i) {
        this.type = i;
    }

    static AudioClip createAudioClip(String str) throws UnsupportedFormatException {
        throw new Error("Unresolved compilation problems: \n\tXSound cannot be resolved\n\tXSound cannot be resolved\n");
    }

    @Override // com.skt.m.AudioClip
    public void open(byte[] bArr, int i, int i2) throws UnsupportedFormatException, ResourceAllocException {
        throw new Error("Unresolved compilation problem: \n\tXSound cannot be resolved\n");
    }

    @Override // com.skt.m.AudioClip
    public void close() throws IOException {
        if (this.playing) {
            stop();
        }
        this.data = null;
        this.offset = 0;
        this.length = 0;
    }

    private void play(boolean z) throws UserStopException, IOException {
        throw new Error("Unresolved compilation problem: \n\tXSound cannot be resolved\n");
    }

    @Override // com.skt.m.AudioClip
    public void play() throws UserStopException, IOException {
        play(false);
    }

    @Override // com.skt.m.AudioClip
    public void loop() throws UserStopException, IOException {
        play(true);
    }

    @Override // com.skt.m.AudioClip
    public void stop() throws IOException {
        throw new Error("Unresolved compilation problem: \n\tXSound cannot be resolved\n");
    }

    @Override // com.skt.m.AudioClip
    public void pause() throws IOException {
        throw new Error("Unresolved compilation problem: \n\tXSound cannot be resolved\n");
    }

    @Override // com.skt.m.AudioClip
    public void resume() throws IOException {
        throw new Error("Unresolved compilation problem: \n\tXSound cannot be resolved\n");
    }
}
