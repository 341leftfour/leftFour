package cn.itcast.core.service;

import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

/**
 * 搜索管理
 * Transactional  : Mysql
 * 现在连接 是索引库  事务 自行提交事务
 */
@Service
//@Transactional
public class ItemsearchServiceImpl implements ItemsearchService {

    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    //开始搜索
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {

        Map<String, Object> resultMap = new HashMap<>();

        //1:商品分类
        List<String> categoryList = searchCategoryListByKeywords(searchMap);
        resultMap.put("categoryList", categoryList);
        //2:品牌
        //3:规格
        if(null != categoryList && categoryList.size() > 0){
            resultMap.putAll(searchBrandListAndSpecListByCategory(categoryList.get(0)));
        }
        //4:查询结果集(根据关键词,分类,规格 ,品牌 价格,排序  查询高亮结果集)  总条数 总页数
        resultMap.putAll(searchHighlightPage(searchMap));


        return resultMap;
    }

    //2:品牌
    //3:规格
    public Map<String,Object> searchBrandListAndSpecListByCategory(String category){
        Map<String, Object> resultMap = new HashMap<>();

        //模板ID
        Object typeId = redisTemplate.boundHashOps("itemCat").get(category);

        //品牌
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        //规格
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);

        resultMap.put("brandList",brandList);
        resultMap.put("specList",specList);

        return resultMap;
    }
    //查询商品分类  手机 电视   选号入网
    public List<String> searchCategoryListByKeywords(Map<String, String> searchMap) {

        //关键词
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        Query query = new SimpleQuery(criteria);

        //查询商品分类 分组查询
        GroupOptions groupOptions = new GroupOptions();
        //分组的域名
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //商品分类 结果集
        List<String> categoryList = new ArrayList<>();

        //执行查询
        GroupPage<Item> page = solrTemplate.queryForGroupPage(query, Item.class);

        GroupResult<Item> item_category = page.getGroupResult("item_category");

        Page<GroupEntry<Item>> groupEntries = item_category.getGroupEntries();

        List<GroupEntry<Item>> content = groupEntries.getContent();

        for (GroupEntry<Item> itemGroupEntry : content) {
            //商品分类名称
            categoryList.add(itemGroupEntry.getGroupValue());
        }

        return categoryList;

    }

    //查询高亮分页结果集
    public Map<String, Object> searchHighlightPage(Map<String, String> searchMap) {

        Map<String, Object> resultMap = new HashMap<>();

        //使用关键词之前 先处理一下  三           星 手  机
        searchMap.put("keywords",searchMap.get("keywords").replaceAll(" ",""));


        //关键词
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        HighlightQuery highlightQuery = new SimpleHighlightQuery(criteria);

        //分页
        String pageNo = searchMap.get("pageNo");
        String pageSize = searchMap.get("pageSize");
        highlightQuery.setOffset((Integer.parseInt(pageNo) - 1) * Integer.parseInt(pageSize));
        highlightQuery.setRows(Integer.parseInt(pageSize));

        //高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        //高亮的域
        highlightOptions.addField("item_title");
        //前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //后缀
        highlightOptions.setSimplePostfix("</em>");
        highlightQuery.setHighlightOptions(highlightOptions);


        //过滤条件
        //商品分类
        if(null != searchMap.get("category") && !"".equals(searchMap.get("category").trim())){

            FilterQuery filterQuery = new SimpleFilterQuery();
            //商品分类
            filterQuery.addCriteria(new Criteria("item_category").is(searchMap.get("category")));
            highlightQuery.addFilterQuery(filterQuery);

        }
        //品牌
        if(null != searchMap.get("brand") && !"".equals(searchMap.get("brand").trim())){

            FilterQuery filterQuery = new SimpleFilterQuery();
            //品牌
            filterQuery.addCriteria(new Criteria("item_brand").is(searchMap.get("brand")));
            highlightQuery.addFilterQuery(filterQuery);

        }
        //规格
        //定义搜索对象的结构  category:商品分类
        // $scope.searchMap={'spec':'{'网络':3G,...}','pageNo':1,'pageSize':40,'sort':'','sortField':''};

        if(null != searchMap.get("spec") && !"".equals(searchMap.get("spec"))){
            FilterQuery filterQuery = new SimpleFilterQuery();
            //转换Map
            Map<String,String> specMap = JSON.parseObject(searchMap.get("spec"), Map.class);
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                filterQuery.addCriteria(new Criteria("item_spec_" + entry.getKey()).is(entry.getValue()));
            }
            highlightQuery.addFilterQuery(filterQuery);
        }


        //价格  0-500  3000-*  语法   域名:域值
        if(null != searchMap.get("price") && !"".equals(searchMap.get("price").trim())){

            FilterQuery filterQuery = new SimpleFilterQuery();
            //价格
            String[] p = searchMap.get("price").split("-");

            //判断价格区间是否包含*
            if(!"*".equals(p[1])){
                filterQuery.addCriteria(
                        new Criteria("item_price").between(p[0],p[1],true,true));

            }else{
                filterQuery.addCriteria(
                        new Criteria("item_price").greaterThanEqual(p[0]));
            }

            highlightQuery.addFilterQuery(filterQuery);

        }


        //排序
        //定义搜索对象的结构  category:商品分类
        // $scope.searchMap={'sort':'asc','sortField':'price'};updatetime

        if(null != searchMap.get("sortField") && !"".equals(searchMap.get("sortField"))){

            //判断排序的方式
            if("ASC".equals(searchMap.get("sort"))){

                highlightQuery.addSort(new Sort(Sort.Direction.ASC,"item_" + searchMap.get("sortField")));
            }else{
                highlightQuery.addSort(new Sort(Sort.Direction.DESC,"item_" + searchMap.get("sortField")));
            }

        }



        //执行查询
        HighlightPage<Item> page = solrTemplate.queryForHighlightPage(highlightQuery, Item.class);

        //获取高亮的结果集
        List<HighlightEntry<Item>> highlighted = page.getHighlighted();
        //判断
        if (null != highlighted && highlighted.size() > 0) {
            for (HighlightEntry<Item> item : highlighted) {
                //获取普通对象
                Item entity = item.getEntity();

                List<HighlightEntry.Highlight> highlights = item.getHighlights();
                if(null != highlights && highlights.size() > 0){
                    //获取高亮的值
                    entity.setTitle(highlights.get(0).getSnipplets().get(0));
                }

            }

        }

        //结果集
        resultMap.put("rows", page.getContent());
        //总条数
        resultMap.put("total", page.getTotalElements());
        //总页数
        resultMap.put("totalPages", page.getTotalPages());
        return resultMap;
    }

    //查询普通分页结果集
    public Map<String, Object> search1(Map<String, String> searchMap) {
        Map<String, Object> resultMap = new HashMap<>();

        //入参:Map  现在目前只有关键词
        //定义搜索对象的结构  category:商品分类
        // $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};

        //关键词 本次是精准查询   但支持模糊查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        Query query = new SimpleQuery(criteria);

        //分页
        String pageNo = searchMap.get("pageNo");
        String pageSize = searchMap.get("pageSize");
        query.setOffset((Integer.parseInt(pageNo) - 1) * Integer.parseInt(pageSize));
        query.setRows(Integer.parseInt(pageSize));

        //执行查询
        ScoredPage<Item> page = solrTemplate.queryForPage(query, Item.class);

        //结果集
        resultMap.put("rows", page.getContent());

        //总条数
        resultMap.put("total", page.getTotalElements());
        //总页数
        resultMap.put("totalPages", page.getTotalPages());
        return resultMap;
    }
}
