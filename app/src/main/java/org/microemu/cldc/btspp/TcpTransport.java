package org.microemu.cldc.btspp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

class TcpTransport implements SppTransport {
	public static final int PORT = 17342;

	private final Socket socket;

	private TcpTransport(Socket socket) {
		this.socket = socket;
	}

	public static TcpTransport connect(String host, int timeoutMs) throws IOException {
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(host, PORT), timeoutMs);
		return new TcpTransport(socket);
	}

	public static TcpTransport accept(ServerSocket serverSocket) throws IOException {
		return new TcpTransport(serverSocket.accept());
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
}
