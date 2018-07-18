package com.example.hystrix;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.feignclient.HelloRemote;

@Component
public class HelloRemoteHystrix implements HelloRemote{

    @Override
    public String hello(@RequestParam(value = "name") String name) {
        return " messge send failed ";
    }
}
