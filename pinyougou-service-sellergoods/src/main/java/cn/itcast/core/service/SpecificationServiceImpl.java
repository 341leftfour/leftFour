package cn.itcast.core.service;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import vo.SpecificationVo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 规格管理
 */
@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecificationDao specificationDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    //查询分页 条件
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {

        //分页插件
        PageHelper.startPage(page,rows);
        //查询分页对象


        //条件查询
        SpecificationQuery specificationQuery = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();


        //判断状态
        if (null != specification.getStatus() && !"".equals(specification.getStatus().trim())) {
            criteria.andStatusEqualTo(specification.getStatus().trim());
        }
        //规格名称  模糊查询
        if(null != specification.getSpecName() && !"".equals(specification.getSpecName().trim())){
            criteria.andSpecNameLike("%" + specification.getSpecName().trim() + "%");
        }

        Page<Specification> p = (Page<Specification>) specificationDao.selectByExample(specificationQuery);

        return new PageResult(p.getTotal(),p.getResult());
    }

    //添加
    @Override
    public void add(SpecificationVo vo) {

        vo.getSpecification().setStatus("0");
        //规格表  1  返回ID
        specificationDao.insertSelective(vo.getSpecification());
         
        //规格选项表 多
        List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptionList) {
            //设置规格的ID 作为 外键
            specificationOption.setSpecId(vo.getSpecification().getId());
            specificationOptionDao.insertSelective(specificationOption);
        }
    }

    //查询一个
    @Override
    public SpecificationVo findOne(Long id) {

        SpecificationVo vo = new SpecificationVo();
        //规格对象
        vo.setSpecification(specificationDao.selectByPrimaryKey(id));
        //规格选项结果集对象
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(id);
        vo.setSpecificationOptionList(specificationOptionDao.selectByExample(query));

        return vo;
    }

    //修改
    @Override
    public void update(SpecificationVo vo) {

        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        specificationOptionQuery.createCriteria().andSpecIdEqualTo(vo.getSpecification().getId());
        List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(specificationOptionQuery);
        SpecificationVo vo2 = new SpecificationVo();
        vo2.setSpecificationOptionList(specificationOptions);
        vo2.setSpecification(specificationDao.selectByPrimaryKey(vo.getSpecification().getId()));

        if (!vo.equals(vo2)) {
            vo.getSpecification().setStatus("0");

            //规格表 修改
            specificationDao.updateByPrimaryKeySelective(vo.getSpecification());

            //规格选项表
            // 1:先删除
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo(vo.getSpecification().getId());
            specificationOptionDao.deleteByExample(query);

            //2:再添加

            List<SpecificationOption> specificationOptionList = vo.getSpecificationOptionList();
            for (SpecificationOption specificationOption : specificationOptionList) {
                //设置规格的ID 作为 外键
                specificationOption.setSpecId(vo.getSpecification().getId());
                specificationOptionDao.insertSelective(specificationOption);
            }
        }
    }

    //查询所有规格
    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }

    //删除规格
    @Override
    public void delete(Long[] ids) {
        if(null != ids && ids.length > 0){
            //一个一个删除
         /*   for (Long id : ids) {
                brandDao.deleteByPrimaryKey(id);
            }*/
            //批量删除 delete * from tb_brand where id in (1,2,3)
            //删除主规格
            SpecificationQuery query = new SpecificationQuery();
            query.createCriteria().andIdIn(Arrays.asList(ids));
            specificationDao.deleteByExample(query);


            //删除副规格
            SpecificationOptionQuery query2 = new SpecificationOptionQuery();
            query2.createCriteria().andSpecIdIn(Arrays.asList(ids));
            specificationOptionDao.deleteByExample(query2);
        }
    }



    @Override
    public void updateStatus(Long[] ids, String status) {

        Specification specification = new Specification();
        specification.setStatus(status);
        for (Long id : ids) {
            specification.setId(id);
            specificationDao.updateByPrimaryKeySelective(specification);


        }
    }
}
