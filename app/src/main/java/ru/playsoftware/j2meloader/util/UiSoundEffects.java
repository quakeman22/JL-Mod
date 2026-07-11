package ru.playsoftware.j2meloader.util;

import static ru.playsoftware.j2meloader.util.Constants.PREF_UI_SOUNDS;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import androidx.annotation.RawRes;
import androidx.preference.PreferenceManager;

import ru.playsoftware.j2meloader.R;

public final class UiSoundEffects {
	private static volatile UiSoundEffects instance;

	private final Context appContext;
	private final SoundPool soundPool;
	private final int openingSound;
	private final int backSound;
	private final int browseSound;
	private final int confirmSound;
	private final int playSound;
	private final int dpadSound;
	private final int actionSound;
	private final int lrSound;
	private final int startSound;

	private UiSoundEffects(Context context) {
		appContext = context.getApplicationContext();
		soundPool = new SoundPool.Builder()
				.setMaxStreams(6)
				.setAudioAttributes(new AudioAttributes.Builder()
						.setUsage(AudioAttributes.USAGE_GAME)
						.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
						.build())
				.build();
		openingSound = load(R.raw.sfx_opening);
		backSound = load(R.raw.sfx_ui_back);
		browseSound = load(R.raw.sfx_ui_browse);
		confirmSound = load(R.raw.sfx_ui_confirm);
		playSound = load(R.raw.sfx_ui_play);
		dpadSound = load(R.raw.sfx_controller_button_dpad);
		actionSound = load(R.raw.sfx_controller_button_abxy);
		lrSound = load(R.raw.sfx_controller_button_lr);
		startSound = load(R.raw.sfx_controller_button_start);
	}

	public static UiSoundEffects get(Context context) {
		if (instance == null) {
			synchronized (UiSoundEffects.class) {
				if (instance == null) {
					instance = new UiSoundEffects(context);
				}
			}
		}
		return instance;
	}

	private int load(@RawRes int resId) {
		return soundPool.load(appContext, resId, 1);
	}

	private boolean isEnabled() {
		return PreferenceManager.getDefaultSharedPreferences(appContext)
				.getBoolean(PREF_UI_SOUNDS, true);
	}

	private void play(int soundId) {
		if (!isEnabled() || soundId == 0) {
			return;
		}
		soundPool.play(soundId, 0.85f, 0.85f, 1, 0, 1f);
	}

	public void playOpening() {
		play(openingSound);
	}

	public void playBack() {
		play(backSound);
	}

	public void playBrowse() {
		play(browseSound);
	}

	public void playConfirm() {
		play(confirmSound);
	}

	public void playPlay() {
		play(playSound);
	}

	public void playDpad() {
		play(dpadSound);
	}

	public void playAction() {
		play(actionSound);
	}

	public void playLr() {
		play(lrSound);
	}

	public void playStart() {
		play(startSound);
	}
}
