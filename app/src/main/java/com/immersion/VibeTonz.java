package com.immersion;

import javax.microedition.util.ContextHolder;

/**
 * Immersion's VibeTonz haptics API. Real "effects" (waveforms, magnitude,
 * frequency curves) can't be replicated on a phone's simple vibration motor,
 * so we translate any effect trigger into a short vibration burst instead of
 * silently doing nothing like KEmulator (a desktop emulator with no haptics
 * hardware) does.
 */
public class VibeTonz {

	public int OpenDevice(int deviceId) {
		return 0;
	}

	public void SetDevicePropertyString(int n1, int n2, String s) {
	}

	public int PlayPeriodicEffect(int n1, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9) {
		ContextHolder.vibrate(120);
		return 0;
	}

	public int CloseDevice(int deviceId) {
		return 0;
	}

	public void Initialize() {
	}

	public void Terminate() {
	}

	public void StopAllPlayingEffects(int deviceId) {
	}

	public int OpenIVTMemoryFile(byte[] data) {
		return 0;
	}

	public void PlayIVTEffect(int n1, int n2, int n3) {
		ContextHolder.vibrate(120);
	}

	public void ModifyPlayingMagSweepEffect(int n1, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9) {
	}

	public void ModifyPlayingPeriodicEffect(int n1, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10) {
	}
}
