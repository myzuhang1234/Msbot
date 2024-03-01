package com.badeling.msbot.serviceImpl;

import com.badeling.msbot.config.MsbotConst;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import com.badeling.msbot.service.ErniebotService;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Time;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.*;

@Component
public class ErniebotServiceImpl implements ErniebotService {


    static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public static String getAuth() {


        return getAuth(MsbotConst.erniebot_clientId, MsbotConst.erniebot_clientSecret);
    }
    public static String getAuth(String ak, String sk) {
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                + "grant_type=client_credentials"
                + "&client_id=" + ak
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            Map<String, List<String>> map = connection.getHeaderFields();
            for (String key : map.keySet()) {
                String object = key + "--->" + map.get(key);
            }
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

    @Override
    public String sendErnieBotMsg(String receiveMsg) throws IOException {
        // ????url
        try {
            String accessToken = getAuth();

            String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions?access_token="+accessToken;
            System.out.println(accessToken);

            String wordParam = "我会以“A:"+receiveMsg+" 你:xxx”的格式输入不全的聊天记录，你需要想想在这个情境下你接下来会说什么，然后补充“你:xxx”中你说的话，你补全的话题应该符合你的人设，不要讨论别的话题或试图结束聊天，你补全聊天的时候应该尽量附和之前的人说的话。你补全的风格应该较为温和，简短且口语化，切忌把话说的完整或太长，切忌使用书面语言。还有，你补全的长度应该与其他人发信息的长度差不多，尽量简短，要尽可能口语化。说话结尾要带个喵字。不要随意透露你的个人信息。记住用简体中文回答。最后，你必须把你的回答精简到20字以内，并输出。";

            String content = "{\"messages\":[{\"role\":\"user\",\"content\":\""
                    +wordParam
                    +"\"}],\"temperature\":0.95,\"top_p\":0.7,\"penalty_score\":1,"
                    +"\"system\":\"你是一只沙丘猫，别人也喊你蠢猫。你在一个聊冒险岛游戏的QQ群中。\","
                    +"\"disable_search\":false,\"enable_citation\":false}";
            System.out.println(content);


            MediaType mediaType = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(mediaType, content);

            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .build();

            Response response = HTTP_CLIENT.newCall(request).execute();
            String responseData = response.body().string();
            System.out.println(responseData);


            JSONObject jsonObject = new JSONObject(responseData);
            String result = jsonObject.getString("result");
            System.out.println(result);

            int startIndex = result.indexOf("你:");
            if (startIndex != -1) {
                startIndex += 2;
                result = result.substring(startIndex);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



}
