package com.leeup.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.leeup.common.Const;
import com.leeup.common.ResponseCode;
import com.leeup.common.ServerResponse;
import com.leeup.dao.CartMapper;
import com.leeup.dao.ProductMapper;
import com.leeup.pojo.Cart;
import com.leeup.pojo.Product;
import com.leeup.service.ICartService;
import com.leeup.util.BigDecimalUtil;
import com.leeup.util.PropertiesUtil;
import com.leeup.vo.CartProductVo;
import com.leeup.vo.CartVo;
import com.sun.org.apache.regexp.internal.RE;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName CartServiceImpl
 * @Description 购物车功能
 * @Author李闯
 * @Date 2018/9/10 18:57
 * @Version 1.0
 **/
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * @Author 李闯
     * @Description 添加商品到购物车
     * @Date 18:58 2018/9/10
     * @Param [userId, productId, count 购物车商品数量]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse<CartVo> add(Integer userId,Integer productId,Integer count){

        if (productId ==null|count ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        //如果根据用户ID和产品ID查到的值是空的
        if (cart == null){
            //这个产品不在购物车里，需要新增一个产品的记录
            Cart cartItem = new Cart();
            cartItem.setQuantity(count);
            cartItem.setChecked(Const.Cart.CHECKED);
            cartItem.setProductId(productId);
            cartItem.setUserId(userId);

            cartMapper.insert(cartItem);
        }else {
            //产品已经在购物车中。数量相加
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        //由于要做对数量库存校验，需要联动起来，所以我们要封装起来一个比较抽象的方法，这个方法就是我们接下来购物车里比较核心的模块
        //经常重用这个方法，里面要有一些限制，同时要对一些数量进行判断，并且假设买家买了十件手机，而我们库存只有10个，买家继续添加的时候 我们不能让数量
        //涨到11，计算价格的总价也是按照十件算的，同时要给前端提示，数量超过了库存限制，并且购物车需要有图片，购物车里面的商品valueObject,并且购物车
        //本身也要封装成一个vo,因为他下面还要挂一些集合，这个集合就是我们要设计的CartProductVo对象
        return list(userId);
    }

    /**
     * @Author 李闯
     * @Description 更新购物车
     * @Date 23:30 2018/9/16
     * @Param [Integer userId,Integer productId,Integer count]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    public ServerResponse<CartVo> update(Integer userId,Integer productId,Integer count){
        if (productId == null||count ==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdProductId(userId,productId);
        if (cart !=null){
            //更新数量
            cart.setQuantity(count);
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return list(userId);
    }

    /**
     * @Author 李闯
     * @Description 删除购物车中的商品
     * @Date 23:30 2018/9/16
     * @Param []
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    public ServerResponse<CartVo> deleteProduct(Integer userId,String productIds){
        //guava 的spilit方法，否则我们需要将id转成数组，再遍历数组添加到集合当中
        List<String> productList = Splitter.on(",").splitToList(productIds);//on后就是用什么分割,splitToList() 转成什么
        if (CollectionUtils.isEmpty(productList)){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdProductIds(userId,productList);
        return list(userId);
    }

    /**
     * @Author 李闯
     * @Description 购物车查询接口
     * @Date 9:58 2018/9/16
     * @Param [userId]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    public ServerResponse<CartVo> list(Integer userId){
        CartVo cartVo = this.getCartVoLimit(userId);//
        return ServerResponse.createBySuccess(cartVo);
    }

    /**
     * @Author 李闯
     * @Description 更新全选所有或者反选所有 状态
     * @Date 10:03 2018/9/16
     * @Param [userId,checked 选择的方式]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.CartVo>
     **/
    public ServerResponse<CartVo> selectOrUnselect(Integer userId,Integer productId,Integer checked){
        cartMapper.checkOrCheckedProduct(userId,productId,checked);
        return this.list(userId);
    }

    /**
     * @Author 李闯
     * @Description 查询购物车的总数量
     * @Date 10:25 2018/9/16
     * @Param [userId]
     * @return com.leeup.common.ServerResponse<java.lang.Integer>
     **/
    public ServerResponse<Integer> getCartProductCount(Integer userId){
        //判断userId
        if (userId == null){
            return ServerResponse.createBySuccess(0);
        }
        //查询购物车中总数量
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    /**
     * @Author 李闯
     * @Description 购物车中购买数量和/库存数量限制。封装购物车产品Vo
     * @Date 23:14 2018/9/16
     * @Param [userId]
     * @return com.leeup.vo.CartVo
     **/
    private CartVo getCartVoLimit(Integer userId){
        CartVo cartVo = new CartVo();
        //获取pojo对象

        //根据Id查一个Cart对象
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        //将cartProductVo放到CartVo中
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        //初始化购物车的总价
        BigDecimal cartTotalPrice = new BigDecimal("0");
        //使用String构造器来初始化它，

        if (CollectionUtils.isNotEmpty(cartList)){
            //如果根据id查到该用户的购物车不为空，就遍历他
            for (Cart cartItem:cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

                //查询购物车中的产品的对象
                Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());
                if (product !=null){
                    //继续组装productVo
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubTitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存，我们初始化一个可以购买的limit限制的数量，初始化为0
                    int buyLimitCount = 0;
                    //当产品库存大于购物车的数量时
                    if (product.getStock()>=cartItem.getQuantity()){
                        //库存充足的时候赋值buyLimitCount
                        buyLimitCount = cartItem.getQuantity();

                        //这里的limit是成功的
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUCCESS);//符合库存需求，限制成功
                    }else {
                        //不符合，我们要把有效库存放到限制购买数种
                        buyLimitCount = product.getStock();//最大值也就是商品的库存
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);//超出库存
                        //购物车中更新有效库存
                        Cart cartForQuantity  = new Cart();
                        //因为我们要更新， 更新我们会选择selective的方式，所以我们只关系，我们去set哪些字段，然后把这个字段进行更新
                        cartForQuantity.setId(cartItem.getId());
                        cartForQuantity.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(cartForQuantity);//通过这个id去找，只会更新set的字段
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价，使用BigDecimal的工具类，这个总价只是说当前产品，这个购物车对应的产品的数量x单价，在下面会算一个购物车总体价格
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));//某个产品的总价

                    //检测进行勾选的状态
                    cartProductVo.setProductChecked(cartItem.getChecked());
                    //是不是被勾选的，如果是被勾选的，我们要增加到总价当中
                }
                if (cartItem.getChecked() == Const.Cart.CHECKED){
                    //如果已经勾选，增加到整个购物车总价中
                    cartTotalPrice =BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));//是不是全选，我们还要写一个save方法来接受这个返回值，因为我们在db中有全选这个字段
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        return cartVo;
    }

    /**
     * @Author 李闯
     * @Description 获取购物车全选/非全选的状态
     * @Date 23:14 2018/9/15
     * @Param [userId]
     * @return boolean
     **/
    private boolean getAllCheckedStatus(Integer userId){
        if (userId==null){
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }

}
