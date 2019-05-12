package cn.itcast.core.controller;

import cn.itcast.core.service.HomeService;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/home")
public class HomeController {

    @Reference
    private HomeService homeService;
    @RequestMapping("/panel")
    public Map<String,Object> panel(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();

        return homeService.panel(name);
    }

}
