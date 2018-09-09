package com.leeup.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.leeup.common.Const;
import com.leeup.common.ResponseCode;
import com.leeup.common.ServerResponse;
import com.leeup.dao.CategoryMapper;
import com.leeup.dao.ProductMapper;
import com.leeup.pojo.Category;
import com.leeup.pojo.Product;
import com.leeup.service.ICategoryService;
import com.leeup.service.IProductService;
import com.leeup.util.DateTimeUtil;
import com.leeup.util.PropertiesUtil;
import com.leeup.vo.ProductDetailVo;
import com.leeup.vo.ProductListVo;
import com.sun.xml.internal.bind.v2.model.core.ID;
import net.sf.jsqlparser.schema.Server;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ProductServiceImpl
 * @Description TODO
 * @Author李闯
 * @Date 2018/8/29 19:34
 * @Version 1.0
 **/
@Service("iProductServiceImpl")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    //我们之前的递归算法，需要将该service注入进来，这个属于平级调用，service调用service
    @Autowired
    private ICategoryService iCategoryService;


    /**
     * @Author 李闯
     * @Description 保存或者更新产品 需要判断
     * @Date 19:36 2018/8/29
     * @Param [product]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse saveOrUpdateProduct(Product product){
        if (product !=null){
            if (StringUtils.isNotBlank(product.getSubImages())){
                //如果子图不是空的话，取第一个子图，赋值给我们的主图

                // 进行分割
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length>0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            //如果id不为空肯定是有Id的因为，更新肯定要知道Id,所以id不为空就是更新操作
            if (product.getId()!=null){
                int rowCount = productMapper.updateByPrimaryKey(product);//代表更新产品的数量
                if (rowCount>0){
                    return ServerResponse.createBySuccess("更新产品成功");
                }
                    return ServerResponse.createBySuccess("更新产品失败");
            }else {
                //反之就是增加操作
                int rowCount = productMapper.insert(product);
                if (rowCount>0){
                    return ServerResponse.createBySuccess("新增产品成功");
                }
                return ServerResponse.createBySuccess("新增产品失败");

            }
        }
        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }
    
    /**
     * @Author 李闯
     * @Description 修改产品的销售状态
     * @Date 20:00 2018/8/29
     * @Param [id, status]
     * @return com.leeup.common.ServerResponse<java.lang.String>
     **/
    public ServerResponse<String> setSaleStauts(Integer productId,Integer status){
        if (productId == null || status == null){
            //如果两个值其中一个为空，我们向其返回值
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        //为了使用updateByPrimaryKeySelective方法，我们需要new一个新的product来放置值，只修改这两个值
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount>0){
            return ServerResponse.createBySuccess("修改产品销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改产品销售状态失败");
    }

    /**
     * @Author 李闯
     * @Description 后台service实现都以manage开头，以后进行前端和后端拆分，维护更加方便
     * @Date 20:52 2018/8/30
     * @Param [productId]
     * @return com.leeup.common.ServerResponse<java.lang.Object>
     **/
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        //不为空返回一个vo对象，
        // vo对象-> value Object 承载对象值得对象
        //复杂方式 pojo -> bo  bussiness Object-> vo view Object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * @Author 李闯
     * @Description 通过ProductVo将Product组装上
     * @Date 21:02 2018/8/30
     * @Param [product]
     * @return
     **/
    private ProductDetailVo assembleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        //设置他的值
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());


        //imageHost  需要从配置文件中获取。这也是为了使配置和代码分离，这样我们就不用将URL硬编码到项目当中。如果图片服务器修改了的话。
        // 我们只需要更改图片的配置。以后可能会将配置优化成热部署的配置 ，做成一个配置中心服务
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        //parentCategoryId
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category==null){
            productDetailVo.setCategoryId(0);//如果传过来的id为空，我们就认为他默认是根节点
        }productDetailVo.setCategoryId(product.getCategoryId());
        //createTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        //updateTime
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

        return productDetailVo;
    }

    /**
     * @Author 李闯
     * @Description 查看产品list列表
     * @Date 12:49 2018/8/31
     * @Param [pageNum, pageSize]
     * @return com.leeup.common.ServerResponse
     **/
    public ServerResponse<PageInfo> getProductList(int pageNum,int pageSize){
        //1 startPage-记录一个开始
        PageHelper.startPage(pageNum,pageSize);
        List<Product> productList =productMapper.selectList();
        //2 填充自己的SQL查询逻辑
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //3 pageHelper的收尾
        //使用PageInfo对象传SQL返回的结果，他会根据这个集合进行自动地分页处理
        //它会将List填充到这里，里面会对List进行计算，包括PageNum之类，导航页，
        PageInfo pageResult = new PageInfo(productList);

        //给前端展示不是把整个Product，但是我们还要运用ProductList进行分页，这时我们将List重置
        pageResult.setList(productListVoList);

        //返回分页结果
        return ServerResponse.createBySuccess(pageResult);

    }

    /**
     * @Author 李闯
     * @Description ProductListVo 组装方法
     * @Date 18:07 2018/8/31
     * @Param [product]
     * @return com.leeup.vo.ProductListVo
     **/
    private ProductListVo assembleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://img.happymmall.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubImages());
        productListVo.setStatus(product.getStatus());

        return productListVo;
    }

    /**
     * @Author 李闯
     * @Description 商品搜索并分页
     * @Date 18:25 2018/8/31
     * @Param [productName, productId, pageNum, pageSize]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    public ServerResponse<PageInfo> searchProduct(String productName,Integer productId,Integer pageNum,Integer pageSize){
        PageHelper.startPage(pageNum,pageSize);
        //判断ProductName
        if (StringUtils.isNotBlank(productName)){
            //非空，我们使用SQL中的某个查询，构建一下ProductName
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        //根据名字和Id进行一个空判断，如果空的话，就不把空的条件放sql查询中

        //这个后台的产品集合，我们查询时不需要判断他的状态，但是在前台用户搜索的时候，我们就要判断了，他查询出来的结果一定是要在线的产品
        List<Product> productList =productMapper.selectByNameAndProductId(productName,productId);

        //将product转成productListVo
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        //分页
        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productListVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    /**
     * @Author 李闯
     * @Description 查看商品详情
     * @Date 21:41 2018/9/7
     * @Param [productId]
     * @return com.leeup.common.ServerResponse<com.leeup.vo.ProductDetailVo>
     **/
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId){
        if (productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }

        //判断product的状态,如果不是在线的话，就提示错误信息
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或删除");
        }
        // 不为空返回一个vo对象，
        // vo对象-> value Object 承载对象值得对象
        //复杂方式 pojo -> bo  bussiness Object-> vo view Object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    /**
     * @Author 李闯
     * @Description  搜索商品
     * @Date 23:39 2018/9/7
     * @Param [keyword, categoryId, pageNum, pageSize,orderby 排序方式]
     * @return com.leeup.common.ServerResponse<com.github.pagehelper.PageInfo>
     **/
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,Integer pageNum,Integer pageSize,String orderby){
        //对值进行判断
        if (StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        //声明categoryId的集合
        //这个集合的作用是，假设当传分类的时候，传了一个高级的分类，例如电子产品，电子产品又分为手机，手机分为非智能机以及智能机，
        //当我们传一个大的父类的时候，我们需要调用之前的递归算法，将这个分类的所有子分类，遍历出来，再加上本身，把这些categoryId放到该List中
        //我们查询sql时，直接用一个in命中我们的产品所在集合的条件
        List<Integer> categoryIdList = new ArrayList<Integer>();

        //单独判断categoryId
        if (categoryId!=null){
            Category category  = categoryMapper.selectByPrimaryKey(categoryId);
            if (category ==null && StringUtils.isBlank(keyword)){
                //查不到catgory并且关键字也是空，即没有该分类，也没有该关键字，这个时候返回空的结果集，不报错
                //代表假设我们有个分类是1，我们db中只有分类是1,前端传值为2，我们查不到，我们不能报错，
                PageHelper.startPage(pageNum,pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                //不需要重新setList将productListVoList set到pageInfo中，因为pageInfo的构造方法中调用了this将传进去的list进行判断是不是page对象
                //如果是page对象，就会把他强制转成page 赋值成实际参数，如果是collection也是一样，所以，如果这个结果集没有变化的话，setList直接用构造器中的即可
                return ServerResponse.createBySuccess(pageInfo);
            }

            //将查出来的categoryId放到这里
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if (StringUtils.isNotBlank(keyword)){
            //关键字不为空的时候
            keyword = new StringBuilder().append("%").append(keyword).append('%').toString();
        }
        //开始-分页
        PageHelper.startPage(pageNum,pageSize);
        //排序处理-动态排序
        if (StringUtils.isNotBlank(orderby)){
            //排序方式不为空的话
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderby)){
                //分割字符串
                String [] orderbyArray = orderby.split("_");
                //PageHelper的orderBy方法里面要传的参数的格式是这样的
                PageHelper.orderBy(orderbyArray[0]+" "+orderbyArray[1]);//price desc 代表price降序
            }
        }
        //搜索Product
        //这里的categoryIdList 在上面已经被赋值了，最上面也new了它,如果我们传一个空盒进去的话，那么它不是空 in里面就没有值，那我们就查不出来结果
        //所以们要使用三元运算符运算一下，简单的if判断
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        //构建ListVo
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        //分页
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.createBySuccess(pageInfo);
    }
}
