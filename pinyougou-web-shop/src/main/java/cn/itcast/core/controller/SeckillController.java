package cn.itcast.core.controller;


import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.SeckillService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;


import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seckill")
public class SeckillController {

    @Reference
    private SeckillService seckillService;
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody(required = false) SeckillOrder seckillOrder){
        //获取商户名称
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            //将商家id存入
           seckillOrder.setSellerId(name);
            return seckillService.search(page,rows,seckillOrder);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
