#!/bin/bash

# 设置错误时退出
set -e

# 清理并创建build目录
rm -rf build
mkdir -p build

# 运行cmake配置
echo "正在配置CMake..."
cmake -B build

# 编译项目
echo "正在编译项目..."
cd build
make

# 运行demo程序
echo "正在运行demo程序..."
./src/innodb_demo