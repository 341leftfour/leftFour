package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import vo.Cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车管理
 */
@Service
@Transactional
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RedisTemplate redisTemplate;

    //根据Id查询库存对象
    @Override
    public Item findItemById(Long itemId) {
        return itemDao.selectByPrimaryKey(itemId);
    }

    //将购物车装满
    @Override
    public List<Cart> findCartList(List<Cart> cartList) {

        for (Cart cart : cartList) {

            Item item = null;
            //商品集合
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                //库存ID  数量
                item = findItemById(orderItem.getItemId());

                //图片
                orderItem.setPicPath(item.getImage());
                //标题
                orderItem.setTitle(item.getTitle());
                //单价
                orderItem.setPrice(item.getPrice());
                //小计 (总金额)
                orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum()));

            }
            //商家名称
            cart.setSellerName(item.getSeller());
        }

        return cartList;
    }

    //添加合并后的购物车到缓存中
    @Override
    public void addCartListToRedis(List<Cart> newCartList,String name) {
        //1:从缓存中获取购物车
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
        //2:将新购物车与缓存中的老购物车进行合并
        oldCartList = mergeCartList(newCartList,oldCartList);
        //3:将老购物车再保存到缓存中
        redisTemplate.boundHashOps("CART").put(name,oldCartList);

    }

    //从缓存中查询购物车集合
    @Override
    public List<Cart> findCartListFromRedis(String name) {
        return (List<Cart>) redisTemplate.boundHashOps("CART").get(name);
    }

    //将新购物车与缓存中的老购物车进行合并
    public List<Cart> mergeCartList(List<Cart> newCartList,List<Cart> oldCartList){
        //1:判断 新购物车集合 是否值
        if(null != newCartList && newCartList.size() > 0){
            //2:判断老购物车集合 是否有值
            if(null != oldCartList && oldCartList.size() > 0){
                //3: 合并新老购物车集合
                for (Cart newCart : newCartList) {

                    //1)判断当前新车在老车集合中是否同商家
                    int i = oldCartList.indexOf(newCart);
                    if(i != -1){
                        //存在  先获取出 跟新车相同的老车
                        Cart oldCart = oldCartList.get(i);
                        List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();

                        List<OrderItem> newOrderItemList = newCart.getOrderItemList();
                        for (OrderItem newOrderItem : newOrderItemList) {
                        //2)判断新商品 在老商品集合中是否已经存在
                            int l = oldOrderItemList.indexOf(newOrderItem);
                            if(l != -1){
                                //存在  追加商品的数量
                                OrderItem oldOrderItem = oldOrderItemList.get(l);
                                oldOrderItem.setNum(newOrderItem.getNum() + oldOrderItem.getNum());
                            }else{
                                //不存在 追加新商品
                                oldOrderItemList.add(newOrderItem);
                            }
                        }

                    }else{
                        //不存在
                        oldCartList.add(newCart);
                    }

                }
            } else{
                return newCartList;
            }

        }
        return oldCartList;

    }


}
