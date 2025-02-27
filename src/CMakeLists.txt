# 设置源文件
set(SOURCES
    page/page.cpp
    btree/btree.cpp
    buffer/buffer_pool.cpp
    table/table.cpp
)

# 创建库文件
add_library(innodb_core ${SOURCES})

# 设置头文件包含目录
target_include_directories(innodb_core PUBLIC
    ${CMAKE_CURRENT_SOURCE_DIR}
)

# 创建可执行文件
add_executable(innodb_demo main.cpp)
target_link_libraries(innodb_demo innodb_core)