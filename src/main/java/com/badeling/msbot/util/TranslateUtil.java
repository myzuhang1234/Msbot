package com.badeling.msbot.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.domain.TransResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class TranslateUtil {
	/**
     * 百度翻译接口地址
     */
    private static final String TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate";


    
    /**
     * 获得翻译结果
     * @param query
     * @param from
     * @param to
     * @return
     * @throws IOException
     */
    public static String getTransResult(String query, String from, String to) throws IOException {
        Map<String, String> params = buildParams(query, from, to);
        JSONObject jsonObject;
        //当请求翻译内容过长 用post
        if (query.length() >= 2000) {
            //post 请求方式
            jsonObject = HttpUtil.doPostStr(TRANS_API_HOST, params);
        } else {
           //  get请求方式
            String url = getUrlWithQueryString(TRANS_API_HOST, params);
            jsonObject = HttpUtil.doGetStr(url);
        }
        if (jsonObject.get("error_code")!=null) {
            return "请输入需要翻译的内容";
        }else{
            TransResult transResult = JSON.parseObject(jsonObject.toString(), TransResult.class);
            return transResult.getTrans_result().get(0).getDst();
        }
    }

    /**
     * 构建参数map
     *
     * @param query
     * @param from
     * @param to
     * @return
     * @throws UnsupportedEncodingException
     */
    private static Map<String, String> buildParams(String query, String from, String to) throws UnsupportedEncodingException {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", MsbotConst.appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = MsbotConst.appid + query + salt + MsbotConst.securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }


    /**
     * 拼接url get方式拼接参数  返回url
     *
     * @param url
     * @param params
     * @return
     */
    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) { // 过滤空的key
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }


    /**
     * 对输入的字符串进行URL编码, 即转换为%20这种形式
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }
}
