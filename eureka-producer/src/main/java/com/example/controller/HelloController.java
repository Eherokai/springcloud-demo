package com.example.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	@Value("${neo.hello}")
    private String hello;
	
	
    @RequestMapping("/hello")
    public String index(@RequestParam String name) {
        //return "hello "+name+"，this is first messge";
        return this.hello;
    }
}
