package main

import (
	"bytes"
	"crypto/md5"
	"encoding/json"
	"fmt"
	"io"
	"mime/multipart"
	"net/http"
	"os"
	"time"
	"encoding/hex"
)

// 定义常量
const (
	APP_KEY    = "xxxx"
	APP_SECRET = "xxxx"
	URL        = "https://openapi.ocrmath.com/v1/text" // 若您 使用的是 pdf 接口请切换成 pdf API:https://openapi.ocrmath.com/v1/pdf
)

// 获取应用签名
func getAppSign(appKey, appSecret string, timestamp int64) (string, error) {
	// 构造待签名字符串
	data := fmt.Sprintf("%s%s%d", appKey, appSecret, timestamp)
	// 使用 MD5 计算签名
	hash := md5.New()
	hash.Write([]byte(data))
	signature := hex.EncodeToString(hash.Sum(nil))
	return signature, nil
}

func main() {
	// 设置请求超时
	client := &http.Client{
		Timeout: 20 * time.Second,
	}

	// 创建文件资源
	file, err := os.Open("../ces.jpg")
	// file, err := os.Open("../ces.pdf")
	if err != nil {
		fmt.Println("打开文件失败:", err)
		return
	}
	defer file.Close()

	// 获取当前时间戳
	timestamp := time.Now().Unix()

	// 计算应用签名
	appSign, err := getAppSign(APP_KEY, APP_SECRET, timestamp)
	if err != nil {
		fmt.Println("计算签名失败:", err)
		return
	}

	// 准备请求参数
	options := map[string]interface{}{
		"app_key":     APP_KEY,
		"sign":        appSign,
		"timestamp":   timestamp,
		"callback_open": 1,
		"call_back_url": "http://xxx/xxx",
		// "start_page": 1, 若您使用 pdf API 则需传递此参数
		// "end_page": 1, 若您使用 pdf API 则需传递此参数
	}

	// 转换请求参数为 JSON
	optionsJson, err := json.Marshal(options)
	if err != nil {
		fmt.Println("构建 JSON 失败:", err)
		return
	}

	// 创建 Multipart 请求
	var requestBody bytes.Buffer
	writer := multipart.NewWriter(&requestBody)

	// 添加文件字段
	filePart, err := writer.CreateFormFile("file", "ces.jpg")
	// filePart, err := writer.CreateFormFile("file", "ces.pdf")
	if err != nil {
		fmt.Println("添加文件字段失败:", err)
		return
	}
	_, err = io.Copy(filePart, file)
	if err != nil {
		fmt.Println("文件复制失败:", err)
		return
	}

	// 添加 options_json 字段
	err = writer.WriteField("options_json", string(optionsJson))
	if err != nil {
		fmt.Println("添加 options_json 字段失败:", err)
		return
	}

	// 关闭 multipart writer
	err = writer.Close()
	if err != nil {
		fmt.Println("关闭 multipart 写入器失败:", err)
		return
	}

	// 创建 POST 请求
	req, err := http.NewRequest("POST", URL, &requestBody)
	if err != nil {
		fmt.Println("创建请求失败:", err)
		return
	}

	// 设置请求头
	req.Header.Add("Content-Type", writer.FormDataContentType())
	req.Header.Add("Accept", "application/json")

	// 发送请求
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("请求失败:", err)
		return
	}
	defer resp.Body.Close()

	// 读取响应
	respBody, err := io.ReadAll(resp.Body)
	if err != nil {
		fmt.Println("读取响应失败:", err)
		return
	}

	// 解析响应 JSON
	var result map[string]interface{}
	err = json.Unmarshal(respBody, &result)
	if err != nil {
		fmt.Println("解析响应失败:", err)
		return
	}

	// 检查 errno 字段
	errno := result["errno"].(float64)
	if errno == 0 {
		// 成功，输出任务 ID
		data := result["data"].(map[string]interface{})
		taskId := data["task_id"].(string)
		fmt.Println("任务id:", taskId)
	} else {
		// 失败，输出错误信息
		msg := result["msg"].(string)
		fmt.Println("错误信息:", msg)
	}
}
