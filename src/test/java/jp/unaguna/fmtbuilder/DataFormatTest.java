package jp.unaguna.fmtbuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

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

    static class DummyProvider implements ValueProvider {
        @Override
        public String get(String key) {
            throw new UnsupportedOperationException();
        }
    }
}
