package com.dafyjk.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dafyjk.mapper.User;
import com.dafyjk.mapper.UserMapper;
import com.dafyjk.service.UserService;

@Service
@Transactional(propagation=Propagation.REQUIRED , rollbackFor=Exception.class)
public class UserServiceImpl implements UserService{

	@Autowired
	private UserMapper userMapper;
	
	@Override
	public User getUserById(long id) {
		// TODO Auto-generated method stub
		return userMapper.selectByPrimaryKey(id);
	}

	@Override
	public User getUserByName(String username) {
		// TODO Auto-generated method stub
		return null;
	}

}
