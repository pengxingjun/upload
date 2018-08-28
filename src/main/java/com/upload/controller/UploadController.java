package com.upload.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.exception.ParamException;
import com.upload.helper.SystemValue;
import com.upload.util.DateUtil;
import com.upload.util.ThreeDesUtil;
import com.upload.util.VideoUtil;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping(value = "")
public class UploadController {

    private Logger logger = Logger.getLogger(this.getClass());

    @RequestMapping(value = "upload")
    @ResponseBody
    public String upload(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        try {
            String unitFilePath = getRelatePath(request, "file");
            String unitUrlPath = getRelatePath(request, "url");
            String savePath = SystemValue.RESOURCE_PATH + File.separator + unitFilePath
                    + DateUtil.dateFormat(new Date(), "yyyyMM") + File.separator;
            File dirPath = new File(savePath);
            if (!dirPath.exists()) {
                boolean success = dirPath.mkdirs();
                if(!success){
                    throw new ParamException("上传文件夹错误");
                }
            }
            // 解析器解析request的上下文
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
            // 先判断request中是否包涵multipart类型的数据，
            if (multipartResolver.isMultipart(request)) {
                // 再将request中的数据转化成multipart类型的数据
                MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
                @SuppressWarnings("rawtypes")
                Iterator iter = multiRequest.getFileNames();
                while (iter.hasNext()) {
                    MultipartFile file = multiRequest.getFile((String) iter.next());
                    if (file != null) {
                        String oriName = file.getOriginalFilename();
                        String fileName = System.currentTimeMillis() + "" + RandomUtils.nextInt(100, 999);
                        File localFile = new File(savePath + fileName + oriName.substring(oriName.lastIndexOf(".")));
                        // 写文件到本地
                        file.transferTo(localFile);
                        //压缩
                        if (file.getContentType().contains("image")) {
                            String resizeParam = request.getParameter("defaultSize");
                            if(StringUtils.isNotEmpty(resizeParam)){
                                JSONArray array = JSONArray.parseArray(resizeParam);
                                for(int i = 0; i< array.size(); i++){
                                    File newFile = new File(savePath + fileName + "!" + array.getIntValue(i) + oriName.substring(oriName.lastIndexOf(".")));
                                    Thumbnails.of(localFile).size(array.getIntValue(i), array.getIntValue(i)).toFile(newFile);
                                }
                            }
                            //开始裁剪
                            handleImage(request, localFile, "");
                        }
                        String thumbUrl = "";
                        int videoTime = 0;
                        if (file.getContentType().contains("video")) {
                            if(VideoUtil.processImg(localFile.getAbsolutePath())){
                                thumbUrl = fileName + ".jpg";
                                videoTime = VideoUtil.getVideoTime(localFile.getAbsolutePath());
                            }
                        }
                        jsonObject.put("resCode", 0);
                        JSONObject result = new JSONObject();
                        String rePath = getBasePath(request) + "/file/" + unitUrlPath + DateUtil.dateFormat(new Date(), "yyyyMM") + "/";
                        result.put("size", file.getSize());
                        result.put("url", rePath + fileName + oriName.substring(oriName.lastIndexOf(".")));
                        if(StringUtils.isNotEmpty(thumbUrl)){
                            result.put("thumb", rePath + thumbUrl);
                        }
                        if(videoTime > 0){
                            result.put("time", videoTime);
                        }
                        jsonObject.put("resultList", result);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("resCode", 1);
            jsonObject.put("message", "文件上传失败");
        }
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "cropper")
    @ResponseBody
    public String cropper(HttpServletRequest request, HttpServletResponse response) {
        String url = request.getParameter("url");
        JSONObject jsonObject = new JSONObject();
        try {
            File newFile = handleImage(request, null, url);
            jsonObject.put("resCode", 0);
            JSONObject result = new JSONObject();
            String basePath = getBasePath(request);
            String urlPath = url.substring(0, url.lastIndexOf("/")).replace(basePath, "")
                    .replace("/file/", "");
            result.put("url", basePath + "/file/" + urlPath + "/" + newFile.getName());
            jsonObject.put("resultList", result);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("resCode", 1);
            jsonObject.put("message", "图片裁剪错误");
        }
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "qrcode")
    @ResponseBody
    public String qrcode(HttpServletRequest request, HttpServletResponse response, String img) {
        byte[] image = Base64.getDecoder().decode(img.getBytes());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("resCode", 0);
            JSONObject result = new JSONObject();
            String savePath = SystemValue.RESOURCE_PATH + File.separator + getRelatePath(request, "file")
                    + DateUtil.dateFormat(new Date(), "yyyyMM") + File.separator;
            String fileName = System.currentTimeMillis() + "" + RandomUtils.nextInt(100, 999) + ".jpg";
            byte2image(image, savePath + fileName);
            result.put("url", getBasePath(request) + "/file/" + getRelatePath(request, "url")
                    + DateUtil.dateFormat(new Date(), "yyyyMM") + fileName);
            jsonObject.put("resultList", result);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("resCode", 1);
            jsonObject.put("message", "图片处理错误");
        }
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "svg2png")
    @ResponseBody
    public String svg2png(HttpServletRequest request, HttpServletResponse response, String svgCode) {
        JSONObject jsonObject = new JSONObject();
        FileOutputStream outputStream = null;
        try {
            String savePath = SystemValue.RESOURCE_PATH + File.separator + getRelatePath(request, "file")
                    + DateUtil.dateFormat(new Date(), "yyyyMM") + File.separator;
            String fileName = System.currentTimeMillis() + ".png";
            File dirPath = new File(savePath);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
            }
            File file = new File(savePath + fileName);
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            byte[] bytes = svgCode.getBytes("utf-8");
            PNGTranscoder t = new PNGTranscoder();
            TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bytes));
            TranscoderOutput output = new TranscoderOutput(outputStream);
            t.transcode(input, output);
            outputStream.flush();
            jsonObject.put("resCode", 0);
            JSONObject result = new JSONObject();
            result.put("url", getBasePath(request) + "/file/"  + getRelatePath(request, "url")
                    + DateUtil.dateFormat(new Date(), "yyyyMM") + "/" + fileName);
            jsonObject.put("resultList", result);
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("resCode", 1);
            jsonObject.put("message", "图片转换失败");
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject.toJSONString();
    }

    /*@RequestMapping(value = "/file/{fileDir}/{fileName:.+}")
    public void file(@PathVariable(value = "fileDir") String fileDir, @PathVariable(value = "fileName") String fileName,
                     HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        InputStream is = null;
        try {
            String path = SystemValue.RESOURCE_PATH + File.separator + "base" + File.separator + fileDir + File.separator;
            String base64Width = fileName.indexOf("!") > 0 ? fileName.substring(fileName.indexOf("!") + 1,fileName.indexOf(".")) : "";
            String oriFileName = fileName.contains("!") ? fileName.substring(0, fileName.indexOf("!")) + fileName.substring(fileName.indexOf(".")) : fileName;
            Integer width = 0;
            if(StringUtils.isNotEmpty(base64Width)){
                Base64.Decoder decoder = Base64.getDecoder();
                width = Integer.valueOf(new String(decoder.decode(base64Width.getBytes("UTF-8")), "UTF-8"));
                fileName = fileName.replace(base64Width, width + "");
            }
            File file = new File(path + fileName);
            if(!file.exists() && StringUtils.isNotEmpty(base64Width)){
                Thumbnails.of(new File(path + oriFileName)).size(width, width).toFile(file);
            }
            OutputStream out = response.getOutputStream();
            response.setContentLength((int) file.length());
            is = new FileInputStream(file);
            IOUtils.copy(is, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/

    @RequestMapping(value = "/file/**")
    public void file(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        InputStream is = null;
        try {
            String url = request.getRequestURI().replace(request.getContextPath(), "").replace("/file/", "").replaceAll("/+", "/");
            String fileDir = url.substring(0, url.lastIndexOf("/"));
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            String path = SystemValue.RESOURCE_PATH + File.separator + fileDir + File.separator;
            String base64Width = fileName.indexOf("!") > 0 ? fileName.substring(fileName.indexOf("!") + 1,fileName.indexOf(".")) : "";
            String oriFileName = fileName.contains("!") ? fileName.substring(0, fileName.indexOf("!")) + fileName.substring(fileName.indexOf(".")) : fileName;
            Integer width = 0;
            if(StringUtils.isNotEmpty(base64Width)){
                Base64.Decoder decoder = Base64.getDecoder();
                width = Integer.valueOf(new String(decoder.decode(base64Width.getBytes("UTF-8")), "UTF-8"));
                fileName = fileName.replace(base64Width, width + "");
            }
            File file = new File(path + fileName);
            Path tmpPath = Paths.get(path + fileName);
            String contentType =  Files.probeContentType(tmpPath);
            if(contentType == null || "".equals(contentType)) {
                contentType = "application/octet-stream";
            }
            response.setContentType(contentType);
            if(!file.exists() && StringUtils.isNotEmpty(base64Width)){
                Thumbnails.of(new File(path + oriFileName)).size(width, width).toFile(file);
            }
            OutputStream out = response.getOutputStream();
            response.setContentLength((int) file.length());
            is = new FileInputStream(file);
            IOUtils.copy(is, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            //e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private File handleImage(HttpServletRequest request, File file, String url) throws Exception {
        //裁剪
        String dataXStr = request.getParameter("dataX");
        if (dataXStr == null || "".equals(dataXStr.trim())) {
            dataXStr = "0";
        }
        String dataYStr = request.getParameter("dataY");
        if (dataYStr == null || "".equals(dataYStr.trim())) {
            dataYStr = "0";
        }
        String dataWidthStr = request.getParameter("dataWidth");
        String dataHeightStr = request.getParameter("dataHeight");
        if (dataHeightStr == null || "".equals(dataHeightStr.trim()) || dataWidthStr == null || "".equals(dataWidthStr.trim())) {
            BufferedImage bi = ImageIO.read(new FileInputStream(file));
            dataHeightStr = bi.getHeight() + "";
            dataWidthStr = bi.getWidth() + "";
        }
        Integer dataX = Integer.valueOf(dataXStr);
        Integer dataY = Integer.valueOf(dataYStr);
        Integer dataHeight = Integer.valueOf(dataHeightStr);
        Integer dataWidth = Integer.valueOf(dataWidthStr);
        if (dataX != null && dataY != null && dataHeight != null && dataWidth != null) {
            if (file != null && StringUtils.isEmpty(url)) {
                Thumbnails.of(file).sourceRegion(dataX, dataY, dataWidth, dataHeight)
                        .size(dataWidth, dataHeight).keepAspectRatio(false).toFile(file);
                return file;
            } else if (file == null && !StringUtils.isEmpty(url)) {
                String[] name = url.substring(url.lastIndexOf("/") + 1).split("\\.");
                String urlPath = url.substring(0, url.lastIndexOf("/")).replace(getBasePath(request),"")
                        .replace("/file/", "").replace("/", File.separator);
                String filePath = SystemValue.RESOURCE_PATH + File.separator + urlPath + File.separator
                        + name[0] + "_" + dataX + "_" + dataWidth + "_" + dataY + "_" + dataHeight + "." + name[1];
                File newFile = new File(filePath);
                if (!newFile.getParentFile().exists()) {
                    newFile.getParentFile().mkdirs();
                }
                if (!newFile.exists()) {
                    newFile.createNewFile();
                }
                URL fileURL = new URL(url);
                Thumbnails.of(fileURL).sourceRegion(dataX, dataY, dataWidth, dataHeight)
                        .size(dataWidth, dataHeight).keepAspectRatio(false).toFile(newFile);
                return newFile;
            }
        }
        return file;
    }

    /**
     * byte转图片
     *
     * @param data 图片数据
     * @param path 图片路径
     */
    private static void byte2image(byte[] data, String path) {
        if (data.length < 3 || path.equals("")) return;
        try {
            File file = new File(path);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            FileImageOutputStream imageOutput = new FileImageOutputStream(file);
            imageOutput.write(data, 0, data.length);
            imageOutput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getRelatePath(HttpServletRequest request, String type) throws Exception{
        String path = "";
        String limit = request.getParameter("limit");
        if(StringUtils.isNotEmpty(limit)){
            String param = ThreeDesUtil.decryptThreeDESECB(limit, ThreeDesUtil.key);
            Map<String, String> paramMap = getQueryString(param);
            if(StringUtils.isNotEmpty(paramMap.get("project"))){
                if("file".equals(type)){
                    path += paramMap.get("project") + File.separator ;
                }else {
                    path += paramMap.get("project") + "/" ;
                }
            }
            if(StringUtils.isNotEmpty(paramMap.get("unit_id"))){
                if("file".equals(type)){
                    path += paramMap.get("unit_id") + File.separator ;
                }else {
                    path += paramMap.get("unit_id") + "/" ;
                }
            }
        }
        return path;
    }

    private String getBasePath(HttpServletRequest request){
        String baseUrl = "";
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
            baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
        } else {
            baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        }
        return baseUrl;
    }

    private Map<String, String> getQueryString(String url){
        Map<String, String> map = null;
        url = url.substring(url.indexOf("?") + 1);
        if (url.contains("=")) {
            map = new HashMap<>();
            String[] arrTemp = url.split("&");
            for (String str : arrTemp) {
                String[] qs = str.split("=");
                map.put(qs[0], qs.length == 2 ? qs[1] : "");
            }
        }
        return map;
    }

}