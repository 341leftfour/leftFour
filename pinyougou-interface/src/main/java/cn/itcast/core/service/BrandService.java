package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Brand;
import entity.PageResult;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface BrandService {
    List<Brand> findAll();

    PageResult findPage(Integer pageNum, Integer pageSize);

    void add(Brand brand);

    Brand findOne(Long id);

    void update(Brand brand);

    void delete(Long[] ids);

    PageResult search(Integer pageNum, Integer pageSize, Brand brand);

    List<Map> selectOptionList();

    void updateStatus(Long[] ids, String status);

    void importExcel(String file) throws IOException;


    void exportExcel(String file);
}
