package jp.unaguna.fmtbuilder;

import java.util.MissingFormatArgumentException;
import java.util.Objects;

class DataFormatPartString implements DataFormatPart {
    private final String key;

    DataFormatPartString(final String key) {
        this.key = Objects.requireNonNull(key);
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
