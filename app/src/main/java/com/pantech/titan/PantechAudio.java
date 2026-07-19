package com.pantech.titan;

import com.samsung.util.AudioClip;

/**
 * Pantech's audio API is a thin wrapper around the same MMF playback Samsung
 * devices exposed, so it's implemented on top of this project's existing
 * com.samsung.util.AudioClip.
 */
public class PantechAudio {

	private AudioClip samsungImpl;

	public PantechAudio(int n, String filename) {
		try {
			samsungImpl = new AudioClip(AudioClip.TYPE_MMF, filename);
		} catch (Exception ignored) {
		}
	}

	public void start(int loop, int volume) {
		try {
			if (samsungImpl != null) samsungImpl.play(loop, volume);
		} catch (Exception ignored) {
		}
	}

	public void stop() {
		try {
			if (samsungImpl != null) samsungImpl.stop();
		} catch (Exception ignored) {
		}
	}
}
