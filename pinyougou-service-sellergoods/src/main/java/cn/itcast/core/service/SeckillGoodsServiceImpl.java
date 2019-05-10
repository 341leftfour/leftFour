package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import cn.itcast.core.pojo.seckill.SeckillOrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;

    @Autowired
    private ItemDao itemDao;


    @Override
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {
        PageHelper.startPage(page,rows);
        SeckillGoodsQuery query = new SeckillGoodsQuery();
        //绑定sellerid
        query.createCriteria().andSellerIdEqualTo(seckillGoods.getSellerId());


        //返回结果集
        Page<SeckillGoods> result = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(query);
        return new PageResult(result.getTotal(),result.getResult());
    }

    @Override
    public List<Item> findItemList(String sellerId) {
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andSellerIdEqualTo(sellerId);
        return itemDao.selectByExample(itemQuery);
    }

    @Override
    public Item findByItem(Long itemId) {

        return itemDao.selectByPrimaryKey(itemId);
    }

    @Override
    public void save(SeckillGoods seckillGoods) {
        //等待审核
        seckillGoods.setStatus("0");
        //加入上传时间
        seckillGoods.setCheckTime(new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date()));
        seckillGoodsDao.insertSelective(seckillGoods);
    }
}
