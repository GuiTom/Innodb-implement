package com.innodb.table;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TableTest {
    private Table table;
    private static final String TABLE_NAME = "test_table";

    @BeforeEach
    void setUp() {
        table = new Table(TABLE_NAME);
    }

    @Test
    void testTableCreation() {
        assertEquals(TABLE_NAME, table.getTableName());
        assertTrue(table.isEmpty());
    }

    @Test
    void testInsertAndRetrieve() {
        byte[] data = "test data".getBytes();
        int rowId = table.insertRow(data);
        
        byte[] retrieved = table.getRow(rowId);
        assertArrayEquals(data, retrieved);
    }

    @Test
    void testDeleteRow() {
        byte[] data = "test data".getBytes();
        int rowId = table.insertRow(data);
        
        assertTrue(table.deleteRow(rowId));
        assertNull(table.getRow(rowId));
    }

    @Test
    void testUpdateRow() {
        byte[] originalData = "original data".getBytes();
        byte[] updatedData = "updated data".getBytes();
        
        int rowId = table.insertRow(originalData);
        assertTrue(table.updateRow(rowId, updatedData));
        
        byte[] retrieved = table.getRow(rowId);
        assertArrayEquals(updatedData, retrieved);
    }

    @Test
    void testInvalidOperations() {
        // 测试无效的行ID
        assertNull(table.getRow(-1));
        assertFalse(table.deleteRow(-1));
        assertFalse(table.updateRow(-1, "test".getBytes()));
        
        // 测试空数据
        assertThrows(IllegalArgumentException.class, () -> table.insertRow(null));
        assertThrows(IllegalArgumentException.class, () -> table.updateRow(1, null));
    }

    @Test
    void testTableStatistics() {
        assertTrue(table.isEmpty());
        assertEquals(0, table.getRowCount());
        
        byte[] data = "test data".getBytes();
        table.insertRow(data);
        
        assertFalse(table.isEmpty());
        assertEquals(1, table.getRowCount());
    }

    @Test
    void testBatchOperations() {
        // 测试批量插入和删除
        int[] rowIds = new int[10];
        for (int i = 0; i < 10; i++) {
            rowIds[i] = table.insertRow(("data" + i).getBytes());
        }
        
        assertEquals(10, table.getRowCount());
        
        // 批量删除
        for (int rowId : rowIds) {
            assertTrue(table.deleteRow(rowId));
        }
        
        assertTrue(table.isEmpty());
    }

    @Test
    void testConcurrentAccess() {
        Thread writer = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                table.insertRow(("data" + i).getBytes());
            }
        });

        Thread reader = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                table.getRow(i);
            }
        });

        writer.start();
        reader.start();

        try {
            writer.join();
            reader.join();
        } catch (InterruptedException e) {
            fail("Concurrent access test failed");
        }
    }
}