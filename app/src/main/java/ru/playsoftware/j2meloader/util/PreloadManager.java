package ru.playsoftware.j2meloader.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PreloadManager {
	private static final String ASSET_ROOT = "preload";
	private static final String VERSION_FILE = "version.txt";
	private static final String MARKER_FILE = ".preload_ready";

	public interface Listener {
		void onStart(int totalFiles);

		void onProgress(int copiedFiles, int totalFiles, @NonNull String relativePath);

		void onComplete(boolean didWork, @NonNull String activeStamp);

		void onError(@NonNull Exception exception);
	}

	private PreloadManager() {
	}

	public static void prepareAsync(@NonNull Context context,
									@NonNull String emulatorDir,
									@NonNull Listener listener) {
		Context appContext = context.getApplicationContext();
		new Thread(() -> {
			try {
				prepareBlocking(appContext, emulatorDir, listener);
			} catch (Exception e) {
				listener.onError(e);
			}
		}, "preload-prepare").start();
	}

	private static void prepareBlocking(@NonNull Context context,
										@NonNull String emulatorDir,
										@NonNull Listener listener) throws IOException {
		AssetManager assets = context.getAssets();
		List<String> assetFiles = listAssetFiles(assets, ASSET_ROOT);
		if (assetFiles.isEmpty() || !hasPayloadFiles(assetFiles)) {
			listener.onComplete(false, "");
			return;
		}
		Collections.sort(assetFiles);
		String stamp = buildAssetStamp(assets, assetFiles);
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String prefKey = Constants.PREF_PRELOAD_STAMP_PREFIX + emulatorDir;
		File marker = new File(emulatorDir, MARKER_FILE);
		if (stamp.equals(prefs.getString(prefKey, null)) && marker.isFile()) {
			listener.onComplete(false, stamp);
			return;
		}

		listener.onStart(assetFiles.size());
		int copiedFiles = 0;
		for (String assetPath : assetFiles) {
			String relativePath = assetPath.substring(ASSET_ROOT.length() + 1);
			copyAsset(assets, assetPath, new File(emulatorDir, relativePath));
			copiedFiles++;
			listener.onProgress(copiedFiles, assetFiles.size(), relativePath);
		}

		if (!marker.exists()) {
			//noinspection ResultOfMethodCallIgnored
			marker.createNewFile();
		}
		prefs.edit().putString(prefKey, stamp).apply();
		listener.onComplete(true, stamp);
	}

	@NonNull
	private static String buildAssetStamp(@NonNull AssetManager assets,
										 @NonNull List<String> assetFiles) throws IOException {
		String versionPath = ASSET_ROOT + "/" + VERSION_FILE;
		for (String assetFile : assetFiles) {
			if (versionPath.equals(assetFile)) {
				return "version:" + readTextAsset(assets, versionPath).trim();
			}
		}
		StringBuilder builder = new StringBuilder("paths:");
		for (String assetFile : assetFiles) {
			builder.append(assetFile).append(';');
		}
		return builder.toString();
	}

	@NonNull
	private static String readTextAsset(@NonNull AssetManager assets, @NonNull String path) throws IOException {
		try (InputStream input = assets.open(path)) {
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int read;
			while ((read = input.read(buffer)) != -1) {
				output.write(buffer, 0, read);
			}
			return output.toString();
		}
	}

	private static boolean hasPayloadFiles(@NonNull List<String> assetFiles) {
		String versionPath = ASSET_ROOT + "/" + VERSION_FILE;
		for (String assetFile : assetFiles) {
			if (!versionPath.equals(assetFile)) {
				return true;
			}
		}
		return false;
	}

	@NonNull
	private static List<String> listAssetFiles(@NonNull AssetManager assets,
												 @NonNull String root) throws IOException {
		String[] children = assets.list(root);
		if (children == null || children.length == 0) {
			return Collections.emptyList();
		}
		List<String> files = new ArrayList<>();
		for (String child : children) {
			String path = root + "/" + child;
			String[] nested = assets.list(path);
			if (nested == null || nested.length == 0) {
				files.add(path);
			} else {
				files.addAll(listAssetFiles(assets, path));
			}
		}
		return files;
	}

	private static void copyAsset(@NonNull AssetManager assets,
								  @NonNull String assetPath,
								  @NonNull File outFile) throws IOException {
		File parent = outFile.getParentFile();
		if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
			throw new IOException("Can't create preload directory: " + parent);
		}
		try (InputStream input = assets.open(assetPath);
			 FileOutputStream output = new FileOutputStream(outFile)) {
			byte[] buffer = new byte[8192];
			int read;
			while ((read = input.read(buffer)) != -1) {
				output.write(buffer, 0, read);
			}
		}
	}
}
