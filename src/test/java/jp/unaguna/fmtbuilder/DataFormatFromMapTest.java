package jp.unaguna.fmtbuilder;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.MissingFormatArgumentException;

import static org.junit.jupiter.api.Assertions.*;

public class DataFormatFromMapTest {
    @Test
    public void testFromMap() {
        final Map<String, Object> map = new HashMap<>();
        map.put("key", "test");
        final ValueProvider valueProvider = ValueProvider.fromMap(map);

        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("key")
                .build();

        final String actual = dataFormat.format(valueProvider);
        assertEquals("value=test", actual);
    }

    @Test
    public void testFromMap__error() {
        // empty map
        final Map<String, Object> map = new HashMap<>();
        final ValueProvider valueProvider = ValueProvider.fromMap(map);

        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("key")
                .build();

        final DataFormattingException actualExc = assertThrowsExactly(
                DataFormattingException.class,
                () -> dataFormat.format(valueProvider));
        assertInstanceOf(IllegalArgumentException.class, actualExc.getCause());
        assertInstanceOf(MissingFormatArgumentException.class, actualExc.getCause());
        assertEquals("some error occurred during formatting data", actualExc.getMessage());
    }
}
