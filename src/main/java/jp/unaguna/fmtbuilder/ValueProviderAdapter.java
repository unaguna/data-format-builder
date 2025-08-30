package jp.unaguna.fmtbuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * This instance acts as a ValueProvider using the value of the inner instance.
 *
 * <p>
 * To determine specific behavior and create an adapter instance, use {@link Builder}.
 * You can specify a lambda expression that returns a value for keys as follows.
 * </p>
 *
 * <pre>{@code
 * final ValueProviderAdapter<YourData> adapter = new ValueProviderAdapter.Builder<YourData>()
 *         .addProvider("key1", (data) -> data.getValueForKey1() )
 *         .addProvider("cls", (data) -> data.getClass().getCanonicalName() )
 *         .build();
 * }</pre>
 *
 * <p>
 * An adapter instance functions as a ValueProvider that extracts the necessary values from the data
 * by holding the data to be formatted using {@link ValueProviderAdapter<T>.setElement(T)}.
 * Since this instance is intended to be reused,
 * if you want to use the adapter for other data,
 * execute {@link ValueProviderAdapter<T>.setElement(T)} again to change the data the adapter holds and then use it.
 * </p>
 *
 * @param <T> type of inner instance
 */
public class ValueProviderAdapter<T> implements ValueProvider {
    private final Map<String, Function<T, Object>> providers;
    private T element = null;

    public void setElement(final T element) {
        this.element = element;
    }

    private ValueProviderAdapter(final Map<String, Function<T, Object>> providers) {
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

    public static class Builder<T> {
        private final Map<String, Function<T, Object>> providers = new HashMap<>();

        public Builder<T> addProvider(final String key, final Function<T, Object> provider) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(provider);

            if (providers.containsKey(key)) {
                throw new IllegalArgumentException("A provider for the key '" + key + "' is already added.");
            }

            providers.put(key, provider);
            return this;
        }

        public ValueProviderAdapter<T> build() {
            return new ValueProviderAdapter<>(this.providers);
        }
    }
}
