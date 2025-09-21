package jp.unaguna.fmtbuilder.buffered;

import jp.unaguna.fmtbuilder.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

public class BufferedDataFormatIteratorTest {
    @Test
    public void testGetBlockSize() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .build();

        final List<ValueProvider> data = new ArrayList<>();
        data.add(null);

        final BufferedDataFormatIterator<ValueProvider> bufferedDataFormatIterator
                = new BufferedDataFormatIterator<>(
                dataFormat,
                data.iterator(),
                new ValueProviderAdapter.AsIs<>()
        );

        bufferedDataFormatIterator.setBlockSize(100);
        assertEquals(100, bufferedDataFormatIterator.getBlockSize());
        bufferedDataFormatIterator.setBlockSize(50);
        assertEquals(50, bufferedDataFormatIterator.getBlockSize());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void testGetBlockSize__error_with_non_positive(int blockSize) {
        final DataFormat dataFormat = new DataFormat.Builder()
                .build();

        final List<ValueProvider> data = new ArrayList<>();
        data.add(null);

        final BufferedDataFormatIterator<ValueProvider> bufferedDataFormatIterator
                = new BufferedDataFormatIterator<>(
                dataFormat,
                data.iterator(),
                new ValueProviderAdapter.AsIs<>()
        );

        assertThrows(IllegalArgumentException.class, () -> bufferedDataFormatIterator.setBlockSize(blockSize));
    }

    @ParameterizedTest
    @ValueSource(ints = {3, -1})
    public void testPadding(int blockSize) {
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
        data.add(key -> "key" + repeat(key.charAt(3), 3 * Integer.parseInt(String.valueOf(key.charAt(3)))));

        final BufferedDataFormatIterator<ValueProvider> bufferedDataFormatIterator
                = new BufferedDataFormatIterator<>(
                dataFormat,
                data.iterator(),
                new ValueProviderAdapter.AsIs<>()
        )
                .useDefaultWidthProviderProvider();
        if (blockSize > 0) {
            bufferedDataFormatIterator.setBlockSize(blockSize);
        }

        final List<String> actualLines = new ArrayList<>();
        for (ValueProvider record : data) {
            assertTrue(bufferedDataFormatIterator.hasNext());
            final String line = bufferedDataFormatIterator.next();
            actualLines.add(line);
            System.out.println(line);
        }
        assertEquals(data.size(), actualLines.size());
        assertEquals("  key1 key22     key333 key4444", actualLines.get(0));
        assertEquals(" key11 key2222   key333333 key44444444", actualLines.get(1));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(2));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(3));
        assertFalse(bufferedDataFormatIterator.hasNext());
    }

    @ParameterizedTest
    @ValueSource(ints = {3, -1})
    public void testPaddingWithoutWidthProvider(int blockSize) {
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
        data.add(key -> "key" + repeat(key.charAt(3), 3 * Integer.parseInt(String.valueOf(key.charAt(3)))));

        final BufferedDataFormatIterator<ValueProvider> bufferedDataFormatIterator
                = new BufferedDataFormatIterator<>(
                dataFormat,
                data.iterator(),
                new ValueProviderAdapter.AsIs<>()
        );
        // don't use width provider provider
                //.useDefaultWidthProviderProvider();
        if (blockSize > 0) {
            bufferedDataFormatIterator.setBlockSize(blockSize);
        }

        final List<String> actualLines = new ArrayList<>();
        for (ValueProvider record : data) {
            assertTrue(bufferedDataFormatIterator.hasNext());
            final String line = bufferedDataFormatIterator.next();
            actualLines.add(line);
            System.out.println(line);
        }
        assertEquals(data.size(), actualLines.size());
        assertEquals("key1 key22 key333 key4444", actualLines.get(0));
        assertEquals("key11 key2222 key333333 key44444444", actualLines.get(1));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(2));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(3));
        assertFalse(bufferedDataFormatIterator.hasNext());
    }

    @ParameterizedTest
    @ValueSource(ints = {3, -1})
    public void testPaddingWithAdapter(int blockSize) {
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
        data.add(3);

        final ValueProviderAdapter<Integer> adapter = new ValueProviderAdapter.Builder<Integer>()
                .addProvider("key1", i -> "key" + repeat("1", i))
                .addProvider("key2", i -> "key" + repeat("22", i))
                .addProvider("key3", i -> "key" + repeat("333", i))
                .addProvider("key4", i -> "key" + repeat("4444", i))
                .build();

        final BufferedDataFormatIterator<Integer> bufferedDataFormatIterator
                = new BufferedDataFormatIterator<>(
                        dataFormat,
                        data.iterator(),
                        adapter
                )
                .useDefaultWidthProviderProvider();
        if (blockSize > 0) {
            bufferedDataFormatIterator.setBlockSize(blockSize);
        }

        final List<String> actualLines = new ArrayList<>();
        for (int record : data) {
            assertTrue(bufferedDataFormatIterator.hasNext());
            final String line = bufferedDataFormatIterator.next();
            actualLines.add(line);
            System.out.println(line);
        }
        assertEquals(data.size(), actualLines.size());
        assertEquals("  key1 key22     key333 key4444", actualLines.get(0));
        assertEquals(" key11 key2222   key333333 key44444444", actualLines.get(1));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(2));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(3));
        assertFalse(bufferedDataFormatIterator.hasNext());
    }

    @ParameterizedTest
    @ValueSource(ints = {3, -1})
    public void testPaddingFromPrintf(int blockSize) {
        // function: a -> 1, b -> 2, ...
        final Function<String, Integer> keyToNum = key -> key.charAt(1) - 96;

        final DataFormat dataFormat = DataFormat.fromPrintfFormat(
                "%a %b %c %d",
                new VariablePaddingSpecifications()
                        .add("%a", ValuePadding.LEFT)
                        .add("%b", ValuePadding.RIGHT)
        );
        final List<ValueProvider> data = new ArrayList<>();
        data.add(key -> "key" + repeat(keyToNum.apply(key).toString(), 1 * keyToNum.apply(key)));
        data.add(key -> "key" + repeat(keyToNum.apply(key).toString(), 2 * keyToNum.apply(key)));
        data.add(key -> "key" + repeat(keyToNum.apply(key).toString(), 3 * keyToNum.apply(key)));
        data.add(key -> "key" + repeat(keyToNum.apply(key).toString(), 3 * keyToNum.apply(key)));

        final BufferedDataFormatIterator<ValueProvider> bufferedDataFormatIterator
                = new BufferedDataFormatIterator<>(
                dataFormat,
                data.iterator(),
                new ValueProviderAdapter.AsIs<>()
        )
                .useDefaultWidthProviderProvider();
        if (blockSize > 0) {
            bufferedDataFormatIterator.setBlockSize(blockSize);
        }

        final List<String> actualLines = new ArrayList<>();
        for (ValueProvider record : data) {
            assertTrue(bufferedDataFormatIterator.hasNext());
            final String line = bufferedDataFormatIterator.next();
            actualLines.add(line);
            System.out.println(line);
        }
        assertEquals(data.size(), actualLines.size());
        assertEquals("  key1 key22     key333 key4444", actualLines.get(0));
        assertEquals(" key11 key2222   key333333 key44444444", actualLines.get(1));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(2));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(3));
        assertFalse(bufferedDataFormatIterator.hasNext());
    }

    @Test
    public void testPaddingWithBufferEditor() {
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
        data.add(3);

        final ValueProviderAdapter<Integer> adapter = new ValueProviderAdapter.Builder<Integer>()
                .addProvider("key1", i -> "key" + repeat("1", i))
                .addProvider("key2", i -> "key" + repeat("22", i))
                .addProvider("key3", i -> "key" + repeat("333", i))
                .addProvider("key4", i -> "key" + repeat("4444", i))
                .build();

        final BufferedDataFormatIterator<Integer> bufferedDataFormatIterator
                = new BufferedDataFormatIterator<>(
                dataFormat,
                data.iterator(),
                adapter
        )
                .useDefaultWidthProviderProvider()
                // sort with BufferEditor
                .applyBufferEditor(((buffer, ad) -> buffer.sort((a, b) -> b - a)));

        final List<String> actualLines = new ArrayList<>();
        for (int record : data) {
            assertTrue(bufferedDataFormatIterator.hasNext());
            final String line = bufferedDataFormatIterator.next();
            actualLines.add(line);
            System.out.println(line);
        }
        assertEquals(data.size(), actualLines.size());
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(0));
        assertEquals("key111 key222222 key333333333 key444444444444", actualLines.get(1));
        assertEquals(" key11 key2222   key333333 key44444444", actualLines.get(2));
        assertEquals("  key1 key22     key333 key4444", actualLines.get(3));
        assertFalse(bufferedDataFormatIterator.hasNext());
    }

    @Test
    public void testPaddingWithBufferEditorAndBufferObserver() {
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
        data.add(3);

        final ValueProviderAdapter<Integer> adapter = new ValueProviderAdapter.Builder<Integer>()
                .addProvider("key1", i -> "key" + repeat("1", i))
                .addProvider("key2", i -> "key" + repeat("22", i))
                .addProvider("key3", i -> "key" + repeat("333", i))
                .addProvider("key4", i -> "key" + repeat("4444", i))
                .build();

        final List<Integer> observed = new ArrayList<>();
        final BufferedDataFormatIterator<Integer> bufferedDataFormatIterator
                = new BufferedDataFormatIterator<>(
                dataFormat,
                data.iterator(),
                adapter
        )
                .useDefaultWidthProviderProvider()
                .applyBufferObserver((buffer, ad) -> observed.addAll(buffer))
                // sort with BufferEditor before BufferObserver runs even if applied after
                .applyBufferEditor(((buffer, ad) -> buffer.sort((a, b) -> b - a)));

        final List<String> actualLines = new ArrayList<>();
        for (int record : data) {
            assertTrue(bufferedDataFormatIterator.hasNext());
            final String line = bufferedDataFormatIterator.next();
            actualLines.add(line);
            System.out.println(line);
        }
        assertEquals(data.size(), actualLines.size());
        assertArrayEquals(new Integer[]{3, 3, 2, 1}, observed.toArray(new Integer[0]));
        assertFalse(bufferedDataFormatIterator.hasNext());
    }

    private String repeat(final Object base, final int num) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < num; i++) {
            builder.append(base);
        }
        return builder.toString();
    }
}
