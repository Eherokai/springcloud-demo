package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.feignclient.HelloRemote;

@RestController
@RefreshScope
public class ConsumerController {

    @Autowired
    HelloRemote HelloRemote;
    
    @Value("${neo.hello}")
    private String hello;

	
    @RequestMapping("/hello/{name}")
    public String index(@PathVariable("name") String name) {
        //return HelloRemote.hello(name);
    	return this.hello + "," + name;
    }

}