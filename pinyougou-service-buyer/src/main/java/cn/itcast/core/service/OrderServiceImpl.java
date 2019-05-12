package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.container.page.PageHandler;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import vo.Cart;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单管理
 */
@Service
@Transactional
public class OrderServiceImpl implements  OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private PayLogDao payLogDao;

    //保存订单
    @Override
    public void add(Order order) {

        //1:保存购物车 每一个购物车 商家为单位  一个购物车对应一个商家 对应一个订单
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(order.getUserId());

        //实付金额 日志表的金额 (总金额)
        long tp = 0;
        //订单集合
        List<String> ids = new ArrayList<>();

        for (Cart cart : cartList) {
            //订单ID 唯一
            long id = idWorker.nextId();
            order.setOrderId(id);

            ids.add(String.valueOf(id));

            //总金额
            double totalPrice = 0;

            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                //订单详情对象 OrderItem

                //订单详情ID
                orderItem.setId(idWorker.nextId());
                //根据库存ID 查询库存对象
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                //商品ID
                orderItem.setGoodsId(item.getGoodsId());
                //订单ID 外键
                orderItem.setOrderId(id);

                //标题
                orderItem.setTitle(item.getTitle());
                //单价
                orderItem.setPrice(item.getPrice());
                //小计
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*orderItem.getNum()));


                //计算此订单的总金额
                totalPrice += orderItem.getTotalFee().doubleValue();

                //图片
                orderItem.setPicPath(item.getImage());
                //商家ID
                orderItem.setSellerId(item.getSellerId());
                //保存订单详情表
                orderItemDao.insertSelective(orderItem);

            }
            //实付金额
            order.setPayment(new BigDecimal(totalPrice));
            //未付款
            order.setStatus("1");
            //创建时间
            order.setCreateTime(new Date());
            //更新时间
            order.setUpdateTime(new Date());
            //来源
            order.setSourceType("2");
            //商家ID
            order.setSellerId(cart.getSellerId());

            tp += order.getPayment().doubleValue()*100;

            //保存订单
            orderDao.insertSelective(order);

        }

        //保存日志表  (支付表)
        PayLog payLog = new PayLog();
        //ID
        payLog.setOutTradeNo(String.valueOf(idWorker.nextId()));

        //生成时间
        payLog.setCreateTime(new Date());
        //总金额 上面所有订单的金额
        payLog.setTotalFee(tp);

        //用户Id
        payLog.setUserId(order.getUserId());

        //交易状态
        payLog.setTradeState("0");
        //支付类型
        payLog.setPayType("1");
        //订单集合   "1,2,3,4,5"
        payLog.setOrderList(ids.toString().replace("[","").replace("]",""));

        //保存
        payLogDao.insertSelective(payLog);

        //保存缓存一份
        redisTemplate.boundHashOps("payLog").put(order.getUserId(),payLog);

        //清除购物车
        //redisTemplate.boundHashOps("CART").delete(order.getUserId());
        //删除购物车中已经购买的商品



    }

    @Override
    public PageResult search(Integer page, Integer rows, Order order) {
        PageHelper.startPage(page,rows);
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();
        if(null!=order.getSellerId() && !"".equals(order.getSellerId())){
            criteria.andSellerIdEqualTo(order.getSellerId());
        }
        orderQuery.setOrderByClause("create_time DESC");
        Page<Order> orders = (Page<Order>) orderDao.selectByExample(orderQuery);
        return new PageResult(orders.getTotal(),orders.getResult());
    }

    @Override
    public void updateStatus(String status, Long[] ids) {
        //修改商品状态
        Order order = new Order();
        order.setStatus(status);

        for (Long id : ids) {
            order.setOrderId(id);
            orderDao.updateByPrimaryKeySelective(order);
        }
    }
}
