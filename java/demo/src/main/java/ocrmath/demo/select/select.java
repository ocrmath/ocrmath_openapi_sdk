package ocrmath.demo.select;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ocrmath.demo.util.HashTools;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

public class select {

    /**
     * app_key、app_secret,从<a href="https://web.ocrmath.com/OpenApi">https://web.ocrmath.com/</a>的开放接口页面获取
     */
    private static final String APP_KEY = "xxx";
    private static final String APP_SECRET = "xxx";

    /**
     * 识别结果查询接口
     */
    private static final String URL = "https://web.ocrmath.com/v1/task_result";

    public static void main(String[] args) {
        // 创建 OkHttpClient 实例，用于发起网络请求
        OkHttpClient client = new OkHttpClient();
        // 创建 JSONObject 实例，用于存储请求参数
        JSONObject json = new JSONObject();
        // 获取当前时间戳，单位为毫秒
        long timeMillis = System.currentTimeMillis();
        // 初始化 appSign 为 null，用于后续存储生成的密钥
        String appSign = null;

        // 尝试生成 appSign，如果生成失败则捕获异常并打印错误信息
        try {
            appSign = HashTools.getAppSign(APP_KEY, APP_SECRET, timeMillis);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("密钥生成失败" + e.getMessage());
        }

        // 将参数放入 JSONObject 中
        json.put("app_key",APP_KEY);
        json.put("sign",appSign);
        json.put("timestamp",timeMillis);
        json.put("task_id","xxx");
        // 将 JSONObject 转换为字符串，用于请求参数
        String jsonParam = json.toJSONString();
        // 构建请求 URL，并对参数进行编码
        String url = URL + "?options_json=" + encode(jsonParam);

        // 创建 Request 对象，使用 GET 方法发起请求
        Request request = new Request.Builder()
                .url(url)
                .get() // GET 请求
                .build();

        // 发起请求并处理响应
        try (Response response = client.newCall(request).execute()) {
            int statusCode = response.code();
            // 如果响应成功，解析响应体中的 JSON 数据
            if (response.isSuccessful()) {
                if (response.body() != null){
                    JSONObject result = JSON.parseObject(response.body().string());
                    Integer errno = result.getInteger("errno");
                    // 根据 errno 判断响应结果
                    if (0 == errno){
                        // 提取任务 ID
                        JSONObject data = result.getJSONObject("data");
                        String latex = data.getString("total_latex");
                        //pdf会有每一页的结果，图片则是null
                        JSONArray detailList = data.getJSONArray("detail_list");
                    }else {
                        // 输出错误代码和消息
                        System.out.println(errno);
                        String msg = result.getString("msg");
                        System.out.println(msg);
                    }
                }
            } else {
                // 输出失败的状态码
                System.out.println("请求失败，状态码: " + statusCode);
            }
        } catch (IOException e) {
            // 输出请求失败的错误信息
            System.out.println("请求失败，错误信息: " + e.getMessage());
        }
    }
    private static String encode(String value) {
        try {
            return URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
