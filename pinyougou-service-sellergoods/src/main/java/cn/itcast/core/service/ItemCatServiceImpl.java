package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
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

    @Override
    public void add(ItemCat itemCat) {
        itemCat.setStatus("0");
        itemCatDao.insertSelective(itemCat);
    }
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


    //分页查询所有商品分类(模糊查询)
    @Override
    public PageResult search(Integer page, Integer rows, ItemCat itemCat) {

        PageHelper.startPage ( page, rows );//(2)相当于在(1)中拼接 limit 开始行 每页数

        ItemCatQuery query = new ItemCatQuery ();

        ItemCatQuery.Criteria criteria = query.createCriteria ();

        Page<ItemCat> p = (Page <ItemCat>) itemCatDao.selectByExample (  query );

        return new PageResult ( p.getTotal (), p.getResult () );
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        //遍历
        for (Long id : ids) {
            ItemCat itemCat = itemCatDao.selectByPrimaryKey ( id );
            itemCat.setStatus ( status );
            itemCatDao.updateByPrimaryKey ( itemCat );
        }
    }
}
