package jp.unaguna.fmtbuilder;

import java.util.MissingFormatArgumentException;
import java.util.Objects;

class DataFormatPartString implements DataFormatPart {
    private final String key;
    private final ValuePadding padding;

    DataFormatPartString(final String key, final ValuePadding padding) {
        this.key = Objects.requireNonNull(key);
        this.padding = padding != null ? padding : ValuePadding.NONE;
    }

    @Override
    public void format(final StringBuilder stringBuilder, final ValueProvider valueProvider) {
        final Object value;
        try {
            value = valueProvider.get(key);
        } catch (IllegalArgumentException e) {
            throw new MissingFormatArgumentException(key);
        }
        stringBuilder.append(value);
    }

    @Override
    public String variableName() {
        return key;
    }
}
