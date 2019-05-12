package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类管理
 */
@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    //根据父ID查询商品分类结果集
    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }

    //查询一个
    @RequestMapping("/findOne")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }

    //查询所有商品分类
    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }

    //分页查询所有商品分类(模糊查询)
    @RequestMapping("/search")
    //public 分页对象 search (当前页,每页数,查询条件)
    public PageResult search (Integer page, Integer rows, @RequestBody ItemCat itemCat){//json串转pojo对象(必须有值)
        return itemCatService.search(page,rows,itemCat);
    }
    //开始审核
    @RequestMapping("/updateStatus")
    public Result updateStatus(Long[] ids, String status){
        try {
            itemCatService.updateStatus(ids,status);
            return new Result(true,"成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
