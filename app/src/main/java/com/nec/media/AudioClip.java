package com.nec.media;

public interface AudioClip {
	void addAudioListener(AudioListener listener);

	void play() throws IllegalStateException;

	void stop() throws IllegalStateException;

	int getLapsedTime() throws IllegalStateException;

	int getTime() throws IllegalStateException;

	int getChannel() throws IllegalStateException;

	int getTempo() throws IllegalStateException;

	void setLoopCount(int count) throws IllegalArgumentException, IllegalStateException;

	AudioListener getAudioListener();
}
