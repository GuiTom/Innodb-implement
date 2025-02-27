#include "btree.h"
#include <algorithm>
#include <cassert>

namespace simple_innodb {

template<typename KeyType>
bool BTreePage<KeyType>::Insert(const KeyType& key, uint32_t child_page_id, uint32_t data_offset) {
    // 找到插入位置
    auto it = std::lower_bound(entries_.begin(), entries_.end(), key,
        [](const BTreeEntry<KeyType>& entry, const KeyType& key) {
            return entry.key < key;
        });
    
    // 如果键已存在，返回失败
    if (it != entries_.end() && it->key == key) {
        return false;
    }
    
    // 创建新条目
    BTreeEntry<KeyType> new_entry{key, child_page_id, data_offset};
    entries_.insert(it, new_entry);
    
    return true;
}

template<typename KeyType>
bool BTreePage<KeyType>::Find(const KeyType& key, uint32_t& page_id) const {
    auto it = std::lower_bound(entries_.begin(), entries_.end(), key,
        [](const BTreeEntry<KeyType>& entry, const KeyType& key) {
            return entry.key < key;
        });
    
    if (it == entries_.end()) {
        return false;
    }
    
    if (is_leaf_) {
        // 叶子节点需要精确匹配
        if (it->key == key) {
            page_id = it->page_id;
            return true;
        }
        return false;
    } else {
        // 非叶子节点返回小于等于key的最大值对应的页面ID
        if (it->key > key && it != entries_.begin()) {
            --it;
        }
        page_id = it->page_id;
        return true;
    }
}

template<typename KeyType>
std::unique_ptr<BTreePage<KeyType>> BTreePage<KeyType>::Split() {
    if (entries_.size() < 2) {
        return nullptr;
    }
    
    // 创建新节点
    auto new_page = std::make_unique<BTreePage<KeyType>>(header_.page_id + 1, is_leaf_);
    
    // 将后半部分移动到新节点
    size_t mid = entries_.size() / 2;
    new_page->entries_.assign(entries_.begin() + mid, entries_.end());
    entries_.resize(mid);
    
    return new_page;
}

template<typename KeyType>
KeyType BTreePage<KeyType>::GetMinKey() const {
    assert(!entries_.empty());
    return entries_.front().key;
}

template<typename KeyType>
void BTreePage<KeyType>::Serialize(char* dest) const {
    // 先序列化基类部分
    Page::Serialize(dest);
    
    // 序列化是否为叶子节点
    char* cur = dest + sizeof(PageHeader);
    std::memcpy(cur, &is_leaf_, sizeof(bool));
    cur += sizeof(bool);
    
    // 序列化条目数量
    size_t count = entries_.size();
    std::memcpy(cur, &count, sizeof(size_t));
    cur += sizeof(size_t);
    
    // 序列化所有条目
    for (const auto& entry : entries_) {
        std::memcpy(cur, &entry, sizeof(BTreeEntry<KeyType>));
        cur += sizeof(BTreeEntry<KeyType>);
    }
}

template<typename KeyType>
void BTreePage<KeyType>::Deserialize(const char* src) {
    // 先反序列化基类部分
    Page::Deserialize(src);
    
    // 反序列化是否为叶子节点
    const char* cur = src + sizeof(PageHeader);
    std::memcpy(&is_leaf_, cur, sizeof(bool));
    cur += sizeof(bool);
    
    // 反序列化条目数量
    size_t count;
    std::memcpy(&count, cur, sizeof(size_t));
    cur += sizeof(size_t);
    
    // 反序列化所有条目
    entries_.resize(count);
    for (size_t i = 0; i < count; ++i) {
        std::memcpy(&entries_[i], cur, sizeof(BTreeEntry<KeyType>));
        cur += sizeof(BTreeEntry<KeyType>);
    }
}

// 显式实例化模板
template class BTreePage<uint32_t>;

} // namespace simple_innodb