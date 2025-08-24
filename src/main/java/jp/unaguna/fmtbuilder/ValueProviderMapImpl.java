package jp.unaguna.fmtbuilder;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

class ValueProviderMapImpl implements ValueProviderMap {
    private final Map<String, Object> impl;

    ValueProviderMapImpl(final Map<String, Object> impl) {
        this.impl = impl;
    }

    @Override
    public int size() {
        return impl.size();
    }

    @Override
    public boolean isEmpty() {
        return impl.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return impl.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return impl.containsValue(value);
    }

    @Override
    public Object get(final Object key) {
        return impl.containsValue(key);
    }

    @Override
    public Object get(String key) {
        return impl.get(key);
    }

    @Override
    public Object put(final String key, final Object value) {
        return impl.put(key, value);
    }

    @Override
    public Object remove(final Object key) {
        return impl.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ?> m) {
        impl.putAll(m);
    }

    @Override
    public void clear() {
        impl.clear();
    }

    @Override
    public Set<String> keySet() {
        return impl.keySet();
    }

    @Override
    public Collection<Object> values() {
        return impl.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return impl.entrySet();
    }
}
