#ifndef SIMPLE_INNODB_PAGE_H
#define SIMPLE_INNODB_PAGE_H

#include <cstdint>
#include <memory>
#include <vector>

namespace simple_innodb {

// 页面大小固定为16KB
constexpr size_t PAGE_SIZE = 16 * 1024;

// 页面类型
enum class PageType {
    INDEX_PAGE,     // B+树索引页
    DATA_PAGE,      // 数据页
    UNDO_PAGE,      // Undo日志页
    SYSTEM_PAGE     // 系统页
};

// 页面头部结构
struct PageHeader {
    uint32_t page_id;           // 页面ID
    PageType page_type;         // 页面类型
    uint32_t free_space;        // 空闲空间大小
    uint16_t slot_count;        // 槽位数量
    uint32_t next_page_id;      // 下一页ID
    uint32_t prev_page_id;      // 上一页ID
};

// 页面基类
class Page {
public:
    explicit Page(uint32_t page_id, PageType type);
    virtual ~Page() = default;

    // 获取页面ID
    uint32_t GetPageId() const;
    
    // 获取页面类型
    PageType GetPageType() const;
    
    // 获取空闲空间大小
    uint32_t GetFreeSpace() const;
    
    // 获取页面数据
    const char* GetData() const;
    char* GetMutableData();

    // 序列化页面数据
    virtual void Serialize(char* dest) const;
    
    // 反序列化页面数据
    virtual void Deserialize(const char* src);

protected:
    PageHeader header_;
    std::vector<char> data_;
};

} // namespace simple_innodb

#endif // SIMPLE_INNODB_PAGE_H