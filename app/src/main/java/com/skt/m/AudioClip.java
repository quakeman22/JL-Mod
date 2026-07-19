package com.skt.m;

import java.io.IOException;

public interface AudioClip {
	void close() throws IOException;

	void loop() throws UserStopException, IOException;

	void open(byte[] data, int offset, int length) throws UnsupportedFormatException, ResourceAllocException;

	void pause() throws IOException;

	void play() throws UserStopException, IOException;

	void resume() throws IOException;

	void stop() throws IOException;
}
