package com.innodb.buffer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

/**
 * 缓冲池管理
 */
public class BufferPool {
    private static final int DEFAULT_BUFFER_SIZE = 1024; // 默认缓冲池大小
    private final int capacity;
    private final Map<Integer, Page> pages; // 页面缓存
    private final Queue<Integer> lruQueue; // LRU队列

    public static class Page {
        private final int pageId;
        private byte[] data;
        private boolean isDirty;

        public Page(int pageId, byte[] data) {
            this.pageId = pageId;
            this.data = data;
            this.isDirty = false;
        }

        public int getPageId() {
            return pageId;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
            this.isDirty = true;
        }

        public boolean isDirty() {
            return isDirty;
        }

        public void setDirty(boolean dirty) {
            isDirty = dirty;
        }
    }

    public BufferPool() {
        this(DEFAULT_BUFFER_SIZE);
    }

    public BufferPool(int capacity) {
        this.capacity = capacity;
        this.pages = new HashMap<>();
        this.lruQueue = new LinkedList<>();
    }

    public Page getPage(int pageId) {
        if (pages.containsKey(pageId)) {
            // 更新LRU队列
            lruQueue.remove(pageId);
            lruQueue.offer(pageId);
            return pages.get(pageId);
        }
        return null;
    }

    public void putPage(Page page) {
        int pageId = page.getPageId();
        if (pages.size() >= capacity && !pages.containsKey(pageId)) {
            // 缓冲池满，需要淘汰页面
            evictPage();
        }
        pages.put(pageId, page);
        lruQueue.offer(pageId);
    }

    private void evictPage() {
        while (!lruQueue.isEmpty()) {
            int pageId = lruQueue.poll();
            Page page = pages.get(pageId);
            if (page != null) {
                if (page.isDirty()) {
                    // 如果是脏页，需要写回磁盘
                    flushPage(page);
                }
                pages.remove(pageId);
                break;
            }
        }
    }

    private void flushPage(Page page) {
        // TODO: 实现页面写回磁盘的逻辑
    }

    public void flushAllPages() {
        for (Page page : pages.values()) {
            if (page.isDirty()) {
                flushPage(page);
            }
        }
    }
}