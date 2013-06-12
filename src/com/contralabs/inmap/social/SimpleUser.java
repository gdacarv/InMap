package com.contralabs.inmap.social;

public class SimpleUser implements User {
	
	private final String name, facebookId;

	SimpleUser(String name, String facebookId) {
		super();
		this.name = name;
		this.facebookId = facebookId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFacebookId() {
		return facebookId;
	}

}
