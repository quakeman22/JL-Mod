package com.nec.device;

import javax.microedition.util.ContextHolder;

/**
 * Backlight/vibration control. Vibration is wired to the device's real
 * vibrator; backlight has no Android equivalent to hook into.
 */
public final class PhoneControl {
	public static final int DEV_BACKLIGHT = 0;
	public static final int ATTR_BACKLIGHT_OFF = 1;
	public static final int ATTR_BACKLIGHT_ON = 2;
	public static final int DEV_VIBRATION = 3;
	public static final int ATTR_VIBRATION_OFF = 4;
	public static final int ATTR_VIBRATION_1_ON = 5;
	public static final int ATTR_VIBRATION_2_ON = 6;
	public static final int ATTR_VIBRATION_3_ON = 7;

	public static void setAttribute(int dev, int attr) throws IllegalArgumentException {
		if (dev == DEV_VIBRATION && attr != ATTR_VIBRATION_OFF) {
			ContextHolder.vibrate(100);
		}
	}

	public static int getAttribute(int dev) throws IllegalArgumentException {
		return 0;
	}

	public static boolean isAvailable(int dev) throws IllegalArgumentException {
		return dev == DEV_VIBRATION;
	}
}
