# Rin Android

[English](README.en.md)

Rin Android 是一个基于 Jetpack Compose 的 [Rin](https://github.com/openRin/Rin) 博客平台 Android 客户端，主要面向移动端写作、文章管理和图片上传。

## 功能特性

- 连接自部署 Rin 博客实例
- 密码登录，并使用加密本地存储保存登录令牌
- 浏览已发布、草稿和未列出的文章
- 编写和更新 Markdown 文章
- 上传图片并自动插入 Markdown 图片链接
- 使用 Room 保存本地草稿
- 搜索文章
- 管理个人资料
- 支持 Rin 服务端存储和外部图床两种上传模式
- 支持中文和英文界面资源

## 兼容项目

- 博客后端/前端：[openRin/Rin](https://github.com/openRin/Rin)
- 外部图床：[0-RTT/telegraph](https://github.com/0-RTT/telegraph)

外部图床信息需要在 App 内自行配置。本仓库不会内置上传地址、用户名、密码或其他个人配置。

## 技术栈

- Kotlin
- Jetpack Compose
- Material 3
- Hilt
- Retrofit + OkHttp
- kotlinx.serialization
- Room
- EncryptedSharedPreferences
- Coil
- CommonMark

## 环境要求

- Android Studio，JDK 17 或更高版本
- Android SDK 36
- 仓库内已包含 Gradle Wrapper
- 最低 Android SDK：26

## 构建

```powershell
.\gradlew.bat assembleDebug
```

生成的 debug APK 位于：

```text
app/build/outputs/apk/debug/app-debug.apk
```

## 使用方法

1. 启动 App。
2. 输入 Rin 博客基础地址。
3. 使用 Rin 账号登录。
4. 可选：进入“我的”页面配置外部图床。

对于兼容 Telegraph 的图床，请填写你自己部署服务提供的上传接口和账号信息。App 会使用 multipart form data 上传文件，字段名为 `file`，并期望接口返回如下 JSON：

```json
{
  "data": "https://example.com/path/to/image.jpg"
}
```

## 隐私说明

本仓库不包含个人博客地址、图床地址、用户名、密码、API Key、签名密钥或本机 Android SDK 路径。

运行时的登录令牌和图床配置会保存在设备本地的加密偏好设置中。

## 许可证

本项目基于 MIT License 开源，详见 [LICENSE](LICENSE)。
