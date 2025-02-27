#ifndef SIMPLE_INNODB_BUFFER_POOL_H
#define SIMPLE_INNODB_BUFFER_POOL_H

#include "page/page.h"
#include <unordered_map>
#include <list>
#include <memory>
#include <mutex>

namespace simple_innodb {

// 缓冲池中的页面帧
struct BufferFrame {
    std::unique_ptr<Page> page;      // 页面数据
    bool is_dirty;                   // 是否为脏页
    uint32_t pin_count;              // 引用计数
    uint64_t last_access_time;       // 最后访问时间
};

// 缓冲池管理器
class BufferPool {
public:
    explicit BufferPool(size_t pool_size);
    
    // 获取页面，如果不在内存中则从磁盘加载
    Page* GetPage(uint32_t page_id);
    
    // 创建新页面
    Page* CreatePage(uint32_t page_id, PageType type);
    
    // 标记页面为脏页
    void MarkDirty(uint32_t page_id);
    
    // 将脏页刷新到磁盘
    void FlushPage(uint32_t page_id);
    
    // 将所有脏页刷新到磁盘
    void FlushAllPages();
    
    // 从缓冲池中移除页面
    void RemovePage(uint32_t page_id);
    
    // 固定页面，防止被换出
    void PinPage(uint32_t page_id);
    
    // 解除页面固定
    void UnpinPage(uint32_t page_id);

private:
    // 使用LRU策略选择要替换的页面
    BufferFrame* EvictPage();
    
    // 从磁盘加载页面
    std::unique_ptr<Page> LoadPageFromDisk(uint32_t page_id);
    
    // 将页面写入磁盘
    void WritePageToDisk(const Page* page);

private:
    size_t pool_size_;                                       // 缓冲池大小
    std::unordered_map<uint32_t, BufferFrame> pages_;       // 页面映射表
    std::list<uint32_t> lru_list_;                          // LRU链表
    std::mutex mutex_;                                       // 互斥锁
};

} // namespace simple_innodb

#endif // SIMPLE_INNODB_BUFFER_POOL_H