package cn.itcast.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家管理
 */
@SuppressWarnings("all")
@RestController
@RequestMapping("/seller")
public class SellerController {
    @Reference
    private SellerService sellerService;

    //查询分页
    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller) {
        return sellerService.search(page, rows, seller);
    }
/*
    //申请入驻
    @RequestMapping("/add")
    public Result add(@RequestBody Seller seller){
        try {
            sellerService.add(seller);
            return new Result(true,"添加成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加失败");
        }
    }
*/

    //查询一个商家对象
    @RequestMapping("/findOne")
    public Seller findOne(String id) {
        return sellerService.findOne(id);
    }

    //修改商家审核状态
    @RequestMapping("/updateStatus")
    public Result updateStatus(String sellerId, String status) {

        try {
            sellerService.updateStatus(sellerId, status);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

}
