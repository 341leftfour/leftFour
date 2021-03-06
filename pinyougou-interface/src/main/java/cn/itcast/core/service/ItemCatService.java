package cn.itcast.core.service;

import cn.itcast.core.pojo.item.ItemCat;
import entity.PageResult;

import java.util.List;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long parentId);

    ItemCat findOne(Long id);

    List<ItemCat> findAll();



    //网站前台商品分类显示
    public List<ItemCat> findItemCatList();

    PageResult search(Integer page, Integer rows, ItemCat itemCat);

    void updateStatus(Long[] ids, String status);

    void add(ItemCat itemCat);
}
