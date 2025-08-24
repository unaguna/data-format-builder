package jp.unaguna.fmtbuilder;

import org.junit.jupiter.api.Test;

import java.util.IllegalFormatException;
import java.util.UnknownFormatConversionException;

import static org.junit.jupiter.api.Assertions.*;

public class DataFormatPrintfTest {
    @Test
    public void testPrintfBuild() {
        final DataFormat dataFormat = DataFormat.fromPrintfFormat("abc %s_def");

        final String actual = dataFormat.format(key -> {
            if ("%s".equals(key)) {
                return "test";
            } else {
                throw new UnsupportedOperationException();
            }
        });
        assertEquals("abc test_def", actual);
    }

    @Test
    public void testPrintfBuild_escape() {
        final DataFormat dataFormat = DataFormat.fromPrintfFormat("abc%% %%%% %%s");

        final String actual = dataFormat.format(key -> {
            throw new UnsupportedOperationException();
        });
        assertEquals("abc% %% %s", actual);
    }

    @Test
    public void testPrintfBuild_empty() {
        final DataFormat dataFormat = DataFormat.fromPrintfFormat("");

        final String actual = dataFormat.format(key -> {
            throw new UnsupportedOperationException();
        });
        assertEquals("", actual);
    }

    @Test
    public void testPrintfBuild_const() {
        final DataFormat dataFormat = DataFormat.fromPrintfFormat("abcde");

        final String actual = dataFormat.format(key -> {
            throw new UnsupportedOperationException();
        });
        assertEquals("abcde", actual);
    }

    @Test
    public void testPrintfBuild_2byte() {
        final DataFormat dataFormat = DataFormat.fromPrintfFormat("\uD842\uDFB7%\uD842\uDFB7");

        final String actual = dataFormat.format(key -> {
            if ("%\uD842\uDFB7".equals(key)) {
                return "あ";
            } else {
                throw new UnsupportedOperationException();
            }
        });
        assertEquals("\uD842\uDFB7あ", actual);
    }

    @Test
    public void testPrintfBuild__error_by_suffix_single_percent() {
        final UnknownFormatConversionException actualExc =
                assertThrowsExactly(UnknownFormatConversionException.class,
                        () -> DataFormat.fromPrintfFormat("abc %s_def%"));
        assertInstanceOf(IllegalFormatException.class, actualExc);
        assertEquals("%", actualExc.getConversion());
    }
}
