package ru.playsoftware.j2meloader.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public final class MultiplayerPrefs {
	public static final String PREF_NETWORK_BT_ENABLED = "pref_network_bt_enabled";
	public static final String PREF_FRIEND_IP = "pref_friend_ip";
	public static final String PREF_USE_RELAY = "pref_use_relay";
	public static final String PREF_RELAY_HOST = "pref_relay_host";
	public static final String PREF_RELAY_PORT = "pref_relay_port";
	public static final String PREF_ROOM_CODE = "pref_room_code";

	private MultiplayerPrefs() {
	}

	public static boolean isNetworkBtEnabled(Context context) {
		return getPrefs(context).getBoolean(PREF_NETWORK_BT_ENABLED, false);
	}

	public static String getFriendIp(Context context) {
		return getPrefs(context).getString(PREF_FRIEND_IP, "");
	}

	public static boolean isUseRelay(Context context) {
		return getPrefs(context).getBoolean(PREF_USE_RELAY, false);
	}

	public static String getRelayHost(Context context) {
		return getPrefs(context).getString(PREF_RELAY_HOST, "");
	}

	public static int getRelayPort(Context context) {
		return getPrefs(context).getInt(PREF_RELAY_PORT, 17342);
	}

	public static String getRoomCode(Context context) {
		return getPrefs(context).getString(PREF_ROOM_CODE, "");
	}

	public static void save(Context context, boolean enabled, String ip) {
		getPrefs(context)
				.edit()
				.putBoolean(PREF_NETWORK_BT_ENABLED, enabled)
				.putString(PREF_FRIEND_IP, ip)
				.apply();
	}

	public static void saveRelay(Context context, boolean enabled, boolean useRelay,
	                              String relayHost, int relayPort, String roomCode) {
		getPrefs(context)
				.edit()
				.putBoolean(PREF_NETWORK_BT_ENABLED, enabled)
				.putBoolean(PREF_USE_RELAY, useRelay)
				.putString(PREF_RELAY_HOST, relayHost)
				.putInt(PREF_RELAY_PORT, relayPort)
				.putString(PREF_ROOM_CODE, roomCode)
				.apply();
	}

	private static SharedPreferences getPrefs(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
}
