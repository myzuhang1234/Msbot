package com.badeling.msbot.serviceImpl;

import com.badeling.msbot.config.MsbotConst;
import com.badeling.msbot.service.RecordService;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Component
public class RecordServiceImpl implements RecordService {

    // 下载的文件格式, 3：mp3(default) 4： pcm-16k 5： pcm-8k 6. wav
    private final int aue = 3;

    // 语速，取值0-15，默认为5中语速
    private final int spd = 5;

    private String cuid = "1234567JAVA";


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

    private String getFormat(int aue) {
        String[] formats = {"mp3", "pcm", "pcm", "wav"};
        return formats[aue - 3];
    }

    public String getAudio(String word) {
        // 请求url
        String url = "https://tsn.baidu.com/text2audio";
        try {
            String accessToken = getAuth();
            String wordParam = URLEncoder.encode(word, "UTF-8");
            String params = "tex=" + wordParam;
            params += "&spd=" + spd;
            params += "&per=4";
            params += "&cuid=" + cuid;
            params += "&tok=" + accessToken;
            params += "&lan=zh&ctp=1";

            System.out.println("accessToken：");
            System.out.println(accessToken);

            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setConnectTimeout(5000);
            PrintWriter printWriter = new PrintWriter(conn.getOutputStream());
            printWriter.write(params);
            printWriter.close();

            String contentType = conn.getContentType();
            if (contentType.contains("audio/")) {
                byte[] bytes = getResponseBytes(conn);
                String format = getFormat(aue);

                String uuid = UUID.randomUUID().toString().replaceAll("-", "");
                String mp3path = MsbotConst.voiceUrl + uuid + ".mp3";
                String mp3name = uuid + ".mp3";

                File file = new File(mp3path);

                FileOutputStream os = new FileOutputStream(file);
                os.write(bytes);
                os.close();
                return "[CQ:record,file=" + mp3name +"]";
            }
            else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String sendRecordMsg(String receiveMsg) throws IOException {
        String back = getAudio(receiveMsg);

        return back;

    }

    public static byte[] getInputStreamContent(InputStream is) throws IOException {
        byte[] b = new byte[1024];
        // 定义一个输出流存储接收到的数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // 开始接收数据
        int len = 0;
        while (true) {
            len = is.read(b);
            if (len == -1) {
                // 数据读完
                break;
            }
            byteArrayOutputStream.write(b, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

    public static byte[] getResponseBytes(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        InputStream inputStream = conn.getInputStream();
        if (responseCode != 200) {
            System.err.println("http 请求返回的状态码错误，期望200， 当前是 " + responseCode);
            if (responseCode == 401) {
                System.err.println("可能是appkey appSecret 填错");
            }
            System.err.println("response headers" + conn.getHeaderFields());
            if (inputStream == null) {
                inputStream = conn.getErrorStream();
            }
            byte[] result = getInputStreamContent(inputStream);
            System.err.println(new String(result));
            return null;
        }

        byte[] result = getInputStreamContent(inputStream);
        return result;
    }



}
