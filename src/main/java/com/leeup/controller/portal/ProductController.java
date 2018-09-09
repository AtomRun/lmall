package com.leeup.controller.portal;

import com.github.pagehelper.PageInfo;
import com.leeup.common.ServerResponse;
import com.leeup.service.IProductService;
import com.leeup.vo.ProductDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName ProductController
 * @Description 前台的产品Controllers
 * @Author李闯
 * @Date 2018/9/7 12:47
 * @Version 1.0
 **/
@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    /**
     * @Author 李闯
     * @Description 前台的商品详情接口
     * @Date 21:38 2018/9/7
     * @Param [productId 根据这个ID获取商品详情]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.ProductDetailVo>
     **/
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId){
        //之前写了后台的产品详情，我们写前台的产品详情和后台差不多，唯一的不同就是，前台查看产品详情的时候，我们要判断或者产品是不是在线状态
        //如果不在线我们就不返回，或者返回一个错误,放到service做判断
        return iProductService.getProductDetail(productId);
    }

    /**
     * @Author 李闯
     * @Description 用户搜索的请求。我们会返回list，list要进行分页，所以我们放进去PageInfo
     * @Date 23:31 2018/9/7
     * @Param [keyword 关键字，不是必须的，所以我们使用注解 required = false,该注解默认为true注定要传, categoryId 分类Id,非必传, pageNum 默认起始页, pageSize 每页展示数量 ]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> lsit(@RequestParam(value = "keyword",required = false)String keyword,@RequestParam(value = "categoryId",required = false)Integer categoryId,
                    @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize",defaultValue = "10")int pageSize,@RequestParam(value = "orderby",defaultValue = "")String orderby){
        return iProductService.getProductByKeywordCategory(keyword,categoryId,pageNum,pageSize,orderby);
    }
}
