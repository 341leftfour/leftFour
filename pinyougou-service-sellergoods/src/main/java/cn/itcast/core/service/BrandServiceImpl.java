package cn.itcast.core.service;

import cn.itcast.common.utils.ExportExcel;
import cn.itcast.common.utils.ImportExcel;
import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 品牌管理
 */
@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandDao brandDao;
    @Value("${importExcelBrand}")
    private String importExcelBrand;
    @Value("${exportExcelBrand}")
    private String exportExcelBrand;
    //查询所有品牌
    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }


    //查询分页对象
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {



        //分页小助手
        PageHelper.startPage(pageNum,pageSize);
        //查询所有
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);



        return new PageResult(page.getTotal(),page.getResult());
    }

    //添加品牌
    @Override
    public void add(Brand brand) {
        brand.setStatus("0");
        brandDao.insertSelective(brand);
                // insert into tb_brand (id,name, 100个字段 ) values (1,haha,null,100个)  执行的效果一样吗？效率？
                // insert into tb_brand (id,name ) values (1,haha)  一样

    }

    //查询一个品牌
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    //修改
    @Override
    public void update(Brand brand) {
        if (!brandDao.selectByPrimaryKey(brand.getId()).equals(brand)){
            brand.setStatus("0");
            brandDao.updateByPrimaryKeySelective(brand);
        }

    }

    //删除   集合 与数组关系  集合底层是 数组
    @Override
    public void delete(Long[] ids) {

        if(null != ids && ids.length > 0){
            //一个一个删除
         /*   for (Long id : ids) {
                brandDao.deleteByPrimaryKey(id);
            }*/
            //批量删除 delete * from tb_brand where id in (1,2,3)
            BrandQuery brandQuery = new BrandQuery();
            brandQuery.createCriteria().andIdIn(Arrays.asList(ids));
            brandDao.deleteByExample(brandQuery);
        }

    }

    //查询分页对象 条件
    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {

        //分页小助手
        PageHelper.startPage(pageNum,pageSize);

        //条件查询
        BrandQuery brandQuery = new BrandQuery();
        BrandQuery.Criteria criteria = brandQuery.createCriteria();


        //判断状态
        if (null != brand.getStatus() && !"".equals(brand.getStatus().trim())) {
            criteria.andStatusEqualTo(brand.getStatus().trim());
        }
        //品牌名称  模糊查询
        if(null != brand.getName() && !"".equals(brand.getName().trim())){
            criteria.andNameLike("%" + brand.getName().trim() + "%");
        }
        if(null != brand.getFirstChar() && !"".equals(brand.getFirstChar().trim())){
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
                    //逆向工程 只能单表操作 只有简单查询  超出此范围  手写Sql
        }
        //查询所有
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);

        return new PageResult(page.getTotal(),page.getResult());
    }

    //查询返回值 为List<Map>
    @Override
    public List<Map> selectOptionList() {
        //List<Brand> brandList = brandDao.selectByExample(null);
        return brandDao.selectOptionList();
    }

    @Override
    public void updateStatus(Long[] ids, String status) {

        Brand brand = new Brand();
        brand.setStatus(status);
        for (Long id : ids) {
            brand.setId(id);
            brandDao.updateByPrimaryKeySelective(brand);
        }
    }

    @Override
    public void importExcel(String file) throws IOException {
        List<Brand> list = (List<Brand>) ImportExcel.importExcel(file, 2, 0, Brand.class);
        for (Brand brand : list) {
            brandDao.insertSelective(brand);
        }
    }

    @Override
    public void exportExcel(String file) {
        String[] headers = {"ID","名称","首字母","状态"};
        List<Brand> brandList = brandDao.selectByExample(null);
        ExportExcel.exportExcel("品牌表","品牌",headers,brandList,file,"yyyy-MM-dd");
    }
}
