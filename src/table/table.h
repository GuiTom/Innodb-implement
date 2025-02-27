#ifndef SIMPLE_INNODB_TABLE_H
#define SIMPLE_INNODB_TABLE_H

#include "buffer/buffer_pool.h"
#include "btree/btree.h"
#include <string>
#include <vector>
#include <memory>

namespace simple_innodb {

// 字段类型
enum class FieldType {
    INT,
    VARCHAR,
    FLOAT,
    DATE
};

// 字段定义
struct FieldDef {
    std::string name;        // 字段名
    FieldType type;          // 字段类型
    uint32_t length;         // 字段长度
    bool is_nullable;        // 是否可为空
    bool is_primary_key;     // 是否为主键
};

// 表定义
class Table {
public:
    Table(const std::string& name, const std::vector<FieldDef>& fields);
    
    // 插入记录
    bool InsertRecord(const std::vector<std::string>& values);
    
    // 删除记录
    bool DeleteRecord(uint32_t record_id);
    
    // 更新记录
    bool UpdateRecord(uint32_t record_id, const std::vector<std::string>& values);
    
    // 查找记录
    bool FindRecord(uint32_t record_id, std::vector<std::string>& values);
    
    // 获取表名
    const std::string& GetName() const { return name_; }
    
    // 获取字段定义
    const std::vector<FieldDef>& GetFields() const { return fields_; }

private:
    // 分配新的记录ID
    uint32_t AllocateRecordId();
    
    // 序列化记录
    void SerializeRecord(const std::vector<std::string>& values, char* buffer) const;
    
    // 反序列化记录
    void DeserializeRecord(const char* buffer, std::vector<std::string>& values) const;

private:
    std::string name_;                      // 表名
    std::vector<FieldDef> fields_;          // 字段定义
    std::unique_ptr<BufferPool> buffer_pool_; // 缓冲池
    std::unique_ptr<BTreePage<uint32_t>> primary_index_; // 主键索引
    uint32_t next_record_id_;               // 下一个可用的记录ID
};

} // namespace simple_innodb

#endif // SIMPLE_INNODB_TABLE_H