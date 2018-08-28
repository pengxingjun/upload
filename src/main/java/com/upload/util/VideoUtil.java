package com.upload.util;

import com.upload.helper.SystemValue;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Dean on 2018/8/27.
 */
public class VideoUtil {

    private static Logger logger = Logger.getLogger("VideoUtil");

    /**
     * 获取视频第一帧
     *
     * @param video_path
     * @return
     */
    public static boolean processImg(String video_path) {
        File file = new File(video_path);
        String path = "";
        if (!file.exists()) {
            System.err.println("路径[" + video_path + "]对应的视频文件不存在!");
            return false;
        }
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(SystemValue.getProperties("ffmepgPath"));
        commands.add("-i");
        commands.add(video_path);
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-ss");
        commands.add("1");//这个参数是设置截取视频多少秒时的画面
        commands.add("-t");
        commands.add("0.001");
        commands.add("-s");
        commands.add("332x188");//宽X高
        path = video_path.substring(0, video_path.lastIndexOf(".")) + ".jpg";
        commands.add(path);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.start();
            System.out.println("截取成功");
            //休眠两秒，要不然上传图片的时候会找不到图片
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 获取视频总时间
     * @param video_path    视频路径
     * @return
     */
    public static int getVideoTime(String video_path) {
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(SystemValue.getProperties("ffmepgPath"));
        commands.add("-i");
        commands.add(video_path);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            final Process p = builder.start();
            //从输入流中读取视频信息
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            //从视频信息中解析时长
            String regexDuration = "Duration: (.*?), start: (.*?), bitrate: (\\d*) kb\\/s";
            Pattern pattern = Pattern.compile(regexDuration);
            Matcher m = pattern.matcher(sb.toString());
            if (m.find()) {
                int time = getTimeLen(m.group(1));
                logger.info(video_path+",视频时长："+time+", 开始时间："+m.group(2)+",比特率："+m.group(3)+"kb/s");
                return time;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    //格式:"00:00:10.68"
    private static int getTimeLen(String timelen){
        int min=0;
        String strs[] = timelen.split(":");
        if (strs[0].compareTo("0") > 0) {
            min+=Integer.valueOf(strs[0])*60*60;//秒
        }
        if(strs[1].compareTo("0")>0){
            min+=Integer.valueOf(strs[1])*60;
        }
        if(strs[2].compareTo("0")>0){
            min+=Math.round(Float.valueOf(strs[2]));
        }
        return min;
    }

}
