package jp.unaguna.fmtbuilder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

    @Test
    public void testVariableNames() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("key")
                .build();

        assertArrayEquals(new String[]{"key"}, dataFormat.getVariableNames().toArray(new String[0]));
    }

    @Test
    public void testVariableNames2() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("key1")
                .constant(",")
                .string("key2")
                .build();

        assertArrayEquals(new String[]{"key1", "key2"}, dataFormat.getVariableNames().toArray(new String[0]));
    }

    @ParameterizedTest
    @CsvSource({
            "LEFT, value='      test'",
            "RIGHT, value='test      '"
    })
    public void testPadding(final ValuePadding padding, final String expected) {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value='")
                .string("key", padding)
                .constant("'")
                .build();

        final FieldWidthProvider fieldWidthProvider = fieldName -> 10;

        final String actual = dataFormat.format(key -> {
            if ("key".equals(key)) {
                return "test";
            } else {
                throw new UnsupportedOperationException();
            }
        }, fieldWidthProvider);
        assertEquals(expected, actual);
    }

    static class DummyProvider implements ValueProvider {
        @Override
        public String get(String key) {
            throw new IllegalArgumentException();
        }
    }
}
