package cn.itcast.core.service;

import cn.itcast.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {
    public List<SeckillGoods> findList();
    /**
     * 根据ID获取实体(从缓存中读取)
     */
    public SeckillGoods findOneFromRedis(Long id);



}
