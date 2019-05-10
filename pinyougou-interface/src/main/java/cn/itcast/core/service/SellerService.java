package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import entity.PageResult;

/**
 *   商家管理
 */
public interface SellerService {

    /*******原有的方法****
    void add(Seller seller);

    Seller findOne(String username);
    ********原有的方法****/

    //申请入驻
    void add(Seller seller);

    //分页查询，带条件
    PageResult search(Integer page, Integer rows, Seller seller);

    //查询一个商家对象
    Seller findOne(String sellerId);

    //修改商家审核状态
    void updateStatus(String sellerId, String status);
}
