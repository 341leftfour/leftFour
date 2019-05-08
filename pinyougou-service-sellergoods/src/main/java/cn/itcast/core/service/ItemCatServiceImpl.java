package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品分类管理
 */
@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {


    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate;
    //根据父ID查询
    @Override
    public List<ItemCat> findByParentId(Long parentId) {

        //查询所有商品分类 保存缓存中  商品分类管理页面上 添加新的按钮  导入Mysql商品分类数据到缓存中
        List<ItemCat> itemCatList = findAll();
        for (ItemCat itemCat : itemCatList) {
            redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
        }
        //正常查询  列表页面 显几个商品分类
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    //查询一个
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    //查询所有商品分类结果集
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }
}
