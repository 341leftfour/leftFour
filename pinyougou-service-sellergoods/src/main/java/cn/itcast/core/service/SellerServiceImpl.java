package cn.itcast.core.service;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 商家管理
 */
@Service
@Transactional
public class SellerServiceImpl implements SellerService {


    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private UserDao userDao;

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

    /********* 原有的方法 ********
    //根据用户名查询一个用户对象
    @Override
    public Seller findOne(String username) {
        return sellerDao.selectByPrimaryKey(username);
    }
    *********** 原有的方法 **********/

    //查询分页，带条件
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        //分页助手
        PageHelper.startPage(page,rows);

        //条件查询
        SellerQuery sellerQuery = new SellerQuery();
        SellerQuery.Criteria queryCriteria = sellerQuery.createCriteria();

        //对 公司名称 进行模糊查询
        if(null != seller.getName() && !"".equals(seller.getName().trim())){
            queryCriteria.andNameLike("%" + seller.getName().trim() + "%");
        }

        //对 店铺名称 进行模糊查询
        if(null != seller.getNickName() && !"".equals(seller.getNickName().trim())){
            queryCriteria.andNickNameLike("%" + seller.getNickName().trim() +"%");
        }


        //查询所有
        Page<Seller> sellerPage = (Page<Seller>) sellerDao.selectByExample(sellerQuery);

        return new PageResult(sellerPage.getTotal(),sellerPage.getResult());
    }

    //查询一个商家对象
    @Override
    public Seller findOne(String sellerId) {
        return sellerDao.selectByPrimaryKey(sellerId);
    }

    //修改商家审核状态
    @Override
    public void updateStatus(String sellerId, String status) {
        sellerDao.updateStatusById(sellerId,status);
    }


    /**
     *   查询一个商家对象
     */
    @Override
    public List<Seller> findAll() {
        UserQuery userQuery = new UserQuery();
        userQuery.setOrderByClause("created");
        userDao.selectByExample(userQuery);

        List<Seller> sellerList = sellerDao.selectByExample(null);
        return sellerList;
    }
}

