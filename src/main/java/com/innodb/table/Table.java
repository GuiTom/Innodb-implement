package com.innodb.table;

import com.innodb.btree.BTree;
import com.innodb.buffer.BufferPool;
import com.innodb.page.Page;

import java.util.HashMap;
import java.util.Map;

/**
 * 表管理实现
 */
public class Table {
    private final String tableName;
    private final BufferPool bufferPool;
    private final BTree<Integer, byte[]> primaryIndex;
    private final Map<String, BTree<? extends Comparable<?>, Integer>> secondaryIndexes;
    
    public Table(String tableName, BufferPool bufferPool) {
        this.tableName = tableName;
        this.bufferPool = bufferPool;
        this.primaryIndex = new BTree<>();
        this.secondaryIndexes = new HashMap<>();
    }
    
    public void insert(int primaryKey, byte[] data) {
        // 将数据写入页面
        Page page = new Page(primaryKey);
        page.writeData(0, data);
        bufferPool.putPage(new BufferPool.Page(primaryKey, page.getBytes()));
        
        // 更新主索引
        primaryIndex.insert(primaryKey, data);
    }
    
    public byte[] get(int primaryKey) {
        // 先查找缓冲池
        BufferPool.Page page = bufferPool.getPage(primaryKey);
        if (page != null) {
            return page.getData();
        }
        
        // 从索引中查找
        return primaryIndex.search(primaryKey);
    }
    
    public <K extends Comparable<K>> void createSecondaryIndex(String indexName) {
        if (!secondaryIndexes.containsKey(indexName)) {
            secondaryIndexes.put(indexName, new BTree<K, Integer>());
        }
    }
    
    public <K extends Comparable<K>> void addSecondaryIndex(String indexName, K key, int primaryKey) {
        @SuppressWarnings("unchecked")
        BTree<K, Integer> index = (BTree<K, Integer>) secondaryIndexes.get(indexName);
        if (index != null) {
            index.insert(key, primaryKey);
        }
    }
    
    public <K extends Comparable<K>> Integer getBySecondaryIndex(String indexName, K key) {
        @SuppressWarnings("unchecked")
        BTree<K, Integer> index = (BTree<K, Integer>) secondaryIndexes.get(indexName);
        if (index != null) {
            return index.search(key);
        }
        return null;
    }
    
    public String getTableName() {
        return tableName;
    }
    
    public void flush() {
        bufferPool.flushAllPages();
    }
}