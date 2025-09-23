package jp.unaguna.fmtbuilder.buffered;

import jp.unaguna.fmtbuilder.ValueProviderAdapter;

import java.util.LinkedList;

public interface BufferHandler<T> {
    void handle(LinkedList<T> buffer, ValueProviderAdapter<T> adapter);
}
