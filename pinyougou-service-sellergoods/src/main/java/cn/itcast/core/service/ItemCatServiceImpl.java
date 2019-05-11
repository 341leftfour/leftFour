package cn.itcast.core.service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
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

    @Override
    public PageResult search(Integer page, Integer rows,ItemCat itemCat) {

        //分页小助手
        PageHelper.startPage(page,rows);

        //条件查询
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();

        //判断状态
        if (null != itemCat.getStatus() && !"".equals(itemCat.getStatus().trim())) {
            criteria.andStatusEqualTo(itemCat.getStatus().trim());
        }
        //分类名称  模糊查询
        if(null != itemCat.getName() && !"".equals(itemCat.getName().trim())){
            criteria.andNameLike("%" + itemCat.getName().trim() + "%");
        }

        //查询所有
        Page<ItemCat> p = (Page<ItemCat>) itemCatDao.selectByExample(itemCatQuery);

        return new PageResult(p.getTotal(),p.getResult());
    }

}
