# 个人资产管理系统 · Android App 设计文档

## 概述

将 Java Swing 桌面版"个人资产管理系统"移植为 Android 原生 App。全本地架构，无需服务端。

## 技术栈

| 层 | 选型 |
|---|---|
| 语言 | Kotlin |
| UI | Jetpack Compose + Material 3 |
| 数据库 | Room (SQLite) |
| 图片存储 | App-specific 本地文件系统 |
| 备份 | JSON 导出到 Downloads，自动周备份（本地） |
| 生物识别 | BiometricPrompt |
| 异步 | Kotlin Coroutines + Flow |
| 导航 | Compose Navigation + BottomNavigation |
| DI | Hilt |
| 最低版本 | Android 12 (API 31) |

## 架构

```
UI (Compose) → ViewModel → Repository → Room DAO → SQLite
```

单 Activity + Compose Navigation + 4 个底部标签。

## 导航

4 标签底部导航：

| 标签 | 功能 |
|---|---|
| 首页 | 总资产价值卡片、状态统计、7 日到期提醒、快捷添加入口 |
| 资产 | 资产列表 + 搜索过滤、CRUD、拍照存证、出售/丢弃、FAB 借出/归还 |
| 统计 | 分类价值图、月度支出、折旧估算、资产分布 |
| 我的 | 联系人管理、借还历史、操作日志、数据导出备份、生物识别锁 |

## 数据模型 (Room Entities)

- **Asset**: name, brand, model, categoryId, price, purchaseDate, purchaseChannel, status, locationId, specs, serialNumber, notes, isVirtual, expiryDate, photoPath (新增)
- **Category**: name, icon（10 个默认分类）
- **Location**: name（6 个默认位置）
- **Person**: name, relationship, phone, wechat, notes
- **LendRecord**: assetId, personId, lendDate, expectedReturnDate, actualReturnDate, status
- **AssetLog**: assetId, operation, detail, timestamp

## 功能范围

### 保留（从桌面版）
- 资产 CRUD + 多条件搜索过滤
- 借还管理（借出、归还、历史）
- 联系人管理（CRUD + 手机号验证）
- 资产处置（出售记录售价、丢弃）
- 统计仪表盘（分类价值、月度支出）
- 折旧计算（三种策略：通用、电子、收藏品）
- 到期提醒（7 天内）
- 操作日志

### 去除
- 用户登录/注册（单机 App 无意义）

### 新增
- 拍照存证：每件资产可拍照保存
- 生物识别锁：指纹/面部验证进入 App
- 本地数据导出：JSON 文件 + 自动周备份（不上传云端）

### 优先级

- **P0 核心**: Asset CRUD、种子数据、Room 初始化
- **P1 必备**: 借还管理、联系人、统计、折旧、到期提醒、生物识别锁
- **P2 增强**: 拍照存证、导出备份、操作日志、暗色主题
- **P3 锦上添花**: Widget、搜索过滤 UI、动画

## 视觉设计

- 风格：HyperOS/小米原生，Material 3 定制
- 主题：跟随系统浅色/深色自动切换
- 主色调：小米橙 `#FF6900`
- 卡片：大圆角、轻阴影、暖白底色
