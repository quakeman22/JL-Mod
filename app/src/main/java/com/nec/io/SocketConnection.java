package com.nec.io;

import java.io.IOException;

import javax.microedition.io.StreamConnection;

public interface SocketConnection extends StreamConnection {
	byte DELAY = 0;
	byte LINGER = 1;
	byte KEEPALIVE = 2;

	void setSocketOption(byte option, int value) throws IOException;

	int getSocketOption(byte option) throws IOException;

	String getAddress();

	int getPort();
}
