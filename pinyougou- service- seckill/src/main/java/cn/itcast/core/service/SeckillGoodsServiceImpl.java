package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seckill.SeckillGoodsDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seckill.SeckillGoods;
import cn.itcast.core.pojo.seckill.SeckillGoodsQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsDao seckillGoodsDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<SeckillGoods> findList() {
        //获取秒杀商品列表
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        if (seckillGoodsList == null || seckillGoodsList.size() == 0) {
            SeckillGoodsQuery example = new SeckillGoodsQuery();
            SeckillGoodsQuery.Criteria criteria = example.createCriteria();
            criteria.andStatusEqualTo("1");//审核通过
            criteria.andStockCountGreaterThan(0);//剩余库存大于0
            criteria.andStartTimeLessThanOrEqualTo(new Date());//开始时间小于等于当前时间
            criteria.andEndTimeGreaterThan(new Date());//结束时间大于当前时间
            seckillGoodsList = seckillGoodsDao.selectByExample(example);
            //将商品列表装入缓存
            System.out.println("将秒杀商品列表装入缓存");
            for (SeckillGoods seckillGoods : seckillGoodsList) {
                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            }
        }
        return seckillGoodsList;
    }

    @Override
    public SeckillGoods findOneFromRedis(Long id) {
        return (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
    }


    @Autowired
    private ItemDao itemDao;


    @Override
    public PageResult search(Integer page, Integer rows, SeckillGoods seckillGoods) {
        PageHelper.startPage(page, rows);
        SeckillGoodsQuery query = new SeckillGoodsQuery();
        SeckillGoodsQuery.Criteria criteria = query.createCriteria();

        //判断是否传入了 状态
        if (null != seckillGoods.getStatus() && !"".equals(seckillGoods.getStatus())) {
            criteria.andStatusEqualTo(seckillGoods.getStatus());
        }

        //绑定sellerid
        criteria.andSellerIdEqualTo(seckillGoods.getSellerId());


        try {
            //判断是否有开始时间
            if (null != seckillGoods.getStartTime() && !"".equals(seckillGoods.getStartTime())) {
                criteria.andStartTimeGreaterThanOrEqualTo(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(seckillGoods.getStartTime()));
            }
            //判断是否有结束时间
            if (null != seckillGoods.getEndTime() && !"".equals(seckillGoods.getEndTime())) {
                criteria.andEndTimeLessThan(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(seckillGoods.getEndTime()));
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


        //返回结果集
        Page<SeckillGoods> result = (Page<SeckillGoods>) seckillGoodsDao.selectByExample(query);
        return new PageResult(result.getTotal(), result.getResult());
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
