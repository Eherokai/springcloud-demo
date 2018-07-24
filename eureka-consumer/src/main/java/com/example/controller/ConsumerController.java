package com.example.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.feignclient.HelloRemote;

@RestController
@RefreshScope
public class ConsumerController {

    @Autowired
    HelloRemote helloRemote;
    
    @Value("${neo.hello}")
    private String hello;
    
    @Autowired
	private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/redis/{name}")
	public String redis(@PathVariable("name") String name) {

		stringRedisTemplate.opsForValue().set("var", name);
		return "success";
    }

	
    @RequestMapping("/hello/{name}")
    public String index(@PathVariable("name") String name,HttpServletRequest request) {
    	/*logger.info("===<call trace-2, TraceId={}, SpanId={}>===",
    			request.getHeader("X-B3-TraceId"), request.getHeader("X-B3-SpanId"));*/
    	 
    	System.out.println(request.getHeader("X-B3-TraceId")+"^^^^^^^^^^^^^");
    	
        return helloRemote.hello(name);
    	//return this.hello + "," + name;
    }

}