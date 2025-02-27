# SimpleInnoDB

这是一个简化版的InnoDB存储引擎实现，用于学习和理解InnoDB的核心工作原理。

## 功能特性

- 基于B+树的索引实现
- 简化的缓冲池管理
- 基本的表空间和页面管理
- 数据表的基础操作支持

## 编译和运行

### 环境要求

- C++17 或更高版本
- CMake 3.10 或更高版本
- 支持现代C++编译器（如GCC、Clang）

### 构建步骤

1. 克隆项目到本地：
```bash
git clone <repository-url>
cd SimpleInnoDB
```

2. 运行构建脚本：
```bash
./build.sh
```

或者手动执行以下步骤：

```bash
mkdir -p build
cmake -B build
cd build
make
```

3. 运行演示程序：
```bash
./build/src/innodb_demo
```

## 项目结构

```
src/
├── btree/          # B+树实现
├── buffer/         # 缓冲池管理
├── page/           # 页面结构和操作
├── table/          # 表管理
└── main.cpp        # 演示程序入口
```

### 核心组件

- **btree**: 实现B+树索引结构
- **buffer**: 管理内存缓冲池，实现页面置换
- **page**: 定义页面结构和基本操作
- **table**: 实现表级操作和管理

## 构建系统

项目使用CMake构建系统，主要的构建文件包括：

- `CMakeLists.txt`: 主项目配置
- `src/CMakeLists.txt`: 源代码编译配置
- `build.sh`: 快速构建脚本

## 开发说明

- 代码遵循C++17标准
- 使用CMake进行项目管理
- 包含基本的错误处理机制
- 主要关注核心存储引擎概念的实现

## 注意事项

- 这是一个教学/学习用途的简化实现
- 不建议用于生产环境
- 部分功能做了简化处理

## 参考资料

- MySQL官方文档
- InnoDB存储引擎相关技术文档
- 数据库系统概念