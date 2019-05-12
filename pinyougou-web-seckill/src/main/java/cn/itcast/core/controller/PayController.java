package cn.itcast.core.controller;

import cn.itcast.core.pojo.seckill.SeckillOrder;
import cn.itcast.core.service.PayService;
import cn.itcast.core.service.SeckillOrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private PayService payService;

    @Reference
    private SeckillOrderService seckillOrderService;

    /**
     * 生成二维码
     *
     * @return
     */
    @RequestMapping("/createNative")
    public Map createNative() {
        //获取当前用户
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //到redis查询秒杀订单
        SeckillOrder seckillOrder = seckillOrderService.searchOrderFromRedisByUserId(userId);
        //判断秒杀订单存在
        if (seckillOrder != null) {
            long fen = (long) (seckillOrder.getMoney().doubleValue() * 100);//金额（分）
//            return weixinPayService.createNativeforSecKill(seckillOrder.getId() + "", +fen + "");
            return payService.createNativeforSecKill(userId);
        } else {
            return new HashMap();
        }
    }
    /**
     * 查询状态
     * @param out_trade_no
     * @return
     */
//    @RequestMapping("/queryPayStatus")
//    public Result queryPayStatus(String out_trade_no) {
//        //获取当前用户
//        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
//        Map map = payService.queryPayStatus(out_trade_no);
//        if(map==null){
//            return new Result(false,"支付失败");
//        }else{
//            if(map.get("trade_state").equals("SUCCESS")){//如果成功
//                //修改订单状态
//                seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), (String)map.get("transaction_id"));
//                return new  Result(true, "支付成功");
//            }else{
//                return new Result(false,"超时");
//            }
//        }
//    }

    //查询支付状态
    @RequestMapping("/queryPayStatus")
    public Result queryPayStatus(String out_trade_no){
        String userId=SecurityContextHolder.getContext().getAuthentication().getName();
        try {
            int x = 0;

            while (true){
                Map<String,String> map = payService.queryPayStatus(out_trade_no);
                //未支付 再查
                if("NOTPAY".equals(map.get("trade_state"))){

                    //睡一会
                    Thread.sleep(3000);
                    x++;
                    if(x > 200){
                        //再次调用 微信服务器Api  关闭订单(同学写了)
                        return new Result(false,"支付超时");
                    }
                }else{
                    //修改订单状态
                    seckillOrderService.saveOrderFromRedisToDb(userId, Long.valueOf(out_trade_no), (String)map.get("transaction_id"));
                    return new Result(true,"支付成功");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }

    @RequestMapping("/cancelPay")
    public Result cancelPay(){
        try {
            String userId=SecurityContextHolder.getContext().getAuthentication().getName();
            seckillOrderService.cancelPay(userId);
            return new Result(true, "取消订单成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "取消订单失败");
        }
    }
}
