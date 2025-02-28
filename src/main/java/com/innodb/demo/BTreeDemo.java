package com.innodb.demo;

import com.innodb.btree.BTree;

public class BTreeDemo {
    public static void main(String[] args) {
        // 创建一个存储Integer类型键和String类型值的B+树
        BTree<Integer, String> bTree = new BTree<>();

        // 插入一些测试数据
        System.out.println("插入测试数据...");
        bTree.insert(10, "Value-10");
        bTree.insert(5, "Value-5");
        bTree.insert(15, "Value-15");
        bTree.insert(3, "Value-3");
        bTree.insert(7, "Value-7");
        bTree.insert(12, "Value-12");
        bTree.insert(18, "Value-18");

        // 查找测试
        System.out.println("\n开始查找测试:");
        System.out.println("查找键值 5: " + bTree.search(5));
        System.out.println("查找键值 12: " + bTree.search(12));
        System.out.println("查找键值 18: " + bTree.search(18));
        System.out.println("查找键值 100: " + bTree.search(100));

        // 插入更多数据测试分裂
        System.out.println("\n插入更多数据测试节点分裂...");
        for (int i = 20; i <= 30; i++) {
            bTree.insert(i, "Value-" + i);
            System.out.println("插入键值 " + i);
        }

        // 再次查找测试
        System.out.println("\n分裂后查找测试:");
        System.out.println("查找键值 25: " + bTree.search(25));
        System.out.println("查找键值 30: " + bTree.search(30));
    }
}