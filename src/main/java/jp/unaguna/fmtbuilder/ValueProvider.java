package jp.unaguna.fmtbuilder;

import java.util.Map;

/**
 * This provides values for formatting by {@link DataFormat}.
 */
public interface ValueProvider {
    /**
     * Returns the value of the specified key.
     *
     * @param key the key for required value
     * @return the value of the specified key
     * @throws IllegalArgumentException no value exists for the specified key
     */
    Object get(String key);

    static ValueProviderMap fromMap(final Map<String, Object> m) {
        return new ValueProviderMapImpl(m);
    }
}
