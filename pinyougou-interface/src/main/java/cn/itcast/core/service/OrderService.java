package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import entity.PageResult;
import vo.OrderVo;

import java.util.List;

public interface OrderService {
    void add(Order order);

    PageResult search(Integer page, Integer rows, Order order);

    void updateStatus(String status, Long[] ids);

    /**
     *   通过商家ID 查询 订单表的订单总数(即销量)
     */
    Integer selectCountBySellerId(String sellerId);

    List<OrderVo> findOrderListByUsername(String name);

    List<OrderItem> findOrderItem(String orderId);

    Item findSpecByItemId(Long itemId);
}
