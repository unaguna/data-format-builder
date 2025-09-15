package jp.unaguna.fmtbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UnknownFormatConversionException;

public interface DataFormat {
    int CODE_POINT_PERCENT = '%';

    /**
     * Formats data.
     *
     * @param valueProvider the data to format
     * @return the formatted String
     * @throws DataFormattingException if some error occurred during formatting
     */
    String format(final ValueProvider valueProvider);

    /**
     * Formats data.
     *
     * @param valueProvider the data to format
     * @param fieldWidthProvider the provider of minimum width of each variable.
     *                           This minimum width is used only for variables whose padding mode is specified.
     * @return the formatted String
     * @throws DataFormattingException if some error occurred during formatting
     */
    String format(final ValueProvider valueProvider, final FieldWidthProvider fieldWidthProvider);

    /**
     * Formats data and appends the resulting text to the string builder.
     *
     * @param valueProvider the data to format
     * @param toAppendTo the string buffer to which the formatted text is to be appended
     * @return the value passed in as toAppendTo
     * @throws DataFormattingException if some error occurred during formatting
     */
    StringBuilder format(final ValueProvider valueProvider, final StringBuilder toAppendTo);

    /**
     * Formats data and appends the resulting text to the string builder.
     *
     * @param valueProvider the data to format
     * @param fieldWidthProvider the provider of minimum width of each variable.
     *                           This minimum width is used only for variables whose padding mode is specified.
     * @param toAppendTo the string buffer to which the formatted text is to be appended
     * @return the value passed in as toAppendTo
     * @throws DataFormattingException if some error occurred during formatting
     */
    StringBuilder format(
            final ValueProvider valueProvider,
            final FieldWidthProvider fieldWidthProvider,
            final StringBuilder toAppendTo);

    /**
     * Returns the variable names used in the format
     *
     * @return the variable names
     */
    List<String> getVariableNames();

    /**
     * Create a DataFormat instance which formats data by printf-formatting such as '%a'.
     *
     * <p>
     * The created DateFormat treats the first two characters starting with % as placeholders,
     * and when formatting, it retrieves the value using the placeholder string as the key and embeds it.
     * For example, if you specify '%abc' as the format, the value of %a is retrieved during formatting.
     * If this value is 'ABC', the result will be 'ABCbc'. However, '%%' is an exception and '%' is embedded.
     * </p>
     *
     * @param fmt printf format
     * @return the DataFormat instance which formats data by the specified format
     */
    static DataFormat fromPrintfFormat(final String fmt) {
        Objects.requireNonNull(fmt);
        final Builder builder = new Builder();
        int head = 0;
        boolean percent = false;
        while (head < fmt.length()) {
            // the char on current head
            final int headCodePoint = fmt.codePointAt(head);
            if (percent) {
                if (headCodePoint == CODE_POINT_PERCENT) {
                    builder.constant("%");
                } else {
                    builder.string("%" + new String(Character.toChars(headCodePoint)));
                }
                percent = false;

            } else {
                if (headCodePoint == CODE_POINT_PERCENT) {
                    percent = true;
                } else {
                    builder.constant(new String(Character.toChars(headCodePoint)));
                }

            }
            head = fmt.offsetByCodePoints(head, 1);
        }

        // If single % is on the end, it is illegal format.
        if (percent) {
            throw new UnknownFormatConversionException("%");
        }
        return builder.build();
    }

    class Builder {
        private List<DataFormatPart> formatParts = new ArrayList<>();

        public DataFormat build() {
            compressConstants();
            return new SimpleDataFormat(formatParts);
        }

        public Builder constant(final String value) {
            formatParts.add(new DataFormatPartConstant(value));
            return this;
        }

        public Builder string(final String key) {
            return string(key, ValuePadding.NONE);
        }

        public Builder string(final String key, final ValuePadding padding) {
            formatParts.add(new DataFormatPartString(key, padding));
            return this;
        }

        /**
         * Compress consecutive {@link this.constant(String)}s into one.
         */
        private void compressConstants() {
            final List<DataFormatPart> newParts = new ArrayList<>();
            StringBuilder currentConst = null;
            for (final DataFormatPart part : this.formatParts) {
                if (part instanceof DataFormatPartConstant) {
                    if (currentConst == null) {
                        currentConst = new StringBuilder();
                    }
                    currentConst.append(((DataFormatPartConstant) part).getConstValue());
                } else {
                    if (currentConst != null) {
                        newParts.add(new DataFormatPartConstant(currentConst.toString()));
                        currentConst = null;
                    }
                    newParts.add(part);
                }
            }

            if (currentConst != null) {
                newParts.add(new DataFormatPartConstant(currentConst.toString()));
            }

            this.formatParts = newParts;
        }
    }
}
