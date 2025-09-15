package jp.unaguna.fmtbuilder;

import java.util.*;

/**
 * DataFormat is a formatter for data consisting of field values.
 * DataFormat.Builder can be used to build formatting rules to create a DataFormat for each project-specific data.
 */
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
    public StringBuilder format(final ValueProvider valueProvider, final StringBuilder toAppendTo) {
        try {
            for (final DataFormatPart formatPart : formatParts) {
                formatPart.format(toAppendTo, valueProvider);
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
