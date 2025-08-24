package jp.unaguna.fmtbuilder;

import java.util.Map;

public interface ValueProvider {
    Object get(String key);

    static ValueProviderMap fromMap(final Map<String, Object> m) {
        return new ValueProviderMapImpl(m);
    }
}
