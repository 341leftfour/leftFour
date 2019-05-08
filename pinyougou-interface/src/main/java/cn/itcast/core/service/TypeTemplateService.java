package cn.itcast.core.service;

import cn.itcast.core.pojo.template.TypeTemplate;
import entity.PageResult;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {
    PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate);

    void add(TypeTemplate typeTemplate);

    TypeTemplate findOne(Long id);

    void update(TypeTemplate typeTemplate);

    List<Map> findBySpecList(Long id);
}
