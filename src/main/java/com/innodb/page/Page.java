package com.innodb.page;

import java.nio.ByteBuffer;

/**
 * 页面结构定义和基本操作
 */
public class Page {
    public static final int PAGE_SIZE = 16 * 1024; // 16KB
    private static final int PAGE_HEADER_SIZE = 128; // 页头大小
    
    private final ByteBuffer buffer;
    private final int pageId;
    
    public Page(int pageId) {
        this.pageId = pageId;
        this.buffer = ByteBuffer.allocate(PAGE_SIZE);
        initPageHeader();
    }
    
    public Page(int pageId, byte[] data) {
        this.pageId = pageId;
        this.buffer = ByteBuffer.wrap(data);
    }
    
    private void initPageHeader() {
        buffer.position(0);
        // 初始化页头信息
        buffer.putInt(pageId);           // 页ID
        buffer.putInt(0);                // 前一页
        buffer.putInt(0);                // 后一页
        buffer.putShort((short) 0);      // 记录数
        buffer.putShort((short) PAGE_HEADER_SIZE); // 空闲空间起始位置
        buffer.putInt(PAGE_SIZE);        // 空闲空间结束位置
    }
    
    public int getPageId() {
        return pageId;
    }
    
    public void writeData(int offset, byte[] data) {
        buffer.position(offset);
        buffer.put(data);
    }
    
    public byte[] readData(int offset, int length) {
        buffer.position(offset);
        byte[] data = new byte[length];
        buffer.get(data);
        return data;
    }
    
    public short getRecordCount() {
        buffer.position(8);
        return buffer.getShort();
    }
    
    public void setRecordCount(short count) {
        buffer.position(8);
        buffer.putShort(count);
    }
    
    public short getFreeSpaceStart() {
        buffer.position(10);
        return buffer.getShort();
    }
    
    public void setFreeSpaceStart(short position) {
        buffer.position(10);
        buffer.putShort(position);
    }
    
    public int getFreeSpaceEnd() {
        buffer.position(12);
        return buffer.getInt();
    }
    
    public void setFreeSpaceEnd(int position) {
        buffer.position(12);
        buffer.putInt(position);
    }
    
    public byte[] getBytes() {
        buffer.position(0);
        byte[] data = new byte[PAGE_SIZE];
        buffer.get(data);
        return data;
    }
    
    public int getFreeSpace() {
        return getFreeSpaceEnd() - getFreeSpaceStart();
    }
}