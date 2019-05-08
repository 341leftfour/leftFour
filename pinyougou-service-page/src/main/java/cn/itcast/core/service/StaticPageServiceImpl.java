package cn.itcast.core.service;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 静态化处理实现类
 */
@Service
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {


    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;


    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private ItemDao itemDao;

    //静态化程序方法  人为调用  定时器
    // 入参: 商品ID
    public void index(Long id){
        //freemarker 对象
        Configuration conf = freeMarkerConfigurer.getConfiguration();

        //输出路径 绝对路径
        String path = getPath("/"+id+".html");//Linux


        //D:\ideaProject1\341\pinyougou-parent\pinyougou-service-sellergoods\
        // target\pinyougou-service-sellergoods-1.0-SNAPSHOT/id.html
        //输出流
        Writer out = null;
        try {
            //加载模板  返回值是模板对象  读取   从磁盘上读取到内存中
            Template template = conf.getTemplate("item.ftl");
            //数据
            Map<String,Object> root = new HashMap<>();

            //商品详情对象
            GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
            root.put("goodsDesc",goodsDesc);

            //商品对象  一级 二级 三级的商品分类的ID
            Goods goods = goodsDao.selectByPrimaryKey(id);
            root.put("goods",goods);

            //一级商品分类 名称
            root.put("itemCat1",itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName());
            //二级商品分类 名称
            root.put("itemCat2",itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName());
            //三级商品分类 名称
            root.put("itemCat3",itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());

            //查询库存结果集  商品ID 外键
            ItemQuery itemQuery = new ItemQuery();
            itemQuery.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1");
            List<Item> itemList = itemDao.selectByExample(itemQuery);
            root.put("itemList",itemList);

            //输出流  写入 从内存中写入磁盘上
            out = new OutputStreamWriter(new FileOutputStream(path),"UTF-8");
            //处理
            template.process(root,out);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != out){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //获取全路径
    public String getPath(String path){
        return servletContext.getRealPath(path);
    }

    private ServletContext servletContext;
    //当前Spring容器  上下文
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
