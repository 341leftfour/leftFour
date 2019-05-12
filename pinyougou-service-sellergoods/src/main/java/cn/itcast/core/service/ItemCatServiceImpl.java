package cn.itcast.core.service;

import cn.itcast.common.utils.ExportExcel;
import cn.itcast.common.utils.ImportExcel;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    @Value("${importExcelItemCat}")
    private String importExcelItemCat;
    @Value("${exportExcelItemCat}")
    private String exportExcelItemCat;
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

    @Override
    public void importExcel() throws IOException {
        List<ItemCat> list = (List<ItemCat>) ImportExcel.importExcel(importExcelItemCat, 2, 0, ItemCat.class);
        for (ItemCat itemCat : list) {
            itemCatDao.insertSelective(itemCat);
        }
    }

    @Override
    public void exportExcel() {
        String[] headers = {"ID","父类目ID","类目名称","模板ID","状态"};
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        ExportExcel.exportExcel("分类表","分类",headers,itemCatList,exportExcelItemCat,"yyyy-MM-dd");
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

    @Override
    public void add(ItemCat itemCat) {
        itemCat.setStatus("0");
        itemCatDao.insertSelective(itemCat);
    }

}
