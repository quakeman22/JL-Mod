package org.microemu.cldc.btspp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SppTransport {
	InputStream getInputStream() throws IOException;
	OutputStream getOutputStream() throws IOException;
	void close() throws IOException;
}
