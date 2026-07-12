package org.microemu.cldc.btspp;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothTransport implements SppTransport {
	private final BluetoothSocket socket;

	BluetoothTransport(BluetoothSocket socket) {
		this.socket = socket;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return socket.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		return socket.getOutputStream();
	}

	@Override
	public void close() throws IOException {
		socket.close();
	}

	BluetoothSocket getSocket() {
		return socket;
	}
}
