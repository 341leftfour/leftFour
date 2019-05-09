package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;


    @RequestMapping("/findByItem")
    public Item findByItem(Long itemId){

        try {

            return  seckillGoodsService.findByItem(itemId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    @RequestMapping("/findItemList")
    public List<Item> findItemList(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        return seckillGoodsService.findItemList(name);
    }


    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody SeckillGoods  seckillGoods){
        //获取商家名称
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        try{
            seckillGoods.setSellerId(name);
    return   seckillGoodsService.search(page,rows,seckillGoods);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
