/*
 * Copyright 2026
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;
import java.util.Locale;

public final class GameLog {
	private static final int MAX_LINES = 2000;
	private static final Object LOCK = new Object();
	private static final SimpleDateFormat DATE_FORMAT =
			new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
	private static final Deque<String> LINES = new ArrayDeque<>(MAX_LINES);

	private static volatile boolean installed;

	private GameLog() {
	}

	public static void install() {
		if (installed) {
			return;
		}
		synchronized (LOCK) {
			if (installed) {
				return;
			}
			PrintStream originalOut = System.out;
			PrintStream originalErr = System.err;
			System.setOut(createInterceptingPrintStream("STDOUT", originalOut));
			System.setErr(createInterceptingPrintStream("STDERR", originalErr));
			installed = true;
		}
		i("System", "Game log installed");
	}

	public static void clear() {
		synchronized (LOCK) {
			LINES.clear();
		}
	}

	public static void i(@NonNull String tag, @NonNull String message) {
		append("INFO", tag, message, null);
	}

	public static void w(@NonNull String tag, @NonNull String message) {
		append("WARN", tag, message, null);
	}

	public static void e(@NonNull String tag, @NonNull String message) {
		append("ERROR", tag, message, null);
	}

	public static void e(@NonNull String tag, @NonNull String message, @Nullable Throwable throwable) {
		append("ERROR", tag, message, throwable);
	}

	@NonNull
	public static String dump() {
		synchronized (LOCK) {
			StringBuilder sb = new StringBuilder(Math.max(256, LINES.size() * 64));
			for (String line : LINES) {
				sb.append(line).append('\n');
			}
			return sb.toString();
		}
	}

	private static void append(@NonNull String level, @NonNull String tag, @NonNull String message,
							   @Nullable Throwable throwable) {
		String timestamp;
		synchronized (DATE_FORMAT) {
			timestamp = DATE_FORMAT.format(new Date());
		}
		StringBuilder sb = new StringBuilder()
				.append('[').append(timestamp).append("] ")
				.append(level).append('/')
				.append(tag).append(": ")
				.append(message);
		if (throwable != null) {
			sb.append('\n').append(stackTraceToString(throwable));
		}
		addLine(sb.toString());
	}

	private static void addLine(@NonNull String line) {
		synchronized (LOCK) {
			if (LINES.size() >= MAX_LINES) {
				LINES.removeFirst();
			}
			LINES.addLast(line);
		}
	}

	@NonNull
	private static PrintStream createInterceptingPrintStream(@NonNull String tag, @NonNull PrintStream original) {
		try {
			return new PrintStream(new LineInterceptingOutputStream(tag, original), true,
					StandardCharsets.UTF_8.name());
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException("UTF-8 not supported", e);
		}
	}

	@NonNull
	private static String stackTraceToString(@NonNull Throwable throwable) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		throwable.printStackTrace(pw);
		pw.flush();
		return sw.toString().trim();
	}

	private static final class LineInterceptingOutputStream extends OutputStream {
		private final String tag;
		private final PrintStream delegate;
		private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(256);

		private LineInterceptingOutputStream(String tag, PrintStream delegate) {
			this.tag = tag;
			this.delegate = delegate;
		}

		@Override
		public void write(int b) throws IOException {
			delegate.write(b);
			if (b == '\n') {
				flushBuffer();
				return;
			}
			if (b != '\r') {
				buffer.write(b);
			}
		}

		@Override
		public void flush() throws IOException {
			delegate.flush();
			flushBuffer();
		}

		private void flushBuffer() {
			if (buffer.size() == 0) {
				return;
			}
			String line = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
			buffer.reset();
			if (!line.isBlank()) {
				GameLog.i(tag, line);
			}
		}
	}
}
