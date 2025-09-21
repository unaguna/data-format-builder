package jp.unaguna.fmtbuilder.buffered;

import jp.unaguna.fmtbuilder.ValueProviderAdapter;

import java.util.List;

public interface BufferObserver<T> {
    void observe(List<T> buffer, ValueProviderAdapter<T> adapter);
}
