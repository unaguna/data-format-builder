package jp.unaguna.fmtbuilder;

import java.util.HashMap;
import java.util.Map;

public class VariablePaddingSpecifications {
    private final Map<String, ValuePadding> specs = new HashMap<>();

    public VariablePaddingSpecifications add(final String variableName, final ValuePadding padding) {
        this.specs.put(variableName, padding);
        return this;
    }

    public ValuePadding get(final String variableName) {
        return this.specs.getOrDefault(variableName, ValuePadding.NONE);
    }
}
