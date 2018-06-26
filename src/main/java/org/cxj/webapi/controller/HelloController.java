package org.cxj.webapi.controller;

import com.alibaba.fastjson.JSONObject;
import org.cxj.webapi.common.Constants;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class HelloController {

    @RequestMapping(value = "/webapi/sys/login")
    public @ResponseBody JSONObject login(HttpSession session, HttpServletRequest request){
        JSONObject params = (JSONObject)request.getAttribute(Constants.REQ_PARAMS);
        session.setAttribute(Constants.SESSION_USER,params);
        return params;
    }
    @RequestMapping("/webapi/work/echo")
    public @ResponseBody JSONObject echo(HttpSession session){
        JSONObject user = (JSONObject) session.getAttribute(Constants.SESSION_USER);
        System.out.println(user);
        return user;
    }
}

