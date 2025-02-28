package com.innodb.demo;

import com.innodb.buffer.BufferPool;
import com.innodb.table.Table;

/**
 * InnoDB存储引擎演示程序
 */
public class InnoDBDemo {
    public static void main(String[] args) {
        // 创建缓冲池
        BufferPool bufferPool = new BufferPool(1000); // 设置缓冲池大小为1000页
        
        // 创建表
        Table table = new Table("demo_table", bufferPool);
        
        // 插入数据
        System.out.println("插入数据...");
        String data1 = "Hello, InnoDB!";
        String data2 = "This is a test.";
        table.insert(1, data1.getBytes());
        table.insert(2, data2.getBytes());
        
        // 查询数据
        System.out.println("查询数据...");
        byte[] result1 = table.get(1);
        byte[] result2 = table.get(2);
        
        System.out.println("ID 1的数据: " + new String(result1));
        System.out.println("ID 2的数据: " + new String(result2));
        
        // 创建并使用二级索引
        System.out.println("\n测试二级索引...");
        table.createSecondaryIndex("name_index");
        table.addSecondaryIndex("name_index", "test_key", 1);
        
        Integer primaryKey = table.getBySecondaryIndex("name_index", "test_key");
        if (primaryKey != null) {
            byte[] dataByIndex = table.get(primaryKey);
            System.out.println("通过二级索引查询到的数据: " + new String(dataByIndex));
        }
        
        // 将缓冲池中的数据刷新到磁盘
        System.out.println("\n将数据刷新到磁盘...");
        table.flush();
        
        System.out.println("演示完成！");
    }
}