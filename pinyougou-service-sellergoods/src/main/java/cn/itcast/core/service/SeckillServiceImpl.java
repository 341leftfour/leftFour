package cn.itcast.core.service;


import cn.itcast.core.dao.seckill.SeckillOrderDao;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.pojo.seckill.SeckillOrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SeckillServiceImpl implements SeckillService {
    @Autowired
    private SeckillOrderDao seckillOrderDao;


    @Override
    public PageResult search(Integer page, Integer rows, SeckillOrder seckillOrder) {
        //分页小助手
        PageHelper.startPage(page, rows);
        SeckillOrderQuery seckillOrderQuery = new SeckillOrderQuery();
        //设置本商户id
        seckillOrderQuery.createCriteria().andSellerIdEqualTo(seckillOrder.getSellerId());
        //查询
        Page<SeckillOrder> pageList = (Page<SeckillOrder>) seckillOrderDao.selectByExample(seckillOrderQuery);

        return new PageResult(  pageList.getTotal(), pageList.getResult());

    }
}
