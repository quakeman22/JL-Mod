/*
 *  Copyright 2020-2026 Yury Kharchenko
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package javax.microedition.shell;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.util.ContextHolder;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import ru.playsoftware.j2meloader.util.GameLog;

public class MidletThread extends HandlerThread implements Handler.Callback {
private static final String TAG = MidletThread.class.getName();
	private static final UncaughtExceptionHandler uncaughtExceptionHandler = (t, e) -> {
		GameLog.e("MidletThread", "Uncaught exception after destroy in thread \"" + t + "\"", e);
		Log.e(TAG, "Error in thread: \"" + t + "\" after destroy app called", e);
	};

	private static final int INIT = 0;
	private static final int START = 1;
	private static final int PAUSE = 2;
	private static final int DESTROY = 3;
	private static final int UNINITIALIZED = 0;
	private static final int INITIALIZED = 1;
	private static final int STARTED = 2;
	private static final int PAUSED = 3;
	private static final int DESTROYED = 4;
	private static MidletThread instance;
	private final MicroLoader microLoader;
	private final String mainClass;
	private final LifecycleEventObserver activityLifecycleObserver = this::onActivityStateChanged;
	private MIDlet midlet;
	private Handler handler;
	private int state;

	MidletThread(MicroLoader microLoader, String mainClass) {
		super("MidletMain");
		this.microLoader = microLoader;
		this.mainClass = mainClass;
		instance = this;
	}

	public static void notifyDestroyed() {
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		GameLog.i("MidletThread", "notifyDestroyed()");
		if (instance != null) {
			instance.state = DESTROYED;
		}
		MicroActivity activity = ContextHolder.getActivity();
		if (activity != null) {
			activity.finish();
		}
		Process.killProcess(Process.myPid());
	}

	public static void notifyPaused() {
		GameLog.i("MidletThread", "notifyPaused()");
		instance.state = PAUSED;
	}

	public static MIDlet getMidlet() {
		return instance == null ? null : instance.midlet;
	}

	public static void resumeRequest() {
		GameLog.i("MidletThread", "resumeRequest()");
		MicroActivity activity = ContextHolder.getActivity();
		if (instance != null && activity != null && activity.isVisible())
			instance.handler.obtainMessage(START).sendToTarget();
	}

	static void destroyApp() {
		Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		GameLog.i("MidletThread", "destroyApp() requested");
		new Thread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException ignored) {}
			Process.killProcess(Process.myPid());
		}, "ForceDestroyTimer").start();
		MicroActivity activity = ContextHolder.getActivity();
		if (activity != null) {
			Displayable current = activity.getCurrent();
			if (current instanceof Canvas canvas) {
				canvas.postKeyPressed(Canvas.KEY_END);
				canvas.postKeyReleased(Canvas.KEY_END);
			}
		}
		if (instance != null) {
			instance.handler.obtainMessage(DESTROY).sendToTarget();
		}
	}

	@Override
	public void start() {
		super.start();
		handler = new Handler(getLooper(), this);
		ContextHolder.getActivity().getLifecycle().addObserver(activityLifecycleObserver);
	}

	@Override
	public boolean handleMessage(@NonNull Message msg) {
		switch (msg.what) {
			case INIT:
				if (state != UNINITIALIZED) {
					break;
				}
				try {
					GameLog.i("MidletThread", "Initializing MIDlet " + mainClass);
					midlet = microLoader.loadMIDlet(this.mainClass);
					state = INITIALIZED;
					GameLog.i("MidletThread", "MIDlet initialized");
				} catch (Throwable t) {
					GameLog.e("MidletThread", "Init midlet failed", t);
					throw new RuntimeException("Init midlet failed", t);
				}
				break;
			case START:
				if (state != INITIALIZED) {
					if (state != PAUSED) {
						break;
					} else if (microLoader.params.skipResumeCall) {
						state = STARTED;
						break;
					}
				}
				try {
					GameLog.i("MidletThread", "Calling startApp()");
					state = STARTED;
					midlet.startApp();
					GameLog.i("MidletThread", "startApp() completed");
				} catch (MIDletStateChangeException e) {
					state = PAUSED;
					GameLog.w("MidletThread", "MIDlet refused startApp()");
					Log.w(TAG, "Midlet doesn't want to start!", e);
				} catch (Throwable t) {
					state = DESTROYED;
					GameLog.e("MidletThread", "Failed startApp", t);
					throw new RuntimeException("Failed startApp", t);
				}
				break;
			case PAUSE:
				if (state != STARTED) {
					break;
				}
				try {
					GameLog.i("MidletThread", "Calling pauseApp()");
					midlet.pauseApp();
					state = PAUSED;
					GameLog.i("MidletThread", "pauseApp() completed");
				} catch (Throwable t) {
					state = DESTROYED;
					try {
						midlet.destroyApp(true);
					} catch (MIDletStateChangeException ignored) {}
					GameLog.e("MidletThread", "Failed pauseApp", t);
					throw new RuntimeException("Filed pauseApp", t);
				}
				break;
			case DESTROY:
				if (state == DESTROYED) {
					GameLog.i("MidletThread", "Destroy requested after state already destroyed");
					notifyDestroyed();
					break;
				}
				state = DESTROYED;
				try {
					GameLog.i("MidletThread", "Calling destroyApp(true)");
					midlet.destroyApp(true);
					GameLog.i("MidletThread", "destroyApp(true) completed");
				} catch (MIDletStateChangeException e) {
					GameLog.w("MidletThread", "MIDlet refused destroyApp(true)");
					Log.w(TAG, "Midlet didn't want to die!", e);
				} catch (Throwable t) {
					GameLog.e("MidletThread", "Failed destroyApp(true)", t);
					Log.e(TAG, "Filed destroyApp:", t);
				}
				notifyDestroyed();
				break;
		}
		return true;
	}

	private void onActivityStateChanged(LifecycleOwner lifecycleOwner, Lifecycle.Event event) {
		switch (event) {
			case ON_CREATE -> handler.obtainMessage(INIT).sendToTarget();
			case ON_START -> handler.obtainMessage(START).sendToTarget();
			case ON_STOP -> handler.obtainMessage(PAUSE).sendToTarget();
			case ON_DESTROY -> handler.obtainMessage(DESTROY).sendToTarget();
		}
	}
}
