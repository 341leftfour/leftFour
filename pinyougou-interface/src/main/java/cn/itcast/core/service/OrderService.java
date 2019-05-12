package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;

public interface OrderService {
    void add(Order order);

    PageResult search(Integer page, Integer rows, Order order);

    void updateStatus(String status, Long[] ids);

    /**
     *   通过商家ID 查询 订单表的订单总数(即销量)
     */
    Integer selectCountBySellerId(String sellerId);
}
