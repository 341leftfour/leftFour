package cn.itcast.core.service;


import cn.itcast.core.pojo.seckill.SeckillOrder;
import entity.PageResult;


public interface SeckillService {
    PageResult search(Integer page, Integer rows, SeckillOrder seckillOrder);

    void updateStatus(String status, Long[] ids);
}
