package jp.unaguna.fmtbuilder;

import org.junit.jupiter.api.Test;

import java.util.MissingFormatArgumentException;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class DataFormatTest {
    @Test
    public void testConst() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("test")
                .build();

        final String actual = dataFormat.format(new DummyProvider());
        assertEquals("test", actual);
    }

    @Test
    public void testConst2() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("test")
                .constant("TEST")
                .build();

        final String actual = dataFormat.format(new DummyProvider());
        assertEquals("testTEST", actual);
    }

    @Test
    public void testConst__StringBuilder() {
        final StringBuilder stringBuilder = new StringBuilder();
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("test")
                .build();

        final String actual = dataFormat.format(new DummyProvider(), stringBuilder)
                .toString();
        assertEquals("test", actual);
    }

    @Test
    public void testVar() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("key")
                .build();

        final String actual = dataFormat.format(key -> {
            if ("key".equals(key)) {
                return "test";
            } else {
                throw new UnsupportedOperationException();
            }
        });
        assertEquals("value=test", actual);
    }

    @Test
    public void testVar__StringBuilder() {
        final StringBuilder stringBuilder = new StringBuilder();
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("key")
                .build();

        final String actual = dataFormat.format(key -> {
            if ("key".equals(key)) {
                return "test";
            } else {
                throw new UnsupportedOperationException();
            }
        }, stringBuilder)
                .toString();
        assertEquals("value=test", actual);
    }

    @Test
    public void testVar__error() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("key")
                .build();

        final DataFormattingException actualExc =
                assertThrowsExactly(DataFormattingException.class, () -> dataFormat.format(new DummyProvider()));
        assertInstanceOf(IllegalArgumentException.class, actualExc.getCause());
        assertInstanceOf(MissingFormatArgumentException.class, actualExc.getCause());
        assertEquals("some error occurred during formatting data", actualExc.getMessage());
    }

    @Test
    public void testVar__StringBuilder__error() {
        final StringBuilder stringBuilder = new StringBuilder();
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("key")
                .build();

        final DataFormattingException actualExc = assertThrowsExactly(
                        DataFormattingException.class,
                        () -> dataFormat.format(new DummyProvider(), stringBuilder));
        assertInstanceOf(IllegalArgumentException.class, actualExc.getCause());
        assertInstanceOf(MissingFormatArgumentException.class, actualExc.getCause());
        assertEquals("some error occurred during formatting data", actualExc.getMessage());
    }

    static class DummyProvider implements ValueProvider {
        @Override
        public String get(String key) {
            throw new IllegalArgumentException();
        }
    }
}
