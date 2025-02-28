package com.innodb.buffer;

import com.innodb.page.Page;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BufferPoolTest {
    private BufferPool bufferPool;
    private static final int POOL_SIZE = 3;

    @BeforeEach
    void setUp() {
        bufferPool = new BufferPool(POOL_SIZE);
    }

    @Test
    void testGetPage() {
        Page page1 = new Page(1);
        bufferPool.putPage(1, page1);
        
        Page retrieved = bufferPool.getPage(1);
        assertNotNull(retrieved);
        assertEquals(1, retrieved.getPageId());
    }

    @Test
    void testBufferFull() {
        // 填满缓冲池
        for (int i = 1; i <= POOL_SIZE; i++) {
            bufferPool.putPage(i, new Page(i));
        }

        // 添加新页面，触发页面置换
        Page newPage = new Page(POOL_SIZE + 1);
        bufferPool.putPage(POOL_SIZE + 1, newPage);

        // 验证最早加入的页面已被置换
        assertNull(bufferPool.getPage(1));
        assertNotNull(bufferPool.getPage(POOL_SIZE + 1));
    }

    @Test
    void testPageEviction() {
        // 测试页面驱逐策略
        Page page1 = new Page(1);
        Page page2 = new Page(2);
        Page page3 = new Page(3);

        bufferPool.putPage(1, page1);
        bufferPool.putPage(2, page2);
        bufferPool.putPage(3, page3);

        // 访问page2，使其成为最近使用的页面
        bufferPool.getPage(2);

        // 添加新页面，触发页面置换
        bufferPool.putPage(4, new Page(4));

        // 验证最少使用的page1被置换
        assertNull(bufferPool.getPage(1));
        assertNotNull(bufferPool.getPage(2));
    }

    @Test
    void testConcurrentAccess() {
        // 测试并发访问
        Page page = new Page(1);
        bufferPool.putPage(1, page);

        Thread reader = new Thread(() -> {
            Page readPage = bufferPool.getPage(1);
            assertNotNull(readPage);
        });

        Thread writer = new Thread(() -> {
            Page newPage = new Page(1);
            bufferPool.putPage(1, newPage);
        });

        reader.start();
        writer.start();

        try {
            reader.join();
            writer.join();
        } catch (InterruptedException e) {
            fail("Concurrent access test failed");
        }
    }

    @Test
    void testInvalidPageAccess() {
        // 测试访问不存在的页面
        assertNull(bufferPool.getPage(999));
    }
}