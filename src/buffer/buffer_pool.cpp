#include "buffer_pool.h"
#include <chrono>
#include <fstream>
#include <algorithm>

namespace simple_innodb {

BufferPool::BufferPool(size_t pool_size) : pool_size_(pool_size) {}

Page* BufferPool::GetPage(uint32_t page_id) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    // 检查页面是否在缓冲池中
    auto it = pages_.find(page_id);
    if (it != pages_.end()) {
        // 更新访问时间
        it->second.last_access_time = std::chrono::system_clock::now().time_since_epoch().count();
        return it->second.page.get();
    }
    
    // 如果缓冲池已满，需要淘汰一个页面
    if (pages_.size() >= pool_size_) {
        BufferFrame* victim = EvictPage();
        if (victim && victim->is_dirty) {
            WritePageToDisk(victim->page.get());
        }
    }
    
    // 从磁盘加载页面
    auto page = LoadPageFromDisk(page_id);
    if (!page) {
        return nullptr;
    }
    
    // 将页面添加到缓冲池
    BufferFrame frame;
    frame.page = std::move(page);
    frame.is_dirty = false;
    frame.pin_count = 0;
    frame.last_access_time = std::chrono::system_clock::now().time_since_epoch().count();
    
    auto result = frame.page.get();
    pages_.emplace(page_id, std::move(frame));
    lru_list_.push_back(page_id);
    
    return result;
}

Page* BufferPool::CreatePage(uint32_t page_id, PageType type) {
    std::lock_guard<std::mutex> lock(mutex_);
    
    // 检查页面是否已存在
    if (pages_.find(page_id) != pages_.end()) {
        return nullptr;
    }
    
    // 如果缓冲池已满，需要淘汰一个页面
    if (pages_.size() >= pool_size_) {
        BufferFrame* victim = EvictPage();
        if (victim && victim->is_dirty) {
            WritePageToDisk(victim->page.get());
        }
    }
    
    // 创建新页面
    auto page = std::make_unique<Page>(page_id, type);
    
    // 将页面添加到缓冲池
    BufferFrame frame;
    frame.page = std::move(page);
    frame.is_dirty = true;
    frame.pin_count = 0;
    frame.last_access_time = std::chrono::system_clock::now().time_since_epoch().count();
    
    auto result = frame.page.get();
    pages_.emplace(page_id, std::move(frame));
    lru_list_.push_back(page_id);
    
    return result;
}

void BufferPool::MarkDirty(uint32_t page_id) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = pages_.find(page_id);
    if (it != pages_.end()) {
        it->second.is_dirty = true;
    }
}

void BufferPool::FlushPage(uint32_t page_id) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = pages_.find(page_id);
    if (it != pages_.end() && it->second.is_dirty) {
        WritePageToDisk(it->second.page.get());
        it->second.is_dirty = false;
    }
}

void BufferPool::FlushAllPages() {
    std::lock_guard<std::mutex> lock(mutex_);
    for (auto& pair : pages_) {
        if (pair.second.is_dirty) {
            WritePageToDisk(pair.second.page.get());
            pair.second.is_dirty = false;
        }
    }
}

void BufferPool::RemovePage(uint32_t page_id) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = pages_.find(page_id);
    if (it != pages_.end()) {
        if (it->second.is_dirty) {
            WritePageToDisk(it->second.page.get());
        }
        pages_.erase(it);
        lru_list_.remove(page_id);
    }
}

void BufferPool::PinPage(uint32_t page_id) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = pages_.find(page_id);
    if (it != pages_.end()) {
        ++it->second.pin_count;
    }
}

void BufferPool::UnpinPage(uint32_t page_id) {
    std::lock_guard<std::mutex> lock(mutex_);
    auto it = pages_.find(page_id);
    if (it != pages_.end() && it->second.pin_count > 0) {
        --it->second.pin_count;
    }
}

BufferFrame* BufferPool::EvictPage() {
    // 使用LRU策略选择要替换的页面
    for (auto it = lru_list_.begin(); it != lru_list_.end(); ++it) {
        auto page_it = pages_.find(*it);
        if (page_it != pages_.end() && page_it->second.pin_count == 0) {
            lru_list_.erase(it);
            return &page_it->second;
        }
    }
    return nullptr;
}

std::unique_ptr<Page> BufferPool::LoadPageFromDisk(uint32_t page_id) {
    std::string filename = "page_" + std::to_string(page_id) + ".dat";
    std::ifstream file(filename, std::ios::binary);
    
    if (!file) {
        return nullptr;
    }
    
    char buffer[PAGE_SIZE];
    file.read(buffer, PAGE_SIZE);
    
    if (!file) {
        return nullptr;
    }
    
    auto page = std::make_unique<Page>(page_id, PageType::DATA_PAGE);
    page->Deserialize(buffer);
    
    return page;
}

void BufferPool::WritePageToDisk(const Page* page) {
    std::string filename = "page_" + std::to_string(page->GetPageId()) + ".dat";
    std::ofstream file(filename, std::ios::binary);
    
    if (!file) {
        return;
    }
    
    char buffer[PAGE_SIZE];
    page->Serialize(buffer);
    file.write(buffer, PAGE_SIZE);
}

} // namespace simple_innodb