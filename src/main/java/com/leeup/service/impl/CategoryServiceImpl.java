package com.leeup.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.leeup.common.ServerResponse;
import com.leeup.dao.CategoryMapper;
import com.leeup.pojo.Category;
import com.leeup.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * @ClassName CategoryServiceImpl
 * @Description 分类模块Service实现类
 * @Author李闯
 * @Date 2018/8/27 21:00
 * @Version 1.0
 **/
@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    private Logger logger = (Logger) LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryMapper categoryMapper;

    //添加品类的方法
    public ServerResponse addCategory(String categoryName,Integer parentId){
        //校验参数
        if (parentId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);//设置这个分类是可用的

        int rowCount = categoryMapper.insert(category);
        if (rowCount>0){
            return ServerResponse.createBySuccessMessage("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    //更新分类名
    public ServerResponse updateCategoryName(Integer categoryId,String categoryName){
        if (categoryId==null || StringUtils.isBlank(categoryName)){
            //为空时
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        Category category = new Category();
        category.setId(categoryId);//下面使用有选择的更新，其实也要通过主键更新，我们把主键id的值也设置上
        category.setName(categoryName);

        int rowCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (rowCount>0){
            //更新成功
            return ServerResponse.createBySuccessMessage("更新品类名称成功");
        }
        return ServerResponse.createByErrorMessage("更新品类名称失败");
    }

    //跟据我们传的categoryId获取当前categoryId下面子节点的category信息，并且是平级的，不递归

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)){
            //isEmpty 不止判断了它本身是不是空，还判断了他是不是一个空的集合
            //如果集合是空的，我们返回信息，但是不报错
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    //获取当前categoryId，并且递归查询他的字节点的categoryId
    public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
        //拿到Id之后，要查找他的子节点然后判断子节点下是否还有字节点，然后把它们再放在一起，
        Set<Category> categorySet = Sets.newHashSet();
        findChildrenCategory(categorySet,categoryId);

        //categoryId的集合
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId!=null){
            //为空直接遍历这个集合
            for (Category categoryItem:categorySet){
                categoryIdList.add(categoryItem.getId());
            }
        }
        return ServerResponse.createBySuccess(categoryIdList);
    }

    //递归函数
    //使用Set可以直接排重
    //Category不是一个基本的普通对象，如果使用String或者Integer很方便，因为String重写了HashCode/equals方法。而我们使用Category对象，
    //我们也要重写他的hashCode方法
    //在处理Set对于对象的时候我们hashCode和equals方法是要都写的，因为
    //1.equals和hashCode关系是这样的，如果两个对象相同，即 用equals比较返回true，那么他们的hashCode值一定要相同，如果两个对象的hashCode相同
    //他们并不一定相同，也就是说hashCode即使一样的话，我们即使用equals比较的话，也有可能返回false，因为我们hashcode只取了id的hashCode,作为一个
    //因子，但是equals完全可以放进去其他的属性，综合的来看，那么hashcode只取了id的hash，equals又取了多个元素。去进行比较，
    //如果工作中碰到set集合去处理的话，最好hash和equals都重写，并且让里面的判断因子是一样的。避免意想不到的问题

    //这个set<>参数，把这个参数当作返回值返回给这个方法本身，再拿这个方法本身的返回值再调用这个方法，当成这个方法的参数
    private Set<Category> findChildrenCategory(Set<Category> categorySet,Integer categoryId){
        //递归算法，自己调用自己，算出子节点
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null) {
            categorySet.add(category);
        }
        //查找子节点，递归算法一定要有一个退出的条件，我们的退出条件就是查找他的子节点，看看是不是空的，遍历的时候，如果子节点是空的话
        //就退出递归算法
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        //上面的Mybatis返回的集合，mybatis返回集合的处理是，如果没有查到的话，mybatis不会返回一个null对象，也就是说这个list不会为空
        //不用对他进行判断，如果我们调用一些不可预知的方法，我们要进行空判断，否则会报空指针异常
        for (Category categoryItem : categoryList){
            findChildrenCategory(categorySet,categoryItem.getId());//遍历孩子节点的Id
        }
        //list如果是空就会直接返回不执行循环
        return categorySet;
    }
}
