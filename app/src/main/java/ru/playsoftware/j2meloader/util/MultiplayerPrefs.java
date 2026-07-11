package ru.playsoftware.j2meloader.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public final class MultiplayerPrefs {
	public static final String PREF_NETWORK_BT_ENABLED = "pref_network_bt_enabled";
	public static final String PREF_FRIEND_IP = "pref_friend_ip";

	private MultiplayerPrefs() {
	}

	public static boolean isNetworkBtEnabled(Context context) {
		return getPrefs(context).getBoolean(PREF_NETWORK_BT_ENABLED, false);
	}

	public static String getFriendIp(Context context) {
		return getPrefs(context).getString(PREF_FRIEND_IP, "");
	}

	public static void save(Context context, boolean enabled, String ip) {
		getPrefs(context)
				.edit()
				.putBoolean(PREF_NETWORK_BT_ENABLED, enabled)
				.putString(PREF_FRIEND_IP, ip)
				.apply();
	}

	private static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
