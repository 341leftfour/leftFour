package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
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

    @RequestMapping("/importExcel")
    public Result importExcel(){
        try {
            itemCatService.importExcel();
            return new Result(true,"导入成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"导入失败");
        }
    }

    @RequestMapping("/exportExcel")
    public Result exportExcel(){
        try {
            itemCatService.exportExcel();
            return new Result(true,"导出成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"导出失败");
        }
    }
}
