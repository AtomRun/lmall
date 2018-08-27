package com.leeup.dao;

import com.leeup.pojo.Category;

import java.util.List;

public interface CategoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);

    //根据categoryId获取子节点信息
    List<Category> selectCategoryChildrenByParentId(Integer parentId);
}