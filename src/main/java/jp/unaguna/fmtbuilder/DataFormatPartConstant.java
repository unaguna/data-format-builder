package jp.unaguna.fmtbuilder;

import java.util.Objects;

class DataFormatPartConstant implements DataFormatPart {
    private final String value;

    DataFormatPartConstant(final String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public void format(final StringBuilder stringBuilder, final ValueProvider valueProvider) {
        stringBuilder.append(value);
    }

    @Override
    public String variableName() {
        return null;
    }

    public String getConstValue() {
        return this.value;
    }
}
