package com.leeup.service.impl;

import com.alipay.api.AlipayResponse;
import com.alipay.api.domain.Car;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.config.Configs;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.GoodsDetail;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.ZxingUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leeup.common.Const;
import com.leeup.common.ServerResponse;
import com.leeup.dao.*;
import com.leeup.pojo.*;
import com.leeup.service.IOrderService;
import com.leeup.util.BigDecimalUtil;
import com.leeup.util.DateTimeUtil;
import com.leeup.util.FTPUtil;
import com.leeup.util.PropertiesUtil;
import com.leeup.vo.OrderItemVo;
import com.leeup.vo.OrderProductVo;
import com.leeup.vo.OrderVo;
import com.leeup.vo.ShippingVo;
import com.sun.org.apache.regexp.internal.RE;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.aspectj.weaver.ast.Or;
import org.joda.time.DateTime;
import org.omg.CORBA.ORB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName OrderServiceImpl
 * @Description TODO
 * @Author李闯
 * @Date 2018/9/20 22:24
 * @Version 1.0
 **/
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    //backend

    /**
     * @Author 李闯
     * @Description 后台对订单进行发货
     * @Date 21:23 2018/10/5
     * @Param [orderNo]
     * @return com.leeup.common.ServerResponse<java.lang.String>
     **/
    public ServerResponse<String> sendGoods(Long orderNo){
        //查询订单
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            if (order.getStatus() == Const.OrderStatusEnum.PAID.getCode()){
                //已付款
                order.setStatus(Const.OrderStatusEnum.SHIPPED.getCode());//更新成已经发货
                order.setSendTime(new Date());

                orderMapper.updateByPrimaryKeySelective(order);

                return ServerResponse.createBySuccess("发货成功");
            }
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    /**
     * @Author 李闯
     * @Description 后台根据订单编号搜索订单
     * @Date 21:07 2018/10/5
     * @Param [orderNo]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.OrderVo>
     **/
    public ServerResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order,orderItemList);

            PageInfo pageResult = new PageInfo(Lists.newArrayList(order));

            pageResult.setList(Lists.newArrayList(orderVo));

            return ServerResponse.createBySuccess(pageResult);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    /**
     * @Author 李闯
     * @Description 后台查看订单详情
     * @Date 21:07 2018/10/5
     * @Param [orderNo]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.OrderVo>
     **/
    public ServerResponse<OrderVo> manageDetail(Long orderNo){
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order!=null){
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = assembleOrderVo(order,orderItemList);

            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("订单不存在");
    }

    /**
     * @Author 李闯
     * @Description 后台管理员订单列表展示
     * @Date 20:50 2018/10/5
     * @Param [pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    public ServerResponse<PageInfo> manageList(int pageNum,int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList,null);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }

    /**
     * @Author 李闯
     * @Description 个人中心-查看我的订单list列表接口
     * @Date 17:05 2018/10/5
     * @Param [userId, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    public ServerResponse<PageInfo> getOrderList(Integer userId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);

        //通过userId，获取用户订单
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = assembleOrderVoList(orderList,userId);

        //分页
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);

        return ServerResponse.createBySuccess(pageResult);

    }


    /**
     * @Author 李闯
     * @Description 转换orderList为orderVo
     * @Date 17:12 2018/10/5
     * @Param [orderList]
     * @return java.util.List<com.leeup.vo.OrderVo>
     **/
    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = Lists.newArrayList();
        for (Order order : orderList){
            List<OrderItem> orderItemList = Lists.newArrayList();
            //管理员/用户，执行不同的操作
            if (userId == null){
                //管理员查询不需要传userId
                orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            }else {
                //将orderItem这个订单明细也添加到orderVo中
                orderItemList = orderItemMapper.getByOrderNoUserId(order.getOrderNo(),userId);
            }
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    /**
     * @Author 李闯
     * @Description 获取订单详情
     * @Date 17:02 2018/10/5
     * @Param [userId, orderNo]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.OrderVo>
     **/
    public ServerResponse<OrderVo> getOrderDetail(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order!=null){
            //获取该订单的orderItem集合，订单详情集合
            List<OrderItem> orderItemList  = orderItemMapper.getByOrderNoUserId(orderNo,userId);
            //组装orderVO
            OrderVo orderVo = assembleOrderVo(order,orderItemList);

            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("没有找到该订单");
    }

    /**
     * @Author 李闯
     * @Description 获取购物车中的选中的产品详情
     * @Date 0:45 2018/10/5
     * @Param [userId]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse getOrderCartProduct(Integer userId){
        OrderProductVo orderProductVo = new OrderProductVo();
        //从购物车中获取数据
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }

        //计算总价
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();

        //组装OrderItemVo
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        //计算目前购物车中选中的总价

        //这个接口就是给我们订单填写页，下面的商品展示，里面也有计算总价

        //初始化总价
        BigDecimal payment = new BigDecimal("0");
        //遍历orderItem
        for (OrderItem orderItem:orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
            orderItemVoList.add(assembleOrderItemVo(orderItem));//组装orderItemVo
        }
        orderProductVo.setProductTotalPrice(payment);
        orderProductVo.setOrderItemVoList(orderItemVoList);
        orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return ServerResponse.createBySuccess(orderProductVo);
    }

    /**
     * @Author 李闯
     * @Description 取消订单
     * @Date 0:12 2018/10/5
     * @Param [userId, orderNo]
     * @return com.leeup.common.ServerResponse<java.lang.String>
     **/
    public ServerResponse<String> cancel(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("该用户此订单不存在");
        }
        if (order.getStatus()!= Const.OrderStatusEnum.NO_PAY.getCode()){
            return ServerResponse.createByErrorMessage("已付款，无法取消订单");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());

        //更新
        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if (row>0){
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }

    /**
     * @Author 李闯
     * @Description 创建订单
     * @Date 0:09 2018/10/5
     * @Param [userId, shippingId]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse createOrder(Integer userId,Integer shippingId){
        //从购物车获取数据
        List<Cart> cartList = cartMapper.selectCheckedCartByUserId(userId);

        //计算订单总价
        ServerResponse serverResponse = this.getCartOrderItem(userId,cartList);
        if (!serverResponse.isSuccess()){
            return serverResponse;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();//只有成功的时候才会走到这一步

        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        //生成订单
        Order order = assembleOrder(userId,shippingId,payment);

        if (order==null){
            return ServerResponse.createByErrorMessage("生成订单错误！");
        }
        if (CollectionUtils.isEmpty(orderItemList)){
            return ServerResponse.createByErrorMessage("购物车为空！");
        }
        for (OrderItem orderItem: orderItemList){
            orderItem.setOrderNo(order.getOrderNo());
        }
        //mybatis批量插入
        orderItemMapper.batchInsert(orderItemList);

        //生成订单成功，我们要减少产品的库存，
        this.reduceProductStock(orderItemList);

        //清空购物车
        this.cleanCart(cartList);

        //返回订单明细给前端 要重新创建一个orderVo，再使用assemble方法来组装vo

        OrderVo orderVo = assembleOrderVo(order,orderItemList);

        return ServerResponse.createBySuccess(orderVo);

    }

    /**
     * @Author 李闯
     * @Description 组装OrderVo包括订单详情和收货地址
     * @Date 0:09 2018/10/5
     * @Param [order, orderItemList]
     * @return com.leeup.vo.OrderVo
     **/
    private OrderVo assembleOrderVo(Order order,List<OrderItem> orderItemList){
        OrderVo orderVo = new OrderVo();

        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setPaymentType(order.getPaymentType());
        orderVo.setPaymentTypeDesc(Const.PaymentTypeEnum.codeOf(order.getPaymentType()).getValue());

        orderVo.setPostage(order.getPostage());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setShippingId(order.getShippingId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (shipping!=null){
            //将收货地址的姓名赋值上
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(assembleShippingVo(shipping));
        }
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setSendTime(DateTimeUtil.dateToStr(order.getSendTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));

        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();

        //遍历orderItem
        for (OrderItem orderItem : orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }

        orderVo.setOrderItemVoList(orderItemVoList);

        return orderVo;
    }

    /**
     * @Author 李闯
     * @Description 组装OrderItemVo
     * @Date 23:42 2018/10/4
     * @Param [orderItem]
     * @return com.leeup.vo.OrderItemVo
     **/
    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));

        return orderItemVo;
    }

    /**
     * @Author 李闯
     * @Description 组装ShippingVo
     * @Date 17:02 2018/10/4
     * @Param [shipping]
     * @return com.leeup.vo.ShippingVo
     **/
    private ShippingVo assembleShippingVo(Shipping shipping){
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverProvince(shipping.getReceiverPhone());
        shippingVo.setReceiverZip(shipping.getReceiverZip());

        return shippingVo;
    }

    /**
     * @Author 李闯
     * @Description 清空购物车
     * @Date 16:48 2018/10/4
     * @Param [cartList]
     * @return void
     **/
    private void cleanCart(List<Cart> cartList){
        for (Cart cart :cartList){
            cartMapper.deleteByPrimaryKey(cart.getId());

        }
    }

    /**
     * @Author 李闯
     * @Description 减少缓存
     * @Date 16:14 2018/10/4
     * @Param
     * @return
     **/
    private void reduceProductStock(List<OrderItem> orderItemList){
        for (OrderItem orderItem:orderItemList){
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock()-orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }


    /**
     * @Author 李闯
     * @Description 组装Order
     * @Date 13:45 2018/10/4
     * @Param [userId, ShippingId, payment]
     * @return com.leeup.pojo.Order
     **/
    private Order assembleOrder(Integer userId,Integer shippingId,BigDecimal payment){
        Order order = new Order();
        long orderNo = this.generateOrderNo();

        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setPostage(0);
        order.setPaymentType(Const.PaymentTypeEnum.NOLINE_PAY.getCode());
        order.setPayment(payment);
        order.setUserId(userId);
        order.setShippingId(shippingId);

        //发货时间等等....
        //付款时间

        int rowCount = orderMapper.insert(order);
        if (rowCount>0){
            return order;
        }
        return null;//
    }


    /**
     * @Author 李闯
     * @Description 生成订单号
     * @Date 14:02 2018/10/4
     * @Param []
     * @return long
     **/
    private long generateOrderNo(){
        long currentTime = System.currentTimeMillis();
        return  currentTime+new Random().nextInt(100);//随机生成0-99的数
    }

    /**
     * @Author 李闯
     * @Description 计算订单子明细中各个商品的总价
     * @Date 13:42 2018/10/4
     * @Param [orderItemList]
     * @return java.math.BigDecimal
     **/
    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){
        BigDecimal payment = new BigDecimal("0");

        for (OrderItem orderItem: orderItemList){
            payment = BigDecimalUtil.add(payment.doubleValue(),orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }

    /**
     * @Author 李闯
     * @Description 组装orderItem
     * @Date 13:46 2018/10/4
     * @Param [userId, cartList]
     * @return com.leeup.common.ServerResponse
     **/
    private ServerResponse getCartOrderItem(Integer userId,List<Cart> cartList){
        List<OrderItem> orderItemList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(cartList)){
            return ServerResponse.createByErrorMessage("购物车为空");
        }
        //校验购物车的数据包括产品的状态和数量
        for (Cart cartItem:cartList){
            OrderItem orderItem = new OrderItem();
            //从购物车中获取产品
            Product product = productMapper.selectByPrimaryKey(cartItem.getProductId());//获取到这个产品
            if (Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()){
                //如果该产品不是在售状态
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"不是在线售卖状态");
            }
            //校验库存
            if (cartItem.getQuantity()> product.getStock()){
                //如果购物车中的库存大于产品的库存
                return ServerResponse.createByErrorMessage("产品"+product.getName()+"库存不足");
            }
            //组装orderItem
            orderItem.setUserId(userId);
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            //购物车中orderItem要进入当时购买的快照，也就是说如果当前商品购买时价格为100，但是过了两天价格为120，但是当时购买的花了一百元，要记录下来
            orderItem.setCurrentUnitPrice(product.getPrice());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartItem.getQuantity().doubleValue()));
            //将orderItem添加到集合中
            orderItemList.add(orderItem);
        }
        return ServerResponse.createBySuccess(orderItemList);
    }

    /**
     * @Author 李闯
     * @Description 支付接口
     * @Date 22:25 2018/9/20
     * @Param [orderNo, userId 需要知道是哪个客户的, path 生成二维码，传到哪里的路径]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse pay(Long orderNo,Integer userId,String path){
        //与前端的约定，我们将订单号返回给他，同时将二维码URL返回给他，我们就不重新创建这个对象了
        //直接使用Map来承载这个对象
        Map<String,String> resultMap = Maps.newHashMap();
        //根据userId和orderNo查出来的order到底存不存在
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);
        if (order==null){
            //该用户没有该订单
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        resultMap.put("orderNo",String.valueOf(order.getOrderNo()));


        // 测试当面付2.0生成支付二维码

        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
        String outTradeNo = order.getOrderNo().toString();

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
        String subject = new StringBuilder().append("lmall扫码支付。订单号:").append(outTradeNo).toString();

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
        String totalAmount = order.getPayment().toString();

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
        String body = new StringBuilder().append("订单").append(outTradeNo).append("购买商品共").append(totalAmount).append("元").toString();

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();

        //一个集合用来放商品，将商品添加到集合中，我们使用foreach循环搞定，在集合new完之后，我们获取这个订单下的item ,也就是订单下的明细，我们看看有多少item
        //根据orderNo和userId拿到订单的item集合，然后遍历这个集合，把goodsDetail填充上，然后添加到支付宝的集合中
        List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo,userId);
        for (OrderItem orderItem : orderItemList){
            //构建goods
            GoodsDetail goods = GoodsDetail.newInstance(orderItem.getProductId().toString(),orderItem.getProductName(),
                    BigDecimalUtil.mul(orderItem.getCurrentUnitPrice().doubleValue(),new Double(100).doubleValue()).longValue(),orderItem.getQuantity());
            goodsDetailList.add(goods);
        }

/*        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
        // 创建好一个商品后添加至商品明细列表
        goodsDetailList.add(goods1);

        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
        goodsDetailList.add(goods2);*/


        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalAmount).setOutTradeNo(outTradeNo)
                .setUndiscountableAmount(undiscountableAmount).setSellerId(sellerId).setBody(body)
                .setOperatorId(operatorId).setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress)
                .setNotifyUrl(PropertiesUtil.getProperty("alipay.callback.url"))//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置
                .setGoodsDetailList(goodsDetailList);

        /** 一定要在创建AlipayTradeService之前调用Configs.init()设置默认参数
         *  Configs会读取classpath下的zfbinfo.properties文件配置信息，如果找不到该文件则确认该文件是否在classpath目录
         */
        Configs.init("zfbinfo.properties");

        /** 使用Configs提供的默认参数
         *  AlipayTradeService可以使用单例或者为静态成员对象，不需要反复new
         */
        AlipayTradeService tradeService = new AlipayTradeServiceImpl.ClientBuilder().build();

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);
        switch (result.getTradeStatus()) {
            case SUCCESS:
                logger.info("支付宝预下单成功: )");

                AlipayTradePrecreateResponse response = result.getResponse();
                dumpResponse(response);

                File folder = new File(path);//判断存不存在，去给予写权限，将目录创建出来
                if (!folder.exists()){
                    folder.setWritable(true);
                    folder.mkdirs();//创建目录，保证我们path下的upload文件夹是有的，生成二维码到这里
                }

                // 需要修改为运行机器上的路径
                //细节：path我们获取的路径最后面是没有/的，
                String qrPath = String.format(path+"/qr-%s.png", response.getOutTradeNo());

                //生成文件
                String qrFileName = String.format("qr-%s.png",response.getOutTradeNo());
                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, qrPath);//将二维码生成在哪里就存哪里

                File targetFile = new File(path,qrFileName);//目标文件路径，目标文件的文件名叫qrFileName
                try {
                    FTPUtil.uploadFile(Lists.newArrayList(targetFile));
                } catch (IOException e) {
                    logger.error("上传二维码异常",e);
                    e.printStackTrace();
                }

                logger.info("filePath:" + qrPath);

                String qrUrl = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFile.getName();
                resultMap.put("qrUrl",qrUrl);
                return ServerResponse.createBySuccess(resultMap);
            case FAILED:
                logger.error("支付宝预下单失败!!!");
                return ServerResponse.createByErrorMessage("支付宝预下单失败!!!");

            case UNKNOWN:
                logger.error("系统异常，预下单状态未知!!!");
                return ServerResponse.createByErrorMessage("系统异常，预下单状态未知!!!");

            default:
                logger.error("不支持的交易状态，交易返回异常!!!");
                return ServerResponse.createByErrorMessage("不支持的交易状态，交易返回异常!!!");
        }

    }

    // 简单打印应答
    private void dumpResponse(AlipayResponse response) {
        if (response != null) {
            logger.info(String.format("code:%s, msg:%s", response.getCode(), response.getMsg()));
            if (StringUtils.isNotEmpty(response.getSubCode())) {
                logger.info(String.format("subCode:%s, subMsg:%s", response.getSubCode(),
                        response.getSubMsg()));
            }
            logger.info("body:" + response.getBody());
        }
    }

    public ServerResponse aliCallBack(Map<String,String> params){
        //拿出订单号
        Long orderNo = Long.parseLong(params.get("out_trade_no"));
        String tradeNo = params.get("trade_no");
        String tradeStatus = params.get("trade_status");

        //根据订单号查询是否有该订单
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order==null){
            return ServerResponse.createByErrorMessage("非本商城的订单,回调忽略");
        }
        //判断订单状态
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            return ServerResponse.createBySuccess("支付宝重复调用");
        }
        //判断回调状态
        if (Const.AlipayCallBack.TRADE_STATUS_TRADE_SUCCESS.equals(tradeStatus)){

            order.setPaymentTime(DateTimeUtil.strToDate(params.get("gmt_payment")));

            //相同则是交易成功，就把订单状态置成已付款
            order.setStatus(Const.OrderStatusEnum.PAID.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
        }

        PayInfo payInfo = new PayInfo();
        payInfo.setId(order.getUserId());
        payInfo.setOrderNo(order.getOrderNo());
        payInfo.setPayPlatform(Const.PayPlatformEnum.ALIPAY.getCode());
        payInfo.setPlatformNumber(tradeNo);
        payInfo.setPlatformStatus(tradeStatus);

        payInfoMapper.insert(payInfo);

        return ServerResponse.createBySuccess();
    }

    /**
     * @Author 李闯
     * @Description 查询订单是否存在
     * @Date 17:51 2018/10/2
     * @Param [userId, orderNo]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse queryOrderPayStatus(Integer userId,Long orderNo){
        Order order = orderMapper.selectByUserIdAndOrderNo(userId,orderNo);

        if (order == null){
            return ServerResponse.createByErrorMessage("用户没有该订单");
        }
        if (order.getStatus() >= Const.OrderStatusEnum.PAID.getCode()){
            //当订单状态>=付款时，我都认为我们的订单是付款成功的订单
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }
}
