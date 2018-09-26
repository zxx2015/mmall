package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.Product;
import com.mmall.pojo.User;
import com.mmall.service.IFileService;
import com.mmall.service.IProductService;
import com.mmall.service.IUserService;
import com.mmall.util.FTPUtil;
import com.mmall.util.PropertiesUtil;
import com.mmall.vo.ProductDetailVo;
import com.mmall.vo.ProductListVo;
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
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * Create by zhouxin
 **/
@Controller
@RequestMapping("/manage/product")
public class ProductManageController {

    @Autowired
    private IUserService iUserService;
    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService iFileService;

    @RequestMapping("save.do")
    @ResponseBody
    //新增OR更新产品
    public ServerResponse productSave(Product product, HttpSession session,HttpServletRequest request) throws UnsupportedEncodingException {
        //是否登录&是否有管理员权限
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登录");
        }
        ServerResponse response = iUserService.checkAdminRole(currentUser);
        if(response.isSuccess()){

            //是管理员则到service层添加或更新产品
            return iProductService.saveProduct(product);
        }

        return ServerResponse.createByErrorMessage("无权限操作，完成该操作需要管理员权限");
    }

    @RequestMapping("set_sale_status.do")
    @ResponseBody
    public ServerResponse setSaleStatus(int productId, int status,HttpSession session){
        //是否登录&是否有管理员权限
        User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        if(currentUser==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登录，需要强制登录");
        }
        ServerResponse response = iUserService.checkAdminRole(currentUser);
        if(response.isSuccess()){
            //是管理员则到service层改变数据库里的product状态
           return iProductService.setSaleStatus(productId,status);
        }
        return ServerResponse.createByErrorMessage("无权限操作，完成该操作需要管理员权限");
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> getDetail(Integer productId, HttpSession session){
        if(productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),"参数错误");
        }

            //到service层获取数据库里的信息
            return iProductService.getDetail(productId);

    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> getList(@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize, HttpSession session){

            //到service层获取数据库里的信息并返回对象
            return iProductService.getList(pageNum,pageSize);

    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse<PageInfo> search(@RequestParam(value = "productName",required = false) String productName,@RequestParam(value = "productId",required = false)Integer productId,@RequestParam(value = "pageNum",defaultValue = "1") Integer pageNum, @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize, HttpSession session){
        if(StringUtils.isBlank(productName)&&productId==null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEAGAL_ARGUMENT.getCode(),"参数错误");
        }
            //是管理员则到service层获取数据库里的信息并返回对象
            return iProductService.search(productName,productId,pageNum,pageSize);
    }

    @RequestMapping("upload.do")
    @ResponseBody
    public ServerResponse upload(HttpSession session,@RequestParam(value = "upload_file",required = false) MultipartFile file, HttpServletRequest request) {
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), "用户未登录，需要强制登录");
        }
        ServerResponse response = iUserService.checkAdminRole(currentUser);
        if (response.isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");

            String filename = iFileService.upload(file, path);
            //如果这里没有上传成功，返回为null，那么最终返回给前端的uri也为null
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + filename;
            Map filemaps = Maps.newHashMap();
            filemaps.put("uri", filename);
            filemaps.put("url", url);

            return ServerResponse.createBySuccess(filemaps);
        }
        return ServerResponse.createByErrorMessage("无权限操作，完成该操作需要管理员权限");
    }

    @RequestMapping("richtext_img_upload.do")
    @ResponseBody
    public Map richtextImgUpload(HttpSession session, @RequestParam(value = "upload_file",required = false)MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        Map map = Maps.newHashMap();
        User currentUser = (User) session.getAttribute(Const.CURRENT_USER);
        if (currentUser == null) {
            map.put("success",false);
            map.put("msg","用户未登录");
            return map;
        }
        ServerResponse response1 = iUserService.checkAdminRole(currentUser);
        if (response1.isSuccess()) {
            String path = request.getSession().getServletContext().getRealPath("upload");

            String filename = iFileService.upload(file, path);
            if(StringUtils.isBlank(filename)){
                map.put("success",false);
                map.put("msg","上传失败");
                return map;
            }
            String url = PropertiesUtil.getProperty("ftp.server.http.prefix") + filename;
            map.put("success",true);
            map.put("msg","上传成功");
            map.put("file_path",url);
            response.addHeader("Access-Control-Allow-Headers","X-File-Name");
            return map;
        }
        map.put("success",false);
        map.put("msg","用户无权限");
        return map;
    }



}
