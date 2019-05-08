package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登陆管理
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    //获取当前登陆人
    @RequestMapping("/name")
    public Map<String,Object> showName(HttpServletRequest request){


        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        Map<String,Object> map = new HashMap<>();
        map.put("loginName",name);
        //map.put("time",new Date());

        return map;

    }
}
