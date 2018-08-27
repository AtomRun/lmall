package com.leeup.service;

import com.leeup.common.ServerResponse;
import com.leeup.pojo.Category;

import java.util.List;

/**
 * @ClassName ICategoryService
 * @Description 分类模块Service接口
 * @Author李闯
 * @Date 2018/8/27 21:05
 * @Version 1.0
 **/
public interface ICategoryService {

    //增减品类
    ServerResponse addCategory(String categoryName, Integer parentId);

    //更新品类名称
    ServerResponse updateCategoryName(Integer categoryId,String categoryName);

    //根据categoryId获取子节点信息
    ServerResponse<List<Category>> getChildrenParallelCategory(Integer parentId);

    //获取当前categoryId，并且递归查询他的字节点的categoryId
    ServerResponse selectCategoryAndChildrenById(Integer categoryId);
}
