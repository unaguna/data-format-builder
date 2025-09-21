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
    public void format(final StringBuilder stringBuilder, final ValueProvider valueProvider, final Integer width) {
        final String value;
        try {
            value = valueProvider.get(key).toString();
        } catch (IllegalArgumentException e) {
            throw new MissingFormatArgumentException(key);
        }

        // padding left
        if (padding == ValuePadding.LEFT) {
            for(int i = value.length(); i< width; i++) {
                stringBuilder.append(" ");
            }
        }

        stringBuilder.append(value);

        // padding right
        if (padding == ValuePadding.RIGHT) {
            for(int i = value.length(); i< width; i++) {
                stringBuilder.append(" ");
            }
        }
    }

    @Override
    public String variableName() {
        return key;
    }
}
