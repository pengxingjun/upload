package com.elfinder.controller.executors;

import com.elfinder.controller.ErrorException;
import com.elfinder.controller.executor.AbstractJsonCommandExecutor;
import com.elfinder.controller.executor.CommandExecutor;
import com.elfinder.controller.executor.FsItemEx;
import com.elfinder.localfs.MysqlFsItem;
import com.elfinder.services.FsItem;
import com.elfinder.services.FsService;
import com.elfinder.services.FsVolume;
import com.web.exception.ParamException;
import com.web.helper.SystemValue;
import com.web.helper.sysinit.SystemValueInit;
import com.web.util.ImageUtils;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 图片处理命令
 */
public class ResizeCommandExecutor extends AbstractJsonCommandExecutor
        implements CommandExecutor {
    @Override
    protected void execute(FsService fsService, HttpServletRequest request,
                           ServletContext servletContext, JSONObject json) throws Exception {
        //'resize' or 'crop' or 'rotate'
        String mode = request.getParameter("mode");
        String target = request.getParameter("target");
        String autoImg = request.getParameter("auto");
        FsItemEx fsi = super.findItem(fsService, target);
        if(!StringUtils.isEmpty(fsi.getURL())){
            throw new ParamException("网络文件无法裁剪");
        }
        String width = request.getParameter("width");
        String height = request.getParameter("height");
        String x = request.getParameter("x"); //mode=crop
        String y = request.getParameter("y"); //mode=crop
        String degree = request.getParameter("degree"); //mode='rotate'
        //String quality = request.getParameter("quality");
        InputStream inputStream = null;
        try {
            inputStream = fsi.openInputStream();
            String[] name = fsi.getName().split("\\.");
            String ext = name[1];
            //BufferedImage image = ImageIO.read(inputStream);
            BufferedImage newFile = null;
            if ("resize".equals(mode)) {
                newFile = Thumbnails.of(inputStream).size(Integer.parseInt(width), Integer.parseInt(height)).keepAspectRatio(false).asBufferedImage();
                //newFile = ImageUtils.scale2(image, Integer.parseInt(width), Integer.parseInt(height), false);
            } else if ("crop".equals(mode)) {
                newFile = Thumbnails.of(inputStream).sourceRegion(Integer.parseInt(x), Integer.parseInt(y)
                        , Integer.parseInt(x) + Integer.parseInt(width), Integer.parseInt(y) + Integer.parseInt(height))
                        .size(Integer.parseInt(width), Integer.parseInt(height)).keepAspectRatio(false).asBufferedImage();
                //newFile = ImageUtils.cut(image, Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(width), Integer.parseInt(height));
            } else {
                newFile = Thumbnails.of(inputStream).size(Integer.parseInt(width), Integer.parseInt(height))
                        .rotate(Double.valueOf(degree)).asBufferedImage();
                //newFile = ImageUtils.Rotate(image, Integer.parseInt(degree));
            }
            //BufferedImage 转 InputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageOutputStream imageOutput = ImageIO.createImageOutputStream(byteArrayOutputStream);
            ImageIO.write(newFile, ext, imageOutput);
            InputStream newInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            if("auto".equals(autoImg)){
                //生成缩略文件或裁剪文件
                FsItemEx dst = fsi.getParent();
                FsItemEx dir = new FsItemEx(dst, dst.getName() + "_" + SystemValue.THUMB);
                if(!dir.exists()){
                    dir.createFolder();
                }
                String oriName = fsi.getName();
                FsItemEx file = new FsItemEx(dir, oriName.substring(0, oriName.lastIndexOf(".") - 1) + "_" + width + "x" + height + oriName.substring(oriName.lastIndexOf(".")));
                file.createFile();
                file.writeStream(newInputStream);
                //fsi.writeStream(newInputStream);
                json.put("changed", new Object[]{super.getFsItemInfo(request, file)});
            }else{
                fsi.writeStream(newInputStream);
                json.put("changed", new Object[]{super.getFsItemInfo(request, fsi)});
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new ParamException("图片处理错误");
        }

    }
}
