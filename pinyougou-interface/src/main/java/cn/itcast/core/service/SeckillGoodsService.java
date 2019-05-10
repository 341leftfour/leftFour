package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import entity.PageResult;

import java.util.List;

public interface SeckillGoodsService {
    PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods);

    List<Item> findItemList(String sellerId);

    Item findByItem(Long itemId);

    void save(SeckillGoods seckillGoods);

    ;
}
