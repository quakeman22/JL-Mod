package com.skt.m;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;
import javax.microedition.shell.AppClassLoader;

final class DefaultAudioClip implements AudioClip {
	private static final String TAG = DefaultAudioClip.class.getName();

	private byte[] data;
	private int offset;
	private int length;
	private String path;
	private String contentType;
	private Player player;

	private DefaultAudioClip() {
	}

	static AudioClip createAudioClip(String path) throws UnsupportedFormatException {
		DefaultAudioClip clip = new DefaultAudioClip();
		clip.path = path;
		clip.contentType = detectContentType(path);
		return clip;
	}

	@Override
	public void open(byte[] data, int offset, int length) throws UnsupportedFormatException, ResourceAllocException {
		if (data == null) {
			throw new NullPointerException("data == null");
		}
		if (offset < 0 || length < 0 || offset + length > data.length) {
			throw new ArrayIndexOutOfBoundsException();
		}
		this.data = data;
		this.offset = offset;
		this.length = length;
		this.contentType = "audio/mmf";
	}

	@Override
	public void close() throws IOException {
		stop();
		data = null;
		offset = 0;
		length = 0;
		path = null;
		player = null;
	}

	@Override
	public void play() throws UserStopException, IOException {
		playInternal(false);
	}

	@Override
	public void loop() throws UserStopException, IOException {
		playInternal(true);
	}

	@Override
	public void pause() throws IOException {
		if (player != null) {
			try {
				player.stop();
			} catch (MediaException e) {
				Log.e(TAG, "pause", e);
			}
		}
	}

	@Override
	public void resume() throws IOException {
		if (player != null) {
			try {
				player.start();
			} catch (MediaException e) {
				Log.e(TAG, "resume", e);
			}
		}
	}

	@Override
	public void stop() throws IOException {
		if (player != null) {
			try {
				player.stop();
			} catch (MediaException e) {
				Log.e(TAG, "stop", e);
			}
			player.close();
			player = null;
		}
	}

	private void playInternal(boolean loop) throws IOException {
		ensurePlayer();
		if (player == null) {
			return;
		}
		try {
			if (player.getState() == Player.STARTED) {
				player.stop();
			}
			player.setLoopCount(loop ? -1 : 1);
			VolumeControl volumeControl = (VolumeControl) player.getControl("VolumeControl");
			if (volumeControl != null) {
				volumeControl.setLevel(100);
			}
			player.start();
		} catch (MediaException e) {
			Log.e(TAG, "playInternal", e);
		}
	}

	private void ensurePlayer() throws IOException {
		if (player != null) {
			return;
		}
		try {
			player = createPlayerWithFallback();
			if (player == null) {
				return;
			}
			player.realize();
		} catch (MediaException e) {
			throw new IOException(e);
		}
	}

	private Player createPlayerWithFallback() throws IOException, MediaException {
		String[] candidates = contentType != null
				? new String[] {contentType, "audio/mmf", "audio/xmf", "audio/midi", "audio/x-wav"}
				: new String[] {"audio/mmf", "audio/xmf", "audio/midi", "audio/x-wav"};
		MediaException lastMediaException = null;
		for (String candidate : candidates) {
			try {
				InputStream stream = openStream();
				if (stream == null) {
					return null;
				}
				return Manager.createPlayer(stream, candidate);
			} catch (MediaException e) {
				lastMediaException = e;
			}
		}
		if (lastMediaException != null) {
			throw lastMediaException;
		}
		throw new MediaException("Unable to create player");
	}

	private InputStream openStream() throws IOException {
		if (data != null) {
			return new ByteArrayInputStream(data, offset, length);
		}
		if (path != null) {
			return AppClassLoader.getResourceAsStream(null, path);
		}
		return null;
	}

	private static String detectContentType(String path) {
		String lower = path.toLowerCase();
		if (lower.endsWith(".mmf")) {
			return "audio/mmf";
		}
		if (lower.endsWith(".xmf")) {
			return "audio/xmf";
		}
		if (lower.endsWith(".mid") || lower.endsWith(".midi")) {
			return "audio/midi";
		}
		if (lower.endsWith(".imy")) {
			return "audio/imy";
		}
		if (lower.endsWith(".wav")) {
			return "audio/x-wav";
		}
		return "audio/midi";
	}
}
