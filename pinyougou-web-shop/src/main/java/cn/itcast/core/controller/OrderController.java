package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.service.BrandService;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 品牌管理
 */
@RestController
@RequestMapping("/order")
public class OrderController {


    //啦啦啦猜猜我是  谁
    //远程调用 品牌接口
    //成员变量
    @Reference
    private OrderService orderService;




    //查询分页对象 条件对象
    @RequestMapping("/search")
    public PageResult search( Integer page, Integer rows, @RequestBody Order order){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        order.setSellerId(name);
        return orderService.search(page,rows,order);


    }

    @RequestMapping("/updateStatus")
    public Result updateStatus(String status,Long[] ids){
        try {
            orderService.updateStatus(status,ids);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return  new Result(false,"失败");
        }

    }






}
