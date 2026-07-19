package com.nec.media;

public interface AudioListener {
	int AUDIO_STARTED = 0;
	int AUDIO_STOPPED = 1;
	int AUDIO_COMPLETE = 2;

	void audioAction(AudioClip clip, int event, int param);
}
