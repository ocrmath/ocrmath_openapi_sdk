package ocrmath.demo.pdf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import ocrmath.demo.util.HashTools;
import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class pdf {

    /**
     * app_key、app_secret,从<a href="https://web.ocrmath.com/OpenApi">https://web.ocrmath.com/</a>的开放接口页面获取
     */
    private static final String APP_KEY = "xxx";
    private static final String APP_SECRET = "xxx";

    /**
     * pdf识别接口
     */
    private static final String URL = "https://web.ocrmath.com/v1/pdf";

    public static void main(String[] args) {
        // 创建 OkHttpClient 实例，配置连接和读取超时时间
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(8, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();

        // 定义要上传的文件路径
        File file = new File("./file/pdf-ces.pdf");

        // 创建 JSON 对象，用于存储请求参数
        JSONObject json = new JSONObject();

        // 获取当前时间戳
        long timeMillis = System.currentTimeMillis();
        // 初始化 appSign 变量
        String appSign = null;

        // 尝试生成应用签名
        try {
            appSign = HashTools.getAppSign(APP_KEY, APP_SECRET, timeMillis);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("密钥生成失败" + e.getMessage());
        }

        // 将参数放入 JSON 对象中
        json.put("app_key",APP_KEY);
        json.put("sign",appSign);
        json.put("timestamp",timeMillis);
        json.put("callback_open",1);
        json.put("call_back_url","http://xxx/xxx");
        json.put("start_page",1);
        json.put("end_page",2);
        // 可选择使用文件URL代替本地文件，但两者不能同时使用
        //json.put("file_url","https://web.ocrmath.com/uploads/ocrmath/pdf/2024-11-13/8cba011947251b832688135d94ded8f4.pdf");

        // 构建 JSON 数据
        String optionsJson = json.toJSONString();

        // 构建 MultipartBody
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"pdf-ces.pdf\""),
                        RequestBody.create(MediaType.parse("image/jpeg"), file))
                .addPart(Headers.of("Content-Disposition", "form-data; name=\"options_json\""),
                        RequestBody.create(optionsJson, MediaType.parse("application/json")))
                .build();

        // 构建请求
        Request request = new Request.Builder()
                .url(URL)
                .post(requestBody)
                .header("Accept", "application/json")
                .build();

        // 发送请求
        try (Response response = client.newCall(request).execute()) {
            int statusCode = response.code();
            // 检查响应是否成功
            if (response.isSuccessful()) {
                if (response.body() != null){
                    // 解析响应体中的 JSON 数据
                    JSONObject result = JSON.parseObject(response.body().string());
                    Integer errno = result.getInteger("errno");
                    if (0 == errno){
                        // 提取任务 ID
                        JSONObject data = result.getJSONObject("data");
                        String taskId = data.getString("task_id");
                        System.out.println("任务id：" + taskId);
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
            // 输出 IO 异常信息
            System.out.println("请求失败，错误信息: " + e.getMessage());
        }
    }
}
