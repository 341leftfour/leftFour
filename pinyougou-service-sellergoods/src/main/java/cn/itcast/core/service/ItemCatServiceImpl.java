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



    //网站前台商品分类显示
    @Override
    public List<ItemCat> findItemCatList() {
        redisTemplate.delete("itemCat");
        //从缓存中查询首页商品分类
        List<ItemCat> itemCatList = (List<ItemCat>) redisTemplate.boundHashOps("itemCat").get("indexItemCat");

        //如果缓存中没有数据，则从数据库查询再存入缓存
        if(itemCatList==null){
            //查询出1级商品分类的集合
            List<ItemCat> itemCatList1 = itemCatDao.findItemCatListByParentId(0L);
            //遍历1级商品分类的集合
            for(ItemCat itemCat1:itemCatList1){
                //查询2级商品分类的集合(将1级商品分类的id作为条件)
                List<ItemCat> itemCatList2 = itemCatDao.findItemCatListByParentId(itemCat1.getId());
                //遍历2级商品分类的集合
                for(ItemCat itemCat2:itemCatList2){
                    //查询3级商品分类的集合(将2级商品分类的父id作为条件)
                    List<ItemCat> itemCatList3 = itemCatDao.findItemCatListByParentId(itemCat2.getId());
                    //将2级商品分类的集合封装到2级商品分类实体中
                    itemCat2.setItemCatList(itemCatList3);
                }
                /*到这一步的时候，3级商品分类已经封装到2级分类中*/
                //将2级商品分类的集合封装到1级商品分类实体中
                itemCat1.setItemCatList(itemCatList2);
            }
            //存入缓存
            redisTemplate.boundHashOps("itemCat").put("indexItemCat",itemCatList1);
            return itemCatList1;
        }
        //到这一步，说明缓存中有数据，直接返回
        return itemCatList;
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
