# 1. 介绍

 超级公式是一个致力于提供全面的AI公式识别的在线平台，专注于利用深度学习技术解决公式识别和文档识别问题，不仅支持50＋各国语言，支持所有学科的公式识别、多端设备识别，还实现了十余种导出格式。无论是学生、教师还是学术爱好者，这里都是你探索奥秘的最佳去处。

超级公式支持各种复杂公式的多格式识别及输出，免费Latex在线公式编辑器，为您提供简单明了的使用方法、完善的公式解决方案以及高精度AI识别。此外，超级公式网站拥有自己的核心技术，在公式检测 、公式识别 、文档版面分析 、OCR识别 、试卷切题等方面都有自己的深度学习模型，助力用户深入理解其背后的原理，提升学习效率。

本文介绍了超级公式开放 API 请求和响应。 如果您有任何疑问或问题，请发送电子邮件至 ocrmath@163.com。 



# 2. 图片识别

## 2.1 接口说明

**HTTPS 地址：**

```json
https://openapi.ocrmath.com/v1/text
```

该 API 为 【POST】 请求，参数有两个，通过 `multipart/form-data`  传递，详情请看下面说明。
您传递图片的方式两种：二进制流传递或提供公网可访问的图片链接。

## 2.2 参数说明

| 参数名       | 说明                                                         | 必选                        | 类型   |
| ------------ | ------------------------------------------------------------ | --------------------------- | ------ |
| file         | 此参数为图片文件的二进制流。此次参数和 options_json 中的 file_url 二选一。 | 否                          | binary |
| options_json | 选项参数，是 string 类型的 json，里面包含一系列参数，详情请看 2.3 ，该表格一一列举该参数的具体取值。需要进行 URL encode。 | 是 | string |

## 2.3 options_json 参数说明

示例数据

```json
{
    "app_key": "6e11a5278d0eae62822bc274009a8261",
    "sign": "f6eaa67d061e5799985d1faf3a0d7262",
    "timestamp": 1731992850234,
    "callback_open": 1,
    "callback_url": "https://www.ocrmath.com/ocrmath/notify"
}
```



| 参数名  | 说明        | 必选 | 类型 |
| ------- | ----------- | -------- | -------- |
| app_key | 您的 app_key，从超级公式获取到您的 app_key | 是 | string |
| timestamp | 当前时间戳，精确到毫秒 | 是 | string |
| sign | 将以下三个字段按顺序拼接后做 md5 摘要（要求 32 位、小写）：<br />app_key + app_secrect + timestamp |是|string|
| callback_open | 是否开启回调，取值： 0 不开启 ，1开启，当您未传递此参数，则默认不开启回调。 |否|integer|
| callback_url | 回调地址，公网可访问的 POST 请求地址<br />该接口用于客户端接收超级公式的回调信息，详情请查看 `结果通知`<br />当您未传递此参数，则默认不开启回调。 |否|string|
| file_url | 图片文件链接，此链接必须是公网可访问。<br />(当通过上传方式传递图片时此参数无需传递)此参数和 2.2 中的 file 参数 二选一，换句话说，我们提供了两种上传图片的方式供您选择。 |否|string|



## 2.4 响应结果

### 2.4.1 正常情况

```json
{
    "errno": 0,
    "data": {
        "task_id": "8ac2823ef44446ab9ab5388b4dc52b32"
    },
    "msg": "success"
}
```

**响应结果说明：**

| 字段  | 说明     | 必选                        | 类型    |
| :---- | :------- | --------------------------- | ------- |
| errno | 响应码   | 是 | integer |
| msg   | 响应信息 | 是 | string  |
| data  | 响应数据 | 是 | object  |

**data 字段详解**

| 字段    | 说明                                          | 必选                        | 类型   |
| ------- | --------------------------------------------- | --------------------------- | ------ |
| task_id | 任务 id，客户端可根据此字段，对此次识别结果进行查询 | 是 | string |

### 2.4.2 异常状态

```json
{
    "errno":-800,
    "msg":"Package expiration"
}
```

请查看 **响应码** 章节进行处理

# 3. pdf 识别

## 3.1 接口说明

**HTTPS 地址：**

```json
https://openapi.ocrmath.com/v1/pdf
```

该 API 为 【POST】 请求，参数有两个，通过 `multipart/form-data`  传递，详情请看下面说明。
您传递 pdf 的方式两种：二进制流传递或提供公网可访问的图片链接。

## 3.2 参数说明

| 参数名       | 说明                                                         | 必选                        | 类型   |
| ------------ | ------------------------------------------------------------ | --------------------------- | ------ |
| file         | 此参数为图片文件的二进制流，此次参数和 options_json 中的 file_url 二选一。 | 否                          | binary |
| options_json | 选项参数，是 string 类型的 json，里面包含一系列参数，详情请看 3.3 ，该表格一一列举该参数的具体取值。需要进行 URL encode。 | 是 | string |

## 3.3 options_json 参数说明

参数示例

```json
{
    "app_key": "6e11b5278ed8a362822bc27d1009a8260",
    "sign": "f6eaa67d061e5799985desd33a0d7262",
    "timestamp": 1731992850234,
    "callback_open": 1,
    "callback_url": "https://www.ocrmath.com/ocrmath/notify",
    "start_page": 1,
    "end_page": 1
}
```



| 参数名        | 说明                                                         | 必选                        | 类型    |
| ------------- | ------------------------------------------------------------ | --------------------------- | ------- |
| app_key       | 您的 app_key，从超级公式获取到您的 app_key                   | 是 | string  |
| timestamp     | 当前时间戳，精确到毫秒                                       | 是 | string  |
| sign          | 将以下三个字段按顺序拼接后做 md5 摘要（要求 32 位、小写）：<br />app_key + app_secrect + timestamp | 是 | string  |
| callback_open | 是否开启回调，取值： 0 不开启 ，1开启，当您未传递此参数，则默认不开启回调。 | 否                          | integer |
| callback_url  | 回调地址，公网可访问的 POST 请求地址<br />该接口用于客户端接收超级公式的回调信息，详情请查看 `结果通知`<br />当您未传递此参数，则默认不开启回调。 | 否                          | string  |
| file_url      | pdf 文件链接，此链接必须是公网可访问。<br />(当通过上传方式传递 pdf 时此参数无需传递)此参数和 3.2 中的 file 参数 二选一，换句话说，我们提供了两种上传 pdf 的方式供您选择。 | 否                          | string  |
| start_page    | 要识别的 pdf 开始页，<font color="#B22222">注意：</font>开始页是在 pdf 的第几页，不是页码 | 是 | integer |
| end_page      | 要识别的 pdf 结束页，<font color='#B22222'>注意：</font>结束页是在 pdf 的第几页，不是页码 | 是 | integer |

## 3.4 响应结果

### 3.4.1 正常情况

```json
{
    "errno": 0,
    "data": {
        "task_id": "8ac2823ef44446ab9ab5388b4dc52b32"
    },
    "msg": "success"
}
```

**响应结果说明：**

| 字段  | 说明     | 必选                        | 类型    |
| :---- | :------- | --------------------------- | ------- |
| errno | 响应码   | 是 | integer |
| msg   | 响应信息 | 是 | string  |
| data  | 响应数据 | 是 | object  |

**data 字段详解**

| 字段    | 说明                                          | 必选                        | 类型   |
| ------- | --------------------------------------------- | --------------------------- | ------ |
| task_id | 任务 id，客户端可根据此字段，对此次识别结果进行查询 | 是 | string |

### 2.4.2 异常情况

```json
{
    "errno":-800,
    "msg":"Package expiration"
}
```

详情，请查看 **响应码** 章节。

# 4 结果通知

当客户端调用接口 `/v1/text` 或 `/v1/pdf` 将表单数据 `options_json` 中的 `callback_open` 参数设置为 1， 设置了`callback_url`，超级公式则通过您填写的`callback_url`将 图片 或 pdf 识别结果返回给开发者。

>**注意**
>
>对后台通知交互时，如果超级公式收到应答不是成功或超时，超级公式认为通知失败，超级公式会通过重新发起通知，尽可能提高通知的成功率，但超级公式不保证通知最终能成功。
>
>- 同样的通知可能会多次发送给客户端。所以您必须能够正确地处理收到的重复通知。
>- 对后台通知交互时，如果超级公式收到客户端的应答不符合规范或超时，超级公式认为通知失败，超级公式会通过一定的策略定期重新发起通知，尽可能提高通知的成功率，但超级公式不保证通知最终能成功。 （通知频率为15s/15s/30s/3m/10m/20m/30m/30m/30m/60m/3h/3h/3h/6h/6h - 总计 24h4m）  

## 4.1 接口规范

开发者需提供一个 HTTP 请求，方式 【POST】的回调 URL，该 URL 是通过客户端调用`/v1/text`或者`/v1/pdf`识别接口时传递的表单参数： `options_json` 中的 `callback_url` 设置，要求回调 URL 必须是外部可正常访问的，且不携带任何后缀参数，否则客户端可能无法收到超级公式的回调通知信息。回调 URL 示例：

```json
https://www.ocrmath.com/ocrmath/notify
```

## 4.2 通知报文

识别结果通知是以`POST `方法访问开发者设置的通知`URL`，通知的数据以`JSON`格式通过请求主体 `BODY`传输。 

## 4.3 通知报文字段说明

```json
{
    "total_latex": "$x-1$\\n$x=2$",
    "detail_list": [
        {
            "pdf_page_num": 1,
            "latex": "$x=1$"
        },
        {
            "pdf_page_num": 1,
            "latex": "$x=2$"
        }
    ]
}
```

| 字段        | 说明                                                        | 必选 | 类型   |
| ----------- | ----------------------------------------------------------- | ---- | ------ |
| total_latex | 识别结果                                                    | 是   | string |
| detail_list | pdf 识别详情列表，当开发者调用 /v1/pdf 接口时需要关心此字段 | 是   | array  |

**detail_list 详解**

若客户端调用的 API 是： `/v1/text`，则无需关心此字段，若客户端调用的 API 是 `/v1/pdf`  detail_list 为 pdf 识别的详情信息。

| 字段         | 说明                                                         | 必选 | 类型    |
| ------------ | ------------------------------------------------------------ | ---- | ------- |
| latex        | 此页对应的识别结果                                           | 是   | string  |
| pdf_page_num | 页码，<font color="#B22222">注意：</font>此页码表示的是在 pdf 的第几页。 | 是   | integer |

## 4.4 通知应答

**接收成功：** HTTP 应答状态码为 200，BODY 应答报文，示例参数格式如下：

```
{
    "errno": 0,
    "msg": "success"
}
```

**接收失败**：HTTP 状态码为 500，同时返回报文，格式如下：

```
{
    "errno": 0,
    "msg": "fail"
}
```

| 字段  | 说明                                   | 必选 | 类型    |
| ----- | -------------------------------------- | ---- | ------- |
| errno | 响应状态码，0 为应答成功，其它为失败。 | 是   | integer |
| msg   | 返回信息                               | 否   | string  |

当通知应答数据与示例数据不同，超级公式则认为应答失败。则会进行重新推送。



# 5 识别结果获取

## 5.1 接口说明

此 API 用于客户端从超级公式获取 图片 或 pdf 的识别结果。该 API 为 【GET】请求， 参数有一个，通过 `multipart/form-data` 传递。

**HTTPS 地址**

```json
https://openapi.ocrmath.com/v1/task_result
```

> 注意
>
> 超级公式提供两种方式供开发者获取识别结果，您只需选择下面任意一种方式即可：
>
> 1. 客户端主动开启回调结果通知，由超级公式将识别结果返回给客户端。
> 2. 客户端通过超级公式提供的 HTTP 接口： /v1/task_result 进行主动查询，以获取识别结果。 



## 5.2 参数说明

| 参数名       | 说明                                                       | 必选 | 类型   |
| ------------ | ---------------------------------------------------------- | ---- | ------ |
| options_json | 选项参数，是 json 字符串，里面包含一系列参数，请看 5.3详解 | 是   | string |

## 5.3 options_json 参数说明

| 参数名    | 说明                                                         | 必选 | 类型   |
| --------- | ------------------------------------------------------------ | ---- | ------ |
| app_key   | 您的 app_key，从超级公式获取到您的 app_key                   | 是   | string |
| timestamp | 当前时间戳，精确到毫秒                                       | 是   | string |
| sign      | 将以下三个字段按顺序拼接后做 md5 摘要（要求 32 位、小写）：<br />app_key + app_secrect + timestamp | 是   | string |
| task_id   | 超级公式返回给客户端的 task_id，客户端使用 task_id 查询 识别结果。 | 是   |    string    |

## 3.4 响应结果

响应的数据为下列所示，开发者需要重点关注 data 部分的数据

```json
{
    "errno": 0,
    "msg": "success",
    "data": {
        "task_status": 1,
        "total_latex": "$x-1$",
        "detail_list": [
            {
                "pdf_page_num": 1,
                "latex": "$x=1$"
            },
            {
                "pdf_page_num": 1,
                "latex": "$x=2$"
            }
        ]
    }
}
```

### 4.4.1 正常情况

| 字段  | 说明     | 必选 | 类型    |
| ----- | -------- | ---- | ------- |
| errno | 响应码   | 是   | integer |
| msg   | 响应信息 | 是   | string  |
| data  | 响应数据 | 是   | object  |

**data 字段详解：**

| 字段        | 说明                                                         | 必选 | 类型    |
| ----------- | ------------------------------------------------------------ | ---- | ------- |
| task_status | 任务处理状态<br />0：未处理、<br />1：处理中<br />2：处理完毕 | 是   | integer |
| total_latex | 识别结果                                                     | 是   | string  |
| detail_list | pdf 识别详情列表，当开发者调用 /v1/pdf 接口时需要关心此字段  | 是   | array   |

**detail_list 详解**

若客户端调用的 API 是： `/v1/text`，则无需关心此字段，若客户端调用的 API 是 `/v1/pdf`  detail_list 为 pdf 识别的详情信息。
| 字段         | 说明                                                         | 必选 | 类型    |
| ------------ | ------------------------------------------------------------ | ---- | ------- |
| latex        | 此页对应的识别结果                                           | 是   | string  |
| pdf_page_num | 页码，<font color="#B22222">注意：</font>此页码表示的是在 pdf 的第几页。 | 是   | integer |

### 4.4.2 **异常情况**

``` json
{
    "errno":-100,
    "msg":"Invalid Sign"
}
```

具体详情，请查看 **响应码** 章节。

# 5 响应码表

| 响应码 | 响应码说明 | 解决方案 |
| ------ | -------- | -------- |
| 0      | 成功 |          |
| -100 | 参数传递不正确 | 请根据提示信息进行修改：包括但不限于，参数丢失、参数不合法... |
| -401 | 签名不正确 | 请检查签名的生成方式 |
| -400 | 必选的参数没有传递 | 请检查您参数传递 |
| -413 | 超过规定大小， pdf 50mb、图片 2mb | 检查上次的文件大小 |
| -500 | 服务器错误 | 请联系超级公式 |
| -700 | 此 app_key 的套餐不存在 |检查您传递的 app_key 是否正确|
| -701 | 图片可用次数为 0 |请充值|
| -800 | 套餐已经过期 |请充值，套餐已经过期|
| -801 | pdf 可用次数为 0 |请充值|
| -802 | pdf 可用次数超过您选择的页数 |请充值或根据提示信息减少到相应的页数|




