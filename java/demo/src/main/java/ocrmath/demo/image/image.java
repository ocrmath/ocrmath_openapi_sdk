package ocrmath.demo.image;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import ocrmath.demo.util.HashTools;
import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class image {

    /**
     * app_key、app_secret,从<a href="https://web.ocrmath.com/OpenApi">https://web.ocrmath.com/</a>的开放接口页面获取
     */
    private static final String APP_KEY = "xxx";
    private static final String APP_SECRET = "xxx";

    /**
     * image识别接口
     */
    private static final String URL = "https://web.ocrmath.com/v1/text";

    public static void main(String[] args) {
        // 创建 OkHttpClient 实例，配置连接和读取超时时间
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        // 创建文件资源
        File file = new File("./file/ces.jpg");

        // 初始化 JSON 对象，用于存储请求参数
        JSONObject json = new JSONObject();

        // 获取当前时间戳
        long timeMillis = System.currentTimeMillis();
        // 初始化 appSign，用于存储计算后的应用签名
        String appSign = null;

        // 尝试计算应用签名，若计算失败则捕获异常并打印错误信息
        try {
            appSign = HashTools.getAppSign(APP_KEY, APP_SECRET, timeMillis);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        // 将应用密钥、签名、时间戳等参数放入 JSON 对象中
        json.put("app_key", APP_KEY);
        json.put("sign", appSign);
        json.put("timestamp", timeMillis);
        json.put("callback_open", 1);
        json.put("call_back_url", "http://xxx/xxx");
        // 可选择使用文件URL代替本地文件，但两者不能同时使用
        //json.put("file_url","https://web.ocrmath.com/uploads/ocrmath/png/2024-11-22/39b83cab9fcce4c13c0ee024da4860ed.png");

        // 构建 JSON 数据字符串
        String optionsJson = json.toJSONString();

        // 构建 MultipartBody，用于发送包含文件和 JSON 数据的 POST 请求
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"ces.jpg\""),
                        RequestBody.create(MediaType.parse("image/jpeg"), file))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"options_json\""),
                        RequestBody.create(optionsJson, MediaType.parse("application/json")))
                .build();

        // 构建请求对象，设置请求 URL 和请求体，并指定接受 JSON 格式的响应
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .header("Accept", "application/json")
                .build();

        // 发送请求并处理响应
        try (Response response = client.newCall(request).execute()) {

            // 检查响应是否成功
            if (!response.isSuccessful()) {
                System.out.println("响应失败");
                return;
            }
            // 确保响应体不为空
            if (response.body() == null) {
                return;
            }
            // 解析响应 JSON 数据
            JSONObject result = JSON.parseObject(response.body().string());
            Integer errno = result.getInteger("errno");
            // 根据 errno 判断请求是否成功
            if (0 == errno) {
                // 提取并打印任务 ID
                JSONObject data = result.getJSONObject("data");
                String taskId = data.getString("task_id");
                System.out.println("任务id：" + taskId);
            } else {
                // 打印错误码和错误信息
                System.out.println(errno);
                String msg = result.getString("msg");
                System.out.println(msg);
                // 进一步处理
            }
        } catch (IOException e) {
            // 捕获 IO 异常并打印错误信息
            System.out.println("请求失败，错误信息: " + e.getMessage());
        }
    }
}
