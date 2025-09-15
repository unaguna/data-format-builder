package jp.unaguna.fmtbuilder;

import java.util.*;

public class SimpleDataFormat implements DataFormat {
    private final List<DataFormatPart> formatParts;
    private final List<String> variables;

    SimpleDataFormat(final List<DataFormatPart> formatParts) {
        this.formatParts = formatParts;

        final List<String> variables = new ArrayList<>();
        formatParts.forEach(part -> {
            final String variableName = part.variableName();
            if (variableName != null) {
                variables.add(variableName);
            }
        });
        this.variables = Collections.unmodifiableList(variables);
    }

    @Override
    public String format(final ValueProvider valueProvider) {
        final StringBuilder stringBuilder = new StringBuilder();
        return this.format(valueProvider, stringBuilder)
                .toString();
    }

    @Override
    public String format(final ValueProvider valueProvider, final FieldWidthProvider fieldWidthProvider) {
        final StringBuilder stringBuilder = new StringBuilder();
        return this.format(valueProvider, fieldWidthProvider, stringBuilder)
                .toString();
    }

    @Override
    public StringBuilder format(final ValueProvider valueProvider, final StringBuilder toAppendTo) {
        return format(valueProvider, FieldWidthProvider.empty, toAppendTo);
    }

    @Override
    public StringBuilder format(
            final ValueProvider valueProvider,
            FieldWidthProvider fieldWidthProvider,
            final StringBuilder toAppendTo) {

        if (fieldWidthProvider == null) {
            fieldWidthProvider = FieldWidthProvider.empty;
        }

        try {
            for (final DataFormatPart formatPart : formatParts) {
                final String variableName = formatPart.variableName();
                final Integer width = variableName != null
                        ? fieldWidthProvider.getWidth(variableName)
                        : null;

                formatPart.format(toAppendTo, valueProvider, width);
            }
        } catch (final Exception e) {
            throw new DataFormattingException("some error occurred during formatting data", e);
        }
        return toAppendTo;
    }

    @Override
    public List<String> getVariableNames() {
        return this.variables;
    }
}
