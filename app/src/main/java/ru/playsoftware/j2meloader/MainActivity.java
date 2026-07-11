/*
 * Copyright 2015-2016 Nickolay Savchenko
 * Copyright 2017-2020 Nikita Shakarun
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

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.WindowManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import java.io.File;

import ru.playsoftware.j2meloader.applist.AppListModel;
import ru.playsoftware.j2meloader.applist.AppsListFragment;
import ru.playsoftware.j2meloader.config.Config;
import ru.playsoftware.j2meloader.util.Constants;
import ru.playsoftware.j2meloader.util.FileUtils;
import ru.playsoftware.j2meloader.util.PreloadManager;
import ru.playsoftware.j2meloader.util.PickDirResultContract;
import ru.playsoftware.j2meloader.util.StoragePermissionHelper;
import ru.woesss.j2me.installer.InstallerDialog;

public class MainActivity extends AppCompatActivity {

	private final StoragePermissionHelper storagePermissionHelper = new StoragePermissionHelper(this, this::onPermissionResult);

	private final ActivityResultLauncher<String> openDirLauncher = registerForActivityResult(
			new PickDirResultContract(),
			this::onPickDirResult
	);
	private final Handler mainHandler = new Handler(Looper.getMainLooper());

	private AppListModel appListModel;
	private Uri launchUri;
	private View preloadOverlay;
	private ProgressBar preloadProgress;
	private TextView preloadStatus;
	private boolean launcherAttached;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		if (getSupportActionBar() != null) {
			getSupportActionBar().hide();
		}
		hideLauncherStatusBar();
		preloadOverlay = findViewById(R.id.preload_overlay);
		preloadProgress = findViewById(R.id.preload_progress);
		preloadStatus = findViewById(R.id.preload_status);
		storagePermissionHelper.launch(this);
		appListModel = new ViewModelProvider(this).get(AppListModel.class);
		Intent intent = getIntent();
		if ((intent.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
			launchUri = intent.getData();
		}
		if (savedInstanceState != null) {
			launcherAttached = getSupportFragmentManager().findFragmentById(R.id.container) != null;
		}
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			hideLauncherStatusBar();
		}
	}

	private void hideLauncherStatusBar() {
		WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
		WindowInsetsControllerCompat controller =
				WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
		if (controller != null) {
			controller.setSystemBarsBehavior(
					WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
			controller.hide(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.navigationBars());
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			WindowManager.LayoutParams attributes = getWindow().getAttributes();
			attributes.layoutInDisplayCutoutMode =
					WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
			getWindow().setAttributes(attributes);
		}
	}

	private void checkAndCreateDirs() {
		String emulatorDir = Config.getEmulatorDir();
		File dir = new File(emulatorDir);
		if (dir.isDirectory() && dir.canWrite()) {
			FileUtils.initWorkDir(dir);
			runPostDirectorySetup(emulatorDir);
			return;
		}
		if (dir.exists() || dir.getParentFile() == null || !dir.getParentFile().isDirectory()
				|| !dir.getParentFile().canWrite()) {
			alertDirCannotCreate(emulatorDir);
			return;
		}
		alertCreateDir();
	}

	private void alertDirCannotCreate(String emulatorDir) {
		new AlertDialog.Builder(this)
				.setTitle(R.string.error)
				.setCancelable(false)
				.setMessage(getString(R.string.create_apps_dir_failed, emulatorDir))
				.setNegativeButton(R.string.exit, (d, w) -> finish())
				.setPositiveButton(R.string.choose, (d, w) -> openDirLauncher.launch(null))
				.show();
	}

	void onPermissionResult(boolean granted) {
		if (granted) {
			checkAndCreateDirs();
			return;
		}
		new AlertDialog.Builder(this)
				.setTitle(android.R.string.dialog_alert_title)
				.setCancelable(false)
				.setMessage(R.string.permission_request_failed)
				.setNegativeButton(R.string.retry, (d, w) -> storagePermissionHelper.launch(this))
				.setPositiveButton(R.string.exit, (d, w) -> finish())
				.show();
	}

	private void onPickDirResult(Uri uri) {
		if (uri == null || uri.getPath() == null) {
			checkAndCreateDirs();
			return;
		}
		File file = new File(uri.getPath());
		applyWorkDir(file);
	}

	private void alertCreateDir() {
		String emulatorDir = Config.getEmulatorDir();
		String msg = getString(R.string.alert_msg_workdir_not_exists, emulatorDir);
		new AlertDialog.Builder(this)
				.setTitle(android.R.string.dialog_alert_title)
				.setCancelable(false)
				.setMessage(msg)
				.setPositiveButton(R.string.create, (d, w) -> applyWorkDir(new File(emulatorDir)))
				.setNeutralButton(R.string.change, (d, w) -> openDirLauncher.launch(emulatorDir))
				.setNegativeButton(R.string.exit, (d, w) -> finish())
				.show();
	}

	private void applyWorkDir(File file) {
		String path = file.getAbsolutePath();
		if (!FileUtils.initWorkDir(file)) {
			alertDirCannotCreate(path);
			return;
		}
		runPostDirectorySetup(path, true);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if (uri != null) {
			InstallerDialog.newInstance(uri).show(getSupportFragmentManager(), "installer");
		}
	}

	private void runPostDirectorySetup(String emulatorDir) {
		runPostDirectorySetup(emulatorDir, false);
	}

	private void runPostDirectorySetup(String emulatorDir, boolean persistDir) {
		PreloadManager.prepareAsync(this, emulatorDir, new PreloadManager.Listener() {
			@Override
			public void onStart(int totalFiles) {
				mainHandler.post(() -> {
					preloadOverlay.setVisibility(View.VISIBLE);
					preloadProgress.setIndeterminate(false);
					preloadProgress.setMax(Math.max(1, totalFiles));
					preloadProgress.setProgress(0);
					preloadStatus.setText(getString(R.string.preload_wait));
				});
			}

			@Override
			public void onProgress(int copiedFiles, int totalFiles, @androidx.annotation.NonNull String relativePath) {
				mainHandler.post(() -> {
					preloadProgress.setMax(Math.max(1, totalFiles));
					preloadProgress.setProgress(copiedFiles);
					preloadStatus.setText(getString(R.string.preload_progress, copiedFiles, totalFiles, relativePath));
				});
			}

			@Override
			public void onComplete(boolean didWork, @androidx.annotation.NonNull String activeStamp) {
				mainHandler.post(() -> {
					if (persistDir) {
						PreferenceManager.getDefaultSharedPreferences(MainActivity.this)
								.edit()
								.putString(Constants.PREF_EMULATOR_DIR, emulatorDir)
								.apply();
					}
					appListModel.setEmulatorDirectory(emulatorDir);
					if (!launcherAttached) {
						AppsListFragment fragment = AppsListFragment.newInstance(launchUri);
						getSupportFragmentManager().beginTransaction()
								.replace(R.id.container, fragment)
								.commit();
						launcherAttached = true;
						launchUri = null;
					}
					preloadOverlay.setVisibility(View.GONE);
				});
			}

			@Override
			public void onError(@androidx.annotation.NonNull Exception exception) {
				mainHandler.post(() -> {
					preloadOverlay.setVisibility(View.GONE);
					new AlertDialog.Builder(MainActivity.this)
							.setTitle(R.string.error)
							.setCancelable(false)
							.setMessage(getString(R.string.preload_failed, exception.getMessage()))
							.setNegativeButton(R.string.exit, (d, w) -> finish())
							.setPositiveButton(R.string.retry, (d, w) -> runPostDirectorySetup(emulatorDir, persistDir))
							.show();
				});
			}
		});
	}
}
