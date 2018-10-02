package com.leeup.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.leeup.common.Const;
import com.leeup.common.ResponseCode;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.User;
import com.leeup.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Iterator;
import java.util.Map;

/**
 * @ClassName OrderController
 * @Description TODO
 * @Author李闯
 * @Date 2018/9/20 22:16
 * @Version 1.0
 **/
@Controller
@RequestMapping("/order/")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private IOrderService iOrderService;

    /**
     * @Author 李闯
     * @Description 支付的接口
     * @Date 22:18 2018/9/20
     * @Param [session 判断用户是否登陆, orderNo 订单编号, request 获取servlet上下文，拿到upload文件夹，然后把自动生成的二维码传到ftp服务器上，然后我们会返回给前端，二维码的图片地址
     * 前端把图片地址进行展示，进行扫码支付]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("pay.do")
    @ResponseBody
    public ServerResponse pay(HttpSession session, Long orderNo, HttpServletRequest request){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        //拿到path
        String path = request.getSession().getServletContext().getRealPath("upload");

        return iOrderService.pay(orderNo,user.getId(), path);
    }

    /**
     * @Author 李闯
     * @Description 使用Object返回是因为要遵循alipay要求的返回进行返回，可能返回的不只是字符串还有可能是别的，暂时用Object
     * @Date 21:07 2018/9/22
     * @Param [request]
     * @return java.lang.Object
     **/
    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallBack(HttpServletRequest request){

        Map<String,String> params = Maps.newHashMap();

        Map requestParams = request.getParameterMap(); //getParameterMap key是string,value是string数组
        //因为支付宝的回调不会把参数放到后面拼接，都是放到request中
        //遍历Map,动态的查一下key和value是什么，并且key和value放到一个数组中，我们也要把数组中的值取出来
        //使用迭代器
        for(Iterator iterator = requestParams.keySet().iterator();iterator.hasNext();){
            //取出name
            String name =(String) iterator.next();
            //拿出value
            String[] values = (String[]) requestParams.get(name);

            //声明valueStr
            String valueStr = "";

            //使用for
            for (int i= 0; i<values.length;i++){
                //我们要在一个Map中遍历这个数组，然后把值取出来

                //对length进行判断，还有i的关系

                //例如现在i是初始化0，如果0等于length-1，就说明该数组中只有一个元素
                valueStr = (i == values.length - 1)?valueStr + values[i]:valueStr + values[i]+",";
            }
            //将key和value都放到map中
            params.put(name,valueStr);
        }
        //从request中拿到了这个参数

        logger.info("支付宝回调，sign:{},trade_status:{},参数:{}",params.get("sign"),params.get("trade_status"),
                params.toString());

        //拿到参数后，验证回调正确性，是不是支付宝发的，还要避免重复通知
        params.remove("sign_type");
        try {
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            //对返回值进行判断
            if (!alipayRSACheckedV2){
                return ServerResponse.createByErrorMessage("非法请求，验证不通过，再恶意请求我就报警了");
            }
        } catch (AlipayApiException e) {
            logger.info("支付宝验证回调异常",e);
        }

        //todo 验证各种数据
        ServerResponse serverResponse = iOrderService.aliCallBack(params);

        //判断返回值
        if (serverResponse.isSuccess()){
            return Const.AlipayCallBack.RESPONSE_SUCCESS;
        }
        return Const.AlipayCallBack.RESPONSE_FAILED;
    }

    /**
     * @Author 李闯
     * @Description 前台轮询查询这个订单的支付状态，当我们付款完成后，在二维码付款的页面我们扫码支付付款成功之后，
     * 前台会调用我们这个接口，查看是否付款成功，如果付款成功
     * 会给予一个指引，跳转到订单
     *
     * 在service中查询订单状态，如果是已经支付，返回true，所以泛型就是boolean
     * @Date 17:47 2018/10/2
     * @Param [session, orderNo]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("queryOrderPayStatus.do")
    @ResponseBody
    public ServerResponse<Boolean> queryOrderPayStatus(HttpSession session,Long orderNo){
        User user = (User)session.getAttribute(Const.CURRENT_USER);
        if(user == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }

        ServerResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if (serverResponse.isSuccess()){
            return ServerResponse.createBySuccess(true);
        }
        return ServerResponse.createBySuccess(false);
    }
}
