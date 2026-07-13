/*
 * Copyright 2017-2018 Nikita Shakarun
 * Copyright 2020-2024 Yury Kharchenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.playsoftware.j2meloader;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.preference.PreferenceManager;

import org.acra.ACRA;
import org.acra.config.CoreConfigurationBuilder;

import ru.playsoftware.j2meloader.crashes.AppCenterCollector;
import ru.playsoftware.j2meloader.crashes.AppCenterSender;
import ru.playsoftware.j2meloader.util.Constants;
import ru.playsoftware.j2meloader.util.FileUtils;
import ru.playsoftware.j2meloader.util.GameLog;

public class EmulatorApplication extends Application implements OnSharedPreferenceChangeListener {
	private static EmulatorApplication instance;

	public static EmulatorApplication getInstance() {
		return instance;
	}

	@Override
	protected void attachBaseContext(Context base) {
		super.attachBaseContext(base);
		instance = this;
		GameLog.install();
		if (BuildConfig.DEBUG) {
			MultiDex.install(this);
		}

		ACRA.init(this, new CoreConfigurationBuilder()
				.withParallel(false)
				.withReportContent(AppCenterCollector.REPORT_FIELDS)
				.withPluginConfigurations(AppCenterSender.buildHttpSenderConfiguration(this)));

		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		sp.edit()
				.putBoolean(Constants.PREF_TOOLBAR, false)
				.putBoolean(Constants.PREF_STATUSBAR, false)
				.apply();
		AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
	}

	@NonNull
	public static String getProcessName() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			return Application.getProcessName();
		} else {
			return FileUtils.getText("/proc/self/cmdline").trim();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	}
}