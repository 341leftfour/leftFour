package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
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
        //分页小助手
        PageHelper.startPage(page, rows);

        //条件查询
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        TypeTemplateQuery.Criteria criteria = typeTemplateQuery.createCriteria();


        //判断状态
        if (null != typeTemplate.getStatus() && !"".equals(typeTemplate.getStatus().trim())) {
            criteria.andStatusEqualTo(typeTemplate.getStatus().trim());
        }
        //规格名称  模糊查询
        if(null != typeTemplate.getName() && !"".equals(typeTemplate.getName().trim())){
            criteria.andNameLike("%" + typeTemplate.getName().trim() + "%");
        }

        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);

        //select * from tb_type_template where ....   order by  id desc limit 开始行,每页数

        return new PageResult(p.getTotal(), p.getResult());

    }

    //添加
    @Override
    public void add(TypeTemplate typeTemplate) {

        typeTemplate.setStatus("0");
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
        if (!typeTemplateDao.selectByPrimaryKey(typeTemplate.getId()).equals(typeTemplate)){
            typeTemplate.setStatus("0");

            typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
        }

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


    //删除
    @Override
    public void delete(Long[] ids) {

        if(null != ids && ids.length > 0){
            //一个一个删除
         /*   for (Long id : ids) {
                brandDao.deleteByPrimaryKey(id);
            }*/
            //批量删除 delete * from tb_brand where id in (1,2,3)

            TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
            typeTemplateQuery.createCriteria().andIdIn(Arrays.asList(ids));
            typeTemplateDao.deleteByExample(typeTemplateQuery);
        }
    }

    @Override
    public List <TypeTemplate> findAll() {
        return typeTemplateDao.selectByExample ( null );
    }

    //开始审核  参数1:数组 商品表 的ID    参数2： 驳回  2
    @Override
    public void updateStatus(Long[] ids, String status) {

        //遍历
        for (Long id : ids) {
            TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey ( id );
            typeTemplate.setStatus ( status );
            typeTemplateDao.updateByPrimaryKey ( typeTemplate );
        }
    }
}
