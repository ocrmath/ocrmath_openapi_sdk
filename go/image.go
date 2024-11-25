package main

import (
	"bytes"
	"crypto/md5"
	"encoding/hex"
	"encoding/json"
	"fmt"
	"io"
	"io/ioutil" // 使用 ioutil 读取响应体
	"net/http"
	"time"
	"mime/multipart"
)

const (
	APP_KEY      = "xxx"
	APP_SECRET   = "xxx"
	URL          = "https://web.ocrmath.com/v1/text"
)

func getAppSign(appKey, appSecret string, timestamp int64) string {
	data := []byte(appKey + appSecret + fmt.Sprintf("%d", timestamp))
	hash := md5.New()
	hash.Write(data)
	return hex.EncodeToString(hash.Sum(nil))
}

func main() {
	// 创建 HTTP 客户端，配置连接和读取超时时间
	client := &http.Client{
		Timeout: 20 * time.Second,
	}

	// 获取当前时间戳
	timeMillis := time.Now().UnixNano() / 1000000

	// 计算应用签名
	appSign := getAppSign(APP_KEY, APP_SECRET, timeMillis)

	// 初始化 JSON 对象，用于存储请求参数
	jsonData := map[string]interface{}{
		"app_key":       APP_KEY,
		"sign":          appSign,
		"timestamp":     timeMillis,
		"callback_open": 1,
		"call_back_url": "http://xxx/xxx",
		"file_url":      "https://web.ocrmath.com/uploads/ocrmath/png/2024-11-22/39b83cab9fcce4c13c0ee024da4860ed.png",
	}

	// 构建 JSON 数据字符串
	jsonBytes, _ := json.Marshal(jsonData)
	jsonStr := string(jsonBytes)

	// 构建 multipart/form-data 请求体
	body := &bytes.Buffer{}
	writer := multipart.NewWriter(body)
	part, _ := writer.CreateFormField("options_json")
	io.Copy(part, bytes.NewBufferString(jsonStr))
	writer.Close()

	// 构建请求对象，设置请求 URL 和请求体，并指定接受 JSON 格式的响应
	req, _ := http.NewRequest("POST", URL, body)
	req.Header.Set("Content-Type", writer.FormDataContentType())
	req.Header.Set("Accept", "application/json")

	// 发送请求并处理响应
	resp, err := client.Do(req)
	if err != nil {
		fmt.Println("请求失败，错误信息:", err)
		return
	}
	defer resp.Body.Close()

	// 检查响应是否成功
	if resp.StatusCode == http.StatusOK {
		// 读取响应体
		respBody, _ := ioutil.ReadAll(resp.Body)
		var result map[string]interface{}
		json.Unmarshal(respBody, &result)

		errno, _ := result["errno"].(float64)
		if errno == 0 {
			// 提取并打印任务 ID
			data, _ := result["data"].(map[string]interface{})
			taskId, _ := data["task_id"].(string)
			fmt.Println("任务id:", taskId)
		} else {
			// 打印错误码和错误信息
			msg, _ := result["msg"].(string)
			fmt.Println("错误码:", int(errno))
			fmt.Println("错误信息:", msg)
		}
	} else {
		// 打印请求失败的状态码
		fmt.Println("请求失败，状态码:", resp.StatusCode)
	}
}