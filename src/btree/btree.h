#ifndef SIMPLE_INNODB_BTREE_H
#define SIMPLE_INNODB_BTREE_H

#include "page/page.h"
#include <vector>
#include <memory>

namespace simple_innodb {

// B+树节点中的键值对
template<typename KeyType>
struct BTreeEntry {
    KeyType key;            // 键
    uint32_t page_id;       // 指向的页面ID
    uint32_t data_offset;   // 数据在页面中的偏移量
};

// B+树节点页面
template<typename KeyType>
class BTreePage : public Page {
public:
    BTreePage(uint32_t page_id, bool is_leaf)
        : Page(page_id, PageType::INDEX_PAGE), is_leaf_(is_leaf) {}

    // 插入键值对
    bool Insert(const KeyType& key, uint32_t child_page_id, uint32_t data_offset);
    
    // 查找键对应的页面ID
    bool Find(const KeyType& key, uint32_t& page_id) const;
    
    // 分裂节点
    std::unique_ptr<BTreePage> Split();
    
    // 获取最小键
    KeyType GetMinKey() const;
    
    // 是否是叶子节点
    bool IsLeaf() const { return is_leaf_; }
    
    // 序列化和反序列化
    void Serialize(char* dest) const override;
    void Deserialize(const char* src) override;

private:
    bool is_leaf_;  // 是否是叶子节点
    std::vector<BTreeEntry<KeyType>> entries_;
};

} // namespace simple_innodb

#endif // SIMPLE_INNODB_BTREE_H