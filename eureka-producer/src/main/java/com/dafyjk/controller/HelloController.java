package com.dafyjk.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.dafyjk.mapper.User;
import com.dafyjk.service.UserService;

@RestController
@RefreshScope
public class HelloController {
	
	
	private static Logger LOGGER = LoggerFactory.getLogger(HelloController.class);
	
	@Value("${neo.hello}")
    private String hello;
	
	@Autowired
	private UserService userService;
	
	
    @RequestMapping("/hello")
    public String index(@RequestParam String name) {
        //return "hello "+name+"，this is first messge";
    	LOGGER.info("my log!");
        return this.hello;
    }
    
    @RequestMapping("/user/{id}")
    public User index(@PathVariable long id) {
    	User user = userService.getUserById(id);
    	System.out.println(user.getName()+ user.getPassword());
    	return user;
    }
}
