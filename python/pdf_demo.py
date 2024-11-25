import hashlib
import time
import requests
import json

# app_key 和 app_secret，从 https://web.ocrmath.com/OpenApi 的开放接口页面获取
APP_KEY = "xxx"
APP_SECRET = "xxx"

# image 识别接口
URL = "https://web.ocrmath.com/v1/pdf"

def get_app_sign(app_key, app_secret, timestamp):
    """
    生成签名
    :param app_key: 应用的 APP_KEY
    :param app_secret: 应用的 APP_SECRET
    :param timestamp: 当前时间戳
    :return: 计算后的签名字符串
    """
    try:
        # 拼接字符串并计算 SHA-256 哈希
        raw_string = f"{app_key}{app_secret}{timestamp}"
        a = hashlib.md5(raw_string.encode('utf-8')).hexdigest()
        print('sign=' + a)
        return a
    except Exception as e:
        print(f"密钥生成失败: {str(e)}")
        return None

def main():
    # 获取当前时间戳
    timestamp = int(time.time() * 1000)
    # 计算签名
    app_sign = get_app_sign(APP_KEY, APP_SECRET, timestamp)
    if not app_sign:
        return

    # 本地文件路径,请替换成您的图片真实位置
    file_path = "/python/ces.pdf"

    # 构建 JSON 参数
    options_json = {
        "app_key": APP_KEY,
        "sign": app_sign,
        "timestamp": timestamp,
        "callback_open": 1,
        "start_page":1,
        "end_page":2,
        "call_back_url": "http://xxx/xxx" # 请替换成您的 回调地址
        # 如果使用 URL 文件，可启用以下行,替换成您的 pdf 链接
        # "file_url": "https://web.ocrmath.com/uploads/ocrmath/pdf/2024-11-22/39b83cab9fcce4c13c0ee024da4860ed.pdf" 
    }

    # 构建请求的多部分数据
    files = {
        "file": ("ces.pdf", open(file_path, "rb"), "application/pdf"),
        "options_json": (None, json.dumps(options_json), "application/json")
    }

    try:
        # 发送 POST 请求
        response = requests.post(URL, files=files, headers={"Accept": "application/json"}, timeout=(8, 20))
        # 检查响应状态码
        if response.status_code == 200:
            # 解析响应数据
            result = response.json()
            errno = result.get("errno")
            if errno == 0:
                data = result.get("data", {})
                task_id = data.get("task_id")
                print(f"任务ID: {task_id}")
            else:
                print(f"错误码: {errno}")
                print(f"错误信息: {result.get('msg')}")
        else:
            print(f"请求失败，状态码: {response.status_code}")
    except requests.RequestException as e:
        print(f"请求失败，错误信息: {str(e)}")

if __name__ == "__main__":
    main()
