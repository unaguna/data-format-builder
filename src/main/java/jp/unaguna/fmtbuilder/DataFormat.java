package jp.unaguna.fmtbuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * DataFormat is a formatter for data consisting of field values.
 * DataFormat.Builder can be used to build formatting rules to create a DataFormat for each project-specific data.
 */
public class DataFormat {
    private final List<DataFormatPart> formatParts;

    private DataFormat(final List<DataFormatPart> formatParts) {
        this.formatParts = formatParts;
    }

    /**
     * Formats data.
     *
     * @param valueProvider the data to format
     * @return the formatted String
     */
    public String format(final ValueProvider valueProvider) {
        final StringBuilder stringBuilder = new StringBuilder();
        return this.format(valueProvider, stringBuilder)
                .toString();
    }

    /**
     * Formats data and appends the resulting text to the string builder.
     *
     * @param valueProvider the data to format
     * @param toAppendTo the string buffer to which the formatted text is to be appended
     * @return the value passed in as toAppendTo
     */
    public StringBuilder format(final ValueProvider valueProvider, final StringBuilder toAppendTo) {
        for (final DataFormatPart formatPart : formatParts) {
            formatPart.format(toAppendTo, valueProvider);
        }
        return toAppendTo;
    }

    public static class Builder {
        private final List<DataFormatPart> formatParts = new ArrayList<>();

        public DataFormat build() {
            return new DataFormat(formatParts);
        }

        public Builder constant(final String value) {
            formatParts.add(new DataFormatPartConstant(value));
            return this;
        }

        public Builder string(final String key) {
            formatParts.add(new DataFormatPartString(key));
            return this;
        }
    }
}
