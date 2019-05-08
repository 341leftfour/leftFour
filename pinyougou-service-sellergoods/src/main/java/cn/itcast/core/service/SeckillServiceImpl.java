package cn.itcast.core.service;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SeckillServiceImpl implements SeckillService {
    @Override
    public PageResult search(Integer page, Integer rows, SeckillOrder seckillOrder) {
        return null;
    }
}
