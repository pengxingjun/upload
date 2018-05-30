package com.plupload.controller;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.plupload.entities.Plupload;
import com.plupload.service.PluploadService;
@Controller
@RequestMapping(value = "/pluploadUpload")
public class PluploadUploadController {
	@Autowired
    private PluploadService pluploadService;

    /**Plupload文件上传处理方法*/
    @RequestMapping(value="upload")
    public void upload(Plupload plupload,HttpServletRequest request,HttpServletResponse response) {

        String FileDir = "pluploadDir";//文件保存的文件夹
        plupload.setRequest(request);//手动传入Plupload对象HttpServletRequest属性

        DateFormat df = new SimpleDateFormat("yyyyMM");
        String fileDir = df.format(new Date());
        
        //文件存储绝对路径,会是一个文件夹，项目相应Servlet容器下的"pluploadDir"文件夹，还会以用户唯一id作划分
        File dir = new File(request.getSession().getServletContext().getRealPath("/") + FileDir+"/" + fileDir);
        if(!dir.exists()){
            dir.mkdirs();//可创建多级目录，而mkdir()只能创建一级目录
        }
        //开始上传文件
        pluploadService.upload(plupload, dir);
    }
}
