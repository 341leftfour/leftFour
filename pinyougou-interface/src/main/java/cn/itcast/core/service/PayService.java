package cn.itcast.core.service;

import java.util.Map;

public interface PayService {
    Map<String,String> createNative(String name);

    Map<String,String> queryPayStatus(String out_trade_no);
}
