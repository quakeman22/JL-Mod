package com.immersion;

import javax.microedition.util.ContextHolder;

/** Static-call variant of {@link VibeTonz}; same behavior. */
public class VibeTonzStatic {

	public static int OpenDevice(int deviceId) {
		return 0;
	}

	public static void SetDevicePropertyString(int n1, int n2, String s) {
	}

	public static int PlayPeriodicEffect(int n1, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9) {
		ContextHolder.vibrate(120);
		return 0;
	}

	public static int CloseDevice(int deviceId) {
		return 0;
	}

	public static void Initialize() {
	}

	public static void Terminate() {
	}

	public static void StopAllPlayingEffects(int deviceId) {
	}

	public static int OpenIVTMemoryFile(byte[] data) {
		return 0;
	}

	public static void PlayIVTEffect(int n1, int n2, int n3) {
		ContextHolder.vibrate(120);
	}

	public static void ModifyPlayingMagSweepEffect(int n1, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9) {
	}

	public static void ModifyPlayingPeriodicEffect(int n1, int n2, int n3, int n4, int n5, int n6, int n7, int n8, int n9, int n10) {
	}
}
