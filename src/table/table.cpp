#include "table.h"
#include <cstring>
#include <stdexcept>

namespace simple_innodb {

Table::Table(const std::string& name, const std::vector<FieldDef>& fields)
    : name_(name), fields_(fields), next_record_id_(1) {
    // 创建缓冲池
    buffer_pool_ = std::make_unique<BufferPool>(1000);
    
    // 创建主键索引的根页面
    primary_index_ = std::make_unique<BTreePage<uint32_t>>(0, true);
}

bool Table::InsertRecord(const std::vector<std::string>& values) {
    if (values.size() != fields_.size()) {
        return false;
    }
    
    // 分配新的记录ID
    uint32_t record_id = AllocateRecordId();
    
    // 创建新的数据页面
    Page* page = buffer_pool_->CreatePage(record_id, PageType::DATA_PAGE);
    if (!page) {
        return false;
    }
    
    // 序列化记录数据
    SerializeRecord(values, page->GetMutableData());
    
    // 更新索引
    if (!primary_index_->Insert(record_id, record_id, 0)) {
        buffer_pool_->RemovePage(record_id);
        return false;
    }
    
    // 标记页面为脏页
    buffer_pool_->MarkDirty(record_id);
    return true;
}

bool Table::DeleteRecord(uint32_t record_id) {
    // 查找记录
    uint32_t page_id;
    if (!primary_index_->Find(record_id, page_id)) {
        return false;
    }
    
    // 从缓冲池中移除页面
    buffer_pool_->RemovePage(record_id);
    return true;
}

bool Table::UpdateRecord(uint32_t record_id, const std::vector<std::string>& values) {
    if (values.size() != fields_.size()) {
        return false;
    }
    
    // 查找记录
    uint32_t page_id;
    if (!primary_index_->Find(record_id, page_id)) {
        return false;
    }
    
    // 获取页面
    Page* page = buffer_pool_->GetPage(record_id);
    if (!page) {
        return false;
    }
    
    // 更新记录数据
    SerializeRecord(values, page->GetMutableData());
    
    // 标记页面为脏页
    buffer_pool_->MarkDirty(record_id);
    return true;
}

bool Table::FindRecord(uint32_t record_id, std::vector<std::string>& values) {
    // 查找记录
    uint32_t page_id;
    if (!primary_index_->Find(record_id, page_id)) {
        return false;
    }
    
    // 获取页面
    Page* page = buffer_pool_->GetPage(record_id);
    if (!page) {
        return false;
    }
    
    // 反序列化记录数据
    DeserializeRecord(page->GetData(), values);
    return true;
}

uint32_t Table::AllocateRecordId() {
    return next_record_id_++;
}

void Table::SerializeRecord(const std::vector<std::string>& values, char* buffer) const {
    char* cur = buffer;
    
    // 序列化每个字段的值
    for (size_t i = 0; i < values.size(); ++i) {
        const auto& value = values[i];
        const auto& field = fields_[i];
        
        switch (field.type) {
            case FieldType::INT: {
                int32_t int_value = std::stoi(value);
                std::memcpy(cur, &int_value, sizeof(int32_t));
                cur += sizeof(int32_t);
                break;
            }
            case FieldType::FLOAT: {
                float float_value = std::stof(value);
                std::memcpy(cur, &float_value, sizeof(float));
                cur += sizeof(float);
                break;
            }
            case FieldType::VARCHAR: {
                uint32_t length = std::min(static_cast<uint32_t>(value.length()), field.length);
                std::memcpy(cur, &length, sizeof(uint32_t));
                cur += sizeof(uint32_t);
                std::memcpy(cur, value.c_str(), length);
                cur += length;
                break;
            }
            case FieldType::DATE: {
                // 简单起见，这里将日期存储为整数
                int32_t date_value = std::stoi(value);
                std::memcpy(cur, &date_value, sizeof(int32_t));
                cur += sizeof(int32_t);
                break;
            }
        }
    }
}

void Table::DeserializeRecord(const char* buffer, std::vector<std::string>& values) const {
    const char* cur = buffer;
    values.clear();
    
    // 反序列化每个字段的值
    for (const auto& field : fields_) {
        switch (field.type) {
            case FieldType::INT: {
                int32_t int_value;
                std::memcpy(&int_value, cur, sizeof(int32_t));
                values.push_back(std::to_string(int_value));
                cur += sizeof(int32_t);
                break;
            }
            case FieldType::FLOAT: {
                float float_value;
                std::memcpy(&float_value, cur, sizeof(float));
                values.push_back(std::to_string(float_value));
                cur += sizeof(float);
                break;
            }
            case FieldType::VARCHAR: {
                uint32_t length;
                std::memcpy(&length, cur, sizeof(uint32_t));
                cur += sizeof(uint32_t);
                values.push_back(std::string(cur, length));
                cur += length;
                break;
            }
            case FieldType::DATE: {
                int32_t date_value;
                std::memcpy(&date_value, cur, sizeof(int32_t));
                values.push_back(std::to_string(date_value));
                cur += sizeof(int32_t);
                break;
            }
        }
    }
}

} // namespace simple_innodb