package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.service.SeckillGoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import entity.PageResult;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/seckill")
public class SeckillController {
    @Reference
    private SeckillGoodsService seckillGoodsService;



    @RequestMapping("/save")
    @JsonFormat(pattern="yyyy-MM-dd HH-mm-ss")
    public Result save(@RequestBody(required = false) SeckillGoods seckillGoods){


        try { if (seckillGoods!=null){
            seckillGoodsService.save(seckillGoods);
            return new Result(true,"申请成功");
        }

        } catch (Exception e) {
            e.printStackTrace();

        }
        return new Result(true,"申请失败");


    }

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
        return seckillGoodsService.search(page,rows,seckillGoods);
    }
    //开始审核
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids,String status){
        try {
            seckillGoodsService.updateStatus(ids,status);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
