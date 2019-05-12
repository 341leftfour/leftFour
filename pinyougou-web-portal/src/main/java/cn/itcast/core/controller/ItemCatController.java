package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品分类管理
 */
@RestController
@RequestMapping("/index")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;
//实战需求,网站前台商品分类展示
    //根据父ID查询商品分类结果集
    /**
     * 查询商品分类信息
     *
     * @return
     */
    @RequestMapping("/findItemCatList")
    public List<ItemCat> findItemCatList() {
        return itemCatService.findItemCatList();
    }

}
