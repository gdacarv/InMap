package com.contralabs.inmap.social;

import java.util.List;

import com.contralabs.inmap.server.Receiver;

public interface UsersCallback extends Receiver<List<User>>{

	void onReceived(List<User> users);
}
