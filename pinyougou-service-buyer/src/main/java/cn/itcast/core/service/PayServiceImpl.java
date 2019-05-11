package cn.itcast.core.service;

import cn.itcast.common.utils.HttpClient;
import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.seckill.SeckillOrder;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 支付管理
 */
@Service
public class PayServiceImpl implements PayService {



    //获取二维码内容 value
    //生成二维码
/*    var qr=new QRious({
            element:document.getElementById('qrious'),
            size:250,
            value:response.code_url,//微信服务器       手机微信客户端 App
            level:'H'
});*/
    //连接微信服务器  code_url

    @Autowired
    private RedisTemplate redisTemplate;


    @Value("${appid}")
    private String appid;
    @Value("${partner}")
    private String partner;
    @Value("${partnerkey}")
    private String partnerkey;

    @Autowired
    private IdWorker idWorker;


    @Override
    public Map<String, String> createNative(String name) {
        //日志对象  理解为 订单对象 (支付ID就是订单ID) 总金额 就是支付金额 (分)
        PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(name);

        //订单ID payLog.getOutTradeNo()
        //支付金额 payLog.getTotalFee() 分
        //连接微信服务器的连接
        //此连接目的是:统一下单   微信那边预支付交易      (白话:提前跟银行打好招呼 准备要收一笔巨款)
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        //请求Http请求  响应  使用Apache开发的 HttpClient  Http请求的客户端 Java代码写了一个浏览器  (模拟了浏览器)
        HttpClient httpClient = new HttpClient(url);
        //设置https协议
        httpClient.setHttps(true);

        Map<String,String> param = new HashMap<>();

//        公众账号ID 	appid 	是 	String(32) 	wxd678efh567hg6787 	微信支付分配的公众账号ID（企业号corpid即为此appId）
        param.put("appid",appid);
//        商户号 	mch_id 	是 	String(32) 	1230000109 	微信支付分配的商户号
        param.put("mch_id",partner);
//        设备号 	device_info 	否 	String(32) 	013467007045764 	自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
//        随机字符串 	nonce_str 	是 	String(32) 	5K8264ILTKCH16CQ2502SI8ZNMTM67VS 	随机字符串，长度要求在32位以内。推荐随机数生成算法
        param.put("nonce_str", WXPayUtil.generateNonceStr());
//        商品描述 	body 	是 	String(128) 	腾讯充值中心-QQ会员充值
        param.put("body", "品优购商城");
//
//        商品简单描述，该字段请按照规范传递，具体请见参数规定
//        商品详情 	detail 	否 	String(6000) 	  	商品详细描述，对于使用单品优惠的商户，改字段必须按照规范上传，详见“单品优惠参数说明”
//        附加数据 	attach 	否 	String(127) 	深圳分店 	附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
//        商户订单号 	out_trade_no 	是 	String(32) 	20150806125346 	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
        //param.put("out_trade_no", payLog.getOutTradeNo());
        String id = String.valueOf(idWorker.nextId());
        param.put("out_trade_no", id);
//        标价币种 	fee_type 	否 	String(16) 	CNY 	符合ISO 4217标准的三位字母代码，默认人民币：CNY，详细列表请参见货币类型

//        标价金额 	total_fee 	是 	Int 	88 	订单总金额，单位为分，详见支付金额
        param.put("total_fee", "1");
        //param.put("total_fee", String.valueOf(payLog.getTotalFee())); 正确

//        终端IP 	spbill_create_ip 	是 	String(64) 	123.12.12.123 	支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
        param.put("spbill_create_ip", "127.0.0.1");

//        交易起始时间 	time_start 	否 	String(14) 	20091225091010 	订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
//        交易结束时间 	time_expire 	否 	String(14) 	20091227091010
//
//        订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
//
//        建议：最短失效时间间隔大于1分钟
//        订单优惠标记 	goods_tag 	否 	String(32) 	WXG 	订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠
//        通知地址 	notify_url 	是 	String(256) 	http://www.weixin.qq.com/wxpay/pay.php 	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        param.put("notify_url", "http://www.itcast.cn");
//        交易类型 	trade_type 	是 	String(16) 	JSAPI
        param.put("trade_type", "NATIVE");
        try {
            String xml = WXPayUtil.generateSignedXml(param, partnerkey);
//        签名 	sign 	是 	String(32) 	C380BEC2BFD727A4B6845133519F3AD6 	通过签名算法计算得出的签名值，详见签名生成算法
//        签名类型 	sign_type 	否 	String(32) 	MD5 	签名类型，默认为MD5，支持HMAC-SHA256和MD5。
        //设置入参
        httpClient.setXmlParam(xml);
        //POST提交
        httpClient.post();
        //响应
            String content = httpClient.getContent();

            Map<String, String> map = WXPayUtil.xmlToMap(content);
            //链接 code_url
            //支付ID
         /*   map.put("out_trade_no",payLog.getOutTradeNo());*/
            map.put("out_trade_no",id);
            //总金额
            map.put("total_fee",String.valueOf(payLog.getTotalFee()));

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //根据订单ID 查询支付状态
    @Override
    public Map<String, String> queryPayStatus(String out_trade_no) {
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        //请求Http请求  响应  使用Apache开发的 HttpClient  Http请求的客户端 Java代码写了一个浏览器  (模拟了浏览器)
        HttpClient httpClient = new HttpClient(url);
        //设置https协议
        httpClient.setHttps(true);
        //入参
        Map<String,String> param = new HashMap<>();
//        公众账号ID 	appid 	是 	String(32) 	wxd678efh567hg6787 	微信支付分配的公众账号ID（企业号corpid即为此appId）
        param.put("appid",appid);
//        商户号 	mch_id 	是 	String(32) 	1230000109 	微信支付分配的商户号
        param.put("mch_id",partner);
//        设备号 	device_info 	否 	String(32) 	013467007045764 	自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
//        随机字符串 	nonce_str 	是 	String(32) 	5K8264ILTKCH16CQ2502SI8ZNMTM67VS 	随机字符串，长度要求在32位以内。推荐随机数生成算法
        param.put("nonce_str", WXPayUtil.generateNonceStr());
        param.put("out_trade_no", out_trade_no);
        try {
            String xml = WXPayUtil.generateSignedXml(param, partnerkey);
//        签名 	sign 	是 	String(32) 	C380BEC2BFD727A4B6845133519F3AD6 	通过签名算法计算得出的签名值，详见签名生成算法
//        签名类型 	sign_type 	否 	String(32) 	MD5 	签名类型，默认为MD5，支持HMAC-SHA256和MD5。
            //设置入参
            httpClient.setXmlParam(xml);
            //POST提交
            httpClient.post();
            //响应
            String content = httpClient.getContent();

            Map<String, String> map = WXPayUtil.xmlToMap(content);

            System.out.println(content);

            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    //秒杀商品的二维码生成
    @Override
    public Map<String, String> createNativeforSecKill(String id) {
        //日志对象  理解为 订单对象 (支付ID就是订单ID) 总金额 就是支付金额 (分)
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.boundHashOps("seckillOrder").get(id);

        //订单ID payLog.getOutTradeNo()
        //支付金额 payLog.getTotalFee() 分
        //连接微信服务器的连接
        //此连接目的是:统一下单   微信那边预支付交易      (白话:提前跟银行打好招呼 准备要收一笔巨款)
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        //请求Http请求  响应  使用Apache开发的 HttpClient  Http请求的客户端 Java代码写了一个浏览器  (模拟了浏览器)
        HttpClient httpClient = new HttpClient(url);
        //设置https协议
        httpClient.setHttps(true);

        Map<String,String> param = new HashMap<>();

//        公众账号ID 	appid 	是 	String(32) 	wxd678efh567hg6787 	微信支付分配的公众账号ID（企业号corpid即为此appId）
        param.put("appid",appid);
//        商户号 	mch_id 	是 	String(32) 	1230000109 	微信支付分配的商户号
        param.put("mch_id",partner);
//        设备号 	device_info 	否 	String(32) 	013467007045764 	自定义参数，可以为终端设备号(门店号或收银设备ID)，PC网页或公众号内支付可以传"WEB"
//        随机字符串 	nonce_str 	是 	String(32) 	5K8264ILTKCH16CQ2502SI8ZNMTM67VS 	随机字符串，长度要求在32位以内。推荐随机数生成算法
        param.put("nonce_str", WXPayUtil.generateNonceStr());
//        商品描述 	body 	是 	String(128) 	腾讯充值中心-QQ会员充值
        param.put("body", "品优购商城秒杀支付");
//
//        商品简单描述，该字段请按照规范传递，具体请见参数规定
//        商品详情 	detail 	否 	String(6000) 	  	商品详细描述，对于使用单品优惠的商户，改字段必须按照规范上传，详见“单品优惠参数说明”
//        附加数据 	attach 	否 	String(127) 	深圳分店 	附加数据，在查询API和支付通知中原样返回，可作为自定义参数使用。
//        商户订单号 	out_trade_no 	是 	String(32) 	20150806125346 	商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|* 且在同一个商户号下唯一。详见商户订单号
        //param.put("out_trade_no", payLog.getOutTradeNo());
        //String id = String.valueOf(idWorker.nextId());
        String s = String.valueOf(idWorker.nextId());
        param.put("out_trade_no", s);
//        标价币种 	fee_type 	否 	String(16) 	CNY 	符合ISO 4217标准的三位字母代码，默认人民币：CNY，详细列表请参见货币类型

//        标价金额 	total_fee 	是 	Int 	88 	订单总金额，单位为分，详见支付金额
        param.put("total_fee", "1");
        //param.put("total_fee", String.valueOf(payLog.getTotalFee())); 正确

//        终端IP 	spbill_create_ip 	是 	String(64) 	123.12.12.123 	支持IPV4和IPV6两种格式的IP地址。调用微信支付API的机器IP
        param.put("spbill_create_ip", "127.0.0.1");

//        交易起始时间 	time_start 	否 	String(14) 	20091225091010 	订单生成时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则
//        交易结束时间 	time_expire 	否 	String(14) 	20091227091010
//
//        订单失效时间，格式为yyyyMMddHHmmss，如2009年12月27日9点10分10秒表示为20091227091010。订单失效时间是针对订单号而言的，由于在请求支付的时候有一个必传参数prepay_id只有两小时的有效期，所以在重入时间超过2小时的时候需要重新请求下单接口获取新的prepay_id。其他详见时间规则
//
//        建议：最短失效时间间隔大于1分钟
//        订单优惠标记 	goods_tag 	否 	String(32) 	WXG 	订单优惠标记，使用代金券或立减优惠功能时需要的参数，说明详见代金券或立减优惠
//        通知地址 	notify_url 	是 	String(256) 	http://www.weixin.qq.com/wxpay/pay.php 	异步接收微信支付结果通知的回调地址，通知url必须为外网可访问的url，不能携带参数。
        param.put("notify_url", "http://www.itcast.cn");
//        交易类型 	trade_type 	是 	String(16) 	JSAPI
        param.put("trade_type", "NATIVE");
        try {
            String xml = WXPayUtil.generateSignedXml(param, partnerkey);
//        签名 	sign 	是 	String(32) 	C380BEC2BFD727A4B6845133519F3AD6 	通过签名算法计算得出的签名值，详见签名生成算法
//        签名类型 	sign_type 	否 	String(32) 	MD5 	签名类型，默认为MD5，支持HMAC-SHA256和MD5。
            //设置入参
            httpClient.setXmlParam(xml);
            //POST提交
            httpClient.post();
            //响应
            String content = httpClient.getContent();
            Map<String, String> map = WXPayUtil.xmlToMap(content);
            //链接 code_url
            //支付ID
            /*   map.put("out_trade_no",payLog.getOutTradeNo());*/
            map.put("out_trade_no",s);
            //总金额
            map.put("total_fee",String.valueOf(seckillOrder.getMoney().doubleValue()*100));
            return map;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}
