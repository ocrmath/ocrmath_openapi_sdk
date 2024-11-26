<?php

// app_key、app_secret,从 https://web.ocrmath.com/OpenApi 的开放接口页面获取
define('APP_KEY', 'xxx');
define('APP_SECRET', 'xxx');

// image识别接口 若您 使用的是 pdf 接口请切换成 pdf API:https://openapi.ocrmath.com/v1/pdf
define('URL', 'https://openapi.ocrmath.com/v1/text');

// 创建文件资源
$file_path = '../ces.jpg';

// 初始化 JSON 对象，用于存储请求参数
$options = [
    'app_key' => APP_KEY,
    'timestamp' => time() * 1000,
    'callback_open' => 1,
    // 'start_page' => 1, 若您使用 pdf API 则需传递此参数
    // 'end_page' => 1, 若您使用 pdf API 则需传递此参数
    'call_back_url' => 'http://xxx/xxx',
];

// 获取当前时间戳
$timeMillis = $options['timestamp'];

// 计算应用签名
$appSign = md5(APP_KEY . APP_SECRET . $timeMillis);

// 将应用密钥、签名、时间戳等参数放入 JSON 对象中
$options['sign'] = $appSign;

// 构建 JSON 数据字符串
$optionsJson = json_encode($options);

// 初始化 cURL 会话
$ch = curl_init();

// 设置 cURL 选项
curl_setopt($ch, CURLOPT_URL, URL);
curl_setopt($ch, CURLOPT_POST, true);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_HTTPHEADER, [
    'Accept: application/json',
]);

// 构建 multipart/form-data 请求体
$postFields = [
    'file' => new CURLFile($file_path, 'image/jpeg', 'ces.jpg'),
    'options_json' => $optionsJson,
];

curl_setopt($ch, CURLOPT_POSTFIELDS, $postFields);

// 发送请求并处理响应
$response = curl_exec($ch);

if ($response === false) {
    echo "请求失败，错误信息: " . curl_error($ch);
} else {
    // 解析响应 JSON 数据
    $result = json_decode($response, true);

    if (isset($result['errno']) && $result['errno'] == 0) {
        // 提取并打印任务 ID
        $taskId = $result['data']['task_id'];
        echo "任务id：$taskId";
    } else {
        // 打印错误码和错误信息
        echo "错误码: " . $result['errno'];
        echo "错误信息: " . $result['msg'];
    }
}

// 关闭 cURL 会话
curl_close($ch);
?>