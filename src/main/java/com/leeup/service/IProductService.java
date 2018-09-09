package com.leeup.service;

import com.github.pagehelper.PageInfo;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.Product;
import com.leeup.vo.ProductDetailVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName ProductService
 * @Description 产品相关模块的接口
 * @Author李闯
 * @Date 2018/8/29 19:33
 * @Version 1.0
 **/
public interface IProductService {

    /**
     * @Author 李闯
     * @Description 增加或者修改产品的接口
     * @Date 19:55 2018/8/29
     * @Param [product]
     * @return com.leeup.common.ServerResponse
     **/
    ServerResponse saveOrUpdateProduct(Product product);

    /**
     * @Author 李闯
     * @Description 修改产品销售状态的接口
     * @Date 20:06 2018/8/29
     * @Param [productId, status]
     * @return com.leeup.common.ServerResponse<java.lang.String>
     **/
    ServerResponse<String> setSaleStauts(Integer productId,Integer status);

    /**
     * @Author 李闯
     * @Description 查看产品详情的接口，包含了转换日期，并且封装了productDetailVo,配置了ImageHost
     * @Date 23:00 2018/8/30
     * @Param [productId]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.ProductDetailVo>
     **/
    ServerResponse<ProductDetailVo> manageProductDetail(Integer productId);

    /**
     * @Author 李闯
     * @Description 分页查看产品列表
     * @Date 18:20 2018/8/31
     * @Param [pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    ServerResponse<PageInfo> getProductList(int pageNum, int pageSize);

    /**
     * @Author 李闯
     * @Description 分页查找产品
     * @Date 18:45 2018/8/31
     * @Param [productName, productId, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize);

    /**
     * @Author 李闯
     * @Description 查询商品详情
     * @Date 22:02 2018/9/7
     * @Param [productId]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.ProductDetailVo>
     **/
    ServerResponse<ProductDetailVo> getProductDetail(Integer productId);

    /**
     * @Author 李闯
     * @Description 根据分类ID和关键字进行升序/降序搜索
     * @Date 23:20 2018/9/8
     * @Param [keyword, categoryId, pageNum, pageSize, orderby]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderby);

}
