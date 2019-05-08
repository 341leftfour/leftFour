package cn.itcast.core.service;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 商家管理
 */
@Service
@Transactional
public class SellerServiceImpl implements SellerService {


    @Autowired
    private SellerDao sellerDao;
    //申请入驻
    @Override
    public void add(Seller seller) {

        //加密密码
        seller.setPassword(new BCryptPasswordEncoder().encode(seller.getPassword()));

        //未审核
        seller.setStatus("0");

        //时间
        seller.setCreateTime(new Date());

        //保存
        sellerDao.insertSelective(seller);

    }

    //根据用户名查询一个用户对象
    @Override
    public Seller findOne(String username) {
        return sellerDao.selectByPrimaryKey(username);
    }
}
