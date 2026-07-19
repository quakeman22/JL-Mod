package com.skt.m;

public final class AudioSystem {
	private static final String[] CLIP_FORMATS = {
			"audio/mmf",
			"audio/xmf",
			"audio/midi",
			"audio/imy",
			"audio/x-wav"
	};

	private AudioSystem() {
	}

	public static String[] getClipFormats() {
		return CLIP_FORMATS.clone();
	}

	public static AudioClip getAudioClip(String path) throws UnsupportedFormatException {
		if (path == null) {
			throw new NullPointerException("path == null");
		}
		return DefaultAudioClip.createAudioClip(path);
	}

	public static int getMaxVolume(String name) throws UnsupportedFormatException {
		return 10;
	}

	public static int getVolume(String name) throws UnsupportedFormatException {
		return 10;
	}

	public static void setVolume(String name, int volume) throws UnsupportedFormatException {
		// Some games expect this call for side effects. The JL-Mod audio stack
		// already handles runtime volume elsewhere, so keep this a safe no-op.
	}
}
