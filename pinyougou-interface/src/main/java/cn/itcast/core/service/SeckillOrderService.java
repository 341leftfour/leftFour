package cn.itcast.core.service;

import cn.itcast.core.pojo.seckill.SeckillOrder;

public interface SeckillOrderService {
    /**
     * 提交订单
     * @param seckillId
     * @param userId
     */
    public void submitOrder(Long seckillId,String userId);
    /**
     * 根据用户名查询秒杀订单
     * @param userId
     */
    public SeckillOrder searchOrderFromRedisByUserId(String userId);

    /**
     * 支付成功保存订单
     * @param userId
     * @param orderId
     */
    public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);

    /**
     * 从缓存中删除订单
     * @param userId
     * @param orderId
     */
    public void deleteOrderFromRedis(String userId,Long orderId);


}
