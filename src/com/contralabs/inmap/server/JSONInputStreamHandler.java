package com.contralabs.inmap.server;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class JSONInputStreamHandler extends StringInputStreamHandler {

	@Override
	protected Object handleString(String string) {
		try {
			return handleJSONObject(new JSONObject(string));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected abstract Object handleJSONObject(JSONObject jsonObject);

}
