package ru.playsoftware.j2meloader;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import ru.playsoftware.j2meloader.util.UiSoundEffects;

public class SplashActivity extends AppCompatActivity {
	private static final long SPLASH_DELAY_MS = 950L;

	private final Handler handler = new Handler(Looper.getMainLooper());
	private final Runnable launchMainRunnable = () -> {
		startActivity(new Intent(this, MainActivity.class)
				.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
		finish();
	};

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		UiSoundEffects.get(this).playOpening();
		hideSystemBars();
		handler.postDelayed(launchMainRunnable, SPLASH_DELAY_MS);
	}

	@Override
	protected void onDestroy() {
		handler.removeCallbacks(launchMainRunnable);
		super.onDestroy();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			hideSystemBars();
		}
	}

	private void hideSystemBars() {
		WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
		WindowInsetsControllerCompat controller =
				WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
		if (controller != null) {
			controller.setSystemBarsBehavior(
					WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
			controller.hide(WindowInsetsCompat.Type.statusBars() | WindowInsetsCompat.Type.navigationBars());
		}
		WindowManager.LayoutParams attributes = getWindow().getAttributes();
		attributes.layoutInDisplayCutoutMode =
				WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
		getWindow().setAttributes(attributes);
	}
}
