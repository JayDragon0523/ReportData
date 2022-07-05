package com.crowbar.reportdata.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.crowbar.reportdata.pojo.Article;
import com.crowbar.reportdata.util.HttpUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ReportData
 * @description:
 * @author: JayDragon
 * @create: 2022-07-04 10:32
 **/
@Service
public class BaiduApiService {

    //文本审核
    private static final String CENSOR_URL = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";
    //情感分类
    private static final String SENTI_URL = "https://aip.baidubce.com/rpc/2.0/nlp/v1/sentiment_classify";
    //文章分类
    private static final String TOPIC_URL = "https://aip.baidubce.com/rpc/2.0/nlp/v1/topic";

    //我的token
    private static String accessToken = "24.5b952507363e04f8ca76c1bcb176492a.2592000.1653731542.282335-25657946";

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");



    /**
    * @description: 返回调用百度api并解析过的结果
    * @param: [rawfile源文件, type调用接口类别]
    * @return: java.util.List<java.lang.String>
    * @author: JayDragon
    * @date: 2022/7/4
    */
    public List<String> requestApiResult(MultipartFile rawfile, String type) throws IOException, InterruptedException {
        accessToken = AuthService.getAuth();

        List<String> result = new ArrayList<>();

        InputStreamReader read = null;
        try {
            //1.读字节流
            read = new InputStreamReader(rawfile.getInputStream(), "utf-8");//输入流

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufferedReader = new BufferedReader(read);
        String lineTxt = null;
        while ((lineTxt = bufferedReader.readLine()) != null) {//读取文件内容
            // System.out.println(lineTxt);
            Article article = JSON.parseObject(lineTxt, Article.class);
            System.out.println(article);
            String res = "";
            if(type.equals("censor")){
                res = textCensor(article);
                System.out.println(res);
            }else if(type.equals("sentiment")){
                res = sentiment(article);
                System.out.println(res);
            }else if(type.equals("topic")){
                res = topic(article);
                System.out.println(res);
            }
            if(!res.equals("")){
                result.add(res);
            }
        }

        return result;
    }

    /**
    * @description: 文本审核具体调用方法
    * @param: [lineTxt]
    * @return: java.lang.String
    * @author: JayDragon
    * @date: 2022/7/4
    */
    public static String textCensor(Article article) throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        int retryTimes = 5;
        String title;
        if(article.articleTitle.equals("")){
            title = article.articleContent;
        }else{
            title = article.articleTitle;
        }
        String result = "";
        do {
            try {
                //文本审核
                String param = "text=" + title;
                result = doPost(CENSOR_URL, param);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(result != null && !result.contains("error_code")){
                break;
            }else {
                retryTimes--;
            }
            Thread.sleep(500);

        }while (retryTimes > 0);

        //解析结果
        if(result != null && !result.contains("error_code")){
            JSONObject jsonObject = JSON.parseObject(result);
            String[] score = new String[6];
            for (int i = 0; i < score.length; i++) {
                score[i] = "0";
            }
            JSONArray data = null;
            try {
                data = jsonObject.getJSONArray("data");
                if(data == null) return "";
                for (int i = 0; i < data.size(); i++) {
                    JSONObject o = (JSONObject) data.get(i);
                    JSONArray hits = (JSONArray) o.get("hits");
                    JSONObject o1 = (JSONObject) hits.get(0);
                    // System.out.println(o1);
                    int type = Integer.parseInt(o.getString("type"));
                    if(type != 12){
                        continue;
                    }
                    int subType = Integer.parseInt(o.getString("subType"));
                    score[subType] = o1.getString("probability");
                }
            }catch (Exception e){
                System.out.println(title);
                e.printStackTrace();
            }
            sb.append(sdf.format(article.articleExposedTime)).append(", ").append(title).append(", ").append(article.articleAuthorName);
            for (int i = 0; i < score.length; i++) {
                sb.append(", ").append(score[i]);
            }
        }

        return sb.toString();
    }

    /**
    * @description: 情感倾向具体调用方法
    * @param: [lineText]
    * @return: java.lang.String
    * @author: JayDragon
    * @date: 2022/7/4
    */
    public static String sentiment(Article article) throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        String result = "";
        int retryTimes = 5;
        String title;
        if(article.articleTitle.equals("")){
            title = article.articleContent;
        }else{
            title = article.articleTitle;
        }
        do {
            try {
                Map<String, String> map = new HashMap<String, String>();
                map.put("text", title);
                String param = JSON.toJSONString(map);
                // String param = "text=" + lineTxt;
                result = doPost(SENTI_URL, param);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(result != null && !result.contains("error_code")){
                break;
            }else {
                retryTimes--;
            }
            Thread.sleep(500);

        }while (retryTimes > 0);

        if(result != null && !result.contains("error_code")){
            if(!result.contains("items")) return "";
            // 分割得到保存的api结果json
            JSONObject jsonObject = JSON.parseObject(result);

            String[] score = new String[4];
            for (int i = 0; i < score.length; i++) {
                score[i] = "0";
            }
            //======================api返回结果解析=========================================
            // 根据json格式进行字段解析，获取关注的字段
            // 原始json：{"log_id": 586180075570200253, "text": "凭残疾证可领10万元补助？全国300多位残疾人受骗 金额超30万",
            // "items": [{"positive_prob": 0.000938794, "confidence": 0.997914, "negative_prob": 0.999061, "sentiment": 0}]}
            JSONObject items = null;
            try {
                items = jsonObject.getJSONArray("items").getJSONObject(0);
                if(items == null) return "";
                score[0] = items.getString("positive_prob");
                score[1] = items.getString("confidence");
                score[2] = items.getString("negative_prob");
                score[3] = items.getString("sentiment");
            }catch (Exception e){
                e.printStackTrace();
            }

            sb.append(sdf.format(article.articleExposedTime)).append(", ").append(title).append(", ").append(article.articleAuthorName);
            for (int i = 0; i < score.length; i++) {
                sb.append(", ").append(score[i]);
            }
        }

        return sb.toString();
    }

    /**
    * @description: 文章分类具体调用方法
    * @param: [lineText]
    * @return: java.lang.String
    * @author: JayDragon
    * @date: 2022/7/4
    */
    public static String topic(Article article) throws InterruptedException {
        StringBuilder sb = new StringBuilder();
        String result = "";
        int retryTimes = 5;
        String title;
        if(article.articleTitle.equals("")){
            title = article.articleContent;
        }else{
            title = article.articleTitle;
        }
        String content  = article.articleContent;
        //title限制80字节
        if(title.length() >= 40){
            title = title.substring(0,40);
        }
        do {
            try {
                Map <String, String> map = new HashMap <String, String>();
                map.put("title", title);
                map.put("content", content);
                String param = JSON.toJSONString(map);
                // String param = "text=" + lineTxt;
                result = doPost(TOPIC_URL,param);
            }catch (Exception e){
                e.printStackTrace();
            }

            if(result != null && !result.contains("error_code")){
                break;
            }else {
                retryTimes--;
            }
            Thread.sleep(500);

        }while (retryTimes > 0);

        if(result != null && !result.contains("error_code")){

            JSONObject jsonObject = JSON.parseObject(result).getJSONObject("item");
            List<String> list = new ArrayList<>();
            JSONArray lv1 = jsonObject.getJSONArray("lv1_tag_list");
            for (int i = 0; i < lv1.size(); i++) {
                JSONObject object = (JSONObject) lv1.get(i);
                String tag = object.getString("tag");
                String score = object.getString("score");
                list.add(tag);list.add(score);
            }

            sb.append(sdf.format(article.articleExposedTime)).append(", ").append(title).append(", ").append(article.articleAuthorName);
            sb.append(", ").append(list.get(0)).append(", ").append(list.get(1));

        }

        return sb.toString();
    }


    /**
     * @description: 请求api
     * @param: [url百度api地址, param请求参数]
     * @return: java.lang.String
     * @author: JayDragon
     * @date: 2022/2/24
     */
    public static String doPost(String url, String param) {
        // 请求url
        // String url = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";
        try {
            // 这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            // String accessToken = AuthService.getAuth();

            String result = "";
            if(url.contains("nlp")){
                result = HttpUtil.post(url, accessToken,"application/json", param);
            }else{
                result = HttpUtil.post(url, accessToken, param);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
