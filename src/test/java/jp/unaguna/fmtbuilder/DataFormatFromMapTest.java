package jp.unaguna.fmtbuilder;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
