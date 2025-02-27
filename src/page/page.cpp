#include "page.h"
#include <cstring>

namespace simple_innodb {

Page::Page(uint32_t page_id, PageType type) {
    header_.page_id = page_id;
    header_.page_type = type;
    header_.free_space = PAGE_SIZE - sizeof(PageHeader);
    header_.slot_count = 0;
    header_.next_page_id = 0;
    header_.prev_page_id = 0;
    
    // 初始化数据区域
    data_.resize(PAGE_SIZE - sizeof(PageHeader));
}

uint32_t Page::GetPageId() const {
    return header_.page_id;
}

PageType Page::GetPageType() const {
    return header_.page_type;
}

uint32_t Page::GetFreeSpace() const {
    return header_.free_space;
}

const char* Page::GetData() const {
    return data_.data();
}

char* Page::GetMutableData() {
    return data_.data();
}

void Page::Serialize(char* dest) const {
    // 序列化页面头
    std::memcpy(dest, &header_, sizeof(PageHeader));
    
    // 序列化数据区域
    std::memcpy(dest + sizeof(PageHeader), data_.data(), data_.size());
}

void Page::Deserialize(const char* src) {
    // 反序列化页面头
    std::memcpy(&header_, src, sizeof(PageHeader));
    
    // 反序列化数据区域
    data_.resize(PAGE_SIZE - sizeof(PageHeader));
    std::memcpy(data_.data(), src + sizeof(PageHeader), data_.size());
}

} // namespace simple_innodb