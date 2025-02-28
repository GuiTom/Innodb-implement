package com.innodb.page;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PageTest {
    private Page page;
    private static final int PAGE_ID = 1;
    private static final int DATA_SIZE = 1024;

    @BeforeEach
    void setUp() {
        page = new Page(PAGE_ID);
    }

    @Test
    void testPageInitialization() {
        assertEquals(PAGE_ID, page.getPageId());
        assertEquals(0, page.getDataSize());
        assertFalse(page.isDirty());
    }

    @Test
    void testWriteAndReadData() {
        byte[] data = new byte[DATA_SIZE];
        for (int i = 0; i < DATA_SIZE; i++) {
            data[i] = (byte) (i % 256);
        }

        page.writeData(data);
        assertTrue(page.isDirty());
        assertEquals(DATA_SIZE, page.getDataSize());

        byte[] readData = page.readData();
        assertArrayEquals(data, readData);
    }

    @Test
    void testClearPage() {
        byte[] data = new byte[DATA_SIZE];
        page.writeData(data);
        page.clear();

        assertEquals(0, page.getDataSize());
        assertFalse(page.isDirty());
    }

    @Test
    void testPageOverflow() {
        byte[] data = new byte[Page.MAX_PAGE_SIZE + 1];
        assertThrows(IllegalArgumentException.class, () -> page.writeData(data));
    }

    @Test
    void testPageMetadata() {
        page.setNextPageId(2);
        page.setPreviousPageId(0);
        page.setType(PageType.DATA);

        assertEquals(2, page.getNextPageId());
        assertEquals(0, page.getPreviousPageId());
        assertEquals(PageType.DATA, page.getType());
    }

    @Test
    void testPageCopy() {
        byte[] data = new byte[DATA_SIZE];
        page.writeData(data);
        page.setType(PageType.INDEX);

        Page copyPage = page.copy();
        assertEquals(page.getPageId(), copyPage.getPageId());
        assertEquals(page.getDataSize(), copyPage.getDataSize());
        assertEquals(page.getType(), copyPage.getType());
        assertArrayEquals(page.readData(), copyPage.readData());
    }
}