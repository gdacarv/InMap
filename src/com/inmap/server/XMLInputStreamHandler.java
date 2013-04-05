package com.inmap.server;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public abstract class XMLInputStreamHandler implements InputStreamHandler {

	@Override
	public void handleInputStream(InputStream inputStream) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(inputStream, null);
			handleXml(xpp);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	protected abstract void handleXml(XmlPullParser xpp);

}
