package cn.itcast.core.controller;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vo.KeyValueVo;
import vo.OrderVo;
import java.util.*;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    OrderService orderService;

    @RequestMapping("/findOrderListByUsername")
    public Map<String,Object> findOrderListByUsername(){
        //获取登录名
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //创建一个需要返回的Map
        HashMap<String, Object> map = new HashMap<>();
        //根据用户名查到订单集合
        List<OrderVo> orderVoList = orderService.findOrderListByUsername(name);
        //创建一个商品详情集合
        List<OrderItem> orderItemList = new ArrayList<>();
        //创建一个规格的Map
        Map<String, String> specMap = new HashMap<>();
        //创建一个库存对象
        Item item = new Item();
        for (OrderVo orderVo : orderVoList) {
            orderItemList=orderService.findOrderItem(orderVo.getOrderId());
            for (OrderItem orderItem : orderItemList) {
                item = orderService.findSpecByItemId(orderItem.getItemId());
                specMap = JSON.parseObject(item.getSpec(), Map.class);
                Set<String> set = specMap.keySet();
                List<KeyValueVo> keyValueVoList = new ArrayList<>();
                for (String key : set) {
                    String value = specMap.get(key);
                    KeyValueVo keyValueVo = new KeyValueVo();
                    keyValueVo.setKey(key);
                    keyValueVo.setValue(value);
                    keyValueVoList.add(keyValueVo);
                }
                item.setKeyValueVoList(keyValueVoList);
                orderItem.setItem(item);

            }
            orderVo.setOrderItemList(orderItemList);
        }
        map.put("orderVoList",orderVoList);
        return map;
    }



}


