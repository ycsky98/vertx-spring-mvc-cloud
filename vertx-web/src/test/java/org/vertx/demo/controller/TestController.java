package org.vertx.demo.controller;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.vertx.demo.service.TestService;
import org.vertx.web.annotations.Controller;
import org.vertx.web.annotations.RequestMappping;
import org.vertx.web.config.RpcConfig;
import org.vertx.web.method.Method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
public class TestController {

    @Autowired
    private TestService testService;

    @RequestMappping(version = "v1", type = Method.GET)
    public Object result(HttpServerRequest request) {
        int count = 0;
        List<String> in = new ArrayList<>();
        while (count < 10) {
            in.add(RpcConfig.getService("test2Server").<String>sendService("Test2Service",
                    "hello2",
                    new Object[]{String.valueOf(count)}));
            count++;
        }
        return new HashMap<String, Object>() {
            {
                put("code", 200);
                put("message", in);
            }
        };
    }

    @RequestMappping(version = "v2", type = Method.GET)
    public Object result2(HttpServerResponse response) {
        response.putHeader("Content-Type", "application/octet-stream");
        response.putHeader("Content-Disposition", "attachment;fileName=abc.txt");

        return new byte[20];
    }
}
