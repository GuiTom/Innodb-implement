package com.innodb.btree;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BTreeTest {
    private BTree<Integer, String> bTree;

    @BeforeEach
    void setUp() {
        bTree = new BTree<>();
    }

    @Test
    void testEmptyTreeSearch() {
        assertNull(bTree.search(1), "空树搜索应该返回null");
    }

    @Test
    void testSingleNodeOperations() {
        // 测试单个节点的插入和查询
        bTree.insert(1, "one");
        assertEquals("one", bTree.search(1), "单节点查询失败");
        assertNull(bTree.search(2), "查询不存在的键应返回null");
    }

    @Test
    void testMultipleInsertions() {
        // 测试多个键值对的插入和查询
        String[] values = {"one", "two", "three", "four", "five"};
        for (int i = 1; i <= 5; i++) {
            bTree.insert(i, values[i-1]);
        }

        // 验证所有插入的值
        for (int i = 1; i <= 5; i++) {
            assertEquals(values[i-1], bTree.search(i), "键 " + i + " 的值不匹配");
        }
    }

    @Test
    void testNodeSplitting() {
        // 测试节点分裂情况
        // 插入足够多的键值对触发节点分裂
        for (int i = 1; i <= 10; i++) {
            bTree.insert(i, "value-" + i);
        }

        // 验证分裂后的查询
        for (int i = 1; i <= 10; i++) {
            assertEquals("value-" + i, bTree.search(i), "分裂后键 " + i + " 的值不匹配");
        }
    }

    @Test
    void testDuplicateKeys() {
        // 测试重复键的处理
        bTree.insert(1, "original");
        bTree.insert(1, "duplicate");
        assertEquals("duplicate", bTree.search(1), "重复键应更新为新值");
    }

    @Test
    void testRandomInsertions() {
        // 测试随机顺序插入
        int[] keys = {5, 2, 8, 1, 9, 3, 7, 4, 6, 10};
        for (int key : keys) {
            bTree.insert(key, "value-" + key);
        }

        // 验证所有键值对
        for (int key : keys) {
            assertEquals("value-" + key, bTree.search(key), "随机插入后键 " + key + " 的值不匹配");
        }
    }

    @Test
    void testLargeNumberOfInsertions() {
        // 测试大量数据插入
        int count = 1000;
        for (int i = 0; i < count; i++) {
            bTree.insert(i, "value-" + i);
        }

        // 验证随机键值
        for (int i = 0; i < 10; i++) {
            int key = (int) (Math.random() * count);
            assertEquals("value-" + key, bTree.search(key), "大量数据插入后键 " + key + " 的值不匹配");
        }
    }

    @Test
    void testNegativeKeys() {
        // 测试负数键
        bTree.insert(-1, "negative one");
        bTree.insert(-5, "negative five");
        bTree.insert(-3, "negative three");

        assertEquals("negative one", bTree.search(-1), "负数键 -1 的值不匹配");
        assertEquals("negative five", bTree.search(-5), "负数键 -5 的值不匹配");
        assertEquals("negative three", bTree.search(-3), "负数键 -3 的值不匹配");
    }

    @Test
    void testNullValue() {
        // 测试null值
        bTree.insert(1, null);
        assertNull(bTree.search(1), "null值应该正确存储和检索");
    }

    @Test
    void testBoundaryValues() {
        // 测试边界值
        bTree.insert(Integer.MAX_VALUE, "max value");
        bTree.insert(Integer.MIN_VALUE, "min value");
        bTree.insert(0, "zero");

        assertEquals("max value", bTree.search(Integer.MAX_VALUE), "最大整数值测试失败");
        assertEquals("min value", bTree.search(Integer.MIN_VALUE), "最小整数值测试失败");
        assertEquals("zero", bTree.search(0), "零值测试失败");
    }

    @Test
    void testSequentialAccess() {
        // 测试顺序访问性能
        int count = 100;
        // 按顺序插入
        for (int i = 0; i < count; i++) {
            bTree.insert(i, "seq-" + i);
        }
        // 按顺序查询
        for (int i = 0; i < count; i++) {
            assertEquals("seq-" + i, bTree.search(i), "顺序访问测试失败");
        }
    }

    @Test
    void testReverseSequentialAccess() {
        // 测试逆序访问性能
        int count = 100;
        // 逆序插入
        for (int i = count - 1; i >= 0; i--) {
            bTree.insert(i, "rev-" + i);
        }
        // 逆序查询
        for (int i = count - 1; i >= 0; i--) {
            assertEquals("rev-" + i, bTree.search(i), "逆序访问测试失败");
        }
    }
}