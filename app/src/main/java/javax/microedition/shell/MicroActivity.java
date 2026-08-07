/*
 * Copyright 2015-2016 Nickolay Savchenko
 * Copyright 2017-2021 Nikita Shakarun
 * Copyright 2019-2026 Yury Kharchenko
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

package javax.microedition.shell;

import static android.content.pm.ActivityInfo.*;
import static ru.playsoftware.j2meloader.util.Constants.*;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.lifecycle.Lifecycle;
import androidx.preference.PreferenceManager;

import com.google.android.material.textfield.TextInputLayout;

import org.acra.ACRA;
import org.acra.ErrorReporter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.List;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.ViewHandler;
import javax.microedition.lcdui.event.SimpleEvent;
import javax.microedition.lcdui.keyboard.KeyMapper;
import javax.microedition.lcdui.keyboard.VirtualKeyboard;
import javax.microedition.lcdui.skin.SkinLayer;
import javax.microedition.util.ContextHolder;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import ru.playsoftware.j2meloader.BuildConfig;
import ru.playsoftware.j2meloader.R;
import ru.playsoftware.j2meloader.config.Config;
import ru.playsoftware.j2meloader.databinding.ActivityMicroBinding;
import ru.playsoftware.j2meloader.databinding.DialogInputBinding;
import ru.playsoftware.j2meloader.util.Constants;
import ru.playsoftware.j2meloader.util.GameLog;
import ru.playsoftware.j2meloader.util.LogUtils;
import ru.playsoftware.j2meloader.util.MultiplayerPrefs;
import ru.playsoftware.j2meloader.util.SavestateManager;
import ru.playsoftware.j2meloader.util.UiSoundEffects;

public class MicroActivity extends AppCompatActivity {
	private static final int ORIENTATION_DEFAULT = 0;
	private static final int ORIENTATION_AUTO = 1;
	private static final int ORIENTATION_PORTRAIT = 2;
	private static final int ORIENTATION_LANDSCAPE = 3;
	private static final String CLASSICS_STYLE_JOYSTICK = "joystick";
	private static final String CLASSICS_STYLE_PHONE = "phone";
	private static final String CLASSICS_STYLE_HANDSET = "handset";
	private static final String CLASSICS_STYLE_CUSTOM = "custom";

	private Displayable current;
	private boolean actionBarEnabled;
	private boolean statusBarEnabled;
	private MicroLoader microLoader;
	private String appName;
	private InputMethodManager inputMethodManager;
	private int menuKey;
	private String appPath;
	private ActivityMicroBinding binding;
	private String classicsControlStyle = CLASSICS_STYLE_JOYSTICK;
	private String classicsHandsetSkin = "dark";
	private String classicsKeyMode = "mixed";
	private AlertDialog gameplayMenuDialog;

	private UiSoundEffects uiSounds() {
		return UiSoundEffects.get(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		lockNightMode();
		super.onCreate(savedInstanceState);
		ContextHolder.setCurrentActivity(this);
		bindMicroLayout();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		applyClassicsViewSize(sp.getString(PREF_CLASSICS_VIEW_SIZE, "default"));
		applyClassicsKeyMode(sp.getString(PREF_CLASSICS_KEY_MODE, "mixed"));
		applyClassicsControlStyle(sp.getString(PREF_CLASSICS_CONTROL_STYLE, CLASSICS_STYLE_JOYSTICK));
		actionBarEnabled = sp.getBoolean(PREF_TOOLBAR, false);
		statusBarEnabled = sp.getBoolean(PREF_STATUSBAR, false);
		if (sp.getBoolean(PREF_KEEP_SCREEN, false)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
		ContextHolder.setVibration(sp.getBoolean(PREF_VIBRATION, true));
		Canvas.setScreenshotRawMode(sp.getBoolean(PREF_SCREENSHOT_SWITCH, false));
		Intent intent = getIntent();
		if (BuildConfig.FULL_EMULATOR) {
			appName = intent.getStringExtra(KEY_MIDLET_NAME);
			Uri data = intent.getData();
			if (data == null) {
				showErrorDialog("Invalid intent: app path is null");
				return;
			}
			appPath = data.toString();
		} else {
			appName = getTitle().toString();
			appPath = getApplicationInfo().dataDir + "/files/converted/midlet";
			File dir = new File(appPath);
			if (!dir.exists() && !dir.mkdirs()) {
				throw new RuntimeException("Can't access file system");
			}
		}
		GameLog.clear();
		GameLog.i("Session", "Starting MIDlet session for \"" + appName + "\" from " + appPath);
		microLoader = new MicroLoader(appPath);
		if (!microLoader.init()) {
			Config.openSettings(this, appName, appPath);
			finish();
			return;
		}
		applyPendingSavestateIfAny();
		microLoader.applyConfiguration();
		attachOverlayLayers();
		SkinLayer skinLayer = SkinLayer.getInstance();
		if (skinLayer != null) {
			if (!statusBarEnabled && !actionBarEnabled) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
					WindowManager.LayoutParams attributes = getWindow().getAttributes();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
						attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS;
					} else {
						attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
					}
					getWindow().setAttributes(attributes);
				}
			}
		}
		int orientation = microLoader.getOrientation();
		setOrientation(orientation);
		menuKey = microLoader.getMenuKeyCode();
		inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

		getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
			@Override
			public void handleOnBackPressed() {
				// Intentionally overridden by empty due to support for back-key remapping.
			}
		});
		loadMIDlet();
	}

	private void bindMicroLayout() {
		binding = ActivityMicroBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());
		setSupportActionBar(binding.toolbar);
		binding.buttonBackOverlay.setOnClickListener(v -> {
			uiSounds().playBack();
			showExitConfirmation();
		});
		binding.overlay.setOnTouchListener((v, event) -> {
			if (!(current instanceof Canvas)) {
				return false;
			}
			VirtualKeyboard vk = ContextHolder.getVk();
			if (vk == null || !ContextHolder.hasClassicsControlBounds()) {
				return false;
			}
			switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN -> vk.show();
				case MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> vk.hide();
			}
			int index = event.getActionIndex();
			int id = event.getPointerId(index);
			float x = event.getX(index);
			float y = event.getY(index);
			return switch (event.getActionMasked()) {
				case MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN ->
						vk.pointerPressed(id, x, y);
				case MotionEvent.ACTION_MOVE -> {
					boolean consumed = false;
					for (int i = 0; i < event.getPointerCount(); i++) {
						consumed |= vk.pointerDragged(event.getPointerId(i),
								event.getX(i), event.getY(i));
					}
					yield consumed;
				}
				case MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL ->
						vk.pointerReleased(id, x, y);
				default -> false;
			};
		});
		binding.virtualDisplay.addOnLayoutChangeListener((v, left, top, right, bottom,
				oldLeft, oldTop, oldRight, oldBottom) -> {
			if (current instanceof Canvas && (left != oldLeft || top != oldTop
					|| right != oldRight || bottom != oldBottom)) {
				updateClassicsControlBounds();
			}
		});
		binding.displayableContainer.addOnLayoutChangeListener((v, left, top, right, bottom,
				oldLeft, oldTop, oldRight, oldBottom) -> {
			if (current instanceof Canvas && (left != oldLeft || top != oldTop
					|| right != oldRight || bottom != oldBottom)) {
				updateCanvasViewport();
			}
		});
	}

	private void attachOverlayLayers() {
		SkinLayer skinLayer = SkinLayer.getInstance();
		if (skinLayer != null) {
			binding.overlay.addLayer(skinLayer);
		}
		VirtualKeyboard vk = ContextHolder.getVk();
		if (vk != null) {
			vk.setView(binding.overlay);
			binding.overlay.addLayer(vk);
		}
	}

	public void lockNightMode() {
		int current = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
		if (current == Configuration.UI_MODE_NIGHT_YES) {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
		} else {
			AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
		}
	}

	@Override
	public void onPause() {
		hideSoftInput();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		applyClassicsViewSize(sp.getString(PREF_CLASSICS_VIEW_SIZE, "default"));
		applyClassicsKeyMode(sp.getString(PREF_CLASSICS_KEY_MODE, "mixed"));
		applyClassicsControlStyle(sp.getString(PREF_CLASSICS_CONTROL_STYLE, CLASSICS_STYLE_JOYSTICK));
	}

	@Override
	public void onConfigurationChanged(@NonNull Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		ContextHolder.setCurrentActivity(this);
		bindMicroLayout();
		attachOverlayLayers();
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		applyClassicsViewSize(sp.getString(PREF_CLASSICS_VIEW_SIZE, "default"));
		applyClassicsKeyMode(sp.getString(PREF_CLASSICS_KEY_MODE, "mixed"));
		applyClassicsControlStyle(sp.getString(PREF_CLASSICS_CONTROL_STYLE, CLASSICS_STYLE_JOYSTICK));
		bindDisplayable(current);
	}

	private void hideSoftInput() {
		if (inputMethodManager != null) {
			IBinder windowToken = binding.displayableContainer.getWindowToken();
			inputMethodManager.hideSoftInputFromWindow(windowToken, 0);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
				current instanceof Canvas) {
			hideSystemUI();
		}
	}

	@SuppressLint("SourceLockedOrientationActivity")
	private void setOrientation(int orientation) {
		setRequestedOrientation(switch (orientation) {
			case ORIENTATION_DEFAULT -> SCREEN_ORIENTATION_UNSPECIFIED;
			case ORIENTATION_AUTO -> SCREEN_ORIENTATION_FULL_SENSOR;
			case ORIENTATION_PORTRAIT -> SCREEN_ORIENTATION_SENSOR_PORTRAIT;
			case ORIENTATION_LANDSCAPE -> SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
			default -> SCREEN_ORIENTATION_UNSPECIFIED;
		});
	}

	private void loadMIDlet() {
		Map<String, String> midlets;
		try {
			midlets = microLoader.loadMIDletList();
		} catch (IOException e) {
			showErrorDialog(e.toString());
			return;
		}
		int size = midlets.size();
		String[] midletsNameArray = midlets.values().toArray(new String[0]);
		String[] midletsClassArray = midlets.keySet().toArray(new String[0]);
		if (size == 0) {
			showErrorDialog("No MIDlets found");
		} else if (size == 1) {
			GameLog.i("Session", "Single MIDlet found: \"" + midletsNameArray[0] + "\" (" + midletsClassArray[0] + ')');
			microLoader.loadMidlet(midletsClassArray[0], appName);
		} else {
			showMidletDialog(midletsNameArray, midletsClassArray);
		}
	}

	private void showMidletDialog(String[] names, final String[] classes) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle(R.string.select_dialog_title)
				.setItems(names, (d, n) -> {
					String clazz = classes[n];
					ErrorReporter errorReporter = ACRA.getErrorReporter();
					String report = errorReporter.getCustomData(Constants.KEY_APPCENTER_ATTACHMENT);
					StringBuilder sb = new StringBuilder();
					if (report != null) {
						sb.append(report).append("\n");
					}
					sb.append("Begin app: ").append(names[n]).append(", ").append(clazz);
					errorReporter.putCustomData(Constants.KEY_APPCENTER_ATTACHMENT, sb.toString());
					GameLog.i("Session", "Selected MIDlet \"" + names[n] + "\" (" + clazz + ')');
					microLoader.loadMidlet(clazz, appName);
				})
				.setOnCancelListener(d -> {
					d.dismiss();
					MidletThread.notifyDestroyed();
				});
		builder.show();
	}

	void showErrorDialog(String message) {
		GameLog.e("Session", "Error dialog: " + message);
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle(R.string.error)
				.setMessage(message)
				.setPositiveButton(android.R.string.ok, (d, w) -> MidletThread.notifyDestroyed());
		builder.setOnCancelListener(dialogInterface -> MidletThread.notifyDestroyed());
		builder.show();
	}

	private float getToolBarHeight() {
		TypedValue typedValue = new TypedValue();
		if (getTheme().resolveAttribute(androidx.appcompat.R.attr.actionBarSize, typedValue, true)) {
			return typedValue.getDimension(getResources().getDisplayMetrics());
		}
		return 0;
	}

	private void hideSystemUI() {
		WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
		WindowInsetsControllerCompat controller =
				WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
		if (controller != null) {
			controller.setSystemBarsBehavior(
					WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
			int typesToHide = WindowInsetsCompat.Type.navigationBars();
			if (!statusBarEnabled) {
				typesToHide |= WindowInsetsCompat.Type.statusBars();
			}
			controller.hide(typesToHide);
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
			WindowManager.LayoutParams attributes = getWindow().getAttributes();
			attributes.layoutInDisplayCutoutMode =
					WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
			getWindow().setAttributes(attributes);
		}
	}

	private void showSystemUI() {
		WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
		WindowInsetsControllerCompat controller =
				WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
		if (controller != null) {
			controller.show(WindowInsetsCompat.Type.systemBars());
		}
	}

	private void applyClassicsViewSize(String size) {
		ConstraintLayout.LayoutParams params =
				(ConstraintLayout.LayoutParams) binding.gameFrame.getLayoutParams();
		if ("handset".equals(classicsControlStyle)) {
			params.matchConstraintPercentWidth = "large".equals(size) ? 0.93f : 0.89f;
			params.matchConstraintMaxWidth = dpToPx("large".equals(size) ? 430 : 408);
			params.topMargin = dpToPx(12);
			binding.gameFrame.setLayoutParams(params);
			return;
		}
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			if ("large".equals(size)) {
				params.matchConstraintPercentHeight = 0.88f;
				params.matchConstraintMaxHeight = dpToPx(620);
				params.topMargin = dpToPx(4);
			} else {
				params.matchConstraintPercentHeight = 0.82f;
				params.matchConstraintMaxHeight = dpToPx(580);
				params.topMargin = dpToPx(8);
			}
		} else {
			if ("large".equals(size)) {
				params.matchConstraintPercentWidth = 0.91f;
				params.matchConstraintMaxWidth = dpToPx(420);
				params.topMargin = dpToPx(8);
			} else {
				params.matchConstraintPercentWidth = 0.83f;
				params.matchConstraintMaxWidth = dpToPx(388);
				params.topMargin = dpToPx(14);
			}
		}
		binding.gameFrame.setLayoutParams(params);
	}

	private int dpToPx(int dp) {
		return Math.round(TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP,
				dp,
				getResources().getDisplayMetrics()));
	}

	private boolean isLandscapeUi() {
		return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
	}

	private void applyClassicsControlStyle(String style) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		classicsHandsetSkin = prefs.getString(PREF_CLASSICS_HANDSET_SKIN, "dark");
		boolean customSelected = CLASSICS_STYLE_CUSTOM.equals(style);
		boolean handsetSelected = "handset".equals(style);
		boolean handsetAvailable = handsetSelected && !isLandscapeUi();
		if (handsetAvailable) {
			classicsControlStyle = CLASSICS_STYLE_HANDSET;
		} else if (customSelected) {
			classicsControlStyle = CLASSICS_STYLE_CUSTOM;
		} else if (CLASSICS_STYLE_PHONE.equals(style) || handsetSelected) {
			classicsControlStyle = CLASSICS_STYLE_PHONE;
		} else {
			classicsControlStyle = CLASSICS_STYLE_JOYSTICK;
		}
		if (customSelected && !isLandscapeUi()) {
			setRequestedOrientation(SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		}
		int joystickVisibility = CLASSICS_STYLE_JOYSTICK.equals(classicsControlStyle)
				|| CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle) ? View.VISIBLE : View.GONE;
		int phoneVisibility = (CLASSICS_STYLE_PHONE.equals(classicsControlStyle)
				|| CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle)) ? View.VISIBLE : View.GONE;
		int handsetVisibility = CLASSICS_STYLE_HANDSET.equals(classicsControlStyle) ? View.VISIBLE : View.GONE;
		int actionClusterVisibility = CLASSICS_STYLE_JOYSTICK.equals(classicsControlStyle) ? View.VISIBLE : View.GONE;
		binding.controlPadShell.setVisibility(joystickVisibility);
		binding.actionCluster.setVisibility(actionClusterVisibility);
		binding.phoneShellContainer.setVisibility(phoneVisibility);
		binding.handsetShellContainer.setVisibility(handsetVisibility);
		binding.controlTopRow.setVisibility(CLASSICS_STYLE_HANDSET.equals(classicsControlStyle) ? View.GONE : View.VISIBLE);
		if (CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle)) {
			applyCustomControlArrangement();
			applyCustomControlSkin();
			binding.dpadConsoleBackdrop.setVisibility(View.GONE);
		} else {
			applyDefaultControlArrangement();
			binding.dpadConsoleBackdrop.setVisibility(View.VISIBLE);
		}
		applyHandsetSkin();
		ConstraintLayout.LayoutParams gameFrameParams =
				(ConstraintLayout.LayoutParams) binding.gameFrame.getLayoutParams();
		gameFrameParams.bottomToTop = CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)
				? R.id.handset_shell_container
				: R.id.control_top_row;
		if (CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle)) {
			gameFrameParams.matchConstraintPercentWidth = 0.58f;
			gameFrameParams.matchConstraintMaxWidth = dpToPx(360);
			gameFrameParams.topMargin = dpToPx(10);
			gameFrameParams.horizontalBias = 0.5f;
		}
		binding.gameFrame.setLayoutParams(gameFrameParams);
		applyClassicsViewSize(prefs.getString(PREF_CLASSICS_VIEW_SIZE, "default"));
	}

	private void applyDefaultControlArrangement() {
		ConstraintLayout.LayoutParams phoneParams =
				(ConstraintLayout.LayoutParams) binding.phoneShellContainer.getLayoutParams();
		phoneParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
		phoneParams.startToEnd = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.endToStart = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
		phoneParams.topToTop = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.topToBottom = R.id.control_top_row;
		phoneParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.bottomToBottom = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.horizontalBias = 0.5f;
		phoneParams.setMarginStart(dpToPx(0));
		phoneParams.setMarginEnd(dpToPx(0));
		binding.phoneShellContainer.setLayoutParams(phoneParams);

		ConstraintLayout.LayoutParams dpadParams =
				(ConstraintLayout.LayoutParams) binding.controlPadShell.getLayoutParams();
		dpadParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
		dpadParams.startToEnd = ConstraintLayout.LayoutParams.UNSET;
		dpadParams.endToStart = R.id.action_cluster;
		dpadParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
		dpadParams.topToTop = R.id.guide_controls_top;
		dpadParams.topToBottom = ConstraintLayout.LayoutParams.UNSET;
		dpadParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
		dpadParams.bottomToBottom = R.id.guide_controls_bottom;
		dpadParams.horizontalBias = 0.18f;
		dpadParams.setMarginStart(dpToPx(12));
		dpadParams.setMarginEnd(dpToPx(12));
		binding.controlPadShell.setLayoutParams(dpadParams);
	}

	private void applyCustomControlArrangement() {
		ConstraintLayout.LayoutParams phoneParams =
				(ConstraintLayout.LayoutParams) binding.phoneShellContainer.getLayoutParams();
		phoneParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
		phoneParams.startToEnd = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.endToStart = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.topToTop = R.id.game_frame;
		phoneParams.topToBottom = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
		phoneParams.bottomToBottom = R.id.game_frame;
		phoneParams.horizontalBias = 0.0f;
		phoneParams.verticalBias = 0.5f;
		phoneParams.setMarginStart(dpToPx(12));
		phoneParams.setMarginEnd(dpToPx(10));
		binding.phoneShellContainer.setLayoutParams(phoneParams);

		ConstraintLayout.LayoutParams dpadParams =
				(ConstraintLayout.LayoutParams) binding.controlPadShell.getLayoutParams();
		dpadParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
		dpadParams.startToEnd = ConstraintLayout.LayoutParams.UNSET;
		dpadParams.endToStart = ConstraintLayout.LayoutParams.UNSET;
		dpadParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
		dpadParams.topToTop = R.id.game_frame;
		dpadParams.topToBottom = ConstraintLayout.LayoutParams.UNSET;
		dpadParams.bottomToTop = ConstraintLayout.LayoutParams.UNSET;
		dpadParams.bottomToBottom = R.id.game_frame;
		dpadParams.horizontalBias = 1.0f;
		dpadParams.verticalBias = 0.5f;
		dpadParams.setMarginStart(dpToPx(10));
		dpadParams.setMarginEnd(dpToPx(12));
		binding.controlPadShell.setLayoutParams(dpadParams);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			binding.dpadConsoleBackdrop.setImageResource(R.drawable.bg_handset_game_background);
		} else {
			binding.dpadConsoleBackdrop.setImageResource(R.drawable.bg_handset_game_background);
		}
	}

	private void applyCustomControlSkin() {
		int buttonBackground = R.drawable.bg_handset_key;
		int softLeftBackground = R.drawable.bg_handset_softkey;
		int softRightBackground = R.drawable.bg_handset_softkey_right;
		int menuBackground = R.drawable.bg_handset_menu_key;
		int textColor = Color.parseColor("#D8D8D8");

		binding.buttonSoftLeftShell.setBackgroundResource(softLeftBackground);
		binding.buttonMenuShell.setBackgroundResource(menuBackground);
		binding.buttonSoftRightShell.setBackgroundResource(softRightBackground);
		binding.buttonSoftLeftShell.setTextColor(textColor);
		binding.buttonMenuShell.setTextColor(textColor);
		binding.buttonSoftRightShell.setTextColor(textColor);

		binding.phoneKey1.setBackgroundResource(buttonBackground);
		binding.phoneKey2.setBackgroundResource(buttonBackground);
		binding.phoneKey3.setBackgroundResource(buttonBackground);
		binding.phoneKey4.setBackgroundResource(buttonBackground);
		binding.phoneKey5.setBackgroundResource(buttonBackground);
		binding.phoneKey6.setBackgroundResource(buttonBackground);
		binding.phoneKey7.setBackgroundResource(buttonBackground);
		binding.phoneKey8.setBackgroundResource(buttonBackground);
		binding.phoneKey9.setBackgroundResource(buttonBackground);
		binding.phoneKeyStar.setBackgroundResource(buttonBackground);
		binding.phoneKey0.setBackgroundResource(buttonBackground);
		binding.phoneKeyPound.setBackgroundResource(buttonBackground);
		binding.phoneKey1.setTextColor(textColor);
		binding.phoneKey2.setTextColor(textColor);
		binding.phoneKey3.setTextColor(textColor);
		binding.phoneKey4.setTextColor(textColor);
		binding.phoneKey5.setTextColor(textColor);
		binding.phoneKey6.setTextColor(textColor);
		binding.phoneKey7.setTextColor(textColor);
		binding.phoneKey8.setTextColor(textColor);
		binding.phoneKey9.setTextColor(textColor);
		binding.phoneKeyStar.setTextColor(textColor);
		binding.phoneKey0.setTextColor(textColor);
		binding.phoneKeyPound.setTextColor(textColor);

		binding.dpadUpVisual.setBackgroundResource(buttonBackground);
		binding.dpadLeftVisual.setBackgroundResource(buttonBackground);
		binding.dpadRightVisual.setBackgroundResource(buttonBackground);
		binding.dpadDownVisual.setBackgroundResource(buttonBackground);
	}

	private void applyClassicsKeyMode(String mode) {
		classicsKeyMode = switch (mode) {
			case "arrows", "numbers" -> mode;
			default -> "mixed";
		};
		updateClassicsControlBounds();
	}

	private int getJoystickDpadUpKey() {
		return "numbers".equals(classicsKeyMode) ? Canvas.KEY_NUM2 : Canvas.KEY_UP;
	}

	private int getJoystickDpadLeftKey() {
		return "numbers".equals(classicsKeyMode) ? Canvas.KEY_NUM4 : Canvas.KEY_LEFT;
	}

	private int getJoystickDpadFireKey() {
		return "numbers".equals(classicsKeyMode) ? Canvas.KEY_NUM5 : Canvas.KEY_FIRE;
	}

	private int getJoystickDpadRightKey() {
		return "numbers".equals(classicsKeyMode) ? Canvas.KEY_NUM6 : Canvas.KEY_RIGHT;
	}

	private int getJoystickDpadDownKey() {
		return "numbers".equals(classicsKeyMode) ? Canvas.KEY_NUM8 : Canvas.KEY_DOWN;
	}

	private int getJoystickActionAKey() {
		return switch (classicsKeyMode) {
			case "arrows", "numbers" -> Canvas.KEY_FIRE;
			default -> Canvas.KEY_NUM7;
		};
	}

	private int getJoystickActionBKey() {
		return "numbers".equals(classicsKeyMode) ? Canvas.KEY_NUM9 : Canvas.KEY_NUM8;
	}

	private int getJoystickActionXKey() {
		return "numbers".equals(classicsKeyMode) ? Canvas.KEY_NUM7 : Canvas.KEY_NUM5;
	}

	private int getJoystickActionYKey() {
		return Canvas.KEY_NUM0;
	}

	private void applyHandsetSkin() {
		if (!"handset".equals(classicsControlStyle)) {
			binding.midletFrame.setBackgroundResource(R.drawable.bg_classics_game_background);
			binding.gameFrame.setBackgroundResource(R.drawable.bg_micro_display_frame);
			return;
		}
		boolean gold = "gold".equals(classicsHandsetSkin);
		boolean blue = "blue".equals(classicsHandsetSkin);
		boolean silver = "silver".equals(classicsHandsetSkin);
		int gameBackground = gold ? R.drawable.bg_handset_game_background_gold
				: blue ? R.drawable.bg_handset_game_background_blue
				: silver ? R.drawable.bg_handset_game_background_silver
				: R.drawable.bg_handset_game_background;
		int displayFrame = gold ? R.drawable.bg_handset_display_frame_gold
				: blue ? R.drawable.bg_handset_display_frame_blue
				: silver ? R.drawable.bg_handset_display_frame_silver
				: R.drawable.bg_handset_display_frame;
		int bodyBackground = gold ? R.drawable.bg_handset_body_gold
				: blue ? R.drawable.bg_handset_body_blue
				: silver ? R.drawable.bg_handset_body_silver
				: R.drawable.bg_handset_body;
		int softLeftBackground = gold ? R.drawable.bg_handset_softkey_gold
				: blue ? R.drawable.bg_handset_softkey_blue
				: silver ? R.drawable.bg_handset_softkey_silver
				: R.drawable.bg_handset_softkey;
		int menuBackground = gold ? R.drawable.bg_handset_menu_key_gold
				: blue ? R.drawable.bg_handset_menu_key_blue
				: silver ? R.drawable.bg_handset_menu_key_silver
				: R.drawable.bg_handset_menu_key;
		int softRightBackground = gold ? R.drawable.bg_handset_softkey_right_gold
				: blue ? R.drawable.bg_handset_softkey_right_blue
				: silver ? R.drawable.bg_handset_softkey_right_silver
				: R.drawable.bg_handset_softkey_right;
		int handsetKeyBackground = gold ? R.drawable.bg_handset_key_gold
				: blue ? R.drawable.bg_handset_key_blue
				: silver ? R.drawable.bg_handset_key_silver
				: R.drawable.bg_handset_key;
		binding.midletFrame.setBackgroundResource(gameBackground);
		binding.gameFrame.setBackgroundResource(displayFrame);
		binding.handsetShellContainer.setBackgroundResource(bodyBackground);
		binding.handsetSoftLeft.setBackgroundResource(softLeftBackground);
		binding.handsetMenu.setBackgroundResource(menuBackground);
		binding.handsetSoftRight.setBackgroundResource(softRightBackground);
		binding.handsetKey1.setBackgroundResource(handsetKeyBackground);
		binding.handsetKey2.setBackgroundResource(handsetKeyBackground);
		binding.handsetKey3.setBackgroundResource(handsetKeyBackground);
		binding.handsetKey4.setBackgroundResource(handsetKeyBackground);
		binding.handsetKey5.setBackgroundResource(handsetKeyBackground);
		binding.handsetKey6.setBackgroundResource(handsetKeyBackground);
		binding.handsetKey7.setBackgroundResource(handsetKeyBackground);
		binding.handsetKey8.setBackgroundResource(handsetKeyBackground);
		binding.handsetKey9.setBackgroundResource(handsetKeyBackground);
		binding.handsetKeyStar.setBackgroundResource(handsetKeyBackground);
		binding.handsetKey0.setBackgroundResource(handsetKeyBackground);
		binding.handsetKeyPound.setBackgroundResource(handsetKeyBackground);
		int handsetTextColor = (gold || silver) ? Color.parseColor("#121212") : Color.parseColor("#FFFFFF");
		binding.handsetKey1.setTextColor(handsetTextColor);
		binding.handsetKey2.setTextColor(handsetTextColor);
		binding.handsetKey3.setTextColor(handsetTextColor);
		binding.handsetKey4.setTextColor(handsetTextColor);
		binding.handsetKey5.setTextColor(handsetTextColor);
		binding.handsetKey6.setTextColor(handsetTextColor);
		binding.handsetKey7.setTextColor(handsetTextColor);
		binding.handsetKey8.setTextColor(handsetTextColor);
		binding.handsetKey9.setTextColor(handsetTextColor);
		binding.handsetKeyStar.setTextColor(handsetTextColor);
		binding.handsetKey0.setTextColor(handsetTextColor);
		binding.handsetKeyPound.setTextColor(handsetTextColor);
	}

	private void updateCanvasViewport() {
		if (!(current instanceof Canvas)) {
			ContextHolder.clearCanvasViewport();
			return;
		}
		int width = binding.displayableContainer.getWidth();
		int height = binding.displayableContainer.getHeight();
		if (width <= 0 || height <= 0) {
			return;
		}
		int[] location = new int[2];
		binding.displayableContainer.getLocationInWindow(location);
		ContextHolder.setCanvasViewport(location[0], location[1],
				location[0] + width, location[1] + height);
		((Canvas) current).updateSize();
	}

	private void updateClassicsControlBounds() {
		if (!(current instanceof Canvas)) {
			ContextHolder.clearClassicsControlBounds();
			return;
		}
		SparseArray<Rect> keyBounds = new SparseArray<>();
		addKeyBound(keyBounds, Canvas.KEY_SOFT_LEFT, binding.buttonSoftLeftShell);
		addKeyBound(keyBounds, KeyMapper.KEY_OPTIONS_MENU, binding.buttonMenuShell);
		addKeyBound(keyBounds, Canvas.KEY_SOFT_RIGHT, binding.buttonSoftRightShell);
		if (CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle)) {
			addKeyBound(keyBounds, Canvas.KEY_NUM1, binding.phoneKey1);
			addKeyBound(keyBounds, Canvas.KEY_NUM2, binding.phoneKey2);
			addKeyBound(keyBounds, Canvas.KEY_NUM3, binding.phoneKey3);
			addKeyBound(keyBounds, Canvas.KEY_NUM4, binding.phoneKey4);
			addKeyBound(keyBounds, Canvas.KEY_NUM5, binding.phoneKey5);
			addKeyBound(keyBounds, Canvas.KEY_NUM6, binding.phoneKey6);
			addKeyBound(keyBounds, Canvas.KEY_NUM7, binding.phoneKey7);
			addKeyBound(keyBounds, Canvas.KEY_NUM8, binding.phoneKey8);
			addKeyBound(keyBounds, Canvas.KEY_NUM9, binding.phoneKey9);
			addKeyBound(keyBounds, Canvas.KEY_STAR, binding.phoneKeyStar);
			addKeyBound(keyBounds, Canvas.KEY_NUM0, binding.phoneKey0);
			addKeyBound(keyBounds, Canvas.KEY_POUND, binding.phoneKeyPound);
			Rect dpad = getViewBounds(binding.controlPadShell);
			addKeyBound(keyBounds, Canvas.KEY_UP, subdivideRect(dpad, 1, 0));
			addKeyBound(keyBounds, Canvas.KEY_LEFT, subdivideRect(dpad, 0, 1));
			addKeyBound(keyBounds, Canvas.KEY_FIRE, subdivideRect(dpad, 1, 1));
			addKeyBound(keyBounds, Canvas.KEY_RIGHT, subdivideRect(dpad, 2, 1));
			addKeyBound(keyBounds, Canvas.KEY_DOWN, subdivideRect(dpad, 1, 2));
		} else if (CLASSICS_STYLE_PHONE.equals(classicsControlStyle)) {
			addKeyBound(keyBounds, Canvas.KEY_NUM1, binding.phoneKey1);
			addKeyBound(keyBounds, Canvas.KEY_NUM2, binding.phoneKey2);
			addKeyBound(keyBounds, Canvas.KEY_NUM3, binding.phoneKey3);
			addKeyBound(keyBounds, Canvas.KEY_NUM4, binding.phoneKey4);
			addKeyBound(keyBounds, Canvas.KEY_NUM5, binding.phoneKey5);
			addKeyBound(keyBounds, Canvas.KEY_NUM6, binding.phoneKey6);
			addKeyBound(keyBounds, Canvas.KEY_NUM7, binding.phoneKey7);
			addKeyBound(keyBounds, Canvas.KEY_NUM8, binding.phoneKey8);
			addKeyBound(keyBounds, Canvas.KEY_NUM9, binding.phoneKey9);
			addKeyBound(keyBounds, Canvas.KEY_STAR, binding.phoneKeyStar);
			addKeyBound(keyBounds, Canvas.KEY_NUM0, binding.phoneKey0);
			addKeyBound(keyBounds, Canvas.KEY_POUND, binding.phoneKeyPound);
		} else if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) {
			addKeyBound(keyBounds, Canvas.KEY_SOFT_LEFT, binding.handsetSoftLeft);
			addKeyBound(keyBounds, KeyMapper.KEY_OPTIONS_MENU, binding.handsetMenu);
			addKeyBound(keyBounds, Canvas.KEY_SOFT_RIGHT, binding.handsetSoftRight);
			addKeyBound(keyBounds, Canvas.KEY_NUM1, binding.handsetKey1);
			addKeyBound(keyBounds, Canvas.KEY_NUM2, binding.handsetKey2);
			addKeyBound(keyBounds, Canvas.KEY_NUM3, binding.handsetKey3);
			addKeyBound(keyBounds, Canvas.KEY_NUM4, binding.handsetKey4);
			addKeyBound(keyBounds, Canvas.KEY_NUM5, binding.handsetKey5);
			addKeyBound(keyBounds, Canvas.KEY_NUM6, binding.handsetKey6);
			addKeyBound(keyBounds, Canvas.KEY_NUM7, binding.handsetKey7);
			addKeyBound(keyBounds, Canvas.KEY_NUM8, binding.handsetKey8);
			addKeyBound(keyBounds, Canvas.KEY_NUM9, binding.handsetKey9);
			addKeyBound(keyBounds, Canvas.KEY_STAR, binding.handsetKeyStar);
			addKeyBound(keyBounds, Canvas.KEY_NUM0, binding.handsetKey0);
			addKeyBound(keyBounds, Canvas.KEY_POUND, binding.handsetKeyPound);
		} else {
			Rect dpad = getViewBounds(binding.controlPadShell);
			addKeyBound(keyBounds, getJoystickDpadUpKey(), subdivideRect(dpad, 1, 0));
			addKeyBound(keyBounds, getJoystickDpadLeftKey(), subdivideRect(dpad, 0, 1));
			addKeyBound(keyBounds, getJoystickDpadFireKey(), subdivideRect(dpad, 1, 1));
			addKeyBound(keyBounds, getJoystickDpadRightKey(), subdivideRect(dpad, 2, 1));
			addKeyBound(keyBounds, getJoystickDpadDownKey(), subdivideRect(dpad, 1, 2));
			if (CLASSICS_STYLE_JOYSTICK.equals(classicsControlStyle)) {
				addKeyBound(keyBounds, getJoystickActionAKey(), binding.buttonAShell);
				addKeyBound(keyBounds, getJoystickActionBKey(), binding.buttonBShell);
				addKeyBound(keyBounds, getJoystickActionXKey(), binding.buttonXShell);
				addKeyBound(keyBounds, getJoystickActionYKey(), binding.buttonYShell);
			}
		}
		if (keyBounds.get(Canvas.KEY_SOFT_LEFT) == null
				|| keyBounds.get(KeyMapper.KEY_OPTIONS_MENU) == null
				|| keyBounds.get(Canvas.KEY_SOFT_RIGHT) == null
				|| keyBounds.size() < 6) {
			ContextHolder.clearClassicsControlBounds();
			return;
		}
		ContextHolder.setClassicsControlBounds(classicsControlStyle, keyBounds);
		((Canvas) current).updateSize();
	}

	private void addKeyBound(SparseArray<Rect> keyBounds, int keyCode, View view) {
		Rect rect = getViewBounds(view);
		if (!rect.isEmpty()) {
			keyBounds.put(keyCode, rect);
		}
	}

	private void addKeyBound(SparseArray<Rect> keyBounds, int keyCode, Rect rect) {
		if (!rect.isEmpty()) {
			keyBounds.put(keyCode, rect);
		}
	}

	private Rect subdivideRect(Rect bounds, int column, int row) {
		if (bounds.isEmpty()) {
			return new Rect();
		}
		float cellWidth = bounds.width() / 3f;
		float cellHeight = bounds.height() / 3f;
		float insetX = cellWidth * 0.06f;
		float insetY = cellHeight * 0.06f;
		return new Rect(
				Math.round(bounds.left + column * cellWidth + insetX),
				Math.round(bounds.top + row * cellHeight + insetY),
				Math.round(bounds.left + (column + 1) * cellWidth - insetX),
				Math.round(bounds.top + (row + 1) * cellHeight - insetY));
	}

	private Rect getViewBounds(View view) {
		Rect rect = new Rect();
		int[] overlayLocation = new int[2];
		int[] location = new int[2];
		binding.overlay.getLocationInWindow(overlayLocation);
		view.getLocationInWindow(location);
		rect.set(location[0] - overlayLocation[0], location[1] - overlayLocation[1],
				location[0] - overlayLocation[0] + view.getWidth(),
				location[1] - overlayLocation[1] + view.getHeight());
		return rect;
	}

	private boolean updateJoystickPressedVisual(int keyCode, boolean pressed) {
		if (!CLASSICS_STYLE_JOYSTICK.equals(classicsControlStyle)) {
			return false;
		}
		boolean handled = false;
		if (keyCode == getJoystickDpadUpKey()) {
			binding.dpadUpVisual.setPressed(pressed);
			handled = true;
		}
		if (keyCode == getJoystickDpadDownKey()) {
			binding.dpadDownVisual.setPressed(pressed);
			handled = true;
		}
		if (keyCode == getJoystickDpadLeftKey()) {
			binding.dpadLeftVisual.setPressed(pressed);
			handled = true;
		}
		if (keyCode == getJoystickDpadRightKey()) {
			binding.dpadRightVisual.setPressed(pressed);
			handled = true;
		}
		if (keyCode == getJoystickActionAKey()) {
			binding.buttonAShell.setPressed(pressed);
			handled = true;
		}
		if (keyCode == getJoystickActionBKey()) {
			binding.buttonBShell.setPressed(pressed);
			handled = true;
		}
		if (keyCode == getJoystickActionXKey()) {
			binding.buttonXShell.setPressed(pressed);
			handled = true;
		}
		if (keyCode == getJoystickActionYKey()) {
			binding.buttonYShell.setPressed(pressed);
			handled = true;
		}
		return handled;
	}

	public void setClassicsKeyPressed(int keyCode, boolean pressed) {
		if (pressed) {
			switch (keyCode) {
				case Canvas.KEY_SOFT_LEFT, Canvas.KEY_SOFT_RIGHT -> uiSounds().playLr();
				case KeyMapper.KEY_OPTIONS_MENU -> uiSounds().playStart();
				case Canvas.KEY_UP, Canvas.KEY_DOWN, Canvas.KEY_LEFT, Canvas.KEY_RIGHT ->
						uiSounds().playDpad();
				case Canvas.KEY_FIRE ->
						uiSounds().playStart();
				case Canvas.KEY_NUM1, Canvas.KEY_NUM2, Canvas.KEY_NUM3, Canvas.KEY_NUM4,
						Canvas.KEY_NUM5, Canvas.KEY_NUM6, Canvas.KEY_NUM7, Canvas.KEY_NUM8,
						Canvas.KEY_NUM9, Canvas.KEY_NUM0, Canvas.KEY_STAR, Canvas.KEY_POUND ->
						uiSounds().playAction();
			}
		}
		runOnUiThread(() -> {
			if (updateJoystickPressedVisual(keyCode, pressed)) {
				return;
			}
			switch (keyCode) {
				case Canvas.KEY_SOFT_LEFT -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetSoftLeft.setPressed(pressed);
					else binding.buttonSoftLeftShell.setPressed(pressed);
				}
				case Canvas.KEY_SOFT_RIGHT -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetSoftRight.setPressed(pressed);
					else binding.buttonSoftRightShell.setPressed(pressed);
				}
				case KeyMapper.KEY_OPTIONS_MENU -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetMenu.setPressed(pressed);
					else binding.buttonMenuShell.setPressed(pressed);
				}
				case Canvas.KEY_UP -> binding.dpadUpVisual.setPressed(pressed);
				case Canvas.KEY_DOWN -> binding.dpadDownVisual.setPressed(pressed);
				case Canvas.KEY_LEFT -> binding.dpadLeftVisual.setPressed(pressed);
				case Canvas.KEY_RIGHT -> binding.dpadRightVisual.setPressed(pressed);
				case Canvas.KEY_NUM1 -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetKey1.setPressed(pressed);
					else binding.phoneKey1.setPressed(pressed);
				}
				case Canvas.KEY_NUM2 -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetKey2.setPressed(pressed);
					else binding.phoneKey2.setPressed(pressed);
				}
				case Canvas.KEY_NUM3 -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetKey3.setPressed(pressed);
					else binding.phoneKey3.setPressed(pressed);
				}
				case Canvas.KEY_NUM4 -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetKey4.setPressed(pressed);
					else binding.phoneKey4.setPressed(pressed);
				}
				case Canvas.KEY_NUM5 -> {
					if (CLASSICS_STYLE_PHONE.equals(classicsControlStyle)
							|| CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle)) {
						binding.phoneKey5.setPressed(pressed);
					} else if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) {
						binding.handsetKey5.setPressed(pressed);
					}
					else binding.buttonXShell.setPressed(pressed);
				}
				case Canvas.KEY_NUM6 -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetKey6.setPressed(pressed);
					else binding.phoneKey6.setPressed(pressed);
				}
				case Canvas.KEY_NUM7 -> {
					if (CLASSICS_STYLE_PHONE.equals(classicsControlStyle)
							|| CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle)) {
						binding.phoneKey7.setPressed(pressed);
					} else if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) {
						binding.handsetKey7.setPressed(pressed);
					}
					else binding.buttonAShell.setPressed(pressed);
				}
				case Canvas.KEY_NUM8 -> {
					if (CLASSICS_STYLE_PHONE.equals(classicsControlStyle)
							|| CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle)) {
						binding.phoneKey8.setPressed(pressed);
					} else if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) {
						binding.handsetKey8.setPressed(pressed);
					}
					else binding.buttonBShell.setPressed(pressed);
				}
				case Canvas.KEY_NUM9 -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetKey9.setPressed(pressed);
					else binding.phoneKey9.setPressed(pressed);
				}
				case Canvas.KEY_NUM0 -> {
					if (CLASSICS_STYLE_PHONE.equals(classicsControlStyle)
							|| CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle)) {
						binding.phoneKey0.setPressed(pressed);
					} else if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) {
						binding.handsetKey0.setPressed(pressed);
					}
					else binding.buttonYShell.setPressed(pressed);
				}
				case Canvas.KEY_STAR -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetKeyStar.setPressed(pressed);
					else binding.phoneKeyStar.setPressed(pressed);
				}
				case Canvas.KEY_POUND -> {
					if (CLASSICS_STYLE_HANDSET.equals(classicsControlStyle)) binding.handsetKeyPound.setPressed(pressed);
					else binding.phoneKeyPound.setPressed(pressed);
				}
			}
		});
	}

	public void setCurrent(Displayable displayable) {
		ViewHandler.postEvent(new SetCurrentEvent(current, displayable));
		current = displayable;
	}

	public Displayable getCurrent() {
		return current;
	}

	public boolean isVisible() {
		return getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED);
	}

	public void showExitConfirmation() {
		View view = getLayoutInflater().inflate(R.layout.dialog_gameplay_confirmation, null);
		AlertDialog dialog = new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme)
				.setView(view)
				.create();
		view.findViewById(R.id.gameplay_confirm_ok).setOnClickListener(v -> {
			uiSounds().playConfirm();
			hideSoftInput();
			dialog.dismiss();
			MidletThread.destroyApp();
		});
		view.findViewById(R.id.gameplay_confirm_settings).setOnClickListener(v -> {
			uiSounds().playConfirm();
			hideSoftInput();
			dialog.dismiss();
			Config.openSettings(this, appName, appPath);
			MidletThread.destroyApp();
		});
		view.findViewById(R.id.gameplay_confirm_cancel).setOnClickListener(v -> {
			uiSounds().playBack();
			dialog.dismiss();
		});
		dialog.setOnDismissListener(d -> {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && current instanceof Canvas) {
				hideSystemUI();
			}
		});
		dialog.show();
		if (dialog.getWindow() != null) {
			WindowCompat.setDecorFitsSystemWindows(dialog.getWindow(), false);
			WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(
					dialog.getWindow(),
					dialog.getWindow().getDecorView());
			if (controller != null) {
				controller.setSystemBarsBehavior(
						WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
				controller.hide(WindowInsetsCompat.Type.systemBars());
			}
		}
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU)
			if (current instanceof Canvas && binding.displayableContainer.dispatchKeyEvent(event)) {
				return true;
			} else if (event.getAction() == KeyEvent.ACTION_DOWN) {
				if (event.getRepeatCount() == 0) {
					event.startTracking();
					return true;
				} else if (event.isLongPress()) {
					return onKeyLongPress(event.getKeyCode(), event);
				}
			} else if (event.getAction() == KeyEvent.ACTION_UP) {
				return onKeyUp(event.getKeyCode(), event);
			}
		return super.dispatchKeyEvent(event);
	}

	@Override
	public void openOptionsMenu() {
		if (current instanceof Canvas) {
			uiSounds().playStart();
			showGameplayMenuDialog();
			return;
		}
		if (!actionBarEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			showSystemUI();
		}
		super.openOptionsMenu();
	}

	@Override
	public boolean onKeyLongPress(int keyCode, KeyEvent event) {
		if (keyCode == menuKey || keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU) {
			showExitConfirmation();
			return true;
		}
		return super.onKeyLongPress(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_MENU) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((keyCode == menuKey || keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU)
				&& (event.getFlags() & (KeyEvent.FLAG_LONG_PRESS | KeyEvent.FLAG_CANCELED)) == 0) {
			openOptionsMenu();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.midlet_displayable, menu);
		if (actionBarEnabled) {
			menu.findItem(R.id.action_ime_keyboard).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.findItem(R.id.action_take_screenshot).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		}
		if (inputMethodManager == null) {
			menu.findItem(R.id.action_ime_keyboard).setVisible(false);
		}
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (current instanceof Canvas) {
			menu.setGroupVisible(R.id.action_group_canvas, true);
		} else {
			menu.setGroupVisible(R.id.action_group_canvas, false);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_exit_midlet) {
			uiSounds().playBack();
			showExitConfirmation();
		} else if (id == R.id.action_save_log) {
			uiSounds().playConfirm();
			saveLog();
		} else if (id == R.id.action_lock_orientation) {
			uiSounds().playConfirm();
			if (item.isChecked()) {
				VirtualKeyboard vk = ContextHolder.getVk();
				int orientation = vk != null && vk.isPhone() ? ORIENTATION_PORTRAIT : microLoader.getOrientation();
				setOrientation(orientation);
				item.setChecked(false);
			} else {
				lockOrientation();
				item.setChecked(true);
			}
		} else if (id == R.id.action_ime_keyboard) {
			uiSounds().playConfirm();
			inputMethodManager.toggleSoftInputFromWindow(binding.displayableContainer.getWindowToken(),
					InputMethodManager.SHOW_FORCED, 0);
		} else if (id == R.id.action_take_screenshot) {
			uiSounds().playConfirm();
			takeScreenshot();
		} else if (id == R.id.action_limit_fps) {
			uiSounds().playConfirm();
			showLimitFpsDialog();
		} else if (id == R.id.action_multiplayer) {
			uiSounds().playConfirm();
			showMultiplayerDialog();
		}
		return true;
	}

	private void lockOrientation() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			setRequestedOrientation(SCREEN_ORIENTATION_LOCKED);
			return;
		}
		Configuration configuration = getResources().getConfiguration();
		int rotation = getWindowManager().getDefaultDisplay().getRotation();

		// Search for the natural position of the device
		if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
				(rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) ||
				configuration.orientation == Configuration.ORIENTATION_PORTRAIT &&
						(rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) {
			// Natural position is Landscape
			setRequestedOrientation(switch (rotation) {
				case Surface.ROTATION_0 -> SCREEN_ORIENTATION_LANDSCAPE;
				case Surface.ROTATION_90 -> SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				case Surface.ROTATION_180 -> SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				case Surface.ROTATION_270 -> SCREEN_ORIENTATION_PORTRAIT;
				default -> SCREEN_ORIENTATION_UNSPECIFIED;
			});
		} else {
			// Natural position is Portrait
			setRequestedOrientation(switch (rotation) {
				case Surface.ROTATION_0 -> SCREEN_ORIENTATION_PORTRAIT;
				case Surface.ROTATION_90 -> SCREEN_ORIENTATION_LANDSCAPE;
				case Surface.ROTATION_180 -> SCREEN_ORIENTATION_REVERSE_PORTRAIT;
				case Surface.ROTATION_270 -> SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
				default -> SCREEN_ORIENTATION_UNSPECIFIED;
			});
		}
	}

	private boolean isOrientationLocked() {
		int requested = getRequestedOrientation();
		return requested == SCREEN_ORIENTATION_LOCKED
				|| requested == SCREEN_ORIENTATION_LANDSCAPE
				|| requested == SCREEN_ORIENTATION_REVERSE_LANDSCAPE
				|| requested == SCREEN_ORIENTATION_PORTRAIT
				|| requested == SCREEN_ORIENTATION_REVERSE_PORTRAIT
				|| requested == SCREEN_ORIENTATION_SENSOR_LANDSCAPE
				|| requested == SCREEN_ORIENTATION_SENSOR_PORTRAIT;
	}

	private void unlockOrientation() {
		setOrientation(microLoader.getOrientation());
	}

	private void showGameplayMenuDialog() {
		if (!(current instanceof Canvas)) return;
		if (gameplayMenuDialog != null && gameplayMenuDialog.isShowing()) {
			return;
		}
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_gameplay_menu, null, false);
		CheckBox lockCheckBox = view.findViewById(R.id.gameplay_menu_lock_checkbox);
		lockCheckBox.setChecked(isOrientationLocked());
		View lockRow = view.findViewById(R.id.gameplay_menu_lock_row);
		TextView imeRow = view.findViewById(R.id.gameplay_menu_ime);
		view.findViewById(R.id.gameplay_menu_exit).setOnClickListener(v -> {
			uiSounds().playBack();
			if (gameplayMenuDialog != null) gameplayMenuDialog.dismiss();
			showExitConfirmation();
		});
		view.findViewById(R.id.gameplay_menu_save_log).setOnClickListener(v -> {
			uiSounds().playConfirm();
			if (gameplayMenuDialog != null) gameplayMenuDialog.dismiss();
			saveLog();
		});
		lockRow.setOnClickListener(v -> {
			uiSounds().playConfirm();
			boolean locked = isOrientationLocked();
			if (locked) {
				unlockOrientation();
			} else {
				lockOrientation();
			}
			lockCheckBox.setChecked(!locked);
		});
		if (inputMethodManager == null) {
			imeRow.setVisibility(View.GONE);
		} else {
			imeRow.setOnClickListener(v -> {
				uiSounds().playConfirm();
				if (gameplayMenuDialog != null) gameplayMenuDialog.dismiss();
				inputMethodManager.toggleSoftInputFromWindow(
						binding.displayableContainer.getWindowToken(),
						InputMethodManager.SHOW_FORCED,
						0);
			});
		}
		view.findViewById(R.id.gameplay_menu_screenshot).setOnClickListener(v -> {
			uiSounds().playConfirm();
			if (gameplayMenuDialog != null) gameplayMenuDialog.dismiss();
			takeScreenshot();
		});
		view.findViewById(R.id.gameplay_menu_limit_fps).setOnClickListener(v -> {
			uiSounds().playConfirm();
			if (gameplayMenuDialog != null) gameplayMenuDialog.dismiss();
			showLimitFpsDialog();
		});
		view.findViewById(R.id.gameplay_menu_key_mode).setOnClickListener(v -> {
			uiSounds().playConfirm();
			if (gameplayMenuDialog != null) gameplayMenuDialog.dismiss();
			showClassicsKeyModeDialog();
		});
		view.findViewById(R.id.gameplay_menu_savestate).setOnClickListener(v -> {
			uiSounds().playConfirm();
			if (gameplayMenuDialog != null) gameplayMenuDialog.dismiss();
			showSavestateDialog();
		});
		View customControlsRow = view.findViewById(R.id.gameplay_menu_custom_controls);
		customControlsRow.setVisibility(View.VISIBLE);
		customControlsRow.setOnClickListener(v -> {
			uiSounds().playConfirm();
			if (gameplayMenuDialog != null) gameplayMenuDialog.dismiss();
			if (CLASSICS_STYLE_CUSTOM.equals(classicsControlStyle) && ContextHolder.getVk() != null) {
				showCustomControlsEditorDialog();
			} else {
				Toast.makeText(this, R.string.custom_controls_edit_hint, Toast.LENGTH_LONG).show();
			}
		});
		View multiplayerRow = view.findViewById(R.id.gameplay_menu_multiplayer);
		multiplayerRow.setEnabled(true);
		multiplayerRow.setAlpha(1f);
		multiplayerRow.setOnClickListener(v -> {
			uiSounds().playConfirm();
			if (gameplayMenuDialog != null) gameplayMenuDialog.dismiss();
			showMultiplayerDialog();
		});
		gameplayMenuDialog = new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme)
				.setView(view)
				.create();
		gameplayMenuDialog.setOnDismissListener(d -> {
			gameplayMenuDialog = null;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && current instanceof Canvas) {
				hideSystemUI();
			}
		});
		gameplayMenuDialog.show();
		if (gameplayMenuDialog.getWindow() != null) {
			WindowCompat.setDecorFitsSystemWindows(gameplayMenuDialog.getWindow(), false);
			WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(
					gameplayMenuDialog.getWindow(),
					gameplayMenuDialog.getWindow().getDecorView());
			if (controller != null) {
				controller.setSystemBarsBehavior(
						WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
				controller.hide(WindowInsetsCompat.Type.systemBars());
			}
		}
	}

	private void showCustomControlsEditorDialog() {
		final VirtualKeyboard vk = ContextHolder.getVk();
		if (vk == null) {
			return;
		}
		String[] items = {
				getString(R.string.custom_controls_move),
				getString(R.string.custom_controls_resize),
				getString(R.string.hide_buttons)
		};
		new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme)
				.setTitle(R.string.custom_controls_editor)
				.setItems(items, (dialog, which) -> {
					switch (which) {
						case 0 -> {
							vk.setLayoutEditMode(VirtualKeyboard.LAYOUT_KEYS);
							Toast.makeText(this, R.string.custom_controls_edit_hint, Toast.LENGTH_LONG).show();
						}
						case 1 -> {
							vk.setLayoutEditMode(VirtualKeyboard.LAYOUT_SCALES);
							Toast.makeText(this, R.string.custom_controls_edit_hint, Toast.LENGTH_LONG).show();
						}
						case 2 -> showHideButtonDialog();
					}
				})
				.setPositiveButton(R.string.custom_controls_finish, (dialog, which) -> {
					vk.setLayoutEditMode(VirtualKeyboard.LAYOUT_EOF);
					vk.onLayoutChanged(VirtualKeyboard.TYPE_CUSTOM);
					Toast.makeText(this, R.string.custom_controls_editor, Toast.LENGTH_SHORT).show();
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	@SuppressLint("CheckResult")
	private void takeScreenshot() {
		microLoader.takeScreenshot(current, new SingleObserver<>() {
			@Override
			public void onSubscribe(@NonNull Disposable d) {
			}

			@Override
			public void onSuccess(@NonNull String s) {
				Toast.makeText(MicroActivity.this, getString(R.string.screenshot_saved)
						+ " " + s, Toast.LENGTH_LONG).show();
				MediaScannerConnection.scanFile(MicroActivity.this, new String[]{s}, null, null);
			}

			@Override
			public void onError(@NonNull Throwable e) {
				e.printStackTrace();
				Toast.makeText(MicroActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void saveLog() {
		try {
			LogUtils.writeLog();
			GameLog.i("Session", "Filtered game log saved");
			Toast.makeText(this, R.string.log_saved, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			GameLog.e("Session", "Failed to save filtered game log", e);
			e.printStackTrace();
			Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
		}
	}

	private void showSavestateDialog() {
		File appDataDir = getCurrentAppDataDir();
		List<File> states = SavestateManager.listStates(appDataDir);
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme)
				.setTitle(R.string.savestate_title)
				.setPositiveButton(R.string.savestate_save_current, (dialog, which) -> saveCurrentSavestate())
				.setNegativeButton(android.R.string.cancel, null);
		if (states.isEmpty()) {
			builder.setMessage(R.string.savestate_empty);
		} else {
			CharSequence[] items = new CharSequence[states.size()];
			for (int i = 0; i < states.size(); i++) {
				items[i] = formatSavestateLabel(states.get(i));
			}
			builder.setItems(items, (dialog, which) -> requestRestoreSavestate(states.get(which)));
		}
		builder.show();
	}

	private void saveCurrentSavestate() {
		new Thread(() -> {
			try {
				File saved = SavestateManager.saveState(getCurrentAppDataDir());
				GameLog.i("Savestate", "Saved state to " + saved);
				runOnUiThread(() -> Toast.makeText(MicroActivity.this,
						getString(R.string.savestate_saved, saved.getName()),
						Toast.LENGTH_LONG).show());
			} catch (IOException e) {
				GameLog.e("Savestate", "Failed to save state", e);
				runOnUiThread(() -> Toast.makeText(MicroActivity.this,
						R.string.error, Toast.LENGTH_SHORT).show());
			}
		}, "SavestateSave").start();
	}

	private void requestRestoreSavestate(File stateFile) {
		if (stateFile == null || !stateFile.isFile()) {
			Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
			return;
		}
		try {
			SavestateManager.setPendingRestore(this, stateFile.getAbsolutePath());
			scheduleGameRelaunch();
		} catch (Exception e) {
			GameLog.e("Savestate", "Failed to request restore", e);
			Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
		}
	}

	private void scheduleGameRelaunch() {
		Intent launchIntent = new Intent(Intent.ACTION_DEFAULT, Uri.parse(appPath), this, MicroActivity.class);
		launchIntent.putExtra(KEY_MIDLET_NAME, appName);
		int flags = PendingIntent.FLAG_UPDATE_CURRENT;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			flags |= PendingIntent.FLAG_IMMUTABLE;
		}
		PendingIntent pendingIntent = PendingIntent.getActivity(this, appPath.hashCode(), launchIntent, flags);
		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		if (alarmManager != null) {
			long triggerAt = SystemClock.elapsedRealtime() + 1500L;
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pendingIntent);
		}
		Toast.makeText(this, R.string.savestate_restore_pending, Toast.LENGTH_SHORT).show();
		MidletThread.destroyApp();
	}

	private void applyPendingSavestateIfAny() {
		String pendingState = SavestateManager.consumePendingRestore(this);
		if (pendingState == null || pendingState.isBlank()) {
			return;
		}
		File stateFile = new File(pendingState);
		try {
			SavestateManager.restoreState(getCurrentAppDataDir(), stateFile);
			GameLog.i("Savestate", "Restored state from " + stateFile);
		} catch (Exception e) {
			GameLog.e("Savestate", "Failed to restore state from " + stateFile, e);
			Toast.makeText(this, R.string.savestate_restore_failed, Toast.LENGTH_SHORT).show();
		}
	}

	private File getCurrentAppDataDir() {
		return new File(Config.getDataDir(), new File(appPath).getName());
	}

	private CharSequence formatSavestateLabel(File stateFile) {
		String name = stateFile.getName();
		if (name.endsWith(ru.playsoftware.j2meloader.util.SavestateManager.STATE_FILE_SUFFIX)) {
			name = name.substring(0,
					name.length() - ru.playsoftware.j2meloader.util.SavestateManager.STATE_FILE_SUFFIX.length());
		}
		return name;
	}

	private void showHideButtonDialog() {
		final VirtualKeyboard vk = ContextHolder.getVk();
		boolean[] states = vk.getKeysVisibility();
		boolean[] changed = states.clone();
		new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme)
				.setTitle(R.string.hide_buttons)
				.setMultiChoiceItems(vk.getKeyNames(), changed, (dialog, which, isChecked) -> {})
				.setPositiveButton(android.R.string.ok, (dialog, which) -> {
					if (!Arrays.equals(states, changed)) {
						vk.setKeysVisibility(changed);
						showSaveVkAlert(true);
					}
				}).show();
	}

	private void showSaveVkAlert(boolean keepScreenPreferred) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme);
		builder.setTitle(R.string.CONFIRMATION_REQUIRED);
		builder.setMessage(R.string.pref_vk_save_alert);
		builder.setNegativeButton(android.R.string.no, null);
		AlertDialog dialog = builder.create();

		final VirtualKeyboard vk = ContextHolder.getVk();
		if (vk.isPhone()) {
			AppCompatCheckBox cb = new AppCompatCheckBox(this);
			cb.setText(R.string.opt_save_screen_params);
			cb.setChecked(keepScreenPreferred);

			TypedValue out = new TypedValue();
			getTheme().resolveAttribute(androidx.appcompat.R.attr.dialogPreferredPadding, out, true);
			int paddingH = getResources().getDimensionPixelOffset(out.resourceId);
			int paddingT = getResources().getDimensionPixelOffset(androidx.appcompat.R.dimen.abc_dialog_padding_top_material);
			dialog.setView(cb, paddingH, paddingT, paddingH, 0);

			dialog.setButton(dialog.BUTTON_POSITIVE, getText(android.R.string.yes), (d, w) -> {
				if (cb.isChecked()) {
					vk.saveScreenParams();
				}
				vk.onLayoutChanged(VirtualKeyboard.TYPE_CUSTOM);
			});
		} else {
			dialog.setButton(dialog.BUTTON_POSITIVE, getText(android.R.string.yes), (d, w) ->
					ContextHolder.getVk().onLayoutChanged(VirtualKeyboard.TYPE_CUSTOM));
		}
		dialog.show();
	}

	private void showSetLayoutDialog() {
		final VirtualKeyboard vk = ContextHolder.getVk();
		AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme)
				.setTitle(R.string.layout_switch)
				.setSingleChoiceItems(R.array.PREF_VK_TYPE_ENTRIES, vk.getLayout(), null)
				.setPositiveButton(android.R.string.ok, (d, w) -> {
					vk.setLayout(((AlertDialog) d).getListView().getCheckedItemPosition());
					if (vk.isPhone()) {
						setOrientation(ORIENTATION_PORTRAIT);
					} else {
						setOrientation(microLoader.getOrientation());
					}
				});
		builder.show();
	}

	private void showLimitFpsDialog() {
		TextInputLayout inputLayout = DialogInputBinding.inflate(getLayoutInflater()).getRoot();
		EditText editText = Objects.requireNonNull(inputLayout.getEditText());
		editText.setHint(R.string.unlimited);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
		editText.setMaxLines(1);
		editText.setSingleLine(true);
		new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme)
				.setTitle(R.string.PREF_LIMIT_FPS)
				.setView(inputLayout)
				.setPositiveButton(android.R.string.ok, (d, w) -> {
					Editable text = editText.getText();
					int fps = 0;
					try {
						fps = TextUtils.isEmpty(text) ? 0 : Integer.parseInt(text.toString().trim());
					} catch (NumberFormatException ignored) {
					}
					Canvas.setLimitFps(fps);
				})
				.setNegativeButton(android.R.string.cancel, null)
				.setNeutralButton(R.string.reset, ((d, which) -> Canvas.setLimitFps(-1)))
				.show();
	}

	private void showClassicsKeyModeDialog() {
		String[] values = getResources().getStringArray(R.array.pref_classics_key_mode_values);
		int checked = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(classicsKeyMode)) {
				checked = i;
				break;
			}
		}
		new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme)
				.setTitle(R.string.pref_classics_key_mode_title)
				.setSingleChoiceItems(R.array.pref_classics_key_mode_entries, checked, (dialog, which) -> {
					String mode = values[which];
					PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
							.edit()
							.putString(PREF_CLASSICS_KEY_MODE, mode)
							.apply();
					applyClassicsKeyMode(mode);
					uiSounds().playConfirm();
					dialog.dismiss();
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	private void showMultiplayerDialog() {
		View view = LayoutInflater.from(this).inflate(R.layout.dialog_multiplayer, null, false);
		android.widget.Switch networkSwitch = view.findViewById(R.id.multiplayer_network_switch);
		com.google.android.material.textfield.TextInputEditText ipInput =
				view.findViewById(R.id.multiplayer_ip_input);
		android.widget.Switch relaySwitch = view.findViewById(R.id.multiplayer_relay_switch);
		com.google.android.material.textfield.TextInputEditText relayHostInput =
				view.findViewById(R.id.multiplayer_relay_host_input);
		com.google.android.material.textfield.TextInputEditText roomCodeInput =
				view.findViewById(R.id.multiplayer_room_code_input);

		boolean enabled = MultiplayerPrefs.isNetworkBtEnabled(this);
		String savedIp = MultiplayerPrefs.getFriendIp(this);
		boolean useRelay = MultiplayerPrefs.isUseRelay(this);
		String relayHost = MultiplayerPrefs.getRelayHost(this);
		String roomCode = MultiplayerPrefs.getRoomCode(this);
		networkSwitch.setChecked(enabled);
		ipInput.setText(savedIp);
		relaySwitch.setChecked(useRelay);
		relayHostInput.setText(relayHost);
		roomCodeInput.setText(roomCode);

		new AlertDialog.Builder(this, R.style.ClassicsCompactAlertDialogTheme)
				.setTitle(R.string.action_multiplayer)
				.setView(view)
				.setPositiveButton(android.R.string.ok, (d, w) -> {
					boolean useNetwork = networkSwitch.isChecked();
					String ip = ipInput.getText() != null ? ipInput.getText().toString().trim() : "";
					boolean relay = relaySwitch.isChecked();
					String host = relayHostInput.getText() != null ? relayHostInput.getText().toString().trim() : "";
					String code = roomCodeInput.getText() != null ? roomCodeInput.getText().toString().trim() : "";
					MultiplayerPrefs.saveRelay(this, useNetwork, relay, host,
							MultiplayerPrefs.getRelayPort(this), code);
					MultiplayerPrefs.save(this, useNetwork, ip);
				})
				.setNegativeButton(android.R.string.cancel, null)
				.show();
	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		if (current instanceof Form) {
			((Form) current).contextMenuItemSelected(item);
		}

		return super.onContextItemSelected(item);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ContextHolder.notifyOnActivityResult(requestCode, resultCode, data);
	}

	public String getAppName() {
		return appName;
	}

	public void toast(@StringRes int message) {
		runOnUiThread(() -> Toast.makeText(this, message, Toast.LENGTH_LONG).show());
	}

	private class SetCurrentEvent extends SimpleEvent {
		private final Displayable current;
		private final Displayable next;

		private SetCurrentEvent(Displayable current, Displayable next) {
			this.current = current;
			this.next = next;
		}

		@Override
		public void process() {
			closeOptionsMenu();
			if (current != null) {
				current.clearDisplayableView();
			}
			binding.displayableContainer.removeAllViews();
			bindDisplayable(next);
		}
	}

	private void bindDisplayable(Displayable next) {
		ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());
		ViewGroup.LayoutParams layoutParams = binding.toolbar.getLayoutParams();
		int toolbarHeight = 0;
		if (next instanceof Canvas) {
			hideSystemUI();
			binding.buttonBackOverlay.setVisibility(View.VISIBLE);
			binding.gameFrame.setVisibility(View.VISIBLE);
			binding.controlTopRow.setVisibility(View.VISIBLE);
			applyClassicsControlStyle(classicsControlStyle);
			if (!actionBarEnabled) {
				actionBar.hide();
				binding.toolbar.setVisibility(View.GONE);
			} else {
				final String title = next.getTitle();
				actionBar.setTitle(title == null ? appName : title);
				toolbarHeight = (int) (getToolBarHeight() / 1.5);
				layoutParams.height = toolbarHeight;
				binding.toolbar.setVisibility(View.VISIBLE);
			}
		} else {
			showSystemUI();
			binding.buttonBackOverlay.setVisibility(View.GONE);
			binding.gameFrame.setVisibility(View.GONE);
			binding.controlTopRow.setVisibility(View.GONE);
			binding.controlPadShell.setVisibility(View.GONE);
			binding.actionCluster.setVisibility(View.GONE);
			binding.phoneShellContainer.setVisibility(View.GONE);
			ContextHolder.clearCanvasViewport();
			ContextHolder.clearClassicsControlBounds();
			actionBar.show();
			final String title = next != null ? next.getTitle() : null;
			actionBar.setTitle(title == null ? appName : title);
			toolbarHeight = (int) getToolBarHeight();
			layoutParams.height = toolbarHeight;
			binding.toolbar.setVisibility(View.VISIBLE);
		}
		binding.overlay.setLocation(0, toolbarHeight);
		binding.toolbar.setLayoutParams(layoutParams);
		binding.displayableContainer.removeAllViews();
		if (next == null) {
			return;
		}
		View displayableView = next.getDisplayableView();
		ViewParent parent = displayableView.getParent();
		if (parent instanceof ViewGroup) {
			((ViewGroup) parent).removeView(displayableView);
		}
		FrameLayout.LayoutParams displayLayoutParams = new FrameLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT,
				Gravity.CENTER);
		displayableView.setLayoutParams(displayLayoutParams);
		binding.displayableContainer.addView(displayableView, displayLayoutParams);
		if (next instanceof Canvas) {
			binding.displayableContainer.post(() -> {
				updateCanvasViewport();
				updateClassicsControlBounds();
			});
		}
	}
}
