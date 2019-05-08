package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * 安全框架 自定义认证类
 * 查询数据库中的用户名密码
 */
public class UserDetailServiceImpl implements UserDetailsService {


    private SellerService sellerService;
    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        //根据用户名 查询数据库中  用户对象  //要求：必须是审核通过的 如果审核不通过或是未审核 不允许登陆

        Seller seller = sellerService.findOne(username);

        //判断 如果不为NULL 有
        if(null != seller){
          //判断此用户的状态是否为审核通过
            if("1".equals(seller.getStatus())){
                //                //此用户可以登陆
                Set<GrantedAuthority> authorities = new HashSet<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));
                //  参数1：用户名  参数2：加密后的密码
                return new User(username,seller.getPassword(),authorities);
            }

        }
        return null;
    }
}
