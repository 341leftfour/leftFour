package cn.itcast.core.service;

import cn.itcast.core.pojo.item.ItemCat;
import entity.PageResult;

import java.io.IOException;
import java.util.List;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long parentId);

    ItemCat findOne(Long id);

    List<ItemCat> findAll();

    void importExcel() throws IOException;

    void exportExcel();

    PageResult search(Integer page, Integer rows, ItemCat itemCat);

    void add(ItemCat itemCat);
}
