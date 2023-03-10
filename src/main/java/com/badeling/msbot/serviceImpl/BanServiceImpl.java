package com.badeling.msbot.serviceImpl;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.service.BanService;
import com.badeling.msbot.util.HttpUtil;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

@Component
public class BanServiceImpl implements BanService {
    public static String getAuth() {
        // 官网获取的 API Key 更新为你注册的
        String clientId = MsbotConst.baiduKey;
        // 官网获取的 Secret Key 更新为你注册的
        String clientSecret = MsbotConst.baiduSecret;
        return getAuth(clientId, clientSecret);
    }
    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                String object = key + "--->" + map.get(key);
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {

        }
        return null;
    }

    public String getCheckResult(String word) {
        if (word.contains("[CQ:image,file=")){
            int count = word.split("\\[CQ:image,file=", -1).length-1;
            for (int i=0;i<count;i++){
                int start = word.indexOf("[CQ:image,file=");
                int end = word.indexOf("]",start);

                String substring = word.substring(start, end+1);
                word = word.replace(substring, "");
            }
        }
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/text_censor/v2/user_defined";
        if (word.length() != 0){
            try {
                String param = "text=" + word;
                String accessToken = getAuth();
                String result = HttpUtil.post(url, accessToken, param);
                //System.out.println(result);
                JSONObject result_json= new JSONObject(result);
                //System.out.println(result_json.get("conclusion"));

                if(result_json.get("conclusion").equals("不合规")){
                    return "禁言";
                }
                else
                    return "不禁言";

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "不禁言";
    }

}
