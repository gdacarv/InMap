package com.contralabs.inmap.server;

public interface Receiver<T> {
	void onReceived(T data);
}
