package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 模板管理
 */
@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    @Autowired
    private RedisTemplate redisTemplate;

    //查询分页 条件
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
        //查询所有模板结果集
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        for (TypeTemplate template : typeTemplates) {

            //品牌结果集  [{"id":35,"text":"牛栏山"},{"id":36,"text":"剑南春"},{"id":39,"text":"口子窑"}]
            List<Map> brandList = JSON.parseArray(template.getBrandIds(), Map.class);
            redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
            //规格结果集  [{"id":40,"text":"颜色"},{"id":41,"text":"衣服尺码"}]
            List<Map> specList = findBySpecList(template.getId());
            redisTemplate.boundHashOps("specList").put(template.getId(),specList);


        }

        //分页小助手
        PageHelper.startPage(page, rows);
        //排序
        PageHelper.orderBy("id desc");

        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(null);

        //select * from tb_type_template where ....   order by  id desc limit 开始行,每页数

        return new PageResult(p.getTotal(), p.getResult());
    }

    //添加
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    //查询一个模板对象
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    //修改
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    //根据模板ID 查询规格结果集 List<Map> 每个Map里还有结果集
    @Override
    public List<Map> findBySpecList(Long id) {

        //1：根据模板ID 查询模板对象
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //2:模板对象中规格字符串  [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        //3:将上面的Json格式字符串转成List<Map>
        List<Map> specList = JSON.parseArray(specIds, Map.class);

        for (Map map : specList) {
//            id    :   27
//            text : 网络
//            options : 根据27查询规格选项结果集
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            //map.get("id")  Object --> 简单类型 Integer String  -->     长整  Long   类型转换异常
            // query.createCriteria().andSpecIdEqualTo((long)(Integer)(map.get("id")));
            query.createCriteria().andSpecIdEqualTo(Long.parseLong(String.valueOf(map.get("id"))));
            List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(query);
            map.put("options",specificationOptions);
        }

        return specList;
    }
}
