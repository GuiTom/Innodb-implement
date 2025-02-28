# SimpleInnoDB

这是一个简化版的InnoDB存储引擎实现，用于学习和理解InnoDB的核心工作原理。该项目使用Java语言实现，旨在提供一个清晰的InnoDB核心概念的参考实现。

## 功能特性

- 基于B+树的索引实现
- 简化的缓冲池管理
- 基本的表空间和页面管理
- 数据表的基础操作支持

## 编译和运行

### 环境要求

- JDK 11 或更高版本
- Maven 3.6 或更高版本
- IDE推荐：IntelliJ IDEA 或 Eclipse

### 构建步骤

1. 克隆项目到本地：
```bash
git clone <repository-url>
cd {{project-dir}}
```

2. 使用Maven构建项目：
```bash
mvn clean install
```

3. 运行演示程序：
```bash
mvn exec:java -Dexec.mainClass="com.innodb.demo.InnoDBDemo"
```

## 项目结构

```
src/
├── main/
│   └── java/
│       └── com/
│           └── innodb/
│               ├── btree/     # B+树实现
│               ├── buffer/    # 缓冲池管理
│               ├── page/      # 页面结构和操作
│               ├── table/     # 表管理
│               └── demo/      # 演示程序
├── test/                      # 单元测试
└── pom.xml                    # Maven配置文件
```

### 核心组件

- **btree**: 实现B+树索引结构
- **buffer**: 管理内存缓冲池，实现页面置换
- **page**: 定义页面结构和基本操作
- **table**: 实现表级操作和管理

## 构建系统

项目使用Maven构建系统，主要的构建文件包括：

- `pom.xml`: Maven项目配置文件，定义项目依赖和构建配置
- `src/main/java`: 源代码目录
- `src/test/java`: 测试代码目录

## 开发说明

- 代码遵循Java代码规范
- 使用Maven进行依赖管理和项目构建
- 包含完整的单元测试
- 主要关注核心存储引擎概念的实现

## 注意事项

- 这是一个教学/学习用途的简化实现
- 不建议用于生产环境
- 部分功能做了简化处理

## 参考资料

- MySQL官方文档
- InnoDB存储引擎相关技术文档
- 数据库系统概念