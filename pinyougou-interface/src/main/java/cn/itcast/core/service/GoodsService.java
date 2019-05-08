package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Goods;
import entity.PageResult;
import vo.GoodsVo;

public interface GoodsService {
    void add(GoodsVo vo);

    PageResult search(Integer page, Integer rows, Goods goods);

    GoodsVo findOne(Long id);

    void update(GoodsVo vo);

    void updateStatus(Long[] ids, String status);

    void delete(Long[] ids);
}
