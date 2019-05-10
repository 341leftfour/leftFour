package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.dao.seckill.SeckillOrderDao;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService{

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private SeckillOrderDao seckillOrderDao;
    @Override
    public void submitOrder(Long seckillId, String userId) {
        //从缓存中查询秒杀商品
        SeckillGoods seckillGoods =(SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(seckillId);
        if(seckillGoods==null){
            throw new RuntimeException("商品不存在");
        }
        if(seckillGoods.getStockCount()<=0){
            throw new RuntimeException("商品已抢购一空");
        }
        //扣减（redis）库存
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        redisTemplate.boundHashOps("seckillGoods").put(seckillId, seckillGoods);//放回缓存
        if(seckillGoods.getStockCount()==0){//如果已经被秒光
            seckillGoodsDao.updateByPrimaryKey(seckillGoods);//同步到数据库
            redisTemplate.boundHashOps("seckillGoods").delete(seckillId);
        }
        //保存（redis）订单
        long orderId = idWorker.nextId();
        SeckillOrder seckillOrder=new SeckillOrder();
        seckillOrder.setId(orderId);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setMoney(seckillGoods.getCostPrice());//秒杀价格
        seckillOrder.setSeckillId(seckillId);
        seckillOrder.setSellerId(seckillGoods.getSellerId());
        seckillOrder.setUserId(userId);//设置用户ID
        seckillOrder.setStatus("0");//状态
        redisTemplate.boundHashOps("seckillOrder").put(userId, seckillOrder);
    }
    @Override
    public SeckillOrder  searchOrderFromRedisByUserId(String userId) {
        return (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
    }

    @Override
    public void saveOrderFromRedisToDb(String userId, Long orderId, String transactionId) {
        System.out.println("saveOrderFromRedisToDb:"+userId);
        //根据用户ID查询日志
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if(seckillOrder==null){
            throw new RuntimeException("订单不存在");
        }
        //如果与传递过来的订单号不符
//        if(seckillOrder.getId().longValue()!=orderId.longValue()){
//            throw new RuntimeException("订单不相符");
//        }
        seckillOrder.setTransactionId(transactionId);//交易流水号
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setStatus("1");//状态
        seckillOrderDao.insertSelective(seckillOrder);//保存到数据库
        redisTemplate.boundHashOps("seckillOrder").delete(userId);//从redis中清除
    }

    @Override
    public void deleteOrderFromRedis(String userId, Long orderId) {
        SeckillOrder seckillOrder=(SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(userId);
        if(seckillOrder!=null  && seckillOrder.getId().longValue()== orderId.longValue()){
            redisTemplate.boundHashOps("seckillOrder").delete(userId);
            //恢复库存
            SeckillGoods seckillGoods= (SeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(seckillOrder.getSeckillId());
            if(seckillGoods!=null){
                seckillGoods.setStockCount( seckillGoods.getStockCount()+1 );// 库存回收
            }else{
                //从数据库中再次查询，放入缓存，库存数置为1
                seckillGoods = seckillGoodsDao.selectByPrimaryKey(seckillOrder.getSeckillId());
                //是否超过当前活动时间
                if( seckillGoods.getEndTime().getTime()> new Date().getTime()  ){
                    seckillGoods.setStockCount(1);
                    redisTemplate.boundHashOps("seckillGoods").put(seckillOrder.getSeckillId(),seckillGoods);
                }
            }
        }
    }


}
