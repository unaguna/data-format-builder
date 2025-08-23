package jp.unaguna.fmtbuilder;

import java.util.Objects;

class DataFormatPartString implements DataFormatPart {
    private final String key;

    DataFormatPartString(final String key) {
        this.key = Objects.requireNonNull(key);
    }

    @Override
    public void format(final StringBuilder stringBuilder, final ValueProvider valueProvider) {
        stringBuilder.append(valueProvider.get(key));
    }
}
