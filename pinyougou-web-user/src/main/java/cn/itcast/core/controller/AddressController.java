package cn.itcast.core.controller;


import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.service.AddressService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    AddressService addressService;


    @RequestMapping("/findAddressByUserId")
    public List<Address> findAddressByUserId(){
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        List<Address> addressList = addressService.findListByLoginUser(name);
        for (Address address : addressList) {
            String mobile = address.getMobile();
            String substring = mobile.substring(0, 3);
            substring += "****";
            substring+=mobile.substring(7,11);
            address.setMobile(substring);
        }
        return addressList;

    }

}
