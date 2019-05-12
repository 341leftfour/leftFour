package cn.itcast.core.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.OrderService;
import cn.itcast.core.service.SellerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *   商家销量统计 -- 饼状图
 */
@RestController
@RequestMapping("/pieChart")
public class PieChartController {

    @Reference
    private SellerService sellerService;

    @Reference
    private OrderService orderService;


    @RequestMapping("/findSeller")
    public List<Map> findSeller(){
        List<Map> mapList = new ArrayList<>();
        List<Seller> sellerList = sellerService.findAll();
        for (Seller seller : sellerList) {
            Map<String, Object> map = new HashMap<String, Object>();
            String name = seller.getName();
            seller.setNum(orderService.selectCountBySellerId(seller.getSellerId()));
            Integer num =seller.getNum();
            map.put("name",name);
            map.put("value",num);
            mapList.add(map);
        }

        return  mapList;
    }

}
