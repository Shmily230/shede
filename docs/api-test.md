# 舍得 B2C 平台 · 接口测试文档（Postman）

> 覆盖完整购买闭环：浏览商品 → 查看详情 → 注册/登录 → 下单 → 支付 → 取消恢复库存
>
> 服务地址：`http://localhost:8081`（`application.properties` 中 `server.port`）

---

## 1. 环境准备

| 依赖 | 启动方式 | 校验 |
|---|---|---|
| MySQL 8.0 | 系统服务，库 `shede_db` | `select count(*) from product;` 有 1 条种子数据 |
| Redis | 双击 `D:\deepseek工作区\tools\redis\start-redis.bat` | 窗口出现 `Ready to accept connections` |
| 应用 | IDEA 运行 `Application` | 控制台出现 `Tomcat started on port(s): 8081` |

Postman → `Environments` 新建环境，添加变量：

```json
{
  "baseUrl": "http://localhost:8081",
  "userId": "",
  "productId": "P20260001",
  "productDetailId": "PD20260001",
  "orderId": ""
}
```

统一响应结构：

```json
{ "success": true, "code": "200", "message": "success", "data": {}, "timestamp": 1793426000000 }
```

| code | 含义 |
|---|---|
| 200 | 成功 |
| 4001 | 参数不合法 / 库存不足 |
| 4010 | 未登录 / 账号密码错误 |
| 4040 | 资源不存在 |
| 4090 | 冲突（如用户名已存在） |
| 500 | 系统异常（详情看服务端日志） |

---

## 2. 完整购买流程（按顺序执行）

### 步骤 1 · 浏览商品列表

```
GET {{baseUrl}}/api/product/list?pageNum=1&pageSize=10
```
可选参数：`keyword`（模糊搜索）、`category`、`brand`、`status`（1 上架 / 0 下架）

预期：`data.list` 返回商品数组，`data.paging` 返回 `pageNum/pageSize/total/totalPage`。

```json
{
  "success": true,
  "data": {
    "list": [{ "productId": "P20260001", "name": "Shede Classic Sneaker", "price": 399.00, "stock": 100 }],
    "paging": { "pageNum": 1, "pageSize": 10, "total": 1, "totalPage": 1 }
  }
}
```

---

### 步骤 2 · 查看商品详情（选择尺码）

```
GET {{baseUrl}}/api/product/detail?productId={{productId}}
```

预期：`data.productDetails` 是 SKU 数组，记下要购买的 `productDetailId`（如 `PD20260001`，尺码 42，库存 50）。

```json
{
  "success": true,
  "data": {
    "productId": "P20260001",
    "stock": 100,
    "productDetails": [
      { "productDetailId": "PD20260001", "size": "42", "color": "White", "stock": 50 }
    ]
  }
}
```

---

### 步骤 3 · 用户注册

```
POST {{baseUrl}}/api/user/register
Content-Type: application/json
```

```json
{
  "username": "shede_tester",
  "password": "123456",
  "nickname": "舍得测试",
  "phone": "13800000000",
  "email": "test@shede.com"
}
```

预期：`data.userId` 为 32 位字符串，把返回值填进 Postman 变量 `userId`。

```json
{ "success": true, "message": "注册成功", "data": { "userId": "57826b9ae9df4e148921992dd505b2a2", "username": "shede_tester" } }
```

异常用例：重复提交同一 `username` → `code=4090`，`message=用户名已存在`。

---

### 步骤 4 · 用户登录

```
POST {{baseUrl}}/api/user/login
Content-Type: application/json
```

```json
{ "username": "shede_tester", "password": "123456" }
```

预期：`success=true`，`data.userId` 可用于下单。

异常用例：密码改成 `wrong` → `code=4010`，`message=用户名或密码错误`（账号不存在返回同样文案，避免账号枚举）。

---

### 步骤 5 · 下单

```
POST {{baseUrl}}/api/order/create
Content-Type: application/json
```

```json
{
  "userId": "{{userId}}",
  "productId": "{{productId}}",
  "productDetailId": "{{productDetailId}}",
  "quantity": 1
}
```

预期：`data.orderId` 形如 `20261031133606000006`（14 位时间戳 + 6 位 Redis 序号），`status=0`，`statusName=待付款`。把 `orderId` 存进变量。

```json
{
  "success": true,
  "message": "下单成功",
  "data": { "orderId": "20261031133606000006", "status": 0, "statusName": "待付款", "totalAmount": 399.00 }
}
```

此时数据库变化：

```sql
select stock from product where product_id = 'P20260001';         -- 100 -> 99
select stock from product_detail where product_detail_id = 'PD20260001'; -- 50 -> 49
select order_id, status from `order` order by id desc limit 1;    -- status = 0
```

异常用例：

| 场景 | 请求 | 预期 |
|---|---|---|
| 库存不足 | `quantity: 99999` | `4001 库存不足，无法下单`，库存不变 |
| 规格不存在 | `productDetailId: "NOT_EXIST"` | `4040 商品规格不存在` |
| 缺用户 | 去掉 `userId` | `4001 用户不能为空` |

---

### 步骤 6 · 查询订单

```
GET {{baseUrl}}/api/order/{{orderId}}
GET {{baseUrl}}/api/order/list?userId={{userId}}&status=0
```

预期：能看到刚创建的订单；`status=0` 过滤待付款订单。

---

### 步骤 7 · 发起支付

```
POST {{baseUrl}}/api/pay/alipay
Content-Type: application/json
```

```json
{ "orderId": "{{orderId}}", "userId": "{{userId}}" }
```

预期：`data.form` 是支付宝表单 HTML（`<form ... id="punchout_form">`），把该 HTML 写到空页面或用浏览器打开即可跳转支付宝。

```json
{
  "success": true,
  "message": "请提交表单完成支付",
  "data": { "paymentId": "a1b2c3...", "orderId": "20261031133606000006", "payAmount": 399.00, "form": "<form ...>...</form>" }
}
```

> 前置条件：`application.properties` 里 `alipay.app-id`、`alipay.private-key`、`alipay.alipay-public-key` 必须填真实沙箱值，否则返回 `4001 发起支付失败：...`。
> 同时数据库会新增一条 `payment_record`，`pay_status = 0`（PENDING）。

---

### 步骤 8 · 支付宝异步回调（本地模拟）

真实回调由支付宝服务器发出，需要**公网可访问**的 `notify_url`。本地可用 Postman 直接打本地接口验证处理逻辑（参数缺失或签名错误时会返回 `failure`）：

```
POST {{baseUrl}}/alipaycallback
Content-Type: application/x-www-form-urlencoded
```

```
out_trade_no={{paymentId}}&trade_no=2026103122001400000001&trade_status=TRADE_SUCCESS&total_amount=399.00&app_id=2021000000000000&charset=utf-8&sign=xxx&sign_type=RSA2
```

三种返回：

| 返回 | 含义 |
|---|---|
| `success` | 验签通过且已处理（或该笔已处理过，幂等） |
| `failure` | 验签失败 / 金额不符 / 流水不存在 |
| `failure` | 处理异常（服务端有 ERROR 日志） |

幂等验证：**同一个已成功的回调连发两次**，第二次仍返回 `success`，但 `payment_record.gmt_modified`、`order.status` 不再变化：

```sql
select pay_status, trade_no, pay_time from payment_record order by id desc limit 1;
select order_id, status, pay_time from `order` where order_id = '20261031133606000006';
```

同步跳转页（仅展示，非支付依据）：

```
GET {{baseUrl}}/alipayreturnurl?out_trade_no=xxx
```

---

### 步骤 9 · 取消订单 / 支付失败（恢复库存）

```
POST {{baseUrl}}/api/pay/close?orderId={{orderId}}
```

预期：`success=true`，订单 `status=2`（已关闭），库存回到下单前；该订单下 PENDING 的支付流水置为 `pay_status=2`（失败）。

```sql
select stock from product where product_id = 'P20260001';         -- 99 -> 100
select stock from product_detail where product_detail_id = 'PD20260001'; -- 49 -> 50
select order_id, status from `order` where order_id = '20261031133606000006'; -- status = 2
```

保护性用例：

| 场景 | 预期 |
|---|---|
| 已支付订单再调 close | `success=true` 但状态保持 `1`，库存**不**增加 |
| 同一订单连调两次 close | 库存只恢复一次 |
| `orderId` 不存在 | `4040 订单不存在` |

---

## 3. Postman Tests 断言示例

在 `下单` 请求的 **Tests** 标签粘贴：

```javascript
pm.test("HTTP 200", () => pm.response.to.have.status(200));

pm.test("业务成功", () => {
    const json = pm.response.json();
    pm.expect(json.success).to.be.true;
    pm.expect(json.code).to.eql("200");
    pm.environment.set("orderId", json.data.orderId);   // 自动写入环境变量
});
```

在 `登录` 请求里自动保存 userId：

```javascript
const json = pm.response.json();
if (json.success) pm.environment.set("userId", json.data.userId);
```

---

## 4. 常见问题

| 现象 | 原因 | 处理 |
|---|---|---|
| `404` | 访问路径拼错 / 应用未重启到最新代码 | 重启 `Application`，`Build → Rebuild Project` |
| `4001 请求参数不合法` | JSON 字段名拼错或漏字段 | 对照本文档请求体 |
| `500 系统繁忙` | 服务端异常 | 看控制台 ERROR 日志（`GlobalExceptionHandler` 已记录堆栈） |
| 启动报 `Port 8081 was already in use` | 旧进程没退 | 结束旧进程或改 `server.port` |
| 回调一直 `failure` | 未配置真实支付宝公钥 | 填 `alipay.alipay-public-key` 后再测 |
| 中文乱码 | Postman 未按 UTF-8 解码 | `Response` 右上角选 `Pretty → JSON`，编码 UTF-8 |
