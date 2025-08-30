package jp.unaguna.fmtbuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValueProviderAdapterTest {

    @Test
    public void testAdapterWithConstProvider() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("key")
                .build();

        final ValueProviderAdapter<Long> adapter = new ValueProviderAdapter.Builder<Long>()
                .addProvider("key", (d) -> "test" )
                .build();

        adapter.setElement(42L);

        final String actual = dataFormat.format(adapter);
        assertEquals("value=test", actual);
    }

    @Test
    public void testAdapter() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("%b")
                .build();

        final ValueProviderAdapter<String> adapter = new ValueProviderAdapter.Builder<String>()
                .addProvider("%a", (d) -> d.substring(0, 1) )
                .addProvider("%b", (d) -> d.substring(1, 2) )
                .addProvider("%c", (d) -> d.substring(2, 3) )
                .build();

        adapter.setElement("eagle");
        final String actual1 = dataFormat.format(adapter);
        assertEquals("value=a", actual1);

        adapter.setElement("drank");
        final String actual2 = dataFormat.format(adapter);
        assertEquals("value=r", actual2);
    }

    @Test
    public void testAdapter__error_with_null_lambda() {
        final ValueProviderAdapter.Builder<String> builder = new ValueProviderAdapter.Builder<>();

        assertThrows(NullPointerException.class, () -> builder.addProvider("key", null));
    }

    @Test
    public void testAdapter__error_with_unknown_key() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("value=")
                .string("dummy")
                .build();

        final ValueProviderAdapter<String> adapter = new ValueProviderAdapter.Builder<String>()
                .addProvider("%a", (d) -> d.substring(0, 1) )
                .addProvider("%b", (d) -> d.substring(1, 2) )
                .addProvider("%c", (d) -> d.substring(2, 3) )
                .build();

        adapter.setElement("eagle");
        final DataFormattingException actualExc =
                assertThrows(DataFormattingException.class, () -> dataFormat.format(adapter));
        assertInstanceOf(IllegalArgumentException.class, actualExc.getCause());
    }

    @Test
    public void testAdapter__error_without_element() {
        final DataFormat dataFormat = new DataFormat.Builder()
                .constant("test")
                .string("%a")
                .build();

        final ValueProviderAdapter<String> adapter = new ValueProviderAdapter.Builder<String>()
                .addProvider("%a", (d) -> d.substring(0, 1) )
                .build();

        final DataFormattingException actualExc =
                assertThrows(DataFormattingException.class, () -> dataFormat.format(adapter));
        assertInstanceOf(IllegalStateException.class, actualExc.getCause());
        assertEquals("This adapter contains no element.", actualExc.getCause().getMessage());
    }
}
