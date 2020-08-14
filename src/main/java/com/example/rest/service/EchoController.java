package com.example.rest.service;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
public class EchoController {

    @PostMapping(path = "/echo", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Echo echo(@RequestParam(value = "x", defaultValue = "Hello") String queryStringParams,
                     @RequestBody Greeting requestBody, HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String useragent = request.getHeader("User-Agent");
        Echo echo = new Echo(queryStringParams, requestBody,remoteAddr, useragent);
        return echo;
    }

    @GetMapping("/echo/all")
    public String echoall(@RequestParam(value = "x", defaultValue = "World") String x) {
        return "hello" + x;
    }


}