package cn.itcast.core.service;

import cn.itcast.core.dao.log.PayLogDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderQuery;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import org.apache.solr.common.util.Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class HomeServiceImpl implements HomeService {
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private PayLogDao payLogDao;
    @Autowired
    private UserDao userDao;
    @Override
    public Map<String, Object> panel(String name) {
        HashMap<String, Object> map = new HashMap<>();

        // 一.当日订单个数
        OrderQuery orderQuery = new OrderQuery();
        //   1.传入近三个月时间
        orderQuery.createCriteria().andExpireGreaterThanOrEqualTo(new Date( new Date().getTime() - 3600 * 30 * 3));
        //   2.返回商户项
        List<Order> orders = orderDao.selectByExample(orderQuery);
        //  3.存入订单个数
        map.put("orderNumber",orders.size());
        // 二.整体转化率 查询所有订单
        List<PayLog> payLogs = payLogDao.selectByExample(null);
        //  1 . 通过所有订单整除
        map.put("percent",orders.size()/payLogs.size());

        //三 .新注册用户
        List<User> users = userDao.selectByExample(null);
        map.put("newUser",users.size());
        //日 PV 信息
        return map;
    }
}
