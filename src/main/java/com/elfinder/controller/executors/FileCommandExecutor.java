package com.elfinder.controller.executors;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.mail.internet.MimeUtility;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.elfinder.controller.executor.AbstractCommandExecutor;
import com.elfinder.controller.executor.CommandExecutor;
import com.elfinder.controller.executor.FsItemEx;
import com.elfinder.services.FsService;
import com.elfinder.utils.MimeTypesUtils;
import org.apache.commons.lang3.StringUtils;

public class FileCommandExecutor extends AbstractCommandExecutor implements
        CommandExecutor {
    @Override
    public void execute(FsService fsService, HttpServletRequest request,
                        HttpServletResponse response, ServletContext servletContext)
            throws Exception {
        String target = request.getParameter("target");
        boolean download = "1".equals(request.getParameter("download"));
        FsItemEx fsi = super.findItem(fsService, target);
        String mime = fsi.getMimeType();

        response.setCharacterEncoding("utf-8");
        response.setContentType(mime);
        // String fileUrl = getFileUrl(fileTarget);
        // String fileUrlRelative = getFileUrl(fileTarget);
        String fileName = fsi.getName();
        // fileName = new String(fileName.getBytes("utf-8"), "ISO8859-1");
        if (download || MimeTypesUtils.isUnknownType(mime)) {
            response.setHeader(
                    "Content-Disposition",
                    "attachments; "
                            + getAttachmentFileName(fileName,
                            request.getHeader("USER-AGENT")));
            // response.setHeader("Content-Location", fileUrlRelative);
            response.setHeader("Content-Transfer-Encoding", "binary");
        }

        OutputStream out = response.getOutputStream();
        InputStream is = null;
        HttpURLConnection conn = null;
        response.setContentLength((int) fsi.getSize());
        try {
            // serve file
            String path = fsi.getURL();
            if (!StringUtils.isEmpty(path) && download) {
                URL url = new URL(path);
                conn = (HttpURLConnection) url.openConnection();
                //设置超时间为3秒
                conn.setConnectTimeout(3 * 1000);
                //防止屏蔽程序抓取而返回403错误
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                response.setContentLength(conn.getContentLength());
                //得到输入流
                is = conn.getInputStream();
            } else {
                is = fsi.openInputStream();
            }
            IOUtils.copy(is, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private String getAttachmentFileName(String fileName, String userAgent)
            throws UnsupportedEncodingException {
        if (userAgent != null) {
            userAgent = userAgent.toLowerCase();

            if (userAgent.indexOf("msie") != -1) {
                return "filename=\"" + URLEncoder.encode(fileName, "UTF8")
                        + "\"";
            }

            // Opera浏览器只能采用filename*
            if (userAgent.indexOf("opera") != -1) {
                return "filename*=UTF-8''"
                        + URLEncoder.encode(fileName, "UTF8");
            }
            // Safari浏览器，只能采用ISO编码的中文输出
            if (userAgent.indexOf("safari") != -1) {
                return "filename=\""
                        + new String(fileName.getBytes("UTF-8"), "ISO8859-1")
                        + "\"";
            }
            // Chrome浏览器，只能采用MimeUtility编码或ISO编码的中文输出
            if (userAgent.indexOf("applewebkit") != -1) {
                return "filename=\""
                        + MimeUtility.encodeText(fileName, "UTF8", "B") + "\"";
            }
            // FireFox浏览器，可以使用MimeUtility或filename*或ISO编码的中文输出
            if (userAgent.indexOf("mozilla") != -1) {
                return "filename*=UTF-8''"
                        + URLEncoder.encode(fileName, "UTF8");
            }
        }

        return "filename=\"" + URLEncoder.encode(fileName, "UTF8") + "\"";
    }
}
