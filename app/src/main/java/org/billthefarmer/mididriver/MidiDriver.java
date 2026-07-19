package org.billthefarmer.mididriver;

/* JADX INFO: loaded from: classes.dex */
public class MidiDriver {
    private OnMidiStartListener listener;

    public interface OnMidiStartListener {
        void onMidiStart();
    }

    private native boolean init();

    private native boolean shutdown();

    public native int[] config();

    public native boolean setVolume(int i);

    public native boolean write(byte[] bArr);

    public void start() {
        OnMidiStartListener onMidiStartListener;
        if (init() && (onMidiStartListener = this.listener) != null) {
            onMidiStartListener.onMidiStart();
        }
    }

    public void queueEvent(byte[] event) {
        write(event);
    }

    public void stop() {
        shutdown();
    }

    public void setOnMidiStartListener(OnMidiStartListener l) {
        this.listener = l;
    }

    static {
        System.loadLibrary("midi");
    }
}
