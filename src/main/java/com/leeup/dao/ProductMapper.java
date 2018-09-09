package com.leeup.dao;

import com.leeup.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    /**
     * @Author 李闯
     * @Description list分页查询商品的方法
     * @Date 12:59 2018/8/31
     * @Param []
     * @return java.util.List<com.leeup.pojo.Product>
     **/
    List<Product> selectList();

    /**
     * @Author 李闯
     * @Description 根据Id或者Name查询产品
     * @Date 18:31 2018/8/31
     * @Param [productName, productId]
     * @return java.util.List<com.leeup.pojo.Product>
     **/
    List<Product> selectByNameAndProductId(@Param("productName")String productName,@Param("productId") Integer productId);
    /**
     * @Author 李闯
     * @Description 根据关键字和分类ID一个in的sql语句
     * @Date 22:17 2018/9/8
     * @Param [productName, categoryIdList]
     * @return java.util.List<com.leeup.pojo.Product>
     **/
    List<Product> selectByNameAndCategoryIds(@Param("productName")String productName,@Param("categoryIdList") List<Integer> categoryIdList);
}