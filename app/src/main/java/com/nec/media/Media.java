package com.nec.media;

/**
 * Not backed by real playback (same as KEmulator's own reference
 * implementation) - returns an inert AudioClip so games that merely check
 * for its presence/call these methods don't crash.
 */
public final class Media {

	public static AudioClip getAudioClip(String location) throws IllegalArgumentException {
		return new AudioClip() {
			private AudioListener listener;

			@Override
			public void addAudioListener(AudioListener listener) {
				this.listener = listener;
			}

			@Override
			public void play() {
			}

			@Override
			public void stop() {
			}

			@Override
			public int getLapsedTime() {
				return 0;
			}

			@Override
			public int getTime() {
				return 0;
			}

			@Override
			public int getChannel() {
				return 0;
			}

			@Override
			public int getTempo() {
				return 0;
			}

			@Override
			public void setLoopCount(int count) {
			}

			@Override
			public AudioListener getAudioListener() {
				return listener;
			}
		};
	}
}
