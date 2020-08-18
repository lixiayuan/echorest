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
            "x-forwarded-for",
            "proxy-client-ip",
            "wl-proxy-client-ip",
            "http_x_forwarded_for",
            "http_x_forwarded",
            "http_x_cluster_client_ip",
            "http_client_ip",
            "http_forwarded_for",
            "http_forwarded",
            "http_via",
            "via",
            "x-client-ip",
            "x-real-ip",
            "remote_addr"
    };


    public static String getIpAddr(HttpServletRequest request) {

        String ipAddress = "";
        try {
            for (String header : HEADERS_LIST) {
                String ip = request.getHeader(header);
                if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                    ipAddress = ip ;
                    break;
                }
            }

            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
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
    public Echo echo(@RequestBody Greeting requestBody, HttpServletRequest request) {

        //To get all the request params:
        Enumeration<String> params = request.getParameterNames();
        String queryStringParams = "";
        int i=1;
        while(params.hasMoreElements()){
            String paramName = params.nextElement();
            queryStringParams = queryStringParams +
                    " Parameter " + (i++) + ": (Name - "+paramName+", Value - "+ request.getParameter(paramName) +") ";
        }

        String ip = getIpAddr(request);
        String useragent = request.getHeader("User-Agent");
        return new Echo(queryStringParams, requestBody, ip, useragent);
    }

    @GetMapping("/echo/all")
    public String echoall(@RequestParam(value = "x", defaultValue = "World") String x) {
        return "Hello" + x;
    }
}