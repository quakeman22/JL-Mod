/*
 * Copyright 2026 Yury Kharchenko
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

package ru.playsoftware.j2meloader.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import androidx.preference.PreferenceManager;

public final class SavestateManager {
	public static final String STATE_DIR_NAME = "savestates";
	public static final String STATE_FILE_SUFFIX = ".jlstate";
	private static final String PREF_PENDING_RESTORE = "pending_savestate_restore";

	private SavestateManager() {
	}

	public static File getStateDir(File appDataDir) {
		return new File(appDataDir, STATE_DIR_NAME);
	}

	public static List<File> listStates(File appDataDir) {
		File stateDir = getStateDir(appDataDir);
		File[] files = stateDir.listFiles((dir, name) -> name.endsWith(STATE_FILE_SUFFIX));
		if (files == null || files.length == 0) {
			return Collections.emptyList();
		}
		Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());
		return new ArrayList<>(Arrays.asList(files));
	}

	public static File saveState(File appDataDir) throws IOException {
		if (!appDataDir.exists() && !appDataDir.mkdirs()) {
			throw new IOException("Can't create directory: " + appDataDir);
		}
		File stateDir = getStateDir(appDataDir);
		if (!stateDir.exists() && !stateDir.mkdirs()) {
			throw new IOException("Can't create directory: " + stateDir);
		}
		String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(new Date());
		File stateFile = new File(stateDir, "state-" + timestamp + STATE_FILE_SUFFIX);
		try (ZipOutputStream zos = new ZipOutputStream(
				new BufferedOutputStream(new FileOutputStream(stateFile)))) {
			zipDirectory(appDataDir, appDataDir, stateDir, zos);
		}
		return stateFile;
	}

	public static void restoreState(File appDataDir, File stateFile) throws IOException {
		if (stateFile == null || !stateFile.isFile()) {
			throw new IOException("Savestate not found: " + stateFile);
		}
		if (!appDataDir.exists() && !appDataDir.mkdirs()) {
			throw new IOException("Can't create directory: " + appDataDir);
		}
		File stateDir = getStateDir(appDataDir);
		if (!stateDir.exists() && !stateDir.mkdirs()) {
			throw new IOException("Can't create directory: " + stateDir);
		}
		clearDirectoryPreservingStateDir(appDataDir, stateDir);
		try (ZipInputStream zis = new ZipInputStream(
				new BufferedInputStream(new FileInputStream(stateFile)))) {
			ZipEntry entry;
			byte[] buffer = new byte[8192];
			while ((entry = zis.getNextEntry()) != null) {
				File out = new File(appDataDir, entry.getName());
				if (entry.isDirectory()) {
					if (!out.exists() && !out.mkdirs()) {
						throw new IOException("Can't create directory: " + out);
					}
					continue;
				}
				File parent = out.getParentFile();
				if (parent != null && !parent.exists() && !parent.mkdirs()) {
					throw new IOException("Can't create directory: " + parent);
				}
				try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out))) {
					int read;
					while ((read = zis.read(buffer)) != -1) {
						bos.write(buffer, 0, read);
					}
				}
			}
		}
	}

	public static void setPendingRestore(Context context, String statePath) {
		PreferenceManager.getDefaultSharedPreferences(context)
				.edit()
				.putString(PREF_PENDING_RESTORE, statePath)
				.apply();
	}

	public static String consumePendingRestore(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String statePath = prefs.getString(PREF_PENDING_RESTORE, null);
		if (statePath != null) {
			prefs.edit().remove(PREF_PENDING_RESTORE).apply();
		}
		return statePath;
	}

	private static void zipDirectory(File rootDir, File currentDir, File excludedDir, ZipOutputStream zos)
			throws IOException {
		File[] files = currentDir.listFiles();
		if (files == null || files.length == 0) {
			return;
		}
		for (File file : files) {
			if (file.equals(excludedDir)) {
				continue;
			}
			String entryName = buildEntryName(rootDir, file);
			if (file.isDirectory()) {
				if (!entryName.endsWith("/")) {
					entryName += "/";
				}
				zos.putNextEntry(new ZipEntry(entryName));
				zos.closeEntry();
				zipDirectory(rootDir, file, excludedDir, zos);
			} else {
				zos.putNextEntry(new ZipEntry(entryName));
				try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
					byte[] buffer = new byte[8192];
					int read;
					while ((read = bis.read(buffer)) != -1) {
						zos.write(buffer, 0, read);
					}
				}
				zos.closeEntry();
			}
		}
	}

	private static String buildEntryName(File rootDir, File file) {
		String rootPath = rootDir.getAbsolutePath();
		String filePath = file.getAbsolutePath();
		if (!filePath.startsWith(rootPath)) {
			return file.getName().replace('\\', '/');
		}
		String entryName = filePath.substring(rootPath.length()).replace('\\', '/');
		if (entryName.startsWith("/")) {
			entryName = entryName.substring(1);
		}
		return entryName;
	}

	private static void clearDirectoryPreservingStateDir(File rootDir, File stateDir) {
		File[] files = rootDir.listFiles();
		if (files == null) {
			return;
		}
		for (File file : files) {
			if (file.equals(stateDir)) {
				continue;
			}
			FileUtils.deleteDirectory(file);
		}
	}
}
