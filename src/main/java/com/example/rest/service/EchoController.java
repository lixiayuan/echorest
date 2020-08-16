package com.example.rest.service;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Map;

@RestController
public class EchoController {
    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";

    private static final String[] HEADERS_LIST = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "X-CLIENT-IP",
            "X-Real-IP",
            "REMOTE_ADDR"
    };


    public static String getIpAddr(HttpServletRequest request) {
//      print the request type using:
        System.out.println(request.getMethod());

//        You can print all the headers as mentioned here:
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            System.out.println("Header Name - " + headerName + ", Value - " + request.getHeader(headerName));
        }

//        To print all the request params, use this:
        Enumeration<String> params = request.getParameterNames();
        while(params.hasMoreElements()){
            String paramName = params.nextElement();
            System.out.println("Parameter Name - "+paramName+", Value - "+request.getParameter(paramName));
        }

        String ipAddress = "";
        try {
            for (String header : HEADERS_LIST) {
                String ip = request.getHeader(header);
                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                    System.out.println(" ipAddress: " + ipAddress + " header: " + header );
                    ipAddress = ip ;
                    break;
                }
            }

            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                System.out.println("getRemoteAddr - " + ipAddress);
                if (LOCALHOST.equals(ipAddress)) {
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }

            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(SEPARATOR) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

    @PostMapping(path = "/echo", consumes = "application/json", produces = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
//    public Echo echo(@RequestParam(value = "x", defaultValue = "Hello") String queryStringParams,
//                     @RequestBody Greeting requestBody, HttpServletRequest request) {
//    public Echo echo(@RequestBody Greeting requestBody, HttpServletRequest request) {

    public Echo echo(@RequestBody Greeting requestBody, @RequestHeader Map<String, String> headers) {
        headers.forEach((key, value) -> {
            System.out.println("Header: " +  key + "value: "  + value);
        });
//        //To get all the request params:
//        Enumeration<String> params = request.getParameterNames();
//        String queryStringParams = "";
//        int i=1;
//        while(params.hasMoreElements()){
//            String paramName = params.nextElement();
//            queryStringParams = queryStringParams +
//                    " Parameter " + (i++) + ": (Name - "+paramName+", Value - "+ request.getParameter(paramName) +") ";
//        }
//        System.out.println("queryStringParams - " +queryStringParams);
//
//        String ip = getIpAddr(request);
//        String useragent = request.getHeader("User-Agent");
//        Echo echo = new Echo(queryStringParams, requestBody, ip, useragent);
        Echo echo = new Echo("queryStringParams", requestBody, "ip", "useragent");
        return echo;
    }

    @GetMapping("/echo/all")
    public String echoall(@RequestParam(value = "x", defaultValue = "World") String x) {
        return "hello" + x;
    }


}