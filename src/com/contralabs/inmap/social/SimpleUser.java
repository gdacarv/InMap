package com.contralabs.inmap.social;

public class SimpleUser implements User {
	
	private final String name, facebookId, email, birthday;

	public SimpleUser(String name, String facebookId, String email, String birthday) {
		super();
		this.name = name;
		this.facebookId = facebookId;
		this.email = email;
		this.birthday = birthday;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getFacebookId() {
		return facebookId;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getBirthday() {
		return birthday;
	}

}
