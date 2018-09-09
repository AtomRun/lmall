package com.leeup.controller.backend;

import com.google.common.collect.Maps;
import com.leeup.common.Const;
import com.leeup.common.ResponseCode;
import com.leeup.common.ServerResponse;
import com.leeup.pojo.Product;
import com.leeup.pojo.User;
import com.leeup.service.IFileService;
import com.leeup.service.IProductService;
import com.leeup.service.IUserService;
import com.leeup.util.PropertiesUtil;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @ClassName ProductManagerController
 * @Description 后台产品管理Controller
 * @Author李闯
 * @Date 2018/8/29 19:27
 * @Version 1.0
 **/
@Controller
@RequestMapping("/manage/product")
public class ProductManagerController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    /**
     * @Author 李闯
     * @Description //新增 产品
     * @Date 19:29 2018/8/29
     * @Param [session 判断登录权限, product 接收要保存的产品]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("save.do")
    @ResponseBody
    public ServerResponse productSave(HttpSession session, Product product){
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        //判断管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //增加产品
            return iProductService.saveOrUpdateProduct(product);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * @Author 李闯
     * @Description 产品上下架，更新产品销售状态
     * @Date 19:58 2018/8/29
     * @Param [session, product]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(HttpSession session, Integer productId, Integer status){
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        //判断管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //业务逻辑
            return iProductService.setSaleStauts(productId,status);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * @Author 李闯
     * @Description 后台获取产品详情
     * @Date 20:49 2018/8/30
     * @Param [session, productId]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse getDetatil(HttpSession session, Integer productId){
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        //判断管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //业务逻辑
            return iProductService.manageProductDetail(productId);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * @Author 李闯
     * @Description 产品list
     * @Date 12:47 2018/8/31
     * @Param [session, pageNum 第几页, pageSize 页面容量]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse getList(HttpSession session, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        //判断管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            //添加动态分页
            //mybatisPageHelper 分页的开源技术包，它是通过AOP的方式在我们执行我们自己SQL时，
            // 我们在SQL后写上我们的分页处理，MybatisPageHelper会监听到这个切面
            // 然后他会通过AOP自动把分页用到的SQL直接再执行一次，例如我们执行分页时，我们只查某些数据，但是分页中肯定要查总数量，然后根据我们的
            //pageSize页面总数，来进行分页，然后将数据返回
            return iProductService.getProductList(pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * @Author 李闯
     * @Description 后台产品搜索
     * @Date 18:22 2018/8/31
     * @Param [session, productName 根据产品名搜索, productId 根据Id进行搜索, pageNum 默认当前页数, pageSize 默认当前展示几条数据]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse productSearch(HttpSession session,String productName,Integer productId, @RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum,@RequestParam(value = "pageSize",defaultValue = "10") int pageSize){
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        //判断管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            return iProductService.searchProduct(productName,productId,pageNum,pageSize);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * @Author 李闯
     * @Description 文件上传，编辑产品时把产品的图片上传到服务器上
     * @Date 23:21 2018/8/31
     * @Param [file springmvc的文件上传, request 根据Servlet的上下文，动态的创建一个相相对路径出来 ]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(required = false,value = "upload_file") MultipartFile file, HttpServletRequest request){
        //需要进行权限判断，如果被人拿到我们的请求地址，上传一个大文件，虽然我们有10M的限制，但是他多次上传会把我们的硬盘打满
        //
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，请使用管理员登录");
        }
        //判断管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            String path =request.getSession().getServletContext().getRealPath("upload");//从request中拿到Servlet上下文
            String targetFileName = iFileService.upload(file,path);//获得上传的文件名

            //拼接URL
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;

            //使用Map组装
            Map fileMap = Maps.newHashMap();
            fileMap.put("uri",targetFileName);
            fileMap.put("url",url);

            return ServerResponse.createBySuccess(fileMap);
        }else {
            return ServerResponse.createByErrorMessage("无权限操作");
        }
    }

    /**
     * @Author 李闯
     * @Description 富文本文件上传
     * @Date 18:53 2018/9/6
     * @Param [session, file, request,response 使用富文本上传的时候，我们需要修改response的header,]
     * @return com.leeup.common.ServerResponse
     **/
    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(required = false,value = "upload_file") MultipartFile file, HttpServletRequest request, HttpServletResponse response){

        Map resultMap = Maps.newHashMap();

        //需要进行权限判断，如果被人拿到我们的请求地址，上传一个大文件，虽然我们有10M的限制，但是他多次上传会把我们的硬盘打满
        User user = (User)session.getAttribute(Const.CURRENT_USER);

        if (user==null){
            //没有权限设置succeess为false
            resultMap.put("success",false);
            resultMap.put("msg","用户未登录，请使用管理员登录");
            return resultMap;
        }
        //富文本中对于返回值有自己的要求，我们使用的是simditor，所以按照simditor的要求进行返回
        //即上传完成后json返回响应的格式分别是 success:boolean类型，msg: 可选的，file_path:完整的图片服务器域名加上文件名，可以访问到的filepath

        //没有必要为这个返回值单独做一个对象，我们返回一个Map即可

        //判断管理员权限
        if (iUserService.checkAdminRole(user).isSuccess()){
            String path =request.getSession().getServletContext().getRealPath("upload");//从request中拿到Servlet上下文
            String targetFileName = iFileService.upload(file,path);//获得上传的文件名

            //判断targetFile进行判断
            if (StringUtils.isBlank(targetFileName)){
                //如果为空
                resultMap.put("success",false);
                resultMap.put("msg","上传失败");
                return resultMap;
            }
            //拼接URL
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix")+targetFileName;//完整的可以访问的文件地址

            resultMap.put("success",true);
            resultMap.put("msg","上传成功");
            resultMap.put("file_path",url);

            //设置response的header
            //有很多插件对于前端对后端的返回都是有要求的，我们要修改response的header
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return resultMap;
        }else {
            resultMap.put("success",false);
            resultMap.put("msg","无权限操作");
            return resultMap;
        }
    }
}
