package com.contralabs.inmap.server;

import android.util.Log;


public class LoggerInputStreamHandler extends StringInputStreamHandler {
	
	private String tag;
	
	public LoggerInputStreamHandler(String tag) {
		this.tag = tag;
	}

	@Override
	protected Object handleString(String string) {
		Log.i(tag, string == null ? " " : string);
		return string;
	}

}
