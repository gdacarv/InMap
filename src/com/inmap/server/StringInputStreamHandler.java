package com.inmap.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.util.ByteArrayBuffer;

public abstract class StringInputStreamHandler implements InputStreamHandler {

	@Override
	public void handleInputStream(InputStream inputStream) {
		BufferedInputStream bis = new BufferedInputStream(inputStream);
		ByteArrayBuffer baf = new ByteArrayBuffer(50);
		int read = 0;
		int bufSize = 512;
		byte[] buffer = new byte[bufSize];
		while(true){
			try {
				read = bis.read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
				read = -1;
			}
			if(read==-1){
				break;
			}
			baf.append(buffer, 0, read);
		}
		handleString(new String(baf.toByteArray()));
	}

	protected abstract void handleString(String string);

}
