package org.microemu.cldc.btspp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpTransport implements SppTransport {
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

	/**
	 * Modo relay: conecta no servidor relay e manda o codigo de sala (4
	 * caracteres). O servidor pareia com quem mandar o mesmo codigo e
	 * comeca a repassar os bytes crus dos dois lados.
	 */
	public static TcpTransport connectRelay(String relayHost, int relayPort, String roomCode, int timeoutMs) throws IOException {
		if (roomCode == null || roomCode.length() != 4) {
			throw new IOException("Codigo de sala invalido (precisa ter 4 caracteres): " + roomCode);
		}
		Socket socket = new Socket();
		socket.connect(new InetSocketAddress(relayHost, relayPort), timeoutMs);
		socket.getOutputStream().write(roomCode.getBytes("US-ASCII"));
		socket.getOutputStream().flush();
		return new TcpTransport(socket);
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
