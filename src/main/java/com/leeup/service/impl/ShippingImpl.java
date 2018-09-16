package com.leeup.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.leeup.common.ServerResponse;
import com.leeup.dao.ShippingMapper;
import com.leeup.pojo.Shipping;
import com.leeup.service.IShippingService;
import net.sf.jsqlparser.schema.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ShippingImpl
 * @Description TODO
 * @Author李闯
 * @Date 2018/9/16 11:51
 * @Version 1.0
 **/
@Service("iShippingService")
public class ShippingImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;


    /**
     * @Author 李闯
     * @Description 增加地址的方法
     * @Date 13:56 2018/9/16
     * @Param [userId, shipping]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);//前端没有传，所以我们需要set一下
        int rowCount = shippingMapper.insert(shipping);
        //插入完成之后将地址ID返回给前端，前端再查看详情时再将ID传给后端，我们要在insert之后，立刻拿到他的ID,默认的sql返回值是一个int,生效行数
        //我们需要在 xml中更改配置,更改之后，这个ID就会填充到shipping的getID上

        if (rowCount>0){
            //前端要有这样数据，增加完之后将shippingId返回过去，并且shippingId是一个key，我们就不需要单独创建对象来承载数据，我们直接使用
            //Map来承载即可
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());

            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("新建地址失败");

    }

    /**
     * @Author 李闯
     * @Description 删除地址接口
     * @Date 14:30 2018/9/16
     * @Param [userId, shippingId]
     * @return com.leeup.common.ServerResponse<java.lang.String>
     **/
    public ServerResponse<String> del(Integer userId,Integer shippingId){
        int resultCount = shippingMapper.deleteByShippingIdUserId(userId,shippingId);
        //这里有安全漏洞/横向越权 ，因为我们在Controller中判断了登录，大家都是登录用户，也没有管理员，都是普通用户
        //但是，我登陆了之后，找到这个接口地址，传一个不是我的地址的shippingId,那么我们这种写法就会把该ID删除掉，因为他没有和登陆的用户做关联
        //所以我们需要自己写一个SQL
        if (resultCount>0){
            //删除成功
            return ServerResponse.createBySuccessMessage("删除地址成功");
        }
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    /**
     * @Author 李闯
     * @Description 更新地址
     * @Date 14:54 2018/9/16
     * @Param [userId, shipping]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse update(Integer userId,Shipping shipping){
        //更新的时候也有越权问题，我们在更新的时候也需要指定userId,所以我们将shipping中的userId再赋值一下
        //因为shipping中的userId也是可以模拟的，如果我们不用登录用户中的userId,直接用它传过来的userId的话，那么这个问题还是存在的
        //只不过我们在SQL中虽然判断了userId，但是我们接受到了假的userId，会把假的更新掉，所以我们从登陆用户中获取userId再赋值上

        shipping.setUserId(userId);//前端没有传，所以我们需要set一下
        int rowCount = shippingMapper.updateByShipping(shipping);

        if (rowCount>0){
            //更新不需要把Id返回给前端，因为前端已经获取到了
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    /**
     * @Author 李闯
     * @Description 查询收货地址
     * @Date 14:56 2018/9/16
     * @Param [userId, shipping]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse<Shipping> select(Integer userId,Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if (shipping == null){
            return ServerResponse.createByErrorMessage("无法查询到该地址");
        }
        return ServerResponse.createBySuccess("查询地址成功",shipping);
    }

    /**
     * @Author 李闯
     * @Description 列表查看收货地址/分页
     * @Date 15:27 2018/9/16
     * @Param [userId, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    public ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        //根据用户查询它下面的所有的地址
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);

        //构建PageInfo
        PageInfo pageInfo = new PageInfo(shippingList);

        return ServerResponse.createBySuccess(pageInfo);
    }
}