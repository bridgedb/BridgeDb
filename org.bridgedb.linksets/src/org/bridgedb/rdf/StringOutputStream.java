package org.bridgedb.rdf;

import java.io.OutputStream;

/**
 * @author Christian
 */
public class StringOutputStream extends OutputStream {

	// This Builder will contain the stream
	private StringBuilder buf = new StringBuilder();

	public StringOutputStream() {}

    @Override
	public void close() {}

    @Override
	public void flush() {
	}

    @Override
	public void write(byte[] b) {
		this.buf.append(b);
	}
	
    @Override
	public void write(byte[] b, int off, int len) {
		String str = new String(b, off, len);
		this.buf.append(str, off, len);
	}

    @Override
	public void write(int b) {
		this.buf.append(b);
	}

	public String toString() {
		return buf.toString();
	}
}
