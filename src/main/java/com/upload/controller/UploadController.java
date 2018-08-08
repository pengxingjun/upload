package com.upload.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.upload.helper.SystemValue;
import com.upload.util.DateUtil;
import com.upload.util.ThreeDesUtil;
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
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;

@Controller
@RequestMapping(value = "")
public class UploadController {

    private Logger logger = Logger.getLogger(this.getClass());

    @RequestMapping(value = "upload")
    @ResponseBody
    public String upload(HttpServletRequest request, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();
        try {
            String savePath = SystemValue.RESOURCE_PATH + File.separator + DateUtil.dateFormat(new Date(), "yyyyMM") + File.separator;
            File dirPath = new File(savePath);
            if (!dirPath.exists()) {
                dirPath.mkdirs();
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
                        String resizeParam = request.getParameter("defaultSize");
                        if(StringUtils.isNotEmpty(resizeParam)){
                            JSONArray array = JSONArray.parseArray(resizeParam);
                            for(int i = 0; i< array.size(); i++){
                                File newFile = new File(savePath + fileName + "!" + array.getIntValue(i) + oriName.substring(oriName.lastIndexOf(".")));
                                Thumbnails.of(localFile).size(array.getIntValue(i), array.getIntValue(i)).toFile(newFile);
                            }
                        }
                        //其他判断（单位和存储空间等信息）
                        String limit = request.getParameter("limit");
                        if(StringUtils.isNotEmpty(limit)){
                            String param = ThreeDesUtil.decryptThreeDESECB(limit, ThreeDesUtil.key);
                            System.out.println(param);
                        }

                        if (file.getContentType().contains("image")) {
                            //开始裁剪
                            handleImage(request, localFile, "");
                        }
                        jsonObject.put("resCode", 0);
                        JSONObject result = new JSONObject();
                        result.put("size", file.getSize());
                        String baseUrl = "";
                        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
                            baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
                        } else {
                            baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
                        }
                        String url = baseUrl + "/file/" + DateUtil.dateFormat(new Date(), "yyyyMM") + "/" + fileName + oriName.substring(oriName.lastIndexOf("."));
                        result.put("url", url);
                        jsonObject.put("resultList", result);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            jsonObject.put("resCode", 1);
            jsonObject.put("message", "图片上传失败");
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
            String baseUrl = "";
            if (request.getServerPort() == 80 || request.getServerPort() == 443) {
                baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
            } else {
                baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            }
            result.put("url", baseUrl + "/file/" + DateUtil.dateFormat(new Date(), "yyyyMM") + "_thumb/" + newFile.getName());
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
            String baseUrl = "";
            if (request.getServerPort() == 80 || request.getServerPort() == 443) {
                baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
            } else {
                baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            }
            String savePath = SystemValue.RESOURCE_PATH + File.separator + DateUtil.dateFormat(new Date(), "yyyyMM") + "_qrcode" + File.separator;
            String fileName = System.currentTimeMillis() + "" + RandomUtils.nextInt(100, 999) + ".jpg";
            byte2image(image, savePath + fileName);
            result.put("url", baseUrl + "/file/" + DateUtil.dateFormat(new Date(), "yyyyMM") + "_qrcode/" + fileName);
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
        String savePath = SystemValue.RESOURCE_PATH + File.separator + DateUtil.dateFormat(new Date(), "yyyyMM") + File.separator;
        String fileName = System.currentTimeMillis() + ".png";
        FileOutputStream outputStream = null;
        try {
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
            String baseUrl = "";
            if (request.getServerPort() == 80 || request.getServerPort() == 443) {
                baseUrl = request.getScheme() + "://" + request.getServerName() + request.getContextPath();
            } else {
                baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
            }
            result.put("url", baseUrl + "/file/" + DateUtil.dateFormat(new Date(), "yyyyMM") + "/" + fileName);
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

    @RequestMapping(value = "/file/{fileDir}/{fileName:.+}")
    public void file(@PathVariable(value = "fileDir") String fileDir, @PathVariable(value = "fileName") String fileName, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        InputStream is = null;
        try {
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
                String filePath = SystemValue.RESOURCE_PATH + File.separator + DateUtil.dateFormat(new Date(), "yyyyMM")
                        + "_thumb" + File.separator + name[0] + "_" + dataX + "_" + dataWidth + "_" + dataY + "_" + dataHeight + "." + name[1];
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

}