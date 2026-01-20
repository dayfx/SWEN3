package com.fhtechnikum.paperless.services;

import org.junit.jupiter.api.Test;

import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class RFC3339DateFormatTest {

    private final RFC3339DateFormat dateFormat = new RFC3339DateFormat();

    @Test
    void format_ShouldFormatDateToRfc3339String() {

        // create fixed date: 2026-01-20 12:00:00 UTC
        long fixedTimestamp = 1768910400000L;
        Date date = new Date(fixedTimestamp);
        StringBuffer buffer = new StringBuffer();

        dateFormat.format(date, buffer, new FieldPosition(0));
        String result = buffer.toString();

        // expecting ISO-8601 / RFC-3339 format with UTC (+00:00)
        assertNotNull(result);
        assertTrue(result.contains("2026-01-20"));
        assertTrue(result.contains("12:00:00"));
        assertTrue(result.endsWith("+00:00") || result.endsWith("Z"),
                "Should use UTC timezone formatting. Actual: " + result);
    }

    @Test
    void parse_ShouldParseRfc3339StringToDate() {

        String source = "2026-01-20T12:00:00.000+00:00";
        ParsePosition pos = new ParsePosition(0);

        Date result = dateFormat.parse(source, pos);

        assertNotNull(result);
        // verify it converts back to the exact millisecond timestamp
        assertEquals(1768910400000L, result.getTime());
    }
}