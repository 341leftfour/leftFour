package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vo.Cart;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车管理
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    //加入购物车
    //SpringMVC 4.2以上开始

    /**
     * allowCredentials = "true"  默认就是true    (缺省)
     *
     * @param itemId
     * @param num
     * @param response 入参:库存 ID  数量
     *                 <p>
     *                 加入购物车:  Cart对象   购物车有表吗? 没有   临时车子  没有 持久化吗? 不   订单才持久化
     *                 没有表  Cart对象
     * @return
     */

    @RequestMapping("/addGoodsToCartList")
    //@CrossOrigin(origins = {"http://localhost:9003","http://localhost:9005"},allowCredentials = "true")
    @CrossOrigin(origins = {"http://localhost:9003", "http://localhost:9005"})
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        try {
            //声明购物车集合
            List<Cart> cartList = null;
//            1:获取Cookie
            Cookie[] cookies = request.getCookies();
            //Cookie中是否有购物车的标记
            boolean k = false;


            if (null != cookies && cookies.length > 0) {
                for (Cookie cookie : cookies) {
//            2:获取Cookie中的购物车集合
                    if ("CART".equals(cookie.getName())) {
                        //有
                        String value = cookie.getValue();//json格式字符串转成购物车集合对象
                        cartList = JSON.parseArray(value, Cart.class);
                        k = true;
                        break;
                    }

                }

            }
//            3:没有 创建购物车集合
            if (null == cartList) {
                cartList = new ArrayList<>();
            }
//          4:追加当前款

            //新车子

            //根据库存ID 查询库存对象
            Item item = cartService.findItemById(itemId);
            Cart newCart = new Cart();
            //商家ID
            newCart.setSellerId(item.getSellerId());
            //创建新订单详情
            OrderItem newOrderItem = new OrderItem();
            //库存ID  数量 非常重要数据
            newOrderItem.setItemId(itemId);
            newOrderItem.setNum(num);
            //创建订单详情集合
            List<OrderItem> newOrderItemList = new ArrayList<>();
            newOrderItemList.add(newOrderItem);
            //添加订单详情集合到购物车中
            newCart.setOrderItemList(newOrderItemList);

            //1)判断当前要追加的商品在购物车集合中是否已经存在 (商家)
            int newIndexOf = cartList.indexOf(newCart);//newIndexOf  -1 不存在  >=0 存在同时 角标 indexOf比较的是商家ID
            if (-1 != newIndexOf) {
                //--存在
                //2)判断当前商品是否在购物车的商品集合中已经存在 (商品)
                Cart oldCart = cartList.get(newIndexOf);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                int indexOf = oldOrderItemList.indexOf(newOrderItem); //比较 新商品在众多老商品中是否存在
                if (indexOf != -1) {
                    //--存在  追加商品的数量
                    OrderItem oldOrderItem = oldOrderItemList.get(indexOf);
                    oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());
                } else {
                    //--不存在  追加商品的对象
                    oldOrderItemList.add(newOrderItem);
                }
            } else {
                //--不存在  直接追加新购物车
                cartList.add(newCart);
            }

            //获取当前登陆人名称
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //判断当前用户是否登陆  安全框架世界里 永久都是登陆  登陆了:显示你的用户名  未登陆:匿名登陆状态
            if (!"anonymousUser".equals(name)) {
                //登陆了
//                5:将合并后购物车追加到Redis中
                  cartService.addCartListToRedis(cartList,name);

                  //判断Cookie中是否有购物车
                  if(k){
//                6:清空Cookie并回写浏览器
                      Cookie cookie = new Cookie("CART",null);
                      cookie.setMaxAge(0);
                      cookie.setPath("/");
                      response.addCookie(cookie);
                  }


            } else {
                //未登陆
//           5:创建新Cookie(保存现在的购物车集合)
                Cookie cookie = new Cookie("CART", JSON.toJSONString(cartList));
                //设置存活时间  -1  浏览器关闭就销毁  0 立即马上销毁  >0 到了时间销毁  秒
                cookie.setMaxAge(60 * 60 * 24 * 7);
                //设置路径
                // http://localhost:9103/cart/addGoodsToCartList.do
                // http://localhost:9103/haha/addGoodsToCartList.do  获取Cookie 购物车
                cookie.setPath("/");
//            6:写回浏览器 替换之前Cookie
                response.addCookie(cookie);
            }

            return new Result(true, "加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "加入购物车失败");
        }

    }

    //查询购物车集合
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
        //声明购物车集合
        List<Cart> cartList = null;
//            未登陆
//            1:获取Cookie
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
//            2:获取Cookie中的购物车集合
                if ("CART".equals(cookie.getName())) {
                    //有
                    String value = cookie.getValue();//json格式字符串转成购物车集合对象
                    cartList = JSON.parseArray(value, Cart.class);
                }

            }

        }
        //获取当前登陆人名称
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        //判断当前用户是否登陆  安全框架世界里 永久都是登陆  登陆了:显示你的用户名  未登陆:匿名登陆状态
        if (!"anonymousUser".equals(name)) {
            //登陆
//            3:有 将此购物车合并到Redis缓存中  清空Cookie 并回写浏览器
            if(null != cartList){
                cartService.addCartListToRedis(cartList,name);
                Cookie cookie = new Cookie("CART",null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
//            4:查询所有购物车从Redis中
            cartList = cartService.findCartListFromRedis(name);

        }
//        5:有  将购物车集合装满
        if (null != cartList) {
            cartList = cartService.findCartList(cartList);
        }
//        6:回显
        return cartList;
    }
}
