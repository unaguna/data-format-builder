package jp.unaguna.fmtbuilder;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TableDataFormatIteratorTest {

    @Test
    public void testPadding() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .string("key1", ValuePadding.LEFT)
                .constant(" ")
                .string("key2", ValuePadding.RIGHT)
                .constant(" ")
                .string("key3")
                .constant(" ")
                .string("key4")
                .build();
        final List<ValueProvider> data = new ArrayList<>();
        data.add(key -> "key" + repeat(key.charAt(3), 1 * Integer.parseInt(String.valueOf(key.charAt(3)))));
        data.add(key -> "key" + repeat(key.charAt(3), 2 * Integer.parseInt(String.valueOf(key.charAt(3)))));
        data.add(key -> "key" + repeat(key.charAt(3), 3 * Integer.parseInt(String.valueOf(key.charAt(3)))));

        final TableDataFormatIteratorWithoutAdapter<ValueProvider> tableDataFormatIterator
                = new TableDataFormatIteratorWithoutAdapter<>(
                dataFormat,
                data.iterator()
        );

        final List<String> actualLines = new ArrayList<>();
        for (ValueProvider record : data) {
            assertTrue(tableDataFormatIterator.hasNext());
            final String line = tableDataFormatIterator.next();
            actualLines.add(line);
            System.out.println(line);
        }
        assertEquals(data.size(), actualLines.size());
        assertEquals("  key1 key22     key333 key4444", actualLines.get(0));
        assertEquals(" key11 key2222   key333333 key44444444", actualLines.get(1));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(2));
        assertFalse(tableDataFormatIterator.hasNext());
    }

    @Test
    public void testPaddingWithAdapter() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .string("key1", ValuePadding.LEFT)
                .constant(" ")
                .string("key2", ValuePadding.RIGHT)
                .constant(" ")
                .string("key3")
                .constant(" ")
                .string("key4")
                .build();
        final List<Integer> data = new ArrayList<>();
        data.add(1);
        data.add(2);
        data.add(3);

        final ValueProviderAdapter<Integer> adapter = new ValueProviderAdapter.Builder<Integer>()
                .addProvider("key1", i -> "key" + repeat("1", i))
                .addProvider("key2", i -> "key" + repeat("22", i))
                .addProvider("key3", i -> "key" + repeat("333", i))
                .addProvider("key4", i -> "key" + repeat("4444", i))
                .build();

        final TableDataFormatIterator<Integer> tableDataFormatIterator
                = new TableDataFormatIterator<>(
                        dataFormat,
                        data.iterator(),
                        adapter
                );

        final List<String> actualLines = new ArrayList<>();
        for (int record : data) {
            assertTrue(tableDataFormatIterator.hasNext());
            final String line = tableDataFormatIterator.next();
            actualLines.add(line);
            System.out.println(line);
        }
        assertEquals(data.size(), actualLines.size());
        assertEquals("  key1 key22     key333 key4444", actualLines.get(0));
        assertEquals(" key11 key2222   key333333 key44444444", actualLines.get(1));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(2));
        assertFalse(tableDataFormatIterator.hasNext());
    }

    private String repeat(final Object base, final int num) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            builder.append(base);
        }
        return builder.toString();
    }
}
