# 🛍️ 校园二手交易系统

# 获取方式---本文件是项目的部分文件，有需要可看【煮页】
# 企鹅：3660038549

<br>

如需部署，请按照“前台启动方式”和“后台启动方式”完成数据库导入、配置修改和项目启动。前端开发环境已经配置 Vite 代理，会将 `/api` 和 `/upload` 请求转发至本地后端服务。

🛒 **交易场景**：面向在校师生的二手物品交易，覆盖商品浏览、分类筛选、商品详情、购物车、订单结算等完整流程。

🔐 **角色权限**：系统包含管理员和普通用户两类角色，登录后根据角色进入对应的前台或后台界面。

📦 **商品流转**：用户可以浏览商品、收藏商品、加入购物车、提交订单并查看订单状态；管理员可以维护商品、分类和订单信息。

🔎 **求购与捐赠**：用户可以发布求购信息或查看捐赠信息，管理员可以在后台进行统一管理。

🖼️ **图片管理**：支持商品图片、用户头像、首页轮播图等文件上传与访问，上传文件默认保存在项目根目录的 `upload/` 目录。

📢 **内容管理**：管理员可以维护公告、系统简介、关于我们和首页轮播图等平台内容。

📊 **数据统计**：后台仪表盘提供商品数、订单数、用户数、求购信息数、捐赠信息数及订单状态统计。

#### 安装环境

Java 环境：JDK 1.8

Node.js 环境：建议 Node.js 18 或 Node.js 20

Maven 环境：建议 Maven 3.6+

MySQL 数据库：建议 MySQL 5.7 或 MySQL 8.0，请提前确认数据库账号和密码

后端开发工具：推荐使用 IntelliJ IDEA

前端开发工具：推荐使用 VS Code 或 WebStorm

浏览器：Chrome、Edge 等现代浏览器均可

#### 采用技术及功能

后端：Spring Boot 2.7.18、Spring MVC、MyBatis-Plus 3.5.4.1、MySQL Connector 8.0.33、Lombok

前端：Vue 3、Vite 7、TypeScript、Vue Router、Pinia、Element Plus、Axios、ECharts

数据库：MySQL，项目 SQL 脚本为 `campus-second.sql`

平台前端：Vue 3（前端框架）+ Vue Router（路由管理）+ Pinia（状态管理）+ Axios（请求工具）+ Element Plus（UI 组件）+ ECharts（数据图表）

平台后端：Spring Boot（核心框架）+ MyBatis-Plus（ORM）+ RESTful API（接口风格）+ MySQL（数据库）

开发环境：Windows 10/Windows 11、IntelliJ IDEA、VS Code/WebStorm、Maven、JDK 1.8、Node.js

1、实现管理员和普通用户登录、退出登录、登录状态保存及角色权限控制；

2、实现二手商品管理，包括商品名称、分类、价格、库存、图片、商品描述、交易地点和商品状态等信息维护；

3、实现商品分类管理，支持分类新增、编辑、删除和列表查询；

4、实现用户端商品浏览，支持首页展示、商品列表、分类筛选和商品详情查看；

5、实现购物车功能，支持商品加入购物车、购物车列表查看和购物车商品删除；

6、实现订单交易流程，支持购物车结算、订单提交、订单列表查询和订单状态更新；

7、实现商品收藏功能，用户可以收藏或取消收藏感兴趣的商品；

8、实现收货地址管理，支持收货地址新增、编辑、删除和默认地址设置；

9、实现求购信息和捐赠信息管理，用户可以发布相关信息，管理员可以在后台统一维护；

10、实现公告、系统简介、关于我们和首页轮播图等平台内容管理；

11、实现后台仪表盘统计，展示商品、订单、用户、求购和捐赠数据及订单状态分布；

12、实现文件上传功能，为商品图片、用户头像和轮播图提供统一的上传接口。

#### 前台启动方式

1. 进入前端目录：

```bash
cd campus-second-frontend
```

2. 安装前端依赖：

```bash
npm install
```

3. 启动前端开发服务：

```bash
npm run dev
```

4. 浏览器访问：`http://localhost:5173/`

说明：前端 Vite 配置已将 `/api` 和 `/upload` 代理到 `http://localhost:8080`，启动前端前请先启动后端服务，以保证接口和图片资源正常访问。

前端构建命令：

```bash
npm run build
```

构建完成后，可以使用以下命令预览构建结果：

```bash
npm run preview
```

#### 后台启动方式

1. 在 MySQL 中创建数据库：

```sql
CREATE DATABASE `campus-second` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 导入项目根目录下的 `campus-second.sql`：

```bash
mysql -u root -p --database=campus-second < campus-second.sql
```

也可以使用 Navicat、DataGrip 或其他 MySQL 工具导入 SQL 文件。

3. 修改 `campus-second-backend/src/main/resources/application.yml` 中的数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus-second?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 123456
```

其中 `username` 和 `password` 请替换为本机 MySQL 的实际账号和密码。项目默认服务端口为 `8080`，上传目录为项目根目录下的 `upload/`。

4. 使用 IDEA 打开 `campus-second-backend`，等待 Maven 依赖下载完成。

5. 启动 `campus-second-backend/src/main/java/com/campus/CampusSecondApplication.java`。

也可以在后端目录使用 Maven 命令启动：

```bash
cd campus-second-backend
mvn spring-boot:run
```

也可以先打包再运行：

```bash
mvn clean package -DskipTests
java -jar target/campus-second-backend-1.0.0.jar
```

后端启动成功后，前端访问 `http://localhost:5173/` 即可进入系统登录页。

#### 默认账户密码

**管理员**

账号：`admin`

密码：`admin`

**普通用户**

账号：`student01`

密码：`123456`

SQL 脚本中还提供了 `student02` 至 `student20` 等测试用户，默认密码均为 `123456`。

#### 核心模块

| 模块 | 功能说明 |
|:---|:---|
| 用户认证 | 管理员登录、普通用户登录、退出登录、登录状态保存、角色权限控制 |
| 商品管理 | 商品新增、编辑、删除、上下架、库存维护、商品图片上传、商品详情查看 |
| 商品分类 | 商品分类新增、编辑、删除和分类查询 |
| 购物车 | 加入购物车、查看购物车、删除购物车商品、购物车结算 |
| 订单管理 | 提交订单、订单列表、订单详情、订单状态更新、管理员订单维护 |
| 收藏管理 | 商品收藏、取消收藏、我的收藏列表 |
| 地址管理 | 收货地址新增、编辑、删除、默认地址设置 |
| 求购与捐赠 | 求购信息发布与维护、捐赠信息发布与维护 |
| 内容管理 | 公告管理、关于我们、系统简介、首页轮播图配置 |
| 数据统计 | 商品数、订单数、用户数、求购数、捐赠数和订单状态统计 |

#### 项目结构

```text
CampusSecond
├── campus-second-backend
│   ├── src/main/java/com/campus
│   │   ├── common/                 # 通用返回结构
│   │   ├── config/                 # 跨域、Web 和 MyBatis-Plus 配置
│   │   ├── controller/             # REST 接口控制器
│   │   ├── dto/                    # 数据传输对象
│   │   ├── entity/                 # 数据实体类
│   │   ├── mapper/                 # MyBatis-Plus Mapper
│   │   ├── service/                # 业务接口与实现
│   │   └── CampusSecondApplication.java
│   ├── src/main/resources
│   │   ├── mapper/                 # Mapper XML 文件
│   │   └── application.yml         # 项目配置文件
│   └── pom.xml                     # Maven 配置
├── campus-second-frontend
│   ├── src
│   │   ├── api/                    # 接口请求封装
│   │   ├── assets/                 # 前端静态资源
│   │   ├── components/             # 公共组件
│   │   ├── layout/                 # 前后台布局
│   │   ├── router/                 # 路由配置
│   │   ├── stores/                 # Pinia 状态管理
│   │   ├── utils/                  # 请求和工具函数
│   │   └── views/                  # 登录、用户端和管理端页面
│   ├── package.json                # 前端依赖和脚本配置
│   └── vite.config.ts              # Vite 配置及代理
├── upload/                         # 图片和文件上传目录
├── campus-second.sql               # MySQL 数据库脚本
└── README.md                       # 项目说明
```

#### 项目截图

项目运行后可查看以下页面效果：

![img.png](images%2Fimg.png)
![img_1.png](images%2Fimg_1.png)
![img_2.png](images%2Fimg_2.png)
![img_3.png](images%2Fimg_3.png)
![img_4.png](images%2Fimg_4.png)
![img_5.png](images%2Fimg_5.png)
![img_6.png](images%2Fimg_6.png)
![img_7.png](images%2Fimg_7.png)
![img_8.png](images%2Fimg_8.png)
![img_9.png](images%2Fimg_9.png)
![img_10.png](images%2Fimg_10.png)
![img_11.png](images%2Fimg_11.png)
![img_12.png](images%2Fimg_12.png)
![img_13.png](images%2Fimg_13.png)
![img_14.png](images%2Fimg_14.png)

#### 常见问题

1、数据库连接失败：检查 MySQL 服务是否启动，确认 `application.yml` 中的数据库名、账号和密码是否正确；数据库名包含连字符时，请使用反引号或图形化工具创建数据库。

2、SQL 导入后没有数据或表：请确认 `campus-second.sql` 已完整导入 `campus-second` 数据库，而不是只创建了空数据库。

3、前端接口请求失败：确认后端已启动在 `http://localhost:8080`，并检查 `campus-second-frontend/vite.config.ts` 中的代理地址是否与后端端口一致。

4、图片上传或显示失败：检查项目根目录下的 `upload/` 目录是否存在并具有读写权限，同时确认数据库中的图片路径以 `upload/` 开头。

5、前端依赖安装或构建失败：检查 Node.js 版本，建议使用 Node.js 18 或 Node.js 20，并删除 `node_modules` 后重新执行 `npm install`。

6、登录后页面跳转异常：清理浏览器 Local Storage 中保存的 `token` 和 `userInfo`，然后重新登录；前端会根据用户角色跳转到 `/admin` 或 `/home`。
