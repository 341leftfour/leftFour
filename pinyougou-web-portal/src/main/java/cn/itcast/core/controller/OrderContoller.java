package cn.itcast.core.controller;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提交订单
 */
@RestController
@RequestMapping("/order")
public class OrderContoller {

    @Reference
    private OrderService orderService;

    //提交订单

    @RequestMapping("/add")
    public Result add(@RequestBody Order order){

        try {
            //当前登陆人 (用户)
            order.setUserId(SecurityContextHolder.getContext().getAuthentication().getName());

            orderService.add(order);
            return new Result(true,"提交成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"提交失败");
        }

    }
}
