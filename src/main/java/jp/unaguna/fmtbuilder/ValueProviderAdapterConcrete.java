package jp.unaguna.fmtbuilder;

import java.util.Map;
import java.util.function.Function;

class ValueProviderAdapterConcrete<T> extends ValueProviderAdapter<T> {
    private final Map<String, Function<T, Object>> providers;

    ValueProviderAdapterConcrete(final Map<String, Function<T, Object>> providers) {
        super();
        this.providers = providers;
    }

    @Override
    public Object get(final String key) {
        final T element = this.element;
        if (element == null) {
            throw new IllegalStateException("This adapter contains no element.");
        }

        final Function<T, Object> provider = providers.get(key);
        if (provider == null) {
            throw new IllegalArgumentException(key);
        }

        return provider.apply(element);
    }
}
