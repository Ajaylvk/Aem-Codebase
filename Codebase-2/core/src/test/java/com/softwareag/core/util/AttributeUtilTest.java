package com.softwareag.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AttributeUtilTest {

    @Test
    void test_removeEmptyLines() {
        assertNull(AttributeUtil.removeEmptyLines(null));
        assertEquals("", AttributeUtil.removeEmptyLines(""));
        assertEquals("", AttributeUtil.removeEmptyLines(" "));
        assertEquals("", AttributeUtil.removeEmptyLines("  "));
        assertEquals("", AttributeUtil.removeEmptyLines(" \n"));
        assertEquals("A", AttributeUtil.removeEmptyLines("A \n"));
        assertEquals("A and B", AttributeUtil.removeEmptyLines("A\nand\nB"));
        assertEquals("Type word word word word word", AttributeUtil.removeEmptyLines("\r\n\t\n\r Type   \t   \r word\r \r\n word\r\nword\n word\tword\r\n "));
    }

}
