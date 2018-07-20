package com.dafyjk.service;

import com.dafyjk.mapper.User;

public interface UserService {

	User getUserById(long id);
	
	User getUserByName(String username);
}
